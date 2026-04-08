CREATE TABLE IF NOT EXISTS condominios (
    id      UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome    VARCHAR(100) NOT NULL,
    cnpj    VARCHAR(14)  NOT NULL UNIQUE,
    ativo   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS blocos (
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome            VARCHAR(50) NOT NULL,
    condominio_id   UUID NOT NULL,
    CONSTRAINT fk_blocos_condominio FOREIGN KEY (condominio_id) REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS moradores (
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    cpf             VARCHAR(11)  NOT NULL,
    email           VARCHAR(100),
    telefone        VARCHAR(20),
    condominio_id   UUID NOT NULL,
    CONSTRAINT fk_moradores_condominio FOREIGN KEY (condominio_id) REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS unidades (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    numero              VARCHAR(20)    NOT NULL,
    bloco_id            UUID           NOT NULL,
    morador_id          UUID           NOT NULL,
    fracao_ideal        DECIMAL(6, 4)  NOT NULL,
    ativa               BOOLEAN        NOT NULL DEFAULT TRUE,
    condominio_id       UUID           NOT NULL,
    CONSTRAINT fk_unidades_bloco        FOREIGN KEY (bloco_id)        REFERENCES blocos(id),
    CONSTRAINT fk_unidades_morador      FOREIGN KEY (morador_id)      REFERENCES moradores(id),
    CONSTRAINT fk_unidades_condominio   FOREIGN KEY (condominio_id)   REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    senha           VARCHAR(100) NOT NULL,
    role            VARCHAR(20),
    condominio_id   UUID NOT NULL,
    CONSTRAINT fk_usuarios_condominio FOREIGN KEY (condominio_id) REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS competencias (
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    mes             INT  NOT NULL,
    ano             INT  NOT NULL,
    condominio_id   UUID NOT NULL,
    CONSTRAINT fk_competencias_condominio FOREIGN KEY (condominio_id) REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS despesas (
    id              UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    descricao       VARCHAR(150)   NOT NULL,
    valor           DECIMAL(12, 2) NOT NULL,
    competencia_id  UUID           NOT NULL,
    condominio_id   UUID           NOT NULL,
    CONSTRAINT fk_despesas_competencia FOREIGN KEY (competencia_id) REFERENCES competencias(id),
    CONSTRAINT fk_despesas_condominio  FOREIGN KEY (condominio_id)  REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS faturas (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    data_vencimento     DATE           NOT NULL,
    valor               DECIMAL(12, 2) NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    unidade_id          UUID           NOT NULL,
    competencia_id      UUID           NOT NULL,
    condominio_id       UUID           NOT NULL,
    CONSTRAINT fk_faturas_unidade      FOREIGN KEY (unidade_id)     REFERENCES unidades(id),
    CONSTRAINT fk_faturas_competencia  FOREIGN KEY (competencia_id) REFERENCES competencias(id),
    CONSTRAINT fk_faturas_condominio   FOREIGN KEY (condominio_id)  REFERENCES condominios(id)
);

CREATE TABLE IF NOT EXISTS boletos (
    id                  UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    fatura_id           UUID         NOT NULL,
    linha_digitavel     VARCHAR(60),
    data_emissao        DATE         NOT NULL,
    condominio_id       UUID         NOT NULL,
    CONSTRAINT fk_boletos_fatura     FOREIGN KEY (fatura_id)    REFERENCES faturas(id),
    CONSTRAINT fk_boletos_condominio FOREIGN KEY (condominio_id) REFERENCES condominios(id)
);

