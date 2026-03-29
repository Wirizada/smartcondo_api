### 🗺️ Visão de Sistema (O Mapa do SmartCondo)

------------

#### 1. A Camada de Entrada (`infrastructure` -> Web)
É a porta da frente do sistema. Tudo que vem do aplicativo do morador entra por aqui.
* **O que vive aqui:** `Controllers` e `DTOs (Requests/Responses)`.
* **A Regra (O que não fazer):** Essa camada **não** calcula juros, **não** manda e-mail e **não** chama o banco de dados. Ela só recebe o JSON, valida se o e-mail tem "@", e entrega para a camada de dentro.

#### 2. O Cérebro da Operação (`application` -> Regras de Negócio)
É onde o dinheiro é feito. O Controller só liga para cá e pede: "Executa isso pra mim".
* **O que vive aqui:** `UseCases` (Casos de Uso) ou `Services`.
* **A Regra (O que não fazer):** Essa camada **não** sabe que a internet existe. Ela não lida com requisições HTTP e não recebe JSONs da Web. Ela processa as regras lógicas usando as Entidades e pede para o Banco salvar a operação.

#### 3. O Coração do Sistema (`domain` -> O Núcleo)
É o retrato da empresa no mundo real. É a camada mais burra em termos de código (não tem lógica pesada), mas a mais importante em termos de negócios.
* **O que vive aqui:** Nossas `Entidades` (`Condominio`, `Usuario`, `Apartamento`, `Fatura`) e nossos `Enums` (`TipoUsuario`, `StatusFatura`).
* **A Regra:** Essa camada não conhece as outras duas de cima. Ela não tem injeção de dependência e é totalmente cega sobre o mundo externo.

#### 4. A Camada de Saída (`infrastructure` -> Banco de Dados e Ferramentas)
É onde as coisas são gravadas na pedra ou enviadas para o mundo.
* **O que vive aqui:** `Repositories` (interfaces do Spring Data JPA), ferramentas de envio de e-mail e filtros de segurança (Multi-Tenant).

------------
