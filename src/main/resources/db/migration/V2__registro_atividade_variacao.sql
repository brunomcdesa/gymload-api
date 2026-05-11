-- V2: adiciona FK para exercicio_variacao nas tabelas de registro de atividade

ALTER TABLE registro_musculacao
    ADD COLUMN IF NOT EXISTS fk_exercicio_variacao INTEGER,
    ADD CONSTRAINT fk_registro_musculacao_variacao
        FOREIGN KEY (fk_exercicio_variacao) REFERENCES exercicio_variacao (id);

ALTER TABLE registro_aerobico
    ADD COLUMN IF NOT EXISTS fk_exercicio_variacao INTEGER,
    ADD CONSTRAINT fk_registro_aerobico_variacao
        FOREIGN KEY (fk_exercicio_variacao) REFERENCES exercicio_variacao (id);

ALTER TABLE registro_calistenia
    ADD COLUMN IF NOT EXISTS fk_exercicio_variacao INTEGER,
    ADD CONSTRAINT fk_registro_calistenia_variacao
        FOREIGN KEY (fk_exercicio_variacao) REFERENCES exercicio_variacao (id);

CREATE INDEX IF NOT EXISTS idx_registro_musculacao_exercicio_variacao
    ON registro_musculacao (fk_exercicio, fk_exercicio_variacao);

CREATE INDEX IF NOT EXISTS idx_registro_aerobico_exercicio_variacao
    ON registro_aerobico (fk_exercicio, fk_exercicio_variacao);

CREATE INDEX IF NOT EXISTS idx_registro_calistenia_exercicio_variacao
    ON registro_calistenia (fk_exercicio, fk_exercicio_variacao);
