---
description: >
  Use this skill for ALL backend development tasks involving Java and Spring Boot in this project.
  Triggers: creating or reviewing REST APIs, fixing build failures, writing or fixing unit/integration
  tests (JUnit, Mockito), analyzing security vulnerabilities, improving database queries
  (JPA/QueryDSL/SQL), refactoring, applying design patterns, reviewing Spring configurations,
  fixing dependency injection, writing repositories/services/controllers, optimizing performance,
  or when the user shares Java code or mentions Spring Boot, Maven, Hibernate, or JPA.
  Always trigger when the user says: "backend", "API", "Spring", "Java", "JPA", "SQL",
  "build quebrou", "teste unitário", "service", "controller", "repository", or any variation.
---

# Backend Engineer Skill

You are a senior backend engineer specializing in Java 21, Spring Boot 3.4.2, JPA/Hibernate,
QueryDSL, and secure API development. Produce production-grade, clean, secure, and well-tested code
that follows the project's existing conventions.

---

## 1. Análise do Projeto — Faça Isso Primeiro

Antes de escrever código:
1. Leia o CLAUDE.md na raiz do projeto para entender arquitetura, comandos e padrões.
2. Identifique se existe módulo similar ao que será criado — reutilize padrões existentes.
3. Verifique se há testes para o código que será alterado e siga o mesmo estilo.
4. Aponte problemas estruturais, de segurança ou qualidade antes de alterar qualquer coisa.

---

## 2. Estrutura de Módulos

O projeto organiza o código em módulos dentro de `src/main/java/br/com/gymloadapi/modulos/`.
Cada módulo segue esta estrutura interna:

```
modulos/<nome>/
├── controller/     # @RestController — apenas delega para o service
├── dto/            # Java records para Request e Response
├── mapper/         # Interface MapStruct
├── model/          # Entidade JPA principal + entidade *Historico
├── predicate/      # Subclasse de PredicateBase (QueryDSL) — se necessário
├── repository/     # Spring Data JPA + *RepositoryCustom + *RepositoryImpl
└── service/        # *Service (lógica de negócio) + *HistoricoService
```

---

## 3. Padrões do Projeto

### DTOs como Records

```java
public record ExemploRequest(
    @NotBlank String nome,
    @NotNull ETipoExemplo tipo
) {
    public void aplicarGroupValidators(Class<?> groupValidator) {
        ValidacaoUtils.validar(this, groupValidator);
    }
}
```

### Auditoria Obrigatória

Todo CRUD exige uma entidade `*Historico` + `*HistoricoService`. O service principal chama:

```java
private void saveComHistorico(Entidade entidade, Integer usuarioId, EAcao acao) {
    repository.save(entidade);
    historicoService.salvar(entidade, usuarioId, acao);
}
```

Use `EAcao.CADASTRO`, `EAcao.EDICAO` ou `EAcao.EXCLUSAO`.

### Validação Condicional (Group Validators)

Use `IGroupValidators` para validações condicionais por tipo:

```java
// No DTO:
@Null(groups = {Aerobico.class})
@NotNull(groups = {Musculacao.class, Calistenia.class})
Integer grupoMuscularId;

// No service, antes de salvar:
request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator());
```

### QueryDSL (Repositórios Customizados)

Para queries com filtros dinâmicos:
1. Crie `*RepositoryCustom` (interface) e `*RepositoryImpl` (implementação com `JPAQueryFactory`)
2. Use `leftJoin(...).fetchJoin()` para evitar N+1
3. Crie `*Predicate extends PredicateBase` com métodos encadeados
4. O DTO de filtro expõe `toPredicate()` que constrói o predicate

### Caching (Caffeine)

- Declare constantes em `CacheUtils`
- Anote métodos com `@Cacheable`, `@CacheEvict`, `@Caching`
- Use `@CacheEvict(allEntries = true)` em operações de escrita para o cache correto

### MapStruct

Todos os mappers são interfaces `@Mapper`. Inclua:
- `mapToModel(Request)` → entidade
- `mapModelToResponse(Entidade)` → DTO de resposta
- `mapToHistorico(Entidade, Integer usuarioId, EAcao)` → entidade historico
- `editarEntidade(Request, @MappingTarget Entidade)` → atualiza in-place

---

## 4. Segurança

### Roles e Autorização

- Roles disponíveis: `ADMIN`, `USER`
- Operações de leitura: qualquer autenticado
- Operações de escrita (POST/PUT/DELETE): geralmente requerem `ADMIN`
- Controllers recebem o usuário autenticado via `@AuthenticationPrincipal Usuario usuario`

### Regras Sempre Aplicáveis

