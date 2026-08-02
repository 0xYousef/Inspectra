# Inspectra — Coding Standards

This guide is the single source of truth for writing code that fits the Inspectra
automation framework. It reflects how the framework is actually built today and
points out the anti-patterns to avoid. Read it before writing your first class,
and keep it open while you work.

---

## 1. Tech Stack & Environment

| Concern     | Choice                                            |
|-------------|---------------------------------------------------|
| Language    | Java 17                                           |
| Build       | Maven (single module)                             |
| API tests   | Rest Assured + json-schema-validator              |
| UI tests    | Selenium 4 (PageFactory, CDP throttling)          |
| Test runner | TestNG (data providers, soft assertions, listeners) |
| Reports     | Allure (Epic/Feature/Story/Severity, attachments) |
| Data models | Lombok (`@Data`, `@Builder`)                      |
| Data store  | MongoDB (sync driver), Caffeine in-memory cache   |
| Faker       | JavaFaker for random test data                    |
| Logging     | SLF4J + Logback                                   |
| Config      | `application.properties`, `.env` (dotenv)         |

Never add a dependency without updating `pom.xml` and confirming it is
absolutely necessary.

---

## 2. Project Layout & Layering

Code must live in the layer that owns its responsibility. Put files where they
belong; do not invent new top-level packages.

### `src/main/java` (framework code)

| Package          | Responsibility                                                     |
|------------------|--------------------------------------------------------------------|
| `core.base`      | Driver setup (`DriverFactory`, `DRIVERS`, `DEVICES`), `BaseUITest` |
| `core.db`        | MongoDB client and collection names (`MongoDBClient`, `COLLECTIONS`) |
| `core.throttle`  | CPU/network throttling via CDP (profiles, options, `ConfigManager`) |
| `core.utils`     | Cross-cutting helpers (`Configuration`, `AllureUtils`, `TestListener`, `AllureParams`, screenshots) |
| `api.endpoints.<feature>` | Endpoint Object Model classes (`XxxEndpoint`)      |
| `data.DTO`       | Data-transfer objects (Lombok models)                              |
| `data.provider`  | TestNG `@DataProvider` factories                                   |
| `data.variables` | Static reusable test values (random/fixed)                         |
| `data.expectations` | Single home for every expected value (`Expectations`)           |
| `data.mapper`    | Document <-> DTO mappers                                           |
| `data.mongo`     | MongoDB repositories                                               |
| `data.util`      | Data generators/helpers                                             |
| `data.exceptions`| Framework exceptions + `ExceptionHandler`                          |
| `cache.context`  | Thread-local user context (`UserContext`)                          |
| `cache.models`   | In-memory cache models (`SessionCache`, `UserInfo`, `ProductCache`) |
| `cache.repository` | Caffeine-backed cache access                                     |
| `cache.services` | Services; interfaces in `cache.services.interfaces`               |
| `selenium.pages` | Page Objects (extend `BasePage`); grouped by feature               |
| `selenium.components` | Reusable page fragments (`Header`, `Footer`, `PageNavigator`, ...) |
| `selenium.validators` | Assertion/page-check entry points that return result strings    |
| `selenium.support.enums` | UI enums (`BRAND`, `CATEGORY`, ...)                        |
| `selenium.support.helpers` | Read helpers (`ReadFile`)                                |
| `selenium.mapper` | UI document -> DTO mappers                                        |

### `src/test/java` (tests only)

| Package                | Responsibility                                    |
|------------------------|---------------------------------------------------|
| `api.base`             | `BaseAPIClient`, request filters                  |
| `api.tests.<feature>`  | API test classes, grouped by feature then action  |
| `selenium.tests.<feature>` | UI test classes (`TC###_Feature_...`)        |

### Resources

- `src/main/resources/application.properties` — environment/config keys
- `src/main/resources/schemas/*.json` — API response schemas
- `src/main/resources/selectors/*.json` — UI element selectors
- `src/main/resources/logback.xml` — logging config
- `src/test/resources/*.xml` — TestNG suite files (`TestNG-API.xml`, `TestNG-UI.xml`, ...)

