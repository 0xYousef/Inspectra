# 🧪 Full-Stack Test Automation Framework

This project is a **modular, extensible, and data-driven test automation framework** for both API and UI testing, built with **Java, TestNG, Selenium, Rest Assured, MongoDB, and Allure Reports**. It supports parallel execution, dynamic data injection, custom schema validations, and clean report generation.

---

## 📦 Project Structure

```
.
├── automation-api      # API test automation module (Rest Assured + TestNG)
├── automation-ui       # UI test automation module (Selenium + TestNG)
├── automation-data     # Data management (MongoDB integration, DTOs, test data)
├── core                # Shared core utilities, base classes, configs
├── Docs                # Documentation (optional module)
├── TESTNG-FULL.xml     # Master TestNG suite to run all modules
├── pom.xml             # Parent Maven POM
```

---

## 🚀 Modules Breakdown

### ✅ `automation-api`

* API test cases grouped by feature (account, brand, product).
* Uses Rest Assured for HTTP operations.
* JSON Schema validation in `/resources/schemas`.
* Tests: `ValidLoginTest`, `InvalidRegistrationTest`, `GetAccountInfoTest`, etc.
* Schema Validation: `brands-schema.json`, `tests.login-response-schema.json`.

### ✅ `automation-ui`

* UI test cases using Selenium and Page Object Model.
* Pages structured under `pages.testcases` and `pages.validations`.
* Locator abstraction using DTOs and JSON files.
* Tests cover register, tests.login, and interaction flows.

### ✅ `automation-data`

* DTOs: `Register`, `Login`, `ContactUs`, etc.
* MongoDB Integration: Custom repositories and mappers.
* Dynamic test data provider using TestNG’s `@DataProvider`.
* Faker utilities and random data generators.
* Expected result definitions and reusable variables.

### ✅ `core`

* Shared components across modules.
* Base classes: `BaseAPIClient`, `BaseUITest`.
* Configuration reader, Allure support, and listeners.
* `application.properties` for environment settings.
* `logback.xml` for logging config.

---

## ⚙️ Setup & Installation

### Prerequisites

* Java 23
* Maven 3.8+
* MongoDB (local)
* ChromeDriver (for UI)
* Allure CLI (for reporting)

### Clone the Repo

```bash
git clone 
cd <project-directory>
```

### Install Allure (if not already)

```bash
npm install -g allure-commandline --save-dev
```

---

## 🧪 Running Tests

### 🔹 Run All Tests (UI + API)

```bash
mvn clean test -DsuiteXmlFile=TESTNG-FULL.xml
```

### 🔹 Run API Tests Only

```bash
cd automation-api
mvn clean test -DsuiteXmlFile=TestNG-API.xml
```

### 🔹 Run UI Tests Only

```bash
cd automation-ui
mvn clean test -DsuiteXmlFile=TestNG-UI.xml
```

---

## 📊 Reporting with Allure

### Generate Allure Report

```bash
allure generate --clean
```

### Open Report in Browser

```bash
allure serve
```

Allure results are automatically cleaned and generated per module run.

---

## 🧠 Features

* ✅ **Modular Design** (clean separation by test type)
* ✅ **TestNG Parallel Execution**
* ✅ **MongoDB-Driven Test Data**
* ✅ **Schema & Status Code Validation**
* ✅ **Custom DTOs for Locators and Payloads**
* ✅ **Allure Reporting with History**
* ✅ **Reusable Variables & Providers**
* ✅ **TestNG Listeners for Logging & Screenshots**
* ✅ **CPU & Network Throttling** (CDP, opt-in)

---

## ⚙️ CPU & Network Throttling (CDP)

Simulate slow CPUs and slow/unreliable connections in Chrome/Chromium sessions
via the Chrome DevTools Protocol. Implemented in `core.throttle` and applied
automatically by `DriverFactory` when enabled.

**Enable it** (any one of these — first match wins):

```bash
# .env file (project root)
throttle.enabled=true
throttle.cpu.profile=X4
throttle.network.profile=THREE_G
```

```bash
# environment variables
export THROTTLE_ENABLED=true
export THROTTLE_CPU_PROFILE=X2
export THROTTLE_NETWORK_PROFILE=FAST_3G
```

```bash
# system properties
mvn test -Dthrottle.enabled=true -Dthrottle.cpu.profile=X4 -Dthrottle.network.profile=SLOW_3G
```

| Key | Values | Default |
|---|---|---|
| `throttle.enabled` | `true` / `false` | `false` |
| `throttle.cpu.profile` | `NORMAL`, `X2`, `X4`, `X6`, `X8`, `X10`, `X20` | `NORMAL` |
| `throttle.network.profile` | `WIFI`, `FAST_4G`, `FOUR_G`, `SLOW_4G`, `FAST_3G`, `THREE_G`, `SLOW_3G`, `TWO_G`, `EDGE`, `GPRS`, `OFFLINE` | *(unset)* |

Programmatic use:

```java
ThrottleManager throttle = new ThrottleManager(driver);
throttle.apply(CpuProfile.X4, NetworkProfile.THREE_G);
// ...
throttle.reset();
```

Full reference: [`docs/throttling-cheatsheet.md`](docs/throttling-cheatsheet.md).

---

## 🛠 Tech Stack

| Area      | Stack                                 |
|-----------|---------------------------------------|
| Language  | Java 23                               |
| Build     | Maven                                 |
| Testing   | TestNG                                |
| API       | Rest Assured                          |
| UI        | Selenium WebDriver, POM pattern       |
| Data      | MongoDB                               |
| Reports   | Allure                                |
| Utilities | Java Faker, Logback, Custom Providers |

---

## 🔐 Authentication (optional)

* JWT token is stored globally after successful tests.login.
* Reused automatically across authenticated tests.

---

## 🧹 CI-Friendly & Clean

* Results are auto-cleaned before new test runs.
* Supports multi-module CI pipelines.
* Compatible with GitHub Actions, Jenkins, and GitLab CI.

---

## 📚 Future Enhancements

* Mobile testing (Appium module)
* Docker integration for MongoDB and Selenium Grid
* Frontend validation rules schema

---

## 👨‍💻 Author

* **Project by:** *Yousef Mohamed*
* **Contact:** *[yousef.mohamed.12@hotmail.com](mailto:yousef.mohamed.12@hotmail.com)*

---
