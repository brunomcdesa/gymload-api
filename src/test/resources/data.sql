-- USUARIOS
INSERT INTO USUARIO (ID, UUID, NOME, USERNAME, SENHA) VALUES (1, 'c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9', 'USUARIO ADMIN', 'USUARIO_ADMIN', '$2a$10$QByEoLAMS4JYufIFuQDke.Qvn8PSrwJzxMywB.h/90pq7Zq1JSboK');

-- USUARIOS_ROLES
INSERT INTO USUARIO_ROLE (USUARIO_ID, ROLE) VALUES (1, 'ADMIN');
INSERT INTO USUARIO_ROLE (USUARIO_ID, ROLE) VALUES (1, 'USER');

-- GRUPOS MUSCULARES
INSERT INTO GRUPO_MUSCULAR (ID, NOME, CODIGO) VALUES (1, 'Peitoral', 'PEIROTAL');

-- EXERCICIOS
INSERT INTO EXERCICIO (ID, NOME, DESCRICAO, TIPO_EQUIPAMENTO, TIPO_EXERCICIO, TIPO_PEGADA, FK_GRUPO_MUSCULAR) VALUES (1, 'SUPINO RETO', 'Supino Reto', 'HALTER', 'MUSCULACAO', 'PRONADA', 1);

-- TREINOS
INSERT INTO TREINO (ID, NOME, DATA_CADASTRO, FK_USUARIO, SITUACAO) VALUES (1, 'TREINO 1', '2025-03-04', 1, 'ATIVO');

-- TREINOS_EXERCICIOS
INSERT INTO TREINO_EXERCICIO (FK_TREINO, FK_EXERCICIO, ORDEM_EXERCICIO) VALUES (1, 1, 0);

-- REGISTRO_CARGA
INSERT INTO REGISTRO_CARGA (ID, PESO, UNIDADE_PESO, QTD_REPETICOES, DATA_CADASTRO, QTD_SERIES, FK_EXERCICIO, FK_USUARIO) VALUES (1, 22.5, 'KG', 12, '2025-03-04', 4, 1, 1);

-- REGISTRO_CARDIO
INSERT INTO REGISTRO_CARDIO (ID, DURACAO, DISTANCIA, DATA_CADASTRO, FK_EXERCICIO, FK_USUARIO) VALUES (1, 22.5, 1.5,  '2025-03-04', 1, 1);