**Rules:**
- Test classes extend `BaseAPIClient` (API) or `BaseUITest` (UI). Never write a
  test that does not extend one of these.
- Framework infrastructure (drivers, listeners, utils) is framework-owned. Do
  not modify `BaseAPIClient`, `TestListener`, `BaseUITest`, or `DriverFactory`
  without a strong reason — prefer adding new code.
- UI assertions belong in `selenium.validators`, not scattered in pages.

---

## 3. Naming Conventions

| What                | Convention                        | Example                                   |
|---------------------|-----------------------------------|-------------------------------------------|
| Packages            | `lowercase`, feature-based        | `api.tests.account.login`                 |
| Classes             | `PascalCase`                      | `RegisterProvider`, `LoginPage`           |
| Service interfaces  | `XxxService` in `interfaces/`     | `UserProfileService`                      |
| Service impls       | `XxxServiceImpl`                  | `UserProfileServiceImpl`                  |
| Enums               | `ALL_CAPS`, plural group names    | `DRIVERS.CHROME`, `BRAND.POLO`            |
| Test classes (UI)   | `TC###_Feature_Valid/InvalidTest` | `TC01_Registration_ValidTest`             |
| Test classes (API)  | `Feature_Valid/InvalidTest`       | `Login_ValidTest`, `GetAllBrands_ValidTest` |
| Methods (Java)      | `camelCase`, verb-first           | `openSession()`, `fillMandatoryRegisterForm()` |
| Test methods        | `camelCase`, descriptive          | `LoginByValidCredentials()`               |
| Constants / enum vals | `UPPER_SNAKE`                   | `WELCOME_MESSAGE`, `OK`                   |
| Fields / locals     | `camelCase`                       | `homePage`, `driver`, `verifyMessage`     |
| Logger              | `private static final Logger log` | `private static final Logger log = LoggerFactory.getLogger(X.class);` |
| TestNG data provider names | `camelCase`, meaningful    | `@DataProvider(name = "loginValidCredentials")` |
| Allure labels       | `UPPER_SNAKE`                    | `@Epic("ACCOUNT")`, `@Feature("LOGIN")`   |

**Test method naming pattern** — combine the action and the outcome:
`{verb}{Subject}{Outcome}`. Examples: `LoginByValidCredentials`,
`registerUI_validTest`, `captureEvidence`. Pick one spelling and keep it; do not
mix `loginValid` and `LoginByValid` within the same feature.

---

## 4. Class Design Standards

### DTOs (`data.DTO`)
- Use Lombok: `@Data` + `@Builder(toBuilder = true)`.
- Derive variants with `.toBuilder().field(newValue).build()`, never copy-paste a
  builder block.

```java
@Data
@Builder(toBuilder = true)
public class Register {
    private String title, name, email, password;
    private int day, month, year;
    private String firstname, lastname;
    // ...
}
```

### Providers (`data.provider`)
- One provider class per feature; methods annotated `@DataProvider(name = "...")`.
- Return an array of DTOs; build them through builders.
- Reuse static values from `data.variables` via `import static ...`; add
  randomness with `Faker`.

```java
@DataProvider(name = "loginValidCredentials")
public static Login[] loginValidData() {
    Login cur = LOGIN;
    return new Login[]{buildLogin(cur.getEmail(), cur.getPassword(), OK, USER_EXISTS)};
}
```

### Variables (`data.variables`)
- `public static final` fields holding fixed or `Faker`-generated values.
- Imported statically (`import static data.variables.UserVariables.*;`).
- Keep values here or in `Expectations` — never hardcode in tests.

### Expectations (`data.expectations`)
- **All** expected HTTP codes, API messages, and UI texts live in
  `Expectations.Http.*` and `Expectations.Ui.*`.
- Tests must never contain literal expected strings or status codes.

```java
softAssert.assertEquals(validationPage.getTitle(), Expectations.Ui.Register.TITLE);
```

### Enums
- Use Lombok `@Getter @RequiredArgsConstructor`, a `displayName` field, and
  `toString()` returning it.

