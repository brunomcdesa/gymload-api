ALTER TABLE treino
    ADD COLUMN IF NOT EXISTS importado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE SEQUENCE IF NOT EXISTS SEQ_TREINO_COMPARTILHAMENTO START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS treino_compartilhamento (
    id              INTEGER      NOT NULL DEFAULT nextval('SEQ_TREINO_COMPARTILHAMENTO'),
    token           VARCHAR(36)  NOT NULL,
    codigo          VARCHAR(8)   NOT NULL,
    nome_treino     VARCHAR(255) NOT NULL,
    exercicios_ids  TEXT         NOT NULL,
    data_criacao    TIMESTAMP    NOT NULL,
    data_expiracao  TIMESTAMP    NOT NULL,
    fk_usuario      INTEGER      NOT NULL,
    CONSTRAINT pk_treino_compartilhamento         PRIMARY KEY (id),
    CONSTRAINT uq_treino_compartilhamento_token   UNIQUE (token),
    CONSTRAINT uq_treino_compartilhamento_codigo  UNIQUE (codigo),
    CONSTRAINT fk_treino_compartilhamento_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);

CREATE INDEX IF NOT EXISTS idx_treino_compartilhamento_codigo
    ON treino_compartilhamento (codigo);