- Nunca retorne entidades JPA diretamente nos controllers — use DTOs (records)
- Valide ownership antes de retornar/modificar recursos de outros usuários
- Use `NotFoundException`, `ValidacaoException`, `PermissaoException` do pacote `comum.exception`
- Nunca concatene input do usuário em queries — use QueryDSL ou JPQL com parâmetros nomeados

---

## 5. Controllers

Controllers são finos — só delegam:

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exemplos")
public class ExemploController {

    private final ExemploService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public void salvar(@RequestBody @Valid ExemploRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.salvar(request, usuario.getId());
    }

    @GetMapping
    public List<ExemploResponse> buscarTodos() {
        return service.buscarTodos();
    }

    @PutMapping("/{id}/editar")
    @ResponseStatus(NO_CONTENT)
    public void editar(@PathVariable Integer id,
                       @RequestBody @Valid ExemploRequest request,
                       @AuthenticationPrincipal Usuario usuario) {
        service.editar(id, request, usuario.getId());
    }
}
```

---

## 6. Testes

### Service Tests

Use `@ExtendWith(SpringExtension.class)` + `@TestConfiguration` inner class + `@MockitoBean`.
**Não use** `@MockitoExtension` + `@InjectMocks` — o projeto usa Spring context para suportar cache.

```java
@ExtendWith(SpringExtension.class)
@Import({ExemploServiceTest.TestServiceConfig.class, CacheConfig.class})
class ExemploServiceTest {

    @TestConfiguration
    static class TestServiceConfig {

        @Bean
        public ExemploService exemploService(ExemploRepository repository, ExemploMapper mapper) {
            return new ExemploService(repository, mapper);
        }
    }

    @Autowired
    private ExemploService service;
    @Autowired
    private CacheManager cacheManager;
    @MockitoBean
    private ExemploRepository repository;

    @BeforeEach
    void setUp() {
        getCachesExemplo().stream()
            .map(cacheManager::getCache)
            .filter(Objects::nonNull)
            .forEach(Cache::clear);
    }

    @Test
    void salvar_deveSalvarExemplo_quandoSolicitado() { ... }
}
```

### Controller Tests

Use `@WebMvcTest` com testes de autorização para cada endpoint:

```java
@WebMvcTest(ExemploController.class)
class ExemploControllerTest {

    @Test
    @WithAnonymousUser
    void salvar_deveRetornar401_quandoUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(post("/api/exemplos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void salvar_deveRetornar403_quandoUsuarioNaoTemPermissao() throws Exception { ... }

    @Test
    @WithMockUser(roles = "ADMIN")
    void salvar_deveRetornar201_quandoSolicitadoComSucesso() throws Exception { ... }
}
```

### Repository Tests

Use `@DataJpaTest` — roda no H2 em memória com `ddl-auto: create`.

### Helpers de Teste

Cada módulo tem uma classe `*Helper` em `src/test/.../helper/` com static factory methods:

```java
public class ExemploHelper {

    public static ExemploRequest umExemploRequest() { ... }
    public static Exemplo umExemplo(Integer id) { ... }
    public static List<Exemplo> umaListaDeExemplos() { ... }
}
```

### Nomenclatura de Testes

```
metodo_deveXxx_quandoYyy

salvar_deveSalvarExercicio_quandoSolicitado
buscarTodos_deveLancarException_quandoNaoEncontrarRegistros
editar_deveRemoverCaches_quandoAlterarEntidade
```

---

## 7. Idioma do Código

Todo o código é escrito em **português brasileiro**:
- Nomes de variáveis, métodos e campos: `salvar`, `buscarTodos`, `usuarioId`, `dataCadastro`
- Mensagens de erro: `"Exercício não encontrado."`, `"Não é permitido alterar o tipo."`
- Nomes de testes: `deveSalvar`, `deveLancarException`, `quandoSolicitado`
- Entidades e DTOs: `Exercicio`, `GrupoMuscular`, `RegistroAtividade`

---

## 8. Verificação Antes de Concluir

Após qualquer alteração, execute:

```bash
./mvnw checkstyle:check   # garante conformidade com Google checks
./mvnw test               # todos os testes devem passar
```

Checklist:
- [ ] Nenhum dado sensível exposto na resposta da API
- [ ] Validação no controller com `@Valid`
- [ ] Nenhuma query com concatenação de string do usuário
- [ ] `@Transactional` aplicado corretamente no service
- [ ] Nenhum N+1 introduzido (use `fetchJoin` no QueryDSL)
- [ ] Historico salvo em toda operação de escrita
- [ ] Testes cobrem happy path, erros e (se aplicável) cache
- [ ] Código, variáveis e mensagens em português
- [ ] Build e checkstyle passando limpos
