-- V6: grade semanal (treino do dia) + auditoria

CREATE SEQUENCE IF NOT EXISTS seq_grade_semanal START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS seq_grade_semanal_historico START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS grade_semanal (
    id          INTEGER     NOT NULL DEFAULT nextval('seq_grade_semanal'),
    dia_semana  VARCHAR(20) NOT NULL,
    fk_treino   INTEGER     NOT NULL,
    fk_usuario  INTEGER     NOT NULL,
    CONSTRAINT pk_grade_semanal         PRIMARY KEY (id),
    CONSTRAINT uq_grade_semanal_dia     UNIQUE (fk_usuario, dia_semana),
    CONSTRAINT fk_grade_semanal_treino  FOREIGN KEY (fk_treino)  REFERENCES treino  (id),
    CONSTRAINT fk_grade_semanal_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);

CREATE TABLE IF NOT EXISTS grade_semanal_historico (
    id                  INTEGER      NOT NULL DEFAULT nextval('seq_grade_semanal_historico'),
    data_cadastro       TIMESTAMP    NOT NULL,
    usuario_cadastro_id INTEGER      NOT NULL,
    acao                VARCHAR(255) NOT NULL,
    dia_semana          VARCHAR(20)  NOT NULL,
    fk_treino           INTEGER      NOT NULL,
    fk_usuario          INTEGER      NOT NULL,
    CONSTRAINT pk_grade_semanal_historico         PRIMARY KEY (id),
    CONSTRAINT fk_grade_semanal_historico_treino  FOREIGN KEY (fk_treino)  REFERENCES treino  (id),
    CONSTRAINT fk_grade_semanal_historico_usuario FOREIGN KEY (fk_usuario) REFERENCES usuario (id)
);
