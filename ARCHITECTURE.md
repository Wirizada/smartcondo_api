# 🏢 Technical PRD Avançado - SmartCondo API

## 1. Estrutura de Diretórios Rigorosa
Seu projeto deve seguir estritamente esta árvore dentro de `src/main/java/br/com/wirizada/smartcondo_api/`:

```text
├── core/                       # Configurações globais
│   ├── config/                 # TenantContext, TenantIdentifierResolver
│   └── exceptions/             # GlobalExceptionHandler (Detalhado abaixo)
├── condominio/                 # Módulo de Condomínios e Apartamentos
│   ├── domain/                 # Condominio, Apartamento (Entidades)
│   ├── application/            # CadastrarCondominioUseCase, CadastrarApartamentoUseCase
│   └── infrastructure/         # Controllers, Repositories e DTOs (Request/Response)
├── usuario/                    # Módulo de Identidade
│   ├── domain/                 # Usuario, TipoUsuario (Enum)
│   ├── application/            # CadastrarUsuarioUseCase, AutenticarUsuarioUseCase
│   └── infrastructure/         # UsuarioController, AuthController, DTOs
└── faturamento/                # Módulo Financeiro
    ├── domain/                 # Fatura, StatusFatura (Enum)
    ├── application/            # GerarFaturasMensaisUseCase, PagarFaturaUseCase
    └── infrastructure/         # FaturaController, FaturaRepository, DTOs (e Projections)
```

---

## 2. Padrão Global de Tratamento de Erros (Obrigatório)
Um sistema profissional não devolve `Stacktrace` do Java para o Front-end. Você deve criar uma classe `GlobalExceptionHandler` anotada com `@RestControllerAdvice` dentro do pacote `core/exceptions/`.

**Comportamentos esperados:**
* Se um ID não for encontrado no banco (`Optional.isEmpty()`): Lançar uma `EntityNotFoundException` (customizada ou do JPA). O handler deve capturar e devolver **HTTP 404 (Not Found)** com o JSON: `{"erro": "Recurso não encontrado"}`.
* Se a validação do DTO falhar (`@Valid`): Capturar `MethodArgumentNotValidException` e devolver **HTTP 400 (Bad Request)** com a lista dos campos que falharam.
* Se houver quebra de regra de negócio (ex: Fatura já está paga): Criar uma `RegraNegocioException`, capturar e devolver **HTTP 422 (Unprocessable Entity)** com a mensagem do erro.

---

## 3. Especificação Detalhada por Módulo

### 👤 Módulo 1: Usuário (Identidade)

#### Caso de Uso 1.1: Cadastrar Usuário
* **Rota:** `POST /api/usuarios`
* **Request DTO (`CadastrarUsuarioRequest`):**
  ```json
  {
    "nome": "João Silva", // @NotBlank
    "email": "joao@email.com", // @NotBlank, @Email
    "senha": "SenhaForte123", // @NotBlank, tamanho mínimo 6
    "tipo": "MORADOR" // @NotNull, deve validar contra o Enum TipoUsuario
  }
  ```
* **Algoritmo do `CadastrarUsuarioUseCase`:**
  1. Verificar no `UsuarioRepository` se já existe um usuário com este e-mail (`existsByEmail`).
  2. Se existir, lançar `RegraNegocioException("E-mail já cadastrado no sistema")`.
  3. Instanciar a entidade `Usuario`.
  4. Salvar no repositório.
* **Response DTO (`UsuarioResponse`):** Devolver HTTP 201 com `id`, `nome`, `email` e `tipo` (Nunca devolver a senha).

#### Caso de Uso 1.2: Mock de Login (Até o Spring Security)
* **Rota:** `POST /api/auth/login`
* **Request DTO (`LoginRequest`):** `email` e `senha`.
* **Algoritmo:**
  1. Buscar usuário por email (`findByEmail`). Se não achar, HTTP 401 (Unauthorized).
  2. Comparar a senha do DTO com a senha do banco. Se errada, HTTP 401.
  3. *Temporário:* Retornar HTTP 200 com uma String simples simulando um token: `"mock-token-uuid-do-usuario"`.

---

### 🏢 Módulo 2: Gestão Imobiliária (Apartamentos)

