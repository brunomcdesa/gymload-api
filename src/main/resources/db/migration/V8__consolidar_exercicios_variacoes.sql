-- V8: Consolida exercícios duplicados em um único exercício com variações.
--     Move registro_musculacao para a variação correta e remove duplicatas.

-- ============================================================
-- Sanity check: V1_1 deve ter rodado antes (cria exercicio_variacao)
-- ============================================================
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables
                   WHERE table_schema = 'public' AND table_name = 'exercicio_variacao') THEN
        RAISE EXCEPTION 'Tabela exercicio_variacao não existe — V1_1 ainda não foi aplicada';
    END IF;
END $$;

-- ============================================================
-- Passo 1: Tipos de variação (idempotente)
-- ============================================================
INSERT INTO tipo_variacao (nome, data_cadastro, usuario_cadastro_id, usuario_cadastro_nome)
SELECT v.nome, NOW(), 1, 'Sistema'
FROM (VALUES
    ('Halter'),
    ('Máquina'),
    ('Barra'),
    ('Polia'),
    ('Corporal'),
    ('Polia Alta'),
    ('Polia Baixa')
) AS v(nome)
WHERE NOT EXISTS (SELECT 1 FROM tipo_variacao tv WHERE tv.nome = v.nome);

-- ============================================================
-- Passo 2: Consolidação dos grupos com 2+ variantes
-- ============================================================

-- ------------------------------------------------------------
-- GRUPO 1: Barra Fixa (canonical=45; duplicate=46)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Barra Fixa', possui_variacao = TRUE WHERE id = 45;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Corporal', NOW(), 1, 45, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Corporal'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 45 AND nome = 'Corporal');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 45, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 45 AND nome = 'Máquina');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 45 AND nome = 'Corporal')
WHERE fk_exercicio = 45 AND fk_exercicio_variacao IS NULL;

-- duplicate 46 → 45 (Máquina)
UPDATE registro_musculacao SET fk_exercicio = 45,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 45 AND nome = 'Máquina')
WHERE fk_exercicio = 46;
UPDATE treino_exercicio SET fk_exercicio = 45 WHERE fk_exercicio = 46
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 45);
DELETE FROM treino_exercicio WHERE fk_exercicio = 46;
DELETE FROM exercicio WHERE id = 46;

-- ------------------------------------------------------------
-- GRUPO 2: Desenvolvimento (canonical=31; duplicate=32)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Desenvolvimento', possui_variacao = TRUE WHERE id = 31;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 31, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 31 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 31, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 31 AND nome = 'Máquina');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 31 AND nome = 'Halter')
WHERE fk_exercicio = 31 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 31,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 31 AND nome = 'Máquina')
WHERE fk_exercicio = 32;
UPDATE treino_exercicio SET fk_exercicio = 31 WHERE fk_exercicio = 32
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 31);
DELETE FROM treino_exercicio WHERE fk_exercicio = 32;
DELETE FROM exercicio WHERE id = 32;

-- ------------------------------------------------------------
-- GRUPO 3: Elevação Lateral (canonical=33; duplicates=34, 89)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Elevação Lateral', possui_variacao = TRUE WHERE id = 33;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 33, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 33, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Máquina');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia', NOW(), 1, 33, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Polia');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Halter')
WHERE fk_exercicio = 33 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 33,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Máquina')
WHERE fk_exercicio = 34;
UPDATE treino_exercicio SET fk_exercicio = 33 WHERE fk_exercicio = 34
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 33);
DELETE FROM treino_exercicio WHERE fk_exercicio = 34;
DELETE FROM exercicio WHERE id = 34;

UPDATE registro_musculacao SET fk_exercicio = 33,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 33 AND nome = 'Polia')
WHERE fk_exercicio = 89;
UPDATE treino_exercicio SET fk_exercicio = 33 WHERE fk_exercicio = 89
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 33);
DELETE FROM treino_exercicio WHERE fk_exercicio = 89;
DELETE FROM exercicio WHERE id = 89;

-- ------------------------------------------------------------
-- GRUPO 4: Encolhimento (canonical=62; duplicate=63)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Encolhimento', possui_variacao = TRUE WHERE id = 62;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 62, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 62 AND nome = 'Barra');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 62, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 62 AND nome = 'Halter');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 62 AND nome = 'Barra')
WHERE fk_exercicio = 62 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 62,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 62 AND nome = 'Halter')
WHERE fk_exercicio = 63;
UPDATE treino_exercicio SET fk_exercicio = 62 WHERE fk_exercicio = 63
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 62);
DELETE FROM treino_exercicio WHERE fk_exercicio = 63;
DELETE FROM exercicio WHERE id = 63;

-- ------------------------------------------------------------
-- GRUPO 5: Remada Cavalinho (canonical=11; duplicate=12)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Remada Cavalinho', possui_variacao = TRUE WHERE id = 11;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 11, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 11 AND nome = 'Máquina');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 11, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 11 AND nome = 'Barra');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 11 AND nome = 'Máquina')
WHERE fk_exercicio = 11 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 11,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 11 AND nome = 'Barra')
WHERE fk_exercicio = 12;
UPDATE treino_exercicio SET fk_exercicio = 11 WHERE fk_exercicio = 12
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 11);
DELETE FROM treino_exercicio WHERE fk_exercicio = 12;
DELETE FROM exercicio WHERE id = 12;

