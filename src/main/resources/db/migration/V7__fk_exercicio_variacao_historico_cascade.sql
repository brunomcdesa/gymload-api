ALTER TABLE exercicio_variacao_historico
    DROP CONSTRAINT fk_exercicio_variacao_hist_variacao;

ALTER TABLE exercicio_variacao_historico
    ADD CONSTRAINT fk_exercicio_variacao_hist_variacao
        FOREIGN KEY (fk_exercicio_variacao)
        REFERENCES exercicio_variacao (id)
        ON DELETE CASCADE;
