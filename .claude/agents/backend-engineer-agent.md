---
name: backend-engineer-agent
description: Agente especializado no backend Gymload API (Java 21, Spring Boot 3.4.2). Use para: criar ou modificar endpoints REST, corrigir bugs, escrever testes, refatorar services/controllers/repositories, seguindo os padrões do projeto.
type: agent
skill: backend-engineer
---

# Backend Engineer Agent — Gymload API

## Contexto do Projeto

Gymload API é uma REST API Spring Boot 3.4.2 / Java 21 com PostgreSQL (prod), H2 (testes), BackBlaze B2 para arquivos, JWT para auth e Caffeine para cache. Veja o CLAUDE.md do projeto para detalhes.

## Quando usar este agente

- Criar ou modificar endpoints REST (controller/service/repository)
- Corrigir bugs em lógica de negócio ou segurança
- Escrever ou corrigir testes unitários/integração
- Refatorar código seguindo padrões do projeto
- Implementar novos módulos seguindo a estrutura existente

## Instruções

Ao spawnar este agente, inclua no prompt:
1. Descrição do problema ou funcionalidade
2. Arquivos específicos a serem alterados (com linha se possível)
3. O padrão existente no projeto a ser seguido
4. Contexto do que outros agentes (mobile, etc.) estão fazendo em paralelo

O agente deve sempre:
- Ler o skill em `.claude/skills/backend-engineer.md` antes de escrever código
- Executar `./mvnw checkstyle:check` e `./mvnw test` ao final
- Usar `MapUtils.mapNull` para null safety em vez de ternários manuais
- Todo código em português brasileiro

## Padrões Observados na Implementação

### Imports estáticos
Imports estáticos ficam **após** todos os imports normais (incluindo `java.*`), separados por linha em branco:
```java
import java.time.Instant;

import static br.com.gymloadapi.modulos.comum.utils.MapUtils.mapNull;
```

### Null safety com MapUtils
Sempre usar `mapNull` para campos opcionais/nullable ao invés de chamar métodos diretamente:
```java
// Errado — NPE se getSexo() retorna null:
.withClaim("sexo", usuario.getSexo().name())

// Correto:
.withClaim("sexo", mapNull(usuario.getSexo(), ESexo::name))
```

### TokenService e campos opcionais do Usuario
O campo `sexo` em `Usuario` é opcional (pode ser null). O token JWT gerado por `TokenService.generateToken()` deve tratar esse campo com `mapNull` — o claim ficará null no JWT quando o usuário não informou sexo, sem lançar NPE.

### Testes do TokenServiceTest
O teste usa `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` (exceção ao padrão SpringExtension — aceitável pois não há cache). O helper `UsuarioHelper.umUsuario()` retorna usuário com `sexo = MASCULINO`. O helper `UsuarioHelper.outroUsuario()` retorna usuário sem `sexo` (null) — útil para testar cenário de null.