-- ------------------------------------------------------------
-- GRUPO 6: Remada Sentada (canonical=9; duplicate=10)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Remada Sentada', possui_variacao = TRUE WHERE id = 9;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 9, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 9 AND nome = 'Máquina');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 9, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 9 AND nome = 'Halter');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 9 AND nome = 'Máquina')
WHERE fk_exercicio = 9 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 9,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 9 AND nome = 'Halter')
WHERE fk_exercicio = 10;
UPDATE treino_exercicio SET fk_exercicio = 9 WHERE fk_exercicio = 10
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 9);
DELETE FROM treino_exercicio WHERE fk_exercicio = 10;
DELETE FROM exercicio WHERE id = 10;

-- ------------------------------------------------------------
-- GRUPO 7: Rosca Direta (canonical=28; duplicates=65, 68, 88)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Rosca Direta', possui_variacao = TRUE WHERE id = 28;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia Baixa', NOW(), 1, 28, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia Baixa'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Polia Baixa');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 28, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 28, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Barra');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia Alta', NOW(), 1, 28, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia Alta'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Polia Alta');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Polia Baixa')
WHERE fk_exercicio = 28 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 28,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Halter')
WHERE fk_exercicio = 65;
UPDATE treino_exercicio SET fk_exercicio = 28 WHERE fk_exercicio = 65
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 28);
DELETE FROM treino_exercicio WHERE fk_exercicio = 65;
DELETE FROM exercicio WHERE id = 65;

UPDATE registro_musculacao SET fk_exercicio = 28,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Barra')
WHERE fk_exercicio = 68;
UPDATE treino_exercicio SET fk_exercicio = 28 WHERE fk_exercicio = 68
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 28);
DELETE FROM treino_exercicio WHERE fk_exercicio = 68;
DELETE FROM exercicio WHERE id = 68;

UPDATE registro_musculacao SET fk_exercicio = 28,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 28 AND nome = 'Polia Alta')
WHERE fk_exercicio = 88;
UPDATE treino_exercicio SET fk_exercicio = 28 WHERE fk_exercicio = 88
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 28);
DELETE FROM treino_exercicio WHERE fk_exercicio = 88;
DELETE FROM exercicio WHERE id = 88;

-- ------------------------------------------------------------
-- GRUPO 8: Rosca Scott (canonical=27; duplicate=94)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Rosca Scott', possui_variacao = TRUE WHERE id = 27;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 27, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 27 AND nome = 'Máquina');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 27, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 27 AND nome = 'Barra');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 27 AND nome = 'Máquina')
WHERE fk_exercicio = 27 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 27,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 27 AND nome = 'Barra')
WHERE fk_exercicio = 94;
UPDATE treino_exercicio SET fk_exercicio = 27 WHERE fk_exercicio = 94
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 27);
DELETE FROM treino_exercicio WHERE fk_exercicio = 94;
DELETE FROM exercicio WHERE id = 94;

-- ------------------------------------------------------------
-- GRUPO 9: Stiff (canonical=17; duplicate=18)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Stiff', possui_variacao = TRUE WHERE id = 17;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 17, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 17 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 17, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 17 AND nome = 'Barra');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 17 AND nome = 'Halter')
WHERE fk_exercicio = 17 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 17,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 17 AND nome = 'Barra')
WHERE fk_exercicio = 18;
UPDATE treino_exercicio SET fk_exercicio = 17 WHERE fk_exercicio = 18
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 17);
DELETE FROM treino_exercicio WHERE fk_exercicio = 18;
DELETE FROM exercicio WHERE id = 18;

-- ------------------------------------------------------------
-- GRUPO 10: Supino Inclinado (canonical=3; duplicates=7, 69)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Supino Inclinado', possui_variacao = TRUE WHERE id = 3;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 3, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 3, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Barra');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 3, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Máquina');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Halter')
WHERE fk_exercicio = 3 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 3,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Barra')
WHERE fk_exercicio = 7;
UPDATE treino_exercicio SET fk_exercicio = 3 WHERE fk_exercicio = 7
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 3);
DELETE FROM treino_exercicio WHERE fk_exercicio = 7;
DELETE FROM exercicio WHERE id = 7;

UPDATE registro_musculacao SET fk_exercicio = 3,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 3 AND nome = 'Máquina')
WHERE fk_exercicio = 69;
UPDATE treino_exercicio SET fk_exercicio = 3 WHERE fk_exercicio = 69
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 3);
DELETE FROM treino_exercicio WHERE fk_exercicio = 69;
DELETE FROM exercicio WHERE id = 69;

