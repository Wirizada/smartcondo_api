# Especificação Funcional e Arquitetural (PRD/SAD) — SmartCondo SaaS

- **Produto:** `smartcondo` (Plataforma B2B SaaS para Gestão Condominial)
- **Componentes:** `smartcondo_api` (Back-end) / `smartcondo_web` (Front-end)
- **Fase:** Baseline Arquitetural e Funcional (V1.0)
- **Objetivo:** Estabelecer as regras de negócio core, o modelo arquitetural (Monolito Modular Multi-Tenant) e a stack tecnológica adotada.

---

## 1. Visão Geral do Sistema

O SmartCondo é um motor de gestão operacional e financeira para Administradoras de Condomínios. O sistema substitui processos manuais (planilhas e conciliações demoradas) por fluxos automatizados, garantindo que o faturamento de milhares de unidades seja gerado e validado em segundos, com segurança e isolamento total de dados entre condomínios.

---

## 2. Lógica de Negócio e Domínios Core (Bounded Contexts)

O sistema é dividido em módulos de negócio independentes. Cada módulo possui suas entidades, regras e responsabilidades bem delimitadas.

### 2.1. Módulo: Gestão Condominial (`condominio`)

Domínio central de cadastro. Define a estrutura física e jurídica que será faturada.

**Entidades:**

| Entidade      | Tabela       | Descrição                                               |
|---------------|--------------|---------------------------------------------------------|
| `Condominio`  | `condominios`| Entidade raiz. Possui CNPJ único e flag `ativo`.        |
| `Bloco`       | `blocos`     | Agrupamento físico (torre/bloco). Pertence ao tenant.   |
| `Morador`     | `moradores`  | Pessoa física (CPF, nome, contato). Pertence ao tenant. |
| `Unidade`     | `unidades`   | Apartamento/Casa. Vincula bloco, morador e fração ideal.|

**Relacionamentos:**
- `Bloco` → pertence a 1 `Condominio` (via `@TenantId`)
- `Morador` → pertence a 1 `Condominio` (via `@TenantId`)
- `Unidade` → pertence a 1 `Bloco` (via `blocoId`) e tem 1 `Morador` responsável (via `moradorId`)

**Regras de Negócio:**

- **RN-C01 (Vinculação de Responsabilidade):** Uma `Unidade` deve obrigatoriamente ter 1 (um) `Morador` vinculado (`moradorId NOT NULL`) para que o faturamento seja liberado. A responsabilidade financeira (boleto) recai sobre o morador designado.
- **RN-C02 (Validação de Fração Ideal):** A `fracaoIdeal` (campo `DECIMAL(6,4)`) representa o peso da unidade no rateio condominial. A soma das frações ideais de todas as `Unidades` ativas (`ativa = true`) de um `Condominio` deve ser rigidamente igual a `1.0000` (100%). O sistema deve bloquear cadastros que quebrem esta invariante.

**Facade Pública — `CondominioFacade`:**

Este é o **contrato público** do módulo. Outros módulos consomem dados do condomínio **exclusivamente** por esta classe, sem acesso direto a repositories ou entidades.

| Método | Retorno | Finalidade |
|--------|---------|------------|
| `listarUnidadesParaFaturamento()` | `List<UnidadeParaFaturamentoDTO>` | Unidades ativas com `idUnidade`, `nomeSacado` e `fracaoIdeal` para o motor de rateio. |
| `buscarNumerosUnidades(List<UUID>)` | `Map<UUID, String>` | Mapa `id → número` para enriquecer listagens de outros módulos. |

> `UnidadeParaFaturamentoDTO` é um Record Java imutável: `(UUID idUnidade, String nomeSacado, BigDecimal fracaoIdeal)`.

---

### 2.2. Módulo: Faturamento e Arrecadação (`faturamento`)

Coração financeiro do SaaS. Responsável por gerar, calcular e acompanhar a receita do condomínio.

**Entidades:**

