-- V3: tabelas para sessões de treino e auditoria

CREATE SEQUENCE IF NOT EXISTS seq_treino_sessao START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_treino_sessao_historico START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS treino_sessao (
    id          INTEGER NOT NULL DEFAULT nextval('seq_treino_sessao'),
    data_sessao DATE    NOT NULL,
    fk_treino   INTEGER NOT NULL,
    fk_usuario  INTEGER NOT NULL,
    CONSTRAINT pk_treino_sessao         PRIMARY KEY (id),
    CONSTRAINT fk_treino_sessao_treino  FOREIGN KEY (fk_treino)  REFERENCES treino   (id),
    CONSTRAINT fk_treino_sessao_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario  (id)
);

CREATE TABLE IF NOT EXISTS treino_sessao_historico (
    id                   INTEGER      NOT NULL DEFAULT nextval('seq_treino_sessao_historico'),
    data_cadastro        TIMESTAMP    NOT NULL,
    usuario_cadastro_id  INTEGER      NOT NULL,
    acao                 VARCHAR(255) NOT NULL,
    fk_treino_sessao     INTEGER      NOT NULL,
    CONSTRAINT pk_treino_sessao_historico        PRIMARY KEY (id),
    CONSTRAINT fk_treino_sessao_historico_sessao FOREIGN KEY (fk_treino_sessao) REFERENCES treino_sessao (id)
);

CREATE INDEX IF NOT EXISTS idx_treino_sessao_usuario_data
    ON treino_sessao (fk_usuario, data_sessao DESC);
