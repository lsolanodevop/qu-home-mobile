# apps/

Place the application under test here.

Download **app-home-test-mobile.apk** from the release page:
https://github.com/automationapptest/home-test-mobile/releases/tag/v1.0.0

and save it as:

    apps/app-home-test-mobile.apk

The APK itself is intentionally **not** committed (see `.gitignore`). The
`appPath` in `src/test/resources/config.properties` already points to this
location; override it with `-DappPath=/full/path/to.apk` if you keep it elsewhere.
