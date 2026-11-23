# Release Verification Checklist

Do not mark a release as verified until each relevant item has evidence.

## Source and build
- [ ] Gradle sync completes with documented JDK and Android SDK.
- [ ] Debug build completes.
- [ ] Release build completes.
- [ ] APK signature is verified.
- [ ] No secrets, signing keys, or local configuration files are included.

## Functional
- [ ] Valid public GitHub repository preview succeeds.
- [ ] Invalid URL is rejected locally.
- [ ] Private or unavailable repository produces a safe error.
- [ ] Preview does not enqueue an archive download.
- [ ] Download begins only after the explicit user action.
- [ ] Downloaded ZIP opens and matches the selected default branch.

## Device
- [ ] Minimum supported Android version tested.
- [ ] Current Android version tested.
- [ ] Download Manager notification and destination tested.
- [ ] Network loss and retry behavior observed.
- [ ] Low-storage behavior observed.

## Security and privacy
- [ ] Only required permissions are declared.
- [ ] No cleartext network traffic is enabled.
- [ ] No credentials are collected or logged.
- [ ] Downloaded content is never executed.
- [ ] Source license and attribution guidance is present.

## Documentation
- [ ] README matches current behavior.
- [ ] User Setup instructions are reproducible.
- [ ] User Manual matches the UI.
- [ ] Known limitations and unverified areas are explicit.
