# User Setup and Developer Setup

## 1. Installable app status

This repository contains Android source code. A signed APK is not included in the initial source baseline. Build and device verification must be completed before distributing an APK.

## 2. Device requirements

- Android 8.0 (API 26) or later.
- Network access for public GitHub metadata and archive requests.
- Sufficient free storage for the selected ZIP archive.
- Android Download Manager enabled.

## 3. Developer prerequisites

- Android Studio with a compatible Android SDK.
- JDK 17.
- Android SDK Platform 35 (or the version specified by the app module).
- Gradle version compatible with the Android Gradle Plugin configured in the root build file.

## 4. Open and build

1. Clone or download this repository.
2. Open the repository root in Android Studio.
3. Allow Gradle sync to complete.
4. Confirm that the Android SDK platform and JDK versions match the project configuration.
5. Connect an Android device with USB debugging enabled, or start an emulator.
6. Select the app run configuration and build/run the application.

Command-line build from the repository root:

```bash
gradle :app:assembleDebug
```

If Android Studio generates a local Gradle wrapper for the project, the wrapper can be used instead:

```bash
./gradlew :app:assembleDebug
```

The generated debug APK is normally located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 5. Install the debug APK

With Android Debug Bridge available:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 6. First-run verification

- Launch the app.
- Preview a known public GitHub repository.
- Confirm that the metadata and default branch are displayed.
- Confirm that preview does not create a download.
- Start the download manually.
- Confirm the ZIP appears in Downloads and can be opened as an archive.

## 7. Release build

A production release must be signed using a maintainer-controlled keystore. Do not commit keystores, passwords, signing properties, API tokens, or private credentials. Configure signing secrets outside the repository and verify the resulting APK before publication.

## 8. Troubleshooting

- Gradle sync failure: verify JDK 17, SDK installation, and network access to dependency repositories.
- SDK missing: install the configured Android SDK platform from Android Studio SDK Manager.
- Device not detected: verify USB debugging and the host's ADB driver/permissions.
- Download failure: inspect Android Download Manager and device storage.
