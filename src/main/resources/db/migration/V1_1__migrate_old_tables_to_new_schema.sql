-- V1.1: corrige schema herdado do Hibernate (pré-Flyway) para o estado esperado pelo V1
--        e migra dados: registro_carga → registro_musculacao, registro_cardio → registro_aerobico

-- Sequences ausentes do V1
CREATE SEQUENCE IF NOT EXISTS seq_usuario_historico       START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_tipo_variacao           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_tipo_variacao_historico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_exercicio_historico     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_exercicio_variacao      START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_musculacao     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_aerobico       START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_calistenia     START WITH 1 INCREMENT BY 1;

-- Troca o DEFAULT de usuario.id de usuario_temp_id_seq → seq_usuario e remove a sequence legada
ALTER TABLE usuario ALTER COLUMN id SET DEFAULT nextval('seq_usuario');
DROP SEQUENCE IF EXISTS usuario_temp_id_seq;

-- Colunas ausentes na tabela usuario
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS email         VARCHAR(255);
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS idade         INTEGER;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS peso_corporal FLOAT8;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS altura        FLOAT8;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS sexo          VARCHAR(15);

-- Coluna ausente na tabela exercicio
ALTER TABLE exercicio ADD COLUMN IF NOT EXISTS possui_variacao BOOLEAN;

-- Tabelas ausentes do V1
CREATE TABLE IF NOT EXISTS usuario_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_usuario_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_usuario           INTEGER      NOT NULL,
    CONSTRAINT pk_usuario_historico PRIMARY KEY (id),
    CONSTRAINT fk_usuario_historico_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS tipo_variacao (
    id                    INTEGER      NOT NULL DEFAULT nextval('seq_tipo_variacao'),
    nome                  VARCHAR(255) NOT NULL,
    data_cadastro         TIMESTAMP    NOT NULL,
    usuario_cadastro_id   INTEGER      NOT NULL,
    usuario_cadastro_nome VARCHAR(255) NOT NULL,
    CONSTRAINT pk_tipo_variacao PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tipo_variacao_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_tipo_variacao_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_tipo_variacao     INTEGER      NOT NULL,
    CONSTRAINT pk_tipo_variacao_historico PRIMARY KEY (id),
    CONSTRAINT fk_tipo_variacao_historico_tipo_variacao FOREIGN KEY (fk_tipo_variacao) REFERENCES tipo_variacao (id)
);

CREATE TABLE IF NOT EXISTS exercicio_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_exercicio_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_exercicio         INTEGER      NOT NULL,
    CONSTRAINT pk_exercicio_historico PRIMARY KEY (id),
    CONSTRAINT fk_exercicio_historico_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id)
);

CREATE TABLE IF NOT EXISTS exercicio_variacao (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_exercicio_variacao'),
    nome                 VARCHAR(255) NOT NULL,
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    fk_exercicio         INTEGER      NOT NULL,
    fk_tipo_variacao     INTEGER,
    CONSTRAINT pk_exercicio_variacao PRIMARY KEY (id),
    CONSTRAINT fk_exercicio_variacao_exercicio     FOREIGN KEY (fk_exercicio)     REFERENCES exercicio (id),
    CONSTRAINT fk_exercicio_variacao_tipo_variacao FOREIGN KEY (fk_tipo_variacao) REFERENCES tipo_variacao (id)
);

CREATE TABLE IF NOT EXISTS exercicio_variacao_historico (
    id                      INTEGER      NOT NULL DEFAULT nextval('seq_exercicio_historico'),
    data_cadastro           TIMESTAMP    NOT NULL,
    usuario_cadastro_id     INTEGER      NOT NULL,
    acao                    VARCHAR(255) NOT NULL,
    fk_exercicio_variacao   INTEGER      NOT NULL,
    CONSTRAINT pk_exercicio_variacao_historico PRIMARY KEY (id),
    CONSTRAINT fk_exercicio_variacao_hist_variacao FOREIGN KEY (fk_exercicio_variacao) REFERENCES exercicio_variacao (id)
);