| Entidade      | Tabela         | Descrição                                                        |
|---------------|----------------|------------------------------------------------------------------|
| `Competencia` | `competencias` | Período de referência (`mes`/`ano`). Agrupa despesas do mês.     |
| `Despesa`     | `despesas`     | Custo do condomínio (água, luz, manutenção) vinculado à competência. |
| `Fatura`      | `faturas`      | Cobrança individual por unidade em uma competência.              |
| `Boleto`      | `boletos`      | Documento de cobrança bancária vinculado à fatura.               |

**Máquina de Estados da Fatura (`StatusFatura`):**

```
RASCUNHO → ABERTA → VENCIDA → PAGA
                  ↘ CANCELADA
```

| Estado      | Descrição                                                       |
|-------------|-----------------------------------------------------------------|
| `RASCUNHO`  | Gerada pelo sistema, invisível para o morador.                  |
| `ABERTA`    | Publicada e aguardando pagamento.                               |
| `VENCIDA`   | Ultrapassou a data de vencimento. Multas e juros passam a incidir. |
| `PAGA`      | Retorno bancário confirmou a liquidação.                        |
| `CANCELADA` | Cancelada administrativamente pelo síndico.                     |

**Regras de Negócio:**

- **RN-F01 (Motor de Rateio — `GerarFaturaService`):** Ao fechar uma `Competencia`, o sistema recebe um `valorBase` e uma `dataVencimento`. Consulta as unidades ativas via `CondominioFacade.listarUnidadesParaFaturamento()` e gera uma `Fatura` por unidade com status `RASCUNHO`.
  - *Cálculo A (Taxa Fixa):* Total Despesas / Nº de Unidades Ativas.
  - *Cálculo B (Fração Ideal):* Total Despesas × `fracaoIdeal` da Unidade.
- **RN-F02 (Motor de Inadimplência):** Se uma fatura está `VENCIDA`, o back-end aplica regras dinâmicas na 2ª via:
  - **Multa Fixa:** 2% sobre o valor original.
  - **Juros de Mora (pro-rata die):** 1% ao mês, calculado em dias: `valor × 0.01 / 30 × diasAtraso`.
- **RN-F03 (Independência de Módulo):** O módulo de Faturamento **não acessa** tabelas nem repositories do módulo `condominio`. Toda informação sobre unidades é obtida via `CondominioFacade`, recebendo apenas DTOs projetados. JOINs entre domínios são proibidos. Isso garante que a futura extração para microsserviço exija apenas a substituição da Facade por chamada REST/gRPC.

**Listagem — `ListarFaturaService`:**

Busca faturas via `faturaRepository.findAll()` (filtrada automaticamente por `@TenantId`) e enriquece o número da unidade chamando `CondominioFacade.buscarNumerosUnidades()` — sem JOIN cross-domain.

---

### 2.3. Módulo: Identidade e Acesso (`usuario`)

Gerencia autenticação e autorização dos usuários do sistema.

**Entidades:**

| Entidade    | Tabela     | Descrição                                     |
|-------------|------------|-----------------------------------------------|
| `Usuario`   | `usuarios` | Usuário do sistema (login). Email único.       |
| `TipoUsuario` | (enum)  | Papel do usuário: `SINDICO` ou `MORADOR`.      |

**Regras de Negócio:**

- **RN-U01 (Autorização Contextual):** Um usuário (e-mail único) pertence a um condomínio (tenant). O `role` (`SINDICO` | `MORADOR`) define as permissões no contexto daquele tenant. A autenticação será feita via JWT contendo o `tenantId` e o `role` do usuário.

> **Nota:** `Morador` (módulo `condominio`) ≠ `Usuario` (módulo `usuario`). O `Morador` é a pessoa física cadastrada na estrutura condominial (responsável financeiro da unidade). O `Usuario` é o login de acesso ao sistema. São bounded contexts distintos.

---

## 3. Arquitetura: Monolito Modular Pragmático