#### Caso de Uso 2.1: Cadastrar Apartamento
* **Rota:** `POST /api/apartamentos`
* **Request DTO (`CadastrarApartamentoRequest`):**
  ```json
  {
    "numero": "101", // @NotBlank
    "bloco": "A", // @NotBlank
    "titularId": "uuid-do-usuario" // @NotNull
  }
  ```
* **Algoritmo do `CadastrarApartamentoUseCase`:**
  1. Receber `numero`, `bloco` e `titularId`.
  2. Buscar o Usuário no banco usando o `titularId` (`usuarioRepository.findById`). Se não achar, lançar exceção de `NotFound` (HTTP 404).
  3. *Atenção ao Tenant:* O `Condominio` será preenchido na entidade `Apartamento` buscando do `titular.getCondominio()` (ou injetando via TenantContext atual).
  4. Instanciar `Apartamento`, setar valores, ligar ao `titular` e salvar.
* **Response DTO (`ApartamentoResponse`):** Devolver HTTP 201 com `id`, `numero`, `bloco` e um sub-objeto com o `nome` do titular.

---

### 💰 Módulo 3: Faturamento (O Core Financeiro)

#### Caso de Uso 3.1: Listar Faturas do Sistema (Otimizado para Leitura)
* **Rota:** `GET /api/faturas`
* **Query Params (Opcionais):** `?status=PENDENTE`
* **Algoritmo do Controller/UseCase:**
  1. Acessar o `TenantContext` para resgatar o UUID do condomínio atual.
  2. **Atenção de Segurança:** Como a consulta foi projetada visando máxima performance de leitura usando **SQL Nativo** (`@Query(nativeQuery = true)`), a inteligência do Hibernate que aplica o `@TenantId` automaticamente é **desativada**. A cláusula `WHERE f.condominio_id = :tenantId` deve ser explicitamente adicionada na string do SQL para evitar vazamento de dados.
  3. Chamar `faturaRepository.buscarFaturasNativas(tenantId)` passando o ID resgatado.
  4. O Repositório não deve devolver a Entidade `Fatura` para evitar sobrecarga de memória (Out Of Memory). O SQL Nativo deve mapear os campos diretamente para uma **Interface-based Projection** (`ListaFaturaProjection`).
* **Response:** HTTP 200 retornando um JSON Array preenchido através da Interface de Projeção contendo `id`, `valor`, `dataVencimento`, `status` e `numeroApartamento`.

#### Caso de Uso 3.2: Pagar Fatura (Baixa Manual)
* **Rota:** `PUT /api/faturas/{id}/pagar` (Note o ID na URL via `@PathVariable`).
* **Request Body:** Vazio (A ação está implícita na URL).
* **Algoritmo do `PagarFaturaUseCase`:**
  1. Receber o `UUID faturaId`.
  2. Buscar a Fatura no banco através do JPA (pois é uma operação de **Escrita/Command**, precisando da Entidade instanciada para validações). Se não existir, HTTP 404.
  3. Verificar o status atual. Se for `PAGA` ou `CANCELADA`, lançar `RegraNegocioException("Fatura não pode ser paga, status atual: " + status)`.
  4. Alterar o status da entidade para `PAGA`.
  5. Salvar (`faturaRepository.save`).
* **Response:** HTTP 200 OK (pode retornar apenas uma mensagem de sucesso ou a Fatura atualizada no DTO).

---

## 4. O Checklist de Qualidade do Engenheiro (Auto-Revisão)

Antes de considerar qualquer um desses endpoints finalizado, valide as Leis Arquiteturais:
1. **As anotações do Lombok estão seguras?** (`@EqualsAndHashCode.Include` apenas no ID).
2. **A regra de Projeção e Isolamento:** Eu expus alguma Entidade na Controller? (O retorno e a entrada devem ser sempre `record` DTOs ou `Projections`).
3. **A regra da Transação:** O método do UseCase que **altera** o banco (Cadastros/Atualizações) tem a anotação `@Transactional`?
4. **A regra da Injeção:** Eu usei injeção por construtor `private final` (sem usar `@Autowired`)?
5. **A regra do Contrato HTTP:** O Status HTTP de retorno está correto de acordo com o REST (200, 201, 400, 404)?
6. **A regra do SQL Nativo (Alerta Multi-Tenant):** Se eu usei `@Query(nativeQuery = true)` para otimização de busca, eu garanti manualmente que a cláusula `AND condominio_id = :tenantId` está presente para impedir o vazamento de dados de outros clientes?
