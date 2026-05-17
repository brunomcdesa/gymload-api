-- V5: adiciona coluna padrao em exercicio_variacao para identificar variacoes criadas automaticamente na migracao de registros

ALTER TABLE exercicio_variacao
    ADD COLUMN IF NOT EXISTS padrao BOOLEAN NOT NULL DEFAULT FALSE;