### 3.1. Estratégia Monolith-First

Evitamos microsserviços precoces para acelerar a entrega, mas estruturamos o código para separação futura. A organização dispensa a burocracia de Clean Architecture profunda (sem adapters inúteis), focando na entrega de valor via **MVC otimizado por Domínio**.

### 3.2. Estrutura de Diretórios

```
smartcondo_api/
├── core/                           # Infraestrutura cross-cutting
│   └── tenant/                     # Multi-tenancy
│       ├── TenantContext.java      # ThreadLocal com tenant ID
│       ├── TenantFilter.java       # OncePerRequestFilter (valida X-Tenant-Id)
│       └── TenantIdentifierResolver.java  # Hibernate → UUID do tenant
│
├── condominio/                     # Bounded Context: Gestão Condominial
│   ├── api/                        # Controllers HTTP
│   │   ├── CondominioController.java
│   │   └── dto/                    # DTOs de Request/Response (Records)
│   │       ├── CadastrarCondominioRequest.java
│   │       └── CondominioResponse.java
│   ├── application/                # Services + Facades (regras RN-C01, RN-C02)
│   │   ├── CondominioService.java
│   │   ├── CondominioFacade.java   # ← Contrato público inter-módulo
│   │   └── dto/                    # DTOs de aplicação (Facade outputs)
│   │       └── UnidadeParaFaturamentoDTO.java
│   ├── model/                      # Entidades JPA (@Entity)
│   │   ├── Condominio.java
│   │   ├── Bloco.java
│   │   ├── Unidade.java
│   │   └── Morador.java
│   └── infra/                      # Spring Data Repositories
│       ├── CondominioRepository.java
│       ├── BlocoRepository.java
│       ├── UnidadeRepository.java
│       └── MoradorRepository.java
│
├── faturamento/                    # Bounded Context: Faturamento e Arrecadação
│   ├── api/
│   │   ├── FaturaController.java
│   │   └── dto/
│   │       └── GerarFaturasRequest.java
│   ├── application/                # Motor de Rateio + Listagem
│   │   ├── GerarFaturaService.java
│   │   ├── ListarFaturaService.java
│   │   └── dto/
│   │       └── FaturaResumoDTO.java
│   ├── model/
│   │   ├── Fatura.java
│   │   ├── StatusFatura.java       # enum: RASCUNHO|ABERTA|VENCIDA|PAGA|CANCELADA
│   │   ├── Competencia.java
│   │   ├── Despesa.java
│   │   └── Boleto.java
│   └── infra/
│       ├── FaturaRepository.java
│       ├── CompetenciaRepository.java
│       ├── DespesaRepository.java
│       └── BoletoRepository.java
│
└── usuario/                        # Bounded Context: Identidade e Acesso
    ├── model/
    │   ├── Usuario.java
    │   └── TipoUsuario.java        # enum: SINDICO|MORADOR
    └── infra/
        └── UsuarioRepository.java
```

### 3.3. Contratos Internos (Facades)

Nenhum módulo cruza a fronteira do outro via banco de dados. **JOINs entre tabelas de domínios diferentes são proibidos.** A comunicação inter-módulo é feita exclusivamente via Facades — classes `@Service` na camada `application` que expõem DTOs projetados.

Hoje, a `CondominioFacade` é uma chamada local em memória. Na futura extração para microsserviços, basta substituí-la por um cliente REST/gRPC **sem alterar nenhum Service consumidor**.

### 3.4. Convenções de Código

| Camada         | Pacote         | Responsabilidade                         | Padrão de Classe           |
|----------------|----------------|------------------------------------------|----------------------------|
| API            | `api/`         | Controllers REST + DTOs de Request/Response | `XxxController`, Records  |
| Application    | `application/` | Services com regras de negócio + Facades | `XxxService`, `XxxFacade`  |
| Model          | `model/`       | Entidades JPA (`@Entity`) + Enums        | `@Entity`, `enum`          |
| Infra          | `infra/`       | Spring Data Repositories                 | `XxxRepository`            |

