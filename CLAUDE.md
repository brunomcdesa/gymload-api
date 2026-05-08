# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build (also runs checkstyle)
./mvnw clean install

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ExercicioServiceTest

# Run a single test method
./mvnw test -Dtest=ExercicioServiceTest#salvar_deveSalvarExercicioMusculacao_quandoSolicitado

# Checkstyle only
./mvnw checkstyle:check

# Run the application
./mvnw spring-boot:run
```

Checkstyle runs automatically during `process-classes` (before tests) using Google checks. Fix checkstyle errors before committing.

## Environment Variables

Required at runtime (not needed for tests — tests use H2):
- `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` — PostgreSQL connection
- `BACKBLAZE_ACCESS_KEY`, `BACKBLAZE_SECRET_KEY`, `BACKBLAZE_REGION`, `BACKBLAZE_BUCKET`, `BACKBLAZE_ENDPOINT` — S3-compatible file storage
- `JWT_SECRET` — defaults to `my-secret-key` if absent

## Architecture

Spring Boot 3.4.2 / Java 21 REST API. Production database is PostgreSQL; tests run on H2.

**Package layout:** `br.com.gymloadapi`
- `autenticacao/` — JWT login and token service
- `config/` — Security (`SecurityFilter`, `SecurityConfiguration`), AWS/BackBlaze, CORS
- `modulos/` — All domain modules (see below)
- `GymloadApiApplication.java` — entry point

**Modules** under `modulos/`, each following the same internal structure (`controller`, `dto`, `mapper`, `model`, `repository`, `service`):
- `cache/` — Cache constants (`CacheUtils`) and Caffeine configuration
- `comum/` — Shared base classes, enums, exceptions, utils
- `exercicio/` — Exercises and exercise variations
- `grupomuscular/` — Muscle groups
- `registroatividade/` — Activity records (see Strategy pattern below)
- `tipovariacao/` — Variation types (grip, equipment, etc.)
- `treino/` — Training plans
- `usuario/` — Users (`Usuario` implements Spring Security's `UserDetails`)

## Key Patterns

### Strategy pattern for activity records
`RegistroAtividadeService` dispatches to one of three implementations of `IRegistroAtividadeStrategy` based on `ETipoExercicio`:
- `RegistroMusculacaoService` — weight training
- `RegistroAerobicoService` — cardio
- `RegistroCalisteniaService` — calisthenics

`ETipoExercicio` enum carries a direct reference to its strategy's class, which is resolved via Spring `ApplicationContext` at `@PostConstruct`. When adding a new exercise type, add it to this enum with the corresponding service class and group validator.

### Audit history
Every entity that can be mutated has a parallel `*Historico` entity extending `HistoricoBase` (fields: `dataCadastro`, `usuarioCadastroId`, `acao`). Every write operation (save/edit/delete) must call `*HistoricoService.salvar()` to record the change.

### Conditional validation (group validators)
`IGroupValidators` defines marker interfaces (`Musculacao`, `Aerobico`, `Calistenia`). Fields on request DTOs are annotated with `@Null` or `@NotNull` restricted to specific groups. Services call `request.aplicarGroupValidators(exercicio.getTipoExercicio().getGroupValidator())` before saving to activate the right constraint group.

### QueryDSL predicates
Custom filtered queries use `PredicateBase` subclasses (e.g., `ExercicioPredicate`) that build a `BooleanBuilder`. DTOs expose a `toPredicate()` method; repository `*RepositoryImpl` classes accept the built predicate.

### Caching (Caffeine)
Cache names are constants in `CacheUtils`. Services annotate methods with `@Cacheable`, `@CacheEvict`, and `@Caching`. Tests that cover cache behavior import `CacheConfig` via `@Import` and clear all relevant caches in `@BeforeEach`.

### MapStruct mappers
All entity↔DTO conversions use MapStruct with `defaultComponentModel=spring`. Mapper implementations (`*MapperImpl`) are generated at compile time by the annotation processor.

## Testing Conventions

- Unit tests use `@ExtendWith(SpringExtension.class)` with a `@TestConfiguration` inner class that wires the real service under test
- Dependencies are mocked with `@MockitoBean`
- Each module's test package has a `*Helper` class with factory methods for test data (e.g., `ExercicioHelper.umExercicioMusculacao(1)`)
- Repository tests run against H2 with `ddl-auto: create` and `sql.init.mode: always`
- Test method names follow the pattern: `method_deveXxx_quandoYyy`
