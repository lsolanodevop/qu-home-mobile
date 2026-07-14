# Art Gallery — Mobile Test Automation

Automated end‑to‑end tests for the **Art Gallery** Android app
(`com.learnautomationapp`). The suite is written as a Behaviour‑Driven
Development (BDD) project: every business rule is described in plain English
inside Gherkin `.feature` files and executed with Cucumber, driving the real app
through Appium and the UiAutomator2 driver.

## What this project covers

Four scenarios are automated end‑to‑end against the running app:

| # | Scenario | What it checks | Tag |
|---|----------|----------------|-----|
| 1 | Login (happy path) | A valid user logs in and lands on the catalog. | `@scenario1` |
| 2 | Input validation | Empty or invalid credentials are rejected and access is blocked. | `@scenario2` |
| 3 | Catalog exploration | Scrolls the feed to a piece deep in the list ("Twilight Glow"), opens it and verifies its detail screen. | `@scenario3` |
| 4 | Registration (bonus) | Completes the multi‑step sign‑up, including the native date picker, and reaches the success screen. | `@scenario4` |

Scenario 2 is written as a `Scenario Outline`, so it runs the same flow across
several combinations of missing/invalid input — eight test cases in total.

## Tech stack

| Concern | Choice |
|---------|--------|
| Language | Java 17 |
| Build | Maven |
| Automation | Appium (`java-client` 10) with the UiAutomator2 driver |
| BDD | Cucumber 7 (`cucumber-java` + JUnit) |
| Design | Page Object Model, one Appium driver per test thread |

## Project structure

```
qu-home-mobile/
├─ pom.xml                     # Maven build and dependencies
├─ run-tests.ps1              # Windows helper: sets everything up and runs the suite
├─ apps/                       # the .apk lives here (not committed)
├─ docs/                       # demo video
└─ src/test/
   ├─ java/com/homtest/
   │  ├─ support/              # infrastructure
   │  │   ├─ ConfigReader      # reads config.properties, overridable from the CLI
   │  │   └─ DriverManager     # creates/holds the AndroidDriver
   │  ├─ pages/                # Page Objects (one class per screen)
   │  │   ├─ BasePage          # shared waits, typing, tapping, scrolling
   │  │   ├─ LoginPage
   │  │   ├─ GalleryPage       # catalog + item detail screen
   │  │   └─ RegistrationPage
   │  ├─ steps/                # glue between Gherkin and the Page Objects
   │  │   ├─ Hooks             # starts the driver, screenshots on failure
   │  │   ├─ LoginSteps
   │  │   ├─ GallerySteps
   │  │   └─ RegistrationSteps
   │  └─ runners/
   │      ├─ TestRunner        # runs the whole suite (real device)
   │      └─ DryRunTestRunner  # validates steps without a device
   └─ resources/
      ├─ config.properties     # Appium URL, device, APK path, credentials
      └─ features/             # the Gherkin scenarios
          ├─ login.feature
          ├─ gallery.feature
          └─ registration.feature
```

The idea is a clean separation of layers: the `.feature` files describe *what*
the app should do, the step definitions translate each sentence into actions,
the Page Objects know *how* to interact with each screen, and the `support`
classes handle the Appium driver and configuration. If a locator ever changes,
you only touch the relevant Page Object.

## Getting the things you need

- **The app (APK).** It is intentionally not committed to the repo. The
  `run-tests.ps1` script downloads it automatically into `apps/`. If you prefer
  to grab it by hand, download `app-home-test-mobile.apk` from the challenge
  release page and drop it in `apps/app-home-test-mobile.apk`.
- **Test credentials.** `johndoe@email.com` / `123` (already wired into
  `config.properties`).
- **An Android emulator or device.** Create one from Android Studio → Device
  Manager, and confirm it is connected with `adb devices`.

## Prerequisites

- JDK 17 and Maven
- Android SDK, with `ANDROID_HOME` pointing to it (the UiAutomator2 driver needs
  this to reach `adb`)
- Node LTS (20 or 22) and Appium 2 with the UiAutomator2 driver:
  ```bash
  npm install -g appium@2
  appium driver install uiautomator2@3
  ```
  Note: use a Node LTS version — Node 21 is not compatible with Appium and fails
  to start.

## How to run

On Windows, `run-tests.ps1` does the whole setup for you — it downloads the APK
if missing, checks the JDK and `ANDROID_HOME`, starts Appium if it isn't
running, and executes the suite:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
.\run-tests.ps1 -Tags "@scenario3"   # run a single scenario
.\run-tests.ps1 -DryRun              # validate without an emulator
```

Or run it manually:

```bash
# 1. Start Appium and boot an emulator/device
appium

# 2. Run the whole suite
mvn clean test

# Run one scenario by tag
mvn test -Dcucumber.filter.tags="@scenario1"

# Validate the features/steps without a device (no emulator needed)
mvn test -Dtest=DryRunTestRunner
```

A full Spanish walkthrough (prerequisites, troubleshooting, tag combinations) is
in `INSTRUCCIONES_DE_PRUEBA.txt`.

## Reports and evidence

- After a run, an HTML report is generated at
  `target/cucumber-reports/report.html` (a JSON report is produced too).
- If a scenario fails, a screenshot is automatically attached to the report.
- A short end‑to‑end recording of the registration flow is at
  [`docs/scenario4-registration-demo.mp4`](docs/scenario4-registration-demo.mp4).

## A few implementation notes

- Locators are centralised inside each Page Object and matched by
  accessibility‑id, so they are easy to maintain.
- The app is built with React Native and re‑renders often, which can make
  elements go stale mid‑interaction. `BasePage` handles this with explicit waits
  that ignore stale‑element churn and retry, which keeps the suite stable.
- A lightweight GitHub Actions workflow (`.github/workflows/ci.yml`) compiles
  the project and validates that every Gherkin step is defined on each push.
