# ADR 0001: Estratégia de Isolamento Multi-Tenant

**Status:** Aceito
**Data:** 25 de Março de 2026

## 1. Contexto e Problema
O sistema "SmartCondo" é um SaaS (Software as a Service) focado em B2B. Precisamos hospedar múltiplos clientes (Condomínios) na mesma infraestrutura de aplicação e banco de dados.
O desafio principal é garantir o isolamento absoluto dos dados (um condomínio não pode acessar faturas de outro), mantendo os custos de infraestrutura baixos e a manutenção das migrações do banco de dados simples para a equipe de desenvolvimento.

## 2. Decisão Arquitetural
Optamos por utilizar a abordagem de **Banco de Dados Compartilhado e Esquema Compartilhado (Shared Database, Shared Schema)**.

* Todos os inquilinos (Condomínios) compartilharão o mesmo banco de dados PostgreSQL e o mesmo schema `public`.
* O isolamento lógico será feito através de uma coluna discriminadora obrigatória (`condominio_id` / `tenant_id`) em todas as tabelas transacionais do sistema (Apartamentos, Faturas, Moradores, etc).
* Para mitigar o risco de falha humana (desenvolvedores esquecerem de adicionar a cláusula `WHERE` nas consultas), delegaremos a responsabilidade de filtragem ao framework (Hibernate/Spring Data JPA) utilizando mecanismos nativos de injeção de Tenant baseados no contexto do usuário autenticado via JWT.

## 3. Consequências

### Pontos Positivos (Ganhos)
* **Baixo Custo:** Uso de uma única instância de banco de dados, reduzindo drasticamente custos de infraestrutura cloud (AWS/GCP).
* **Manutenção Simplificada:** Ferramentas de migração (Flyway) precisam rodar seus scripts apenas uma vez. Adicionar um novo cliente tem custo computacional e operacional quase zero.
* **Complexidade Inicial Baixa:** Facilita o desenvolvimento do MVP e a orquestração local com Docker.

### Pontos Negativos (Riscos)
* **Risco de Vazamento de Dados:** Exige disciplina da equipe para garantir que o contexto do Tenant seja injetado corretamente nas transações e sessões do banco de dados.
* **Escalabilidade Extrema (Noice Neighbors):** Se um cliente específico gerar uma carga massiva de relatórios, ele pode degradar a performance do banco para os outros clientes (um risco aceitável para o MVP, mitigável futuramente com Read Replicas ou sharding).

------------------------------------------------------------