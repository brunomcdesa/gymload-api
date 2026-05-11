-- V1: schema baseline (replica o que o Hibernate gerava com ddl-auto: create)

-- Sequences
CREATE SEQUENCE IF NOT EXISTS seq_usuario START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_usuario_historico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_grupo_muscular START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_tipo_variacao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_tipo_variacao_historico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_exercicio START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_exercicio_historico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_exercicio_variacao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_treino START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_treino_historico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_musculacao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_aerobico START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_registro_calistenia START WITH 1 INCREMENT BY 1;

-- USUARIO
CREATE TABLE IF NOT EXISTS usuario (
    id               INTEGER      NOT NULL DEFAULT nextval('seq_usuario'),
    uuid             UUID         NOT NULL,
    nome             VARCHAR(255) NOT NULL,
    username         VARCHAR(255) NOT NULL,
    senha            VARCHAR(255) NOT NULL,
    imagem_perfil    VARCHAR(75),
    email            VARCHAR(255),
    idade            INTEGER,
    peso_corporal    FLOAT8,
    altura           FLOAT8,
    sexo             VARCHAR(15),
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_username UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS usuario_role (
    usuario_id INTEGER      NOT NULL,
    role       VARCHAR(255) NOT NULL,
    CONSTRAINT fk_usuario_role_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS usuario_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_usuario_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_usuario           INTEGER      NOT NULL,
    CONSTRAINT pk_usuario_historico PRIMARY KEY (id),
    CONSTRAINT fk_usuario_historico_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);

-- GRUPO_MUSCULAR
CREATE TABLE IF NOT EXISTS grupo_muscular (
    id     INTEGER      NOT NULL DEFAULT nextval('seq_grupo_muscular'),
    nome   VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL,
    CONSTRAINT pk_grupo_muscular PRIMARY KEY (id)
);

-- TIPO_VARIACAO
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

-- EXERCICIO
CREATE TABLE IF NOT EXISTS exercicio (
    id               INTEGER      NOT NULL DEFAULT nextval('seq_exercicio'),
    nome             VARCHAR(255) NOT NULL,
    descricao        VARCHAR(255),
    tipo_exercicio   VARCHAR(255),
    possui_variacao  BOOLEAN,
    fk_grupo_muscular INTEGER,
    CONSTRAINT pk_exercicio PRIMARY KEY (id),
    CONSTRAINT fk_exercicio_grupo_muscular FOREIGN KEY (fk_grupo_muscular) REFERENCES grupo_muscular (id)
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

-- EXERCICIO_VARIACAO
CREATE TABLE IF NOT EXISTS exercicio_variacao (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_exercicio_variacao'),
    nome                 VARCHAR(255) NOT NULL,
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    fk_exercicio         INTEGER      NOT NULL,
    fk_tipo_variacao     INTEGER,
    CONSTRAINT pk_exercicio_variacao PRIMARY KEY (id),
    CONSTRAINT fk_exercicio_variacao_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id),
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

-- TREINO
CREATE TABLE IF NOT EXISTS treino (
    id            INTEGER      NOT NULL DEFAULT nextval('seq_treino'),
    nome          VARCHAR(255) NOT NULL,
    data_cadastro DATE         NOT NULL,
    situacao      VARCHAR(255) NOT NULL,
    fk_usuario    INTEGER      NOT NULL,
    CONSTRAINT pk_treino PRIMARY KEY (id),
    CONSTRAINT fk_treino_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS treino_exercicio (
    fk_treino        INTEGER NOT NULL,
    fk_exercicio     INTEGER NOT NULL,
    ordem_exercicio  INTEGER,
    CONSTRAINT fk_treino_exercicio_treino   FOREIGN KEY (fk_treino)    REFERENCES treino (id),
    CONSTRAINT fk_treino_exercicio_exercicio FOREIGN KEY (fk_exercicio) REFERENCES exercicio (id)
);

CREATE TABLE IF NOT EXISTS treino_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_treino_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_treino            INTEGER      NOT NULL,
    CONSTRAINT pk_treino_historico PRIMARY KEY (id),
    CONSTRAINT fk_treino_historico_treino FOREIGN KEY (fk_treino) REFERENCES treino (id)
);

-- REGISTRO_MUSCULACAO
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

-- REGISTRO_AEROBICO
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

-- REGISTRO_CALISTENIA
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
