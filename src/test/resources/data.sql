-- USUARIOS
INSERT INTO USUARIO (ID, NOME, USERNAME, SENHA) VALUES ('c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9', 'USUARIO ADMIN', 'USUARIO_ADMIN', '$2a$10$QByEoLAMS4JYufIFuQDke.Qvn8PSrwJzxMywB.h/90pq7Zq1JSboK');

-- USUARIOS_ROLES
INSERT INTO USUARIO_ROLE (USUARIO_ID, ROLE) VALUES ('c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9', 'ADMIN');
INSERT INTO USUARIO_ROLE (USUARIO_ID, ROLE) VALUES ('c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9', 'USER');

-- GRUPOS MUSCULARES
INSERT INTO GRUPO_MUSCULAR (ID, NOME, CODIGO) VALUES (1, 'Peitoral', 'PEIROTAL');

-- EXERCICIOS
INSERT INTO EXERCICIO (ID, NOME, DESCRICAO, TIPO_EXERCICIO, TIPO_PEGADA, FK_GRUPO_MUSCULAR) VALUES (1, 'SUPINO RETO', 'Supino Reto', 'HALTER', 'PRONADA', 1);

-- TREINOS
INSERT INTO TREINO (ID, NOME, DATA_CADASTRO, FK_USUARIO) VALUES (1, 'TREINO 1', '2025-03-04', 'c2d83d78-e1b2-4f7f-b79d-1b83f3c435f9');

-- TREINOS_EXERCICIOS
INSERT INTO TREINO_EXERCICIO (FK_TREINO, FK_EXERCICIO) VALUES (1, 1);

