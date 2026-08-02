# Inspectra — Test Automation Framework

**Inspectra** is a single-module Maven test automation framework for both **API** and **UI**
testing. It is built to test the public practice site
[automationexercise.com](https://automationexercise.com/): REST APIs with **Rest Assured**
and browser flows with **Selenium** and the **Page Object Model**. It is **data-driven**
(TestNG `@DataProvider` + JavaFaker), stores state in **MongoDB**, and reports through
**Allure**.

This README explains what the framework does and how a tester writes and runs tests.

---

## Tech Stack

| Area      | Choice                                                        |
|-----------|---------------------------------------------------------------|
| Language  | Java 17                                                       |
| Build     | Maven (single module, `com.eagle0eye:inspectra-framework`)    |
| API tests | Rest Assured 5 + JSON Schema validation                       |
| UI tests  | Selenium 4 (Page Object Model, optional CDP throttling)       |
| Runner    | TestNG 7 (data providers, soft assertions, listeners)         |
| Data      | MongoDB (sync driver) + Caffeine in-memory cache              |
| Reports   | Allure (Epic/Feature/Story/Severity, attachments, screenshots)|
| Test data | JavaFaker, reusable variables + expectations                  |
| Logging   | SLF4J + Logback                                               |
| Config    | `application.properties`, optional `.env`/env vars            |

---

## Project Layout

```
src/
├── main/
│   ├── java/
│   │   ├── api/endpoints/          # Endpoint Object Model (API request logic)
│   │   │   ├── base/               #   Endpoint base + HttpMethod enum
│   │   │   ├── account/            #   verifyLogin, createAccount, updateAccount, ...
│   │   │   ├── brand/              #   brandsList
│   │   │   └── product/            #   productsList, searchProduct
│   │   ├── core/
│   │   │   ├── base/               #   DriverFactory, BaseUITest, DRIVERS/DEVICES
│   │   │   ├── db/                 #   MongoDB client + collection names
│   │   │   ├── throttle/           #   CDP CPU/network throttling
│   │   │   └── utils/              #   Configuration, AllureUtils, TestListener, ...
│   │   ├── data/
│   │   │   ├── DTO/                #   Lombok models (Login, Register, ...)
│   │   │   ├── provider/           #   TestNG @DataProvider factories
│   │   │   ├── variables/          #   Reusable test values
│   │   │   ├── expectations/       #   Every expected code/message lives here
│   │   │   ├── mongo/              #   Repositories (Auth, Register, Products)
│   │   │   ├── mapper/ util/ exceptions/
│   │   │   └── ...
│   │   ├── cache/                  #   In-memory session/product cache
│   │   └── selenium/
│   │       ├── pages/ components/  #   Page Objects + fragments (Header, Footer)
│   │       ├── validators/         #   Assertion helpers
│   │       └── support/            #   Enums, helpers, mapper
│   └── resources/
│       ├── application.properties  #   Environment/config keys
│       ├── schemas/*.json          #   API response JSON schemas
│       ├── selectors/*.json        #   UI element selectors
│       └── logback.xml
└── test/
    ├── java/
    │   ├── api/tests/<feature>/    #   API test classes (grouped by feature/action)
    │   ├── selenium/tests/<feature>/#   UI test classes (TC###_Feature_...)
    │   └── api/base/               #   BaseAPIClient (RestAssured setup)
    └── resources/*.xml             #   TestNG suite files
```

**Key rule:** *logic* (endpoints, pages, providers, repositories) lives under
`src/main/java`; *test cases* (`@Test` methods) live under `src/test/java`.

---

## Prerequisites

| Tool      | Version | Notes                                            |
|-----------|---------|--------------------------------------------------|
| JDK       | 17      | Required by `pom.xml`                           |
| Maven     | 3.8+    |                                                   |
| MongoDB   | local   | Default `mongodb://localhost:27017`, DB `automation` |
| Browser   | Chrome  | Firefox/Edge also supported for UI tests         |
| Allure    | optional| The Maven plugin generates reports; CLI not required |

MongoDB is only needed for tests that use repositories (registration/update data,
products cache, account filtering). Start it before those tests:

```bash
mongod --dbpath /your/mongo/data/path
```

Execution recording is **best-effort**: every finished test is bulk-inserted into
the `executions` collection after the run; if MongoDB is unreachable the insert is
silently skipped and the run is never failed.

---

## Configuration

All keys live in `src/main/resources/application.properties`:

| Key                        | Default                        | Used for                      |
|----------------------------|--------------------------------|-------------------------------|
| `api.base.url`             | `https://automationexercise.com/api` | Rest Assured base URI  |
| `ui.base.url`              | `https://automationexercise.com/`    | Selenium start URL      |
| `mongodb.connection.string`| `mongodb://localhost:27017`   | Mongo connection              |
| `mongodb.database.name`    | `automation`                  | Mongo database                |
| `execution.recording.enabled` | `true`                     | Persist executed cases into the `executions` collection |
| `screenshot.dir`           | `assets/images`               | UI screenshots (PASS + FAIL)  |
| `video.dir`                | `assets/videos`               | UI video (FAIL only)          |
| `download.dir`             | `assets/files`                | Downloads                     |
| `browser.gecko.driver` / `browser.firefox.binary` | ... | Firefox UI runs    |

Do not hardcode URLs, ports, or expectations in test code — put them in
`application.properties` or in `data.expectations.Expectations`.

---

## Running the Tests

`mvn test` runs **all** suites configured in `pom.xml`. To run a specific suite,
override `-Dsurefire.suiteXmlFiles` (note: singular `suiteXmlFiles`):

```bash
# Compile only (fast check that everything builds)
mvn clean test-compile

# API tests only
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/TestNG-API.xml

# UI tests only (opens browsers)
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/TestNG-UI.xml

# Everything
mvn test

# Everything, explicit
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/FULL_TEST.xml
```

Available suite files in `src/test/resources/`:

| Suite            | Scope                                        |
|------------------|----------------------------------------------|
| `TestNG-API.xml` | Standard API suite (account, brand, product) |
| `API_FULL_TEST.xml` | All `api.tests.*` including `FilterAccounts` |
| `TestNG-UI.xml`  | All UI tests (`selenium.tests.*`)            |
| `UI_FULL_TEST.xml` | All UI tests                                 |
| `FULL_TEST.xml`  | UI + API combined                            |

---

## Reporting with Allure

Test results are written to `target/allure-results` automatically.

```bash
# Generate the HTML report
mvn allure:report

# Open it (generated site)
open target/site/allure-maven-plugin/index.html   # macOS
xdg-open target/site/allure-maven-plugin/index.html   # Linux

# Or serve it over HTTP
python3 -m http.server 63311 -d target/site/allure-maven-plugin
```

The report includes API schema attachments, request/response logging, UI screenshots
(PASS and FAIL) and failure videos, grouped by `@Epic` / `@Feature` / `@Story`.

---

## Writing a Test — Tester's Guide

### 1. API test (Endpoint Object Model)

Every API endpoint has a class in `src/main/java/api/endpoints/<feature>/`. The
endpoint owns the HTTP method, path, request body and response schema. A test only
calls the endpoint and asserts on the returned `Response`:

```java
@Epic("ACCOUNT")
@Feature("LOGIN")
@Story("POST https://automationexercise.com/api/verifyLogin")
public class Login_ValidTest extends BaseAPIClient {

    @Description("Login By valid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "loginValidCredentials",
          dataProviderClass = AuthProvider.class, groups = {"API"})
    public void LoginByValidCredentials(Login form) {
        Response response = new VerifyLoginEndpoint().login(form);
        assertEquals(response.jsonPath().getInt("responseCode"),
                form.getExpectation().getStatusCode());
        assertEquals(response.jsonPath().get("message"),
                form.getExpectation().getMessage());
    }
}
```

Rules for API tests:

- Extend `BaseAPIClient` (it configures `baseURI` + Allure/logging filters).
- Annotate with `@Epic`, `@Feature`, `@Story`, `@Severity`, `@Description`.
- Feed data from a `@DataProvider` in `data.provider` — never build DTOs inline.
- Assert against `data.expectations.Expectations` — never hardcode codes/messages.
- Never build requests with `given()` inside a test — that is the endpoint's job.

Need a new endpoint? Create `XxxEndpoint extends Endpoint`, add the path, build the
request, and call `execute(request, method, path, schema, label)`.

### 2. UI test (Page Object Model)

UI tests extend `BaseUITest`, pick a browser, and drive flows through
pages/components. Screenshots and videos are captured automatically — do **not** add
your own screenshot calls.

```java
public class TC02_Login_ValidTest extends BaseUITest {

    public SoftAssert softAssert;

    public TC02_Login_ValidTest() {
        super(DRIVERS.CHROME);
        softAssert = new SoftAssert();
    }

    @Test(dataProvider = "loginValidCredentials", dataProviderClass = AuthProvider.class)
    public void loginValidCredentials(Login form) {
        HomePage homePage = new HomePage(driver);
        Header header = homePage.navigateTo().LoginPage()
                .login(Login.builder().email(form.getEmail()).password(form.getPassword()).build())
                .correctLogin();
        softAssert.assertNotNull(header.getLoggedInUsername());
    }
}
```

- Browsers: `DRIVERS.CHROME`, `DRIVERS.FIREFOX`, `DRIVERS.EDGE`.
- Devices/viewport: `DEVICES.DESKTOP`, `DEVICES.IPHONE`, `DEVICES.IPAD`, etc.
- Selectors live in `src/main/resources/selectors/*.json`, not in page code.
- Use `SoftAssert` for multiple checks; failures surface in the Allure report.

### 3. Data

- DTOs (Lombok `@Data @Builder(toBuilder = true)`) in `data.DTO`.
- Test data from `data.provider` (JavaFaker + `data.variables`).
- Expected codes/messages from `data.expectations.Expectations.Http.*` /
  `Expectations.Ui.*`.
- Mongo repositories in `data.mongo` store registered accounts, updated users, and
  products. All repository calls are best-effort — if MongoDB is down they log a
  warning and return a safe default instead of failing the test.
- Every executed test case is recorded in the `executions` collection
  (see [Execution tracking](#execution-tracking)).

---

## Execution tracking

After a suite finishes, the `TestListener` bulk-inserts one document per executed
test into the `executions` collection of the `automation` database.

Stored fields:

| Field          | Meaning                                        |
|----------------|------------------------------------------------|
| `suite`        | TestNG suite name                              |
| `class`        | Fully-qualified test class                     |
| `test`         | Test method name                               |
| `description`  | Allure / JFR `@Description` (method, then class) |
| `epic`, `feature`, `story`, `severity` | Allure annotations          |
| `status`       | `PASS`, `FAIL`, `SKIP`, or `SUCCESS_PERCENTAGE_FAILURE` |
| `parameters`   | Test parameters as JSON                        |
| `environment`  | `api.base.url` + `ui.base.url` as JSON         |
| `startTime`, `endTime` | Epoch milliseconds                      |
| `durationMs`   | Test duration                                 |
| `errorMessage` | Failure message (null on success)              |
| `stackTrace`   | First 20 lines of the stack trace              |

Example query:

```bash
mongosh automation --eval 'db.executions.find({status:"FAIL"})'
```

Disable with `execution.recording.enabled = false` in `application.properties`.

---

## CPU / Network Throttling (CDP)

Simulate slow CPUs/connections in Chrome via the Chrome DevTools Protocol (opt-in,
implemented in `core.throttle`). Enable with any of: `.env` at project root, env
variables, or system properties.

```bash
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/TestNG-UI.xml \
  -Dthrottle.enabled=true -Dthrottle.cpu.profile=X4 -Dthrottle.network.profile=THREE_G
```

Full reference: [`docs/throttling-cheatsheet.md`](docs/throttling-cheatsheet.md).

---

## Coding Standards

All new code must follow [`docs/CODING_STANDARDS.md`](docs/CODING_STANDARDS.md) —
naming, layering, test authoring, and the anti-pattern list.

---

## Notes & Known Behavior

- `automationexercise.com` can be flaky (`ERR_NAME_NOT_RESOLVED`, timeouts). Rerun
  on transient failures.
- `Delete_ValidTest` deletes the shared LOGIN account on the server, and
  `invalidRegisterWithExistEmail` depends on that account existing. The suite is
  order-sensitive; if the first run reports `201` where `400` is expected, the shared
  account needs to be recreated (rerun).

---

## Author

Project by **Yousef Mohamed** — yousef.mohamed.12@hotmail.com