```java
@Getter
@RequiredArgsConstructor
public enum BRAND {
    POLO("Polo"),
    // ...
    private final String displayName;

    @Override
    public String toString() { return displayName; }
}
```

### Repositories & Mappers
- Mongo access goes through `MongoDBClient`; repositories live in `data.mongo`
  (data) or `cache.repository` (cache).
- Mappers are `public static` methods named `toDocument` / `fromDocument`.

### Exceptions
- Extend `FrameworkException` (a `RuntimeException`).
- Route handling through `ExceptionHandler` (`handle`, `handleSilently`).
- Never `printStackTrace()`, never `System.err.println(...)`, never swallow
  exceptions silently.

### Utilities
- `public final` class with a `private` constructor; all members `static`.

### Caching
- Interfaces in `cache.services.interfaces`, implementations named `XxxServiceImpl`.
- In-memory state only; never put Selenium or Rest Assured objects in the cache.

---

## 5. Test Authoring

### API tests
- Extend `BaseAPIClient`. `baseURI` and the Allure/logging filters are configured
  automatically — do not reconfigure RestAssured in a test.
- Annotate with `@Epic`, `@Feature`, `@Story` (the endpoint), `@Severity`, and
  `@Description`.
- Hit an endpoint through its Endpoint Object Model (EOM) class — never build
  requests inline with `given()` in a test.

#### Endpoint Object Model (EOM)
- Endpoints live in `src/main/java/api/endpoints/<feature>/` as `XxxEndpoint`
  classes extending `api.endpoints.base.Endpoint`. They are framework logic —
  test cases in `src/test` never build requests, they only call endpoints.
- Each endpoint owns its HTTP method, path, request building, and response
  schema; it attaches the schema to Allure and validates the response body
  against it, returning the raw RestAssured `Response`.
- Skip optional/absent fields instead of sending them: guard `multiPart(...)`
  calls so a `null` value (or an optional such as `birth_day == 0`) is never
  added to the request.

```java
public class VerifyLoginEndpoint extends Endpoint {

    private static final String PATH = "/verifyLogin";
    private static final String SCHEMA = "schemas/login-response-schema.json";

    public Response login(Login form) {
        RequestSpecification request = newRequest().contentType(ContentType.MULTIPART);
        addMultipart(request, "email", form.getEmail());
        addMultipart(request, "password", form.getPassword());
        return execute(request, HttpMethod.POST, PATH, SCHEMA, "Login Response Schema");
    }

    private void addMultipart(RequestSpecification request, String name, String value) {
        if (value != null) {
            request.multiPart(name, value);
        }
    }
}
```

Tests stay focused on behavior — call the endpoint, then assert payload values
against `Expectations`:

```java
Response response = new VerifyLoginEndpoint().login(form);
assertEquals(response.jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
assertEquals(response.jsonPath().get("message"), form.getExpectation().getMessage());
```

- Use `var` sparingly and only with clear names — never snake_case locals
  (`response_payload` is an anti-pattern).

### UI tests
- Extend `BaseUITest`; the constructor selects the driver:

```java
public TC01_Registration_ValidTest() { super(CHROME); }
```

- Initialize pages and soft assertions in `@BeforeClass`.
- Drive flows through the Page Object chain (Page -> Component -> Validator) and
  assert against `Expectations.Ui.*`.
- Use `SoftAssert` when a test has multiple checks, then make the failure visible
  in the report.
- Mark page/component actions with Allure `@Step("...")`.
- Evidence (screenshot on PASS+FAIL, video on failure) is captured automatically
  by `BaseUITest`. Do not add your own screenshot calls.

### Data-driven tests
- Always feed DTOs from a `@DataProvider` (`data.provider`). Never construct a
  DTO inline inside a test method.
- Keep field names meaningful: the flattened DTO fields become the parameter
  keys in the Allure report (`personal info - title`, `expectation - status code`).

### Suites
- Register new tests in the appropriate suite file when the suite relies on
  explicit classes/packages:
  `src/test/resources/TestNG-API.xml` and `TestNG-UI.xml`.

