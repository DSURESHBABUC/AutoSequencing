# AutoSequencing — Test Automation Framework

End-to-end UI automation suite for the **TVS Motor AutoSequencing (SNS)** web application, built with Selenium WebDriver, TestNG, and ExtentReports.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Architecture](#architecture)
5. [Setup & Prerequisites](#setup--prerequisites)
6. [Configuration](#configuration)
7. [Running Tests](#running-tests)
8. [Test Suite](#test-suite)
9. [Reporting](#reporting)
10. [Email Notifications](#email-notifications)
11. [Key Classes Reference](#key-classes-reference)
12. [Known Issues & Notes](#known-issues--notes)

---

## Project Overview

AutoSequencing automates the functional verification of the TVS Motor SNS (Sequencing) portal. The suite covers:

- **Login flow** — Microsoft SSO-based authentication
- **Sequence Live** — tab navigation and filter selection
- **MPS (Master Production Schedule)** — tab and plan navigation
- **Data Maintenance** — navigation and verification of 20+ sub-screens

The framework follows the **Page Object Model (POM)** pattern, separating element locators (PageObject) from interaction logic (PageEvent).

---

## Technology Stack

| Component | Library / Version |
|---|---|
| Language | Java 17 |
| Build Tool | Maven |
| Browser Automation | Selenium Java 4.21.0 |
| Driver Management | WebDriverManager 5.8.0 |
| Test Framework | TestNG 7.10.2 |
| Reporting | ExtentReports (Spark) 5.0.9 |
| Email | Jakarta Mail 2.0.1 |
| Logging | SLF4J Simple 2.0.13 |
| Browser | Google Chrome (default) |

---

## Project Structure

```
AutoSequencing/
├── pom.xml                          # Maven build & dependency config
├── testng.xml                       # TestNG suite definition
├── reports/
│   └── TestReports.html             # Generated HTML test report
├── screenshots/                     # Auto-captured failure screenshots
└── src/
    ├── main/java/
    │   ├── base/
    │   │   └── BaseTest.java        # WebDriver lifecycle + ExtentReports setup
    │   ├── PageObject/
    │   │   ├── LoginPageElements.java   # Login page XPath constants
    │   │   └── HomePageElements.java    # Home page XPath constants
    │   ├── PageEvent/
    │   │   ├── LoginPageEvents.java         # Login interaction methods
    │   │   ├── HomePageEvents.java          # Home/navigation interaction methods
    │   │   ├── MpsPageEvents.java           # MPS tab interaction methods
    │   │   ├── SequenceLivePageEvent.java   # Sequence Live filter methods
    │   │   └── DataMantanancePageEvents.java # Data Maintenance interaction methods
    │   └── utils/
    │       ├── Constants.java               # Application URL and global constants
    │       ├── ElementFetch.java            # Generic WebElement finder utility
    │       ├── RetryAnalyzer.java           # TestNG retry logic (1 retry on failure)
    │       ├── SuiteListener.java           # Failure screenshot capture + retry wiring
    │       ├── EmailUtil.java               # SMTP email sender (multi-port fallback)
    │       └── AfterSuiteEmailReporterSMTP.java  # Post-suite email report generator
    └── test/java/
        └── QA/tests/
            └── testcase.java        # Main test class (all test methods)
```

---

## Architecture

```
testng.xml
  └── SuiteListener (ITestListener + IAnnotationTransformer)
        └── RetryAnalyzer (auto-wired to all @Test methods)

BaseTest
  ├── @BeforeTest  → ExtentReports setup
  ├── @BeforeTest  → WebDriver init + browser launch (Chrome)
  ├── @BeforeMethod → Create ExtentTest logger per test
  ├── @AfterMethod  → Log PASS/FAIL/SKIP to report
  └── @AfterTest    → driver.quit() + extent.flush()

testcase (extends BaseTest)
  └── HomePageEvents
        ├── LoginPageEvents  (login credentials)
        ├── SequenceLivePageEvent (tab + filters)
        ├── MpsPageEvents (MPS tab + plan)
        └── DataMantanancePageEvents (sub-screen navigation)
```

The browser is opened **once** per `<test>` block in `testng.xml` and shared across all test methods. Tests are chained using `dependsOnMethods` to enforce execution order.

---

## Setup & Prerequisites

1. **Java 17** — ensure `JAVA_HOME` is set.
2. **Maven 3.6+** — `mvn -version` to verify.
3. **Google Chrome** — latest stable version.
4. WebDriverManager downloads the matching ChromeDriver automatically; no manual driver setup needed.

Clone and install dependencies:

```bash
git clone <repo-url>
cd AutoSequencing
mvn clean install -DskipTests
```

---

## Configuration

### Application URL

Edit `src/main/java/utils/Constants.java`:

```java
String url = "https://uat-sns.tvsmotor.net/Autoseq/login";
```

### Login Credentials

Credentials are currently hardcoded in `HomePageEvents.loginCredentials()` and `LoginPageEvents.loginCredentials()`. Update the email and password values there before running:

```java
ele.getWebElement("XPATH", "//input[@id='i0116']").sendKeys("your-email@tvsmotor.com");
// ...
wait.until(...).sendKeys("YourPassword");
```

> **Note:** Move credentials to a properties file or environment variables before committing to version control.

### Email Notifications

Create `src/test/resources/smtp.properties` with the following keys:

```properties
mail.host=smtp.tvsmotor.com
mail.preferredPorts=587,465,25
mail.from=sender@tvsmotor.com
mail.to=recipient@tvsmotor.com
mail.username=${ENV_MAIL_USER}
mail.password=${ENV_MAIL_PASS}
mail.debug=false
```

Set environment variables `ENV_MAIL_USER` and `ENV_MAIL_PASS` at runtime, or provide plain values in the properties file.

---

## Running Tests

Run the full suite via Maven:

```bash
mvn test
```

Or directly via TestNG XML:

```bash
mvn test -DsuiteXmlFile=testng.xml
```

The suite runs with `browser=chrome` as configured in `testng.xml`. To override:

```bash
mvn test -Dbrowser=chrome
```

---

## Test Suite

All tests are in `QA.tests.testcase` and run sequentially via `dependsOnMethods` chaining.

| # | Test Method | Description |
|---|---|---|
| 1 | `loginTest` | Enters Microsoft SSO email and clicks Next |
| 2 | `signInTest` | Submits the password and completes sign-in |
| 3 | `sequenceLiveTabTest` | Navigates to the Sequence Live tab |
| 4 | `mpsTabTest` | Navigates to MPS tab and clicks Mps Plan |
| 5 | `dataMaintenanceTabTest` | Navigates to the Data Maintenance tab |
| 6 | `stockMaintenanceDetailsTest` | Opens Stock Maintenance Details screen |
| 7 | `shiftActiveDetailsTest` | Opens Shift Active Details screen |
| 8 | `conveyorHourMasterTest` | Opens Conveyor Hour Master screen |
| 9 | `conveyorActiveHourReportTest` | Opens Conveyor Active Hour Report screen |
| 10 | `conveyorLocationMasterTest` | Opens Conveyor Location Master screen |
| 11 | `modelLineBalanceTest` | Opens Model Line Balance screen |
| 12 | `stagewiseLeadtimeTest` | Opens Stagewise Leadtime screen |
| 13 | `engineSkuConveyorPriorityTest` | Opens Engine SKU Conveyor Priority screen |
| 14 | `modelLevelPrioritizationTest` | Opens Model Level Prioritization screen |
| 15 | `variantLevelPrioritizationTest` | Opens Variant Level Prioritization screen |
| 16 | `skuLevelPriorityTest` | Opens SKU Level Priority screen |
| 17 | `rejectionBufferTest` | Opens Rejection Buffer screen |
| 18 | `jigMasterTest` | Opens JIG Master screen |
| 19 | `paintPartTypeJigAllocationTest` | Opens Paint Part Type JIG Allocation screen |
| 20 | `jigDensityTest` | Opens JIG Density screen |
| 21 | `packingTypeTest` | Opens Packing Type screen |
| 22 | `paintShopStockTest` | Opens Paint Shop Stock screen |
| 23 | `partVsPartGroupTest` | Opens Part Vs Part Group screen |
| 24 | `storageLocationMasterTest` | Opens Storage Location Master screen |
| 25 | `variantDayBatchCheckTest` | Opens Variant Day Batch Check screen |
| 26 | `conveyorCapacityCheckTest` | Opens Conveyor Capacity Check screen |
| 27 | `maxDemandSkuCheckTest` | Opens Max Demand SKU Check screen |
| 28 | `openWorkorderTest` | Opens Open Workorder screen |
| 29 | `resourceGroupingTest` | Opens Resource Grouping screen |

---

## Reporting

An HTML report is generated at `reports/TestReports.html` after every run.

- Theme: Dark
- System info: HostName and UserName set to "Purna"
- Each test method gets its own entry with PASS (green) / FAIL (red) / SKIP (orange) status
- On failure, the exception message is also logged in the report

Open the report in any browser:

```bash
open reports/TestReports.html
```

### Failure Screenshots

When a test fails, `SuiteListener.onTestFailure()` automatically captures a PNG screenshot and saves it to:

```
screenshots/<testMethodName>.png
```

---

## Email Notifications

`AfterSuiteEmailReporterSMTP` implements TestNG's `IReporter` interface and sends a summary email after the suite completes. It includes:

- HTML table with per-variant pass/fail counts
- Top 5 failure messages
- Attached HTML report and optional PPT summary

`EmailUtil` handles SMTP delivery with automatic port fallback (587 → 465 → 25) and 15-second timeouts per attempt.

To enable, register the reporter in `testng.xml`:

```xml
<listeners>
    <listener class-name="utils.AfterSuiteEmailReporterSMTP"/>
</listeners>
```

---

## Key Classes Reference

### `BaseTest`
Central test base class. Manages the WebDriver instance (`public static WebDriver driver`), ExtentReports lifecycle, and browser setup. All test classes extend this.

| Method | Annotation | Purpose |
|---|---|---|
| `beforeTestMethod()` | `@BeforeTest` | Initializes ExtentSparkReporter |
| `setUpDriverOnce(browser)` | `@BeforeTest` | Launches Chrome and navigates to app URL |
| `beforeMethod(method)` | `@BeforeMethod` | Creates a new ExtentTest entry per test |
| `afterMethod(result)` | `@AfterMethod` | Logs test result to report |
| `tearDownAll()` | `@AfterTest` | Quits driver and flushes report |

### `ElementFetch`
Utility wrapper around `driver.findElement` / `driver.findElements`. Accepts a string identifier type (`XPATH`, `CSS`, `ID`, `NAME`, `TAGNAME`) and the locator value.

### `HomePageEvents`
Primary page event class used by the test suite. Contains all navigation methods:
- `loginCredentials()` — fills in SSO email
- `signIn()` — clicks the sign-in button
- `goToSequenceLive()` — navigates to Sequence Live tab
- `clickMainTab(tabName)` — generic tab click using Angular Material tab locator
- `clickMpsPlan()` — clicks the Mps Plan sub-tab
- `goToDataMaintenance()` — navigates to Data Maintenance tab
- `clickDataMaintenanceItem(itemName)` — clicks a named item in the Data Maintenance list
- `safeClick(element, name)` — JavaScript-based click with null/disabled guard

### `SequenceLivePageEvent`
Handles filter interactions on the Sequence Live screen:
- `selectPlant()`, `selectUnit()`, `selectConveyor()`, `selectShift()` — Angular Material dropdown selections
- `enterDate(date)` — fills the date input field
- `fillAllFilters()` — master method that calls all of the above

### `MpsPageEvents`
Handles MPS tab interactions and data validation:
- `clickMainTab(tabName)` — navigates to a main tab
- `clickMpsPlan()` — clicks the Mps Plan sub-tab
- `validateDataImportStatus()` — iterates table rows and checks import/validation status columns

### `RetryAnalyzer`
Implements `IRetryAnalyzer`. Retries a failed test **once** (configurable via `retryCount`).

### `SuiteListener`
Implements `ITestListener` and `IAnnotationTransformer`:
- `onTestFailure()` — captures and saves a screenshot on test failure
- `transform()` — auto-wires `RetryAnalyzer` to every `@Test` method at runtime

---

## Known Issues & Notes

- **Hardcoded credentials** — `HomePageEvents` and `LoginPageEvents` both contain the same login logic with hardcoded credentials. Consider consolidating into one class and externalizing credentials.
- **`Thread.sleep` usage** — Several methods use `Thread.sleep` for waits. These should be replaced with explicit `WebDriverWait` conditions for more reliable execution.
- **`DataMantanancePageEvents`** — Class name has a typo ("Mantanance" instead of "Maintenance"). The class duplicates methods already present in `HomePageEvents`; consider refactoring.
- **`AfterSuiteEmailReporterSMTP`** — References `Utils.EmailUtil` (capital U) while the actual package is `utils.EmailUtil` (lowercase). This will cause a compile error unless corrected.
- **Static `WebDriver`** — Using a `public static` driver field works for single-threaded runs but will cause issues if parallel execution is enabled at the method level.