---

## 4. Engenharia Multi-Tenant (Segurança Estrutural)

Estratégia: **Shared Database, Shared Schema** — todos os condomínios compartilham o mesmo banco e as mesmas tabelas, isolados por `condominio_id`.

### 4.1. Fluxo de Isolamento

```
[Angular]                    [Spring Boot]                     [PostgreSQL]
    │                             │                                 │
    │── Header X-Tenant-Id ──────▶│                                 │
    │                             │                                 │
    │                    TenantFilter (OncePerRequestFilter)        │
    │                    ├─ Valida header obrigatório               │
    │                    └─ Injeta no ThreadLocal                   │
    │                             │                                 │
    │                    TenantIdentifierResolver                   │
    │                    └─ Converte String → UUID                  │
    │                             │                                 │
    │                    Hibernate @TenantId                        │
    │                    └─ Reescreve TODA query SQL ──────────────▶│
    │                       adicionando WHERE condominio_id = ?     │
    │                             │                                 │
    │◀──── Response ──────────────│◀────────── Resultado ───────────│
```

**Mecanismo:**
1. O front-end envia o header `X-Tenant-Id` em toda requisição.
2. `TenantFilter` intercepta, valida e armazena o ID no `TenantContext` (ThreadLocal).
3. `TenantIdentifierResolver` fornece o UUID ao Hibernate.
4. O Hibernate, via `@TenantId` nas entidades, intercepta **toda** query e injeta `WHERE condominio_id = ?` automaticamente. O desenvolvedor **não precisa filtrar manualmente** — o isolamento é arquitetural.

**Entidades com `@TenantId`:** `Bloco`, `Morador`, `Unidade`, `Usuario`, `Competencia`, `Despesa`, `Fatura`, `Boleto`.

> `Condominio` é a entidade raiz e **não possui** `@TenantId` (ela **é** o tenant).

---

## 5. Stack Tecnológica

### 5.1. Back-end

| Componente        | Tecnologia                    | Versão   |
|-------------------|-------------------------------|----------|
| Linguagem         | Java (LTS)                    | 21       |
| Framework         | Spring Boot                   | 4.0.4    |
| API REST          | Spring Web MVC                | —        |
| Segurança         | Spring Security + JWT         | —        |
| Validação         | Spring Validation (Bean Validation) | —  |
| Persistência      | Spring Data JPA + Hibernate   | —        |
| Migrations        | Flyway                        | —        |
| Boilerplate       | Lombok                        | —        |
| Build             | Maven                         | —        |

**Records Java** são usados extensivamente como DTOs imutáveis (`CadastrarCondominioRequest`, `CondominioResponse`, `GerarFaturasRequest`, `FaturaResumoDTO`, `UnidadeParaFaturamentoDTO`).

### 5.2. Banco de Dados

| Componente | Tecnologia   | Detalhe                                     |
|------------|--------------|---------------------------------------------|
| SGBD       | PostgreSQL   | 17 (via Docker)                             |
| Porta      | `5433`       | Mapeada no `docker-compose.yml`             |
| Database   | `smartcondo_db` | Configurada no `application.yaml`        |

### 5.3. Governança de Schema (Flyway)

- Configuração: `spring.jpa.hibernate.ddl-auto = validate` — o Hibernate **nunca** cria ou altera tabelas.
- O esquema é tratado como código versionado em `.sql` no diretório `src/main/resources/db/migration/`.
- Migration inicial: `V202604081508__create_initial_schema.sql` — cria todas as 8 tabelas com FKs e constraints.

### 5.4. Front-end (Planejado)

| Componente        | Tecnologia |
|-------------------|------------|
| Framework         | Angular    |
| Linguagem         | TypeScript |
| Estado/Async      | RxJS       |

### 5.5. Infraestrutura

