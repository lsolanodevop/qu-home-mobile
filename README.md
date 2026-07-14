+++++# **Mobile App Automation Test**

1. Download app-home-test-mobile.apk from here:
👉 **[Go to Release Page to Download](https://github.com/automationapptest/home-test-mobile/releases/tag/v1.0.0)**

2. ⚠️ **Important Setup Requirement:** You **must** download this APK to your local machine. Configure your Appium capabilities to point to your local file path (e.g., `app: "./app-home-test-mobile.apk"`). Do not point the Appium server directly to the GitHub remote URL.

3. Create in your personal github a public repository (name it for instance home-test-mobile).

4. Code requested exercises, commit and push your code and send the repository link according to the instructions given by the recruiter who contacted you.

5. Forking this repository is forbidden.

### General requisites for submission

 a. **Programming languages**
    - Java
    - Kotlin

 b. **Drivers**
    - Appium

 c. **Platforms**
    - Android.

### General test requisites
- All tests should run successfully either from IDE or command line.
- Instructions to build and run the code and tests submitted must be provided.
- The app login dummy email is johndoe@email.com and the password is 123.
- A small video of one of the scenarios running end to end can be added/requested (optional)
  
### 🎯 Scenario-Based Challenge

You are required to automate the following core business requirements. The architectural design, framework structure, locator strategies, and implementation details are entirely up to your professional judgment. 

#### Scenario 1 - User Authentication (Happy Path)
* **Goal:** Verify successful user login.
* **Business Requirement:** Log into the application using a valid user profile. Assert that the application successfully authenticates the credentials and seamlessly transitions the user to the main Art Gallery catalog view.
* *(Note: You may use these credentials for this scenario,  `johndoe@email.com` / `123`)*

#### Scenario 2 - Input Validation (Error Handling)
* **Goal:** Verify application security and user feedback.
* **Business Requirement:** Attempt to log into the application using missing inputs (empty fields) or invalid credentials. Assert that the application properly blocks access and displays the corresponding error popup or native validation alert to the user.

#### Scenario 3 - Catalog Exploration (Native Interaction)
* **Goal:** Verify feed browsing and content consistency.
* **Business Requirement:** From the main Art Gallery feed, navigate down the scrollable list of items to locate a specific art piece situated deep in the catalog (e.g., "Twilight Glow"). Select the item or verify its presence and details on the screen.

---

### 🎁 Bonus Challenge (Optional)

If you wish to showcase advanced framework capabilities or mobile engineering experience, implement the following scenario:

#### Scenario 4 - User Registration Flow
* **Goal:** Verify the account creation path.
* **Business Requirement:** Navigate to the registration screen from the login view. Complete the sign-up form fields—including interacting with the selection components—and verify that the user can successfully reach the registration success screen.



<img width="209" height="438" alt="image" src="https://github.com/user-attachments/assets/65f972c7-4914-4d7b-b276-b3cf682e9da0" />
<img width="220" height="465" alt="image" src="https://github.com/user-attachments/assets/63388620-fdbf-4bb5-9908-ce7355f69dd4" />

---

## 🥒 Implementation — Appium + Cucumber (BDD / Gherkin)

This repository implements the four scenarios above as a **Behaviour-Driven
Development** suite: business rules are written in plain-language **Gherkin**
`.feature` files and executed with **Cucumber**, driving the Android app through
**Appium** (UiAutomator2) using **Java** and a **Page Object Model**.

### Tech stack
| Concern            | Choice                                  |
|--------------------|-----------------------------------------|
| Language           | Java 17                                 |
| Build              | Maven                                   |
| Driver             | Appium `java-client` 10.x (UiAutomator2)|
| BDD                | Cucumber 7 (`cucumber-java` + JUnit)    |
| Design pattern     | Page Object Model + ThreadLocal driver  |

### Project layout
```
home-test-mobile/
├─ pom.xml
├─ apps/                                  # put app-home-test-mobile.apk here
└─ src/test/
   ├─ java/com/homtest/
   │  ├─ pages/     BasePage, LoginPage, GalleryPage, RegistrationPage
   │  ├─ steps/     Hooks + LoginSteps, GallerySteps, RegistrationSteps
   │  ├─ support/   ConfigReader, DriverManager
   │  └─ runners/   TestRunner, DryRunTestRunner
   └─ resources/
      ├─ config.properties
      └─ features/  login.feature, gallery.feature, registration.feature
```

### Scenario → Feature mapping
| Challenge scenario                | Feature file            | Tag         |
|-----------------------------------|-------------------------|-------------|
| 1 — Authentication (happy path)   | `login.feature`         | `@scenario1`|
| 2 — Input validation (errors)     | `login.feature`         | `@scenario2`|
| 3 — Catalog exploration (scroll)  | `gallery.feature`       | `@scenario3`|
| 4 — Registration flow (bonus)     | `registration.feature`  | `@scenario4`|

### Prerequisites
- JDK 17 and Maven
- Android SDK with `ANDROID_HOME` set, plus an emulator or device (`adb devices`)
- Node LTS (20/22) + Appium 2 with the UiAutomator2 driver

### How to run
On Windows, `run-tests.ps1` prepares everything (downloads the APK, checks the
JDK/`ANDROID_HOME`, starts Appium and runs the suite):
```powershell
powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
.\run-tests.ps1 -Tags "@scenario3"   # a single scenario
.\run-tests.ps1 -DryRun              # validate without a device
```

Or manually:
```bash
# 1. Start Appium and boot an Android emulator/device
appium

# 2. Run the whole suite
mvn clean test

# Run a single scenario by tag
mvn test -Dcucumber.filter.tags="@scenario3"

# Validate features/steps without a device (no emulator needed)
mvn test -Dtest=DryRunTestRunner
```
HTML report is generated at `target/cucumber-reports/report.html`. Full
step-by-step notes are in `INSTRUCCIONES_DE_PRUEBA.txt`.

> The APK is not committed — `run-tests.ps1` downloads it automatically to
> `apps/`. For a manual `mvn` run, place `app-home-test-mobile.apk` in `apps/`.

### Demo video
A short end-to-end recording of the registration flow (Scenario 4) is available
at [`docs/scenario4-registration-demo.mp4`](docs/scenario4-registration-demo.mp4).

Element locators live centrally inside each Page Object
(`src/test/java/com/homtest/pages`) and are matched by accessibility-id, so they
are easy to maintain if the app changes.