-- ------------------------------------------------------------
-- GRUPO 11: Supino Reto (canonical=1; duplicates=6, 82)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Supino Reto', possui_variacao = TRUE WHERE id = 1;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 1, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 1, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Barra');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Máquina', NOW(), 1, 1, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Máquina'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Máquina');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Halter')
WHERE fk_exercicio = 1 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 1,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Barra')
WHERE fk_exercicio = 6;
UPDATE treino_exercicio SET fk_exercicio = 1 WHERE fk_exercicio = 6
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 1);
DELETE FROM treino_exercicio WHERE fk_exercicio = 6;
DELETE FROM exercicio WHERE id = 6;

UPDATE registro_musculacao SET fk_exercicio = 1,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 1 AND nome = 'Máquina')
WHERE fk_exercicio = 82;
UPDATE treino_exercicio SET fk_exercicio = 1 WHERE fk_exercicio = 82
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 1);
DELETE FROM treino_exercicio WHERE fk_exercicio = 82;
DELETE FROM exercicio WHERE id = 82;

-- ------------------------------------------------------------
-- GRUPO 12: Tríceps Francês (canonical=53; duplicate=71)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Tríceps Francês', possui_variacao = TRUE WHERE id = 53;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 53, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 53 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia', NOW(), 1, 53, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 53 AND nome = 'Polia');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 53 AND nome = 'Halter')
WHERE fk_exercicio = 53 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 53,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 53 AND nome = 'Polia')
WHERE fk_exercicio = 71;
UPDATE treino_exercicio SET fk_exercicio = 53 WHERE fk_exercicio = 71
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 53);
DELETE FROM treino_exercicio WHERE fk_exercicio = 71;
DELETE FROM exercicio WHERE id = 71;

-- ------------------------------------------------------------
-- GRUPO 13: Tríceps Testa (canonical=70; duplicates=72, 93)
-- ------------------------------------------------------------
UPDATE exercicio SET nome = 'Tríceps Testa', possui_variacao = TRUE WHERE id = 70;

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia', NOW(), 1, 70, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Polia');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 70, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Halter');

INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Barra', NOW(), 1, 70, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Barra'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Barra');

UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Polia')
WHERE fk_exercicio = 70 AND fk_exercicio_variacao IS NULL;

UPDATE registro_musculacao SET fk_exercicio = 70,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Halter')
WHERE fk_exercicio = 72;
UPDATE treino_exercicio SET fk_exercicio = 70 WHERE fk_exercicio = 72
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 70);
DELETE FROM treino_exercicio WHERE fk_exercicio = 72;
DELETE FROM exercicio WHERE id = 72;

UPDATE registro_musculacao SET fk_exercicio = 70,
    fk_exercicio_variacao = (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 70 AND nome = 'Barra')
WHERE fk_exercicio = 93;
UPDATE treino_exercicio SET fk_exercicio = 70 WHERE fk_exercicio = 93
  AND NOT EXISTS (SELECT 1 FROM treino_exercicio t2 WHERE t2.fk_treino = treino_exercicio.fk_treino AND t2.fk_exercicio = 70);
DELETE FROM treino_exercicio WHERE fk_exercicio = 93;
DELETE FROM exercicio WHERE id = 93;

-- ============================================================
-- Passo 3: Exercícios de variante única — só rename + variação
-- ============================================================

-- 58: Agachamento Búlgaro (Halter) → Agachamento Búlgaro
UPDATE exercicio SET nome = 'Agachamento Búlgaro', possui_variacao = TRUE WHERE id = 58;
INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 58, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 58 AND nome = 'Halter');
UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 58 AND nome = 'Halter')
WHERE fk_exercicio = 58 AND fk_exercicio_variacao IS NULL;

-- 67: Flexão de Punho (Polia) → Flexão de Punho
UPDATE exercicio SET nome = 'Flexão de Punho', possui_variacao = TRUE WHERE id = 67;
INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Polia', NOW(), 1, 67, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Polia'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 67 AND nome = 'Polia');
UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 67 AND nome = 'Polia')
WHERE fk_exercicio = 67 AND fk_exercicio_variacao IS NULL;

-- 29: Rosca Concentrada (Halter) → Rosca Concentrada
UPDATE exercicio SET nome = 'Rosca Concentrada', possui_variacao = TRUE WHERE id = 29;
INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 29, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 29 AND nome = 'Halter');
UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 29 AND nome = 'Halter')
WHERE fk_exercicio = 29 AND fk_exercicio_variacao IS NULL;

-- 64: Rosca Martelo (Halter) → Rosca Martelo
UPDATE exercicio SET nome = 'Rosca Martelo', possui_variacao = TRUE WHERE id = 64;
INSERT INTO exercicio_variacao (nome, data_cadastro, usuario_cadastro_id, fk_exercicio, fk_tipo_variacao, padrao)
SELECT 'Halter', NOW(), 1, 64, tv.id, TRUE FROM tipo_variacao tv WHERE tv.nome = 'Halter'
  AND NOT EXISTS (SELECT 1 FROM exercicio_variacao WHERE fk_exercicio = 64 AND nome = 'Halter');
UPDATE registro_musculacao SET fk_exercicio_variacao =
    (SELECT id FROM exercicio_variacao WHERE fk_exercicio = 64 AND nome = 'Halter')
WHERE fk_exercicio = 64 AND fk_exercicio_variacao IS NULL;
