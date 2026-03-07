# Copilot Instructions - lemur-project

> Keep these instructions in sync:
>
> - `.github/copilot-instructions.md`
> - `AGENTS.md`

## Modules

- `lemur-app` - REST API, domain, Kafka, JPA (main app)
- `lemur-flyway` - Standalone Flyway migration runner
- `lemur-libs:lemur-bom` - Central BOM (all versions here)
- `lemur-libs:lemur-migration` - Shared Flyway SQL scripts
- `lemur-libs:lemur-testkit` - Shared test infra (`@PostgresAwareTest`, `@KafkaAwareTest`) - Testcontainers and friends
- `buildSrc` - Internal Gradle convention plugins

## Key Commands

- Format + build + test: `./gradlew`
- Test (no containers): `./gradlew test`
- Test (with containers): `./gradlew test -Pcontainers.enabled`
- Auto-format: `./gradlew spotlessApply`
- Run app: `./gradlew :lemur-app:bootRun`

Requires JDK 17+ and Docker (for Testcontainers). Always run `./gradlew spotlessApply build` before PR.

## Coding Guidelines

- No wildcard imports in Java.
- All dependency versions in `gradle/libs.versions.toml`; use `lemur-bom` platform in each module.
- Spotless enforces formatting - never manually reformat.
- Keep REST contracts stable (`PlayerModel`, `RegisterPlayerModel`, controller mappings).

## Test Guidelines

- **Naming:** `givenThis_whenThis_thenThis` - mandatory.
- **Assertions:** AssertJ only.
- **Delays:** Awaitility (`await().pollDelay(...).until(...)`) - never `Thread.sleep`.
- Testcontainers tests are skipped unless `-Pcontainers.enabled` is passed.
- **Scope:** Test both happy path and failure modes.
- **Execution:** Run tests once, save output to `build/test-run.log` inside the repo (`> build/test-run.log 2>&1`), then
  read from that file to extract errors. Never run the same test command multiple times, without changes in sources. You
  can store test output in multiple files if you want to compare before/after changes (ex. `build/test-run-{i}.log`).

## Integration Test Guidelines

- **Setup:**
  - Use `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` to allow `@BeforeAll` non-static methods for setup/teardown.
  - Inject required JPA repositories for direct DB access in setup/teardown.
  - Depending on tested feature, select `@SpringBootTest`, `@PostgresAwareTest`, `@KafkaAwareTest`, and/or
    `@AutoConfigureRestTestClient` (this list may evolve).
- **Isolation:** `deleteAll()` in `@BeforeAll` and `@AfterEach`; seed data via JPA repository in `@BeforeEach`.

## Agent Instructions

- Read files before editing; match existing style.
- Validate with `get_errors` after every edit and fix issues.
- Minimal, focused changes - avoid touching unrelated code.
- Delegate to specialized agents (CVE Remediator, Plan) when applicable.
- Never use `Thread.sleep` in tests - use `Awaitility`.
- Never add dependency versions outside the version catalog.
- Read files using IDE features instead of shell commands.

## Troubleshooting

- Formatting failures -> `./gradlew spotlessApply`
- Testcontainers not running -> Docker must be running + `-Pcontainers.enabled`
- Dependency issues -> `./gradlew --refresh-dependencies`
