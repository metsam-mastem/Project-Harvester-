
# User Setup — Project Harvester 0.2.0

## Requirements

- Android Studio with Android SDK Platform 35 and Build Tools 35.0.0.
- JDK 17.
- Android 8.0 (API 26) or newer for runtime installation.

## Open and build

1. Clone or download this repository.
2. Open the repository root in Android Studio.
3. Allow Gradle sync to finish.
4. Select the app run configuration.
5. Connect an Android device or start an emulator.
6. Build and run the app.

Command-line verification:

~~~bash
./gradlew --no-daemon :app:testDebugUnitTest
./gradlew --no-daemon :app:assembleDebug
~~~

The debug APK is created at app/build/outputs/apk/debug/app-debug.apk.

## Download the build kit

After CI completes, open the repository Actions tab, select the latest Android CI run, and download the Project-Harvester-0.2.0-kit artifact. It contains the debug APK and source kit ZIP.

The APK is a debug build. It is not a signed production release and may show installation warnings.

## Device verification

Before relying on a build, test:
- launch and navigation across all five panels;
- share a GitHub and GitLab public project URL into the app;
- preview valid and invalid URLs;
- confirm that preview alone never starts a download;
- download a small public archive and inspect its manifest;
- test network loss and Android Download Manager failure states;
- check layout with system bars, portrait/landscape and a small phone screen.

Physical-device and emulator checks must be recorded separately from CI unit tests.