---

## 6. Configuration

- Read config through `core.utils.Configuration.get("dot.separated.key")`.
- Keys live in `src/main/resources/application.properties` with dot separators
  (`ui.base.url`, `api.base.url`, `screenshot.dir`).
- Never hardcode base URLs, driver paths, or report paths in Java code.
- Throttling config is intentionally separate from `application.properties`:
  follow the 4-layer chain (system property -> env var -> `.env` -> default) in
  `core.throttle.ConfigManager`. Documented in `docs/throttling-cheatsheet.md`.

---

## 7. Logging & Error Handling

- SLF4J logger, always `private static final Logger log` (never `public`).
- Use `{}` placeholders, never string concatenation in log statements.

```java
log.info("User {} logged in successfully", email);
```

- Levels: `info` for normal flow, `warn` for non-fatal/fallback, `error` for
  failures. Route framework failures through `ExceptionHandler`.
- Config/startup failures fail fast with a meaningful message.

---

## 8. Git & Repo Hygiene

- Commit message style: short imperative summary line, e.g.
  `Add Inspectra automation framework`. Explain the *why*, not the *what*.
- Never commit generated artifacts: `bin/`, `.allure/`, `logs/`,
  `allure-results/`, `test-output/`, `target/`, `.idea/`, `.vscode/`.
- Never commit secrets (`.env`, credentials, tokens). `application.properties`
  holds only non-secret local values.
- Run `mvn -q clean test-compile` before pushing to confirm the code builds.

---

## 9. Do Not (Anti-Patterns)

The following were observed in the repo and must not appear in new code:

- **snake_case** Java locals or methods — `response_payload`, `save_session`,
  `view_all_sessions`. Use `camelCase`.
- **Hardcoded expectation literals** in tests — expected status codes, messages,
  or UI text. Use `Expectations`.
- **Inline `given()` request building in tests** — the HTTP method, path, and
  multipart parameters belong in an `api.endpoints.<feature>.XxxEndpoint` class,
  not in the test.
- **Hardcoded endpoint paths in tests** — `/verifyLogin`, full
  `https://automationexercise.com/api/...` URLs. Endpoints own their paths; tests
  call the endpoint class.
- **`System.err.println(...)`, `printStackTrace()`, `ex.printStackTrace()`** —
  use SLF4J + `ExceptionHandler`.
- **Wildcard imports** — `import io.qameta.allure.*;`. Import explicitly.
- **`public` (non-private) loggers** — e.g. `public static final Logger log`.
  Always `private static final`.
- **Dead/leftover code** — `// Here` markers, unused private methods
  (`isWindows_unused`), commented-out blocks. Delete them.
- **UPPER_SNAKE local XPath strings with typos** — `EMAIl_XPATH`. Prefer the
  `data-qa` selectors via `selectors/*.json` where they exist; otherwise use
  clear camelCase locals.
- **Reconfiguring the framework** — `RestAssured.*` setup, screenshot calls,
  or driver teardown in tests. It is handled by the base classes.
- **Constructing DTOs inline in tests** — always via a provider.
- **Splitting a test across several expected failures silently** — a failed
  `SoftAssert` must surface in the report; never ignore assertion results.

---

## 10. Definition of Done

Before you commit a new feature/test:

- [ ] Class lives in the correct package per Section 2.
- [ ] Naming follows Section 3 (PascalCase class, camelCase methods/locals,
      `TC###_Feature_ValidTest` for UI tests).
- [ ] No hardcoded literals — everything expected comes from `Expectations`.
- [ ] Data is provided by a `@DataProvider`, not built inline.
- [ ] API tests hit endpoints via `api.endpoints` — no inline `given()`, no
      hardcoded paths.
- [ ] Loggers are `private static final`, logging uses `{}` placeholders.
- [ ] No wildcard imports, no `System.err`, no `printStackTrace`, no dead code.
- [ ] No secrets or generated artifacts staged for commit.
- [ ] `mvn -q clean test-compile` passes.
- [ ] Test is registered in the correct TestNG suite when required.