CREATE TABLE IF NOT EXISTS registro_musculacao (
    id               INTEGER      NOT NULL DEFAULT nextval('seq_registro_musculacao'),
    data_cadastro    DATE         NOT NULL,
    observacao       VARCHAR(150),
    fk_exercicio     INTEGER      NOT NULL,
    fk_usuario       INTEGER      NOT NULL,
    peso             FLOAT8       NOT NULL,
    unidade_peso     VARCHAR(255) NOT NULL,
    qtd_repeticoes   INTEGER      NOT NULL,
    qtd_series       INTEGER      NOT NULL,
    tipo_pegada      VARCHAR(255),
    CONSTRAINT pk_registro_musculacao PRIMARY KEY (id),
    CONSTRAINT fk_registro_musculacao_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id),
    CONSTRAINT fk_registro_musculacao_usuario   FOREIGN KEY (fk_usuario)   REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS registro_aerobico (
    id             INTEGER NOT NULL DEFAULT nextval('seq_registro_aerobico'),
    data_cadastro  DATE    NOT NULL,
    observacao     VARCHAR(150),
    fk_exercicio   INTEGER NOT NULL,
    fk_usuario     INTEGER NOT NULL,
    distancia      FLOAT8  NOT NULL,
    duracao        FLOAT8  NOT NULL,
    CONSTRAINT pk_registro_aerobico PRIMARY KEY (id),
    CONSTRAINT fk_registro_aerobico_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id),
    CONSTRAINT fk_registro_aerobico_usuario   FOREIGN KEY (fk_usuario)   REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS registro_calistenia (
    id                              INTEGER NOT NULL DEFAULT nextval('seq_registro_calistenia'),
    data_cadastro                   DATE    NOT NULL,
    observacao                      VARCHAR(150),
    fk_exercicio                    INTEGER NOT NULL,
    fk_usuario                      INTEGER NOT NULL,
    qtd_series                      INTEGER NOT NULL,
    qtd_repeticoes                  INTEGER NOT NULL,
    peso_adicional                  FLOAT8,
    tipo_equipamento_peso_adicional INTEGER,
    unidade_peso                    VARCHAR(255),
    CONSTRAINT pk_registro_calistenia PRIMARY KEY (id),
    CONSTRAINT fk_registro_calistenia_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id),
    CONSTRAINT fk_registro_calistenia_usuario   FOREIGN KEY (fk_usuario)   REFERENCES usuario (id)
);

-- Migração de dados (executada apenas se as tabelas antigas existirem)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_schema = 'public' AND table_name = 'registro_carga') THEN

        INSERT INTO registro_musculacao
            (id, data_cadastro, observacao, fk_exercicio, fk_usuario,
             peso, unidade_peso, qtd_repeticoes, qtd_series, tipo_pegada)
        SELECT id, data_cadastro, observacao, fk_exercicio, fk_usuario,
               peso, unidade_peso, qtd_repeticoes, qtd_series, NULL
        FROM registro_carga;

        PERFORM setval('seq_registro_musculacao', (SELECT MAX(id) FROM registro_musculacao));

        DROP TABLE registro_carga;
        DROP SEQUENCE IF EXISTS seq_registro_carga;
        DROP SEQUENCE IF EXISTS seq_historico_cargas;
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.tables
               WHERE table_schema = 'public' AND table_name = 'registro_cardio') THEN

        INSERT INTO registro_aerobico
            (id, data_cadastro, observacao, fk_exercicio, fk_usuario, distancia, duracao)
        SELECT id, data_cadastro, observacao, fk_exercicio, fk_usuario, distancia, duracao
        FROM registro_cardio;

        PERFORM setval('seq_registro_aerobico', (SELECT MAX(id) FROM registro_aerobico));

        DROP TABLE registro_cardio;
        DROP SEQUENCE IF EXISTS seq_registro_cardio;
    END IF;
END $$;