| Componente         | Tecnologia     | Detalhe                          |
|--------------------|----------------|----------------------------------|
| Containerização    | Docker         | `docker-compose.yml` com Postgres 17 |
| CI/CD (planejado)  | GitHub Actions | Build, testes, push de imagem    |

---

## 6. Modelo de Dados (Schema Físico)

```
┌──────────────┐
│ condominios  │ ← Entidade raiz / Tenant
│──────────────│
│ id (PK/UUID) │
│ nome         │
│ cnpj (UQ)    │
│ ativo        │
└──────┬───────┘
       │ condominio_id (FK / @TenantId)
       ├───────────────────────────────────────────────┐
       │                    │                          │
┌──────▼───────┐   ┌───────▼────────┐   ┌─────────────▼──┐
│   blocos     │   │   moradores    │   │   usuarios     │
│──────────────│   │────────────────│   │────────────────│
│ id (PK)      │   │ id (PK)        │   │ id (PK)        │
│ nome         │   │ nome           │   │ nome           │
│ condominio_id│   │ cpf            │   │ email (UQ)     │
└──────┬───────┘   │ email          │   │ senha          │
       │           │ telefone       │   │ role           │
       │           │ condominio_id  │   │ condominio_id  │
       │           └───────┬────────┘   └────────────────┘
       │                   │
       │  bloco_id (FK)    │ morador_id (FK)
       ├───────────┐       │
       │    ┌──────▼───────▼──┐
       │    │   unidades      │
       │    │─────────────────│
       │    │ id (PK)         │
       │    │ numero          │
       │    │ bloco_id        │
       │    │ morador_id      │
       │    │ fracao_ideal    │
       │    │ ativa           │
       │    │ condominio_id   │
       │    └────────┬────────┘
       │             │ unidade_id (FK)
       │             │
┌──────▼──────────┐  │  ┌─────────────────┐
│ competencias    │  │  │   boletos        │
│─────────────────│  │  │─────────────────│
│ id (PK)         │  │  │ id (PK)         │
│ mes             │  │  │ fatura_id       │
│ ano             │  │  │ linha_digitavel │
│ condominio_id   │  │  │ data_emissao    │
└──────┬──────────┘  │  │ condominio_id   │
       │             │  └─────────────────┘
       │ competencia_id   │                ▲
       ├──────────┐  │    │                │ fatura_id (FK)
       │   ┌──────▼──▼────▼──┐             │
       │   │   faturas        │─────────────┘
       │   │──────────────────│
       │   │ id (PK)          │
       │   │ data_vencimento  │
       │   │ valor            │
       │   │ status           │
       │   │ unidade_id       │
       │   │ competencia_id   │
       │   │ condominio_id    │
       │   └──────────────────┘
       │
┌──────▼──────────┐
│  despesas       │
│─────────────────│
│ id (PK)         │
│ descricao       │
│ valor           │
│ competencia_id  │
│ condominio_id   │
└─────────────────┘
```

---

## 7. Endpoints HTTP (Implementados)

### Módulo `condominio`

| Método | Rota                | Descrição              | Request Body                      | Response                    |
|--------|---------------------|------------------------|-----------------------------------|-----------------------------|
| `POST` | `/api/condominios`  | Cadastra um condomínio | `{ nome, cnpj }`                  | `201` + `{ id, nome, cnpj }`|

### Módulo `faturamento`

| Método | Rota             | Descrição                        | Request Body                              | Response                     |
|--------|------------------|----------------------------------|-------------------------------------------|------------------------------|
| `POST` | `/api/faturas`   | Gera faturas para uma competência| `{ valorBase, dataVencimento, competenciaId }` | `201` + mensagem             |
| `GET`  | `/api/faturas`   | Lista faturas do tenant          | —                                         | `200` + `List<FaturaResumoDTO>`|

> Todos os endpoints exigem o header `X-Tenant-Id` (exceto cadastro de condomínio que cria o tenant).
