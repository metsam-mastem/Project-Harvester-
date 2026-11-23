
# Project Harvester

Project Harvester is a local-first Android workspace for discovering public project sources, previewing provider metadata, approving archive transfers, and preserving provenance records.

**Release line:** 0.2.0  
**Platform:** Android 8.0+ (API 26+)  
**Runtime:** native Android; no custom backend  
**Download providers:** public GitHub repositories and public GitLab.com projects  
**Reference providers:** Hackster.io, GitBook, Instructables and Nature

## Implemented in 0.2.0

- Five-panel workspace: Overview, Discover, Downloads, Library and Settings.
- Strict HTTPS URL classification and provider-specific validation.
- Public GitHub and GitLab.com metadata preview, including default branch and archive state.
- Reference-only classification for Hackster.io, GitBook, Instructables and Nature.
- Separate preview and explicit archive-download actions.
- Android Download Manager queue visibility and completion handling.
- Local source library and Android Share-to-Harvester plain-text link intake.
- Private JSON provenance manifest for each terminal transfer.
- Streaming SHA-256 digest after a completed archive is readable. This is a local fingerprint, not an upstream authenticity check.
- Bounded metadata responses, timeouts and provider-specific error messages.
- CI unit tests, debug APK build and downloadable source-kit artifact.

## Provider capability matrix

| Provider | Classification | Metadata preview | Archive download |
|---|---|---:|---:|
| GitHub | Repository | Yes, public | Yes, default branch ZIP |
| GitLab.com | Repository, nested groups | Yes, public | Yes, default branch ZIP |
| Hackster.io | Hardware project reference | URL classification only | No |
| GitBook | Documentation reference | URL classification only | No |
| Instructables | Tutorial reference | URL classification only | No |
| Nature | Publication reference | URL classification only | No |

Reference-only means the app stores or opens the submitted URL; it does not crawl pages, retrieve attachments, or claim to extract their contents.

## Workflow

1. Discover: paste a URL or share a text link from another Android app.
2. Classify: validate the HTTPS host and determine the provider.
3. Preview: request bounded public metadata for GitHub/GitLab only.
4. Approve: choose Download, Save to Library, or Open in Browser.
5. Preserve: completed transfers receive a private JSON manifest and SHA-256 digest when available.

Preview never starts an archive transfer.

## Repository map

- app/ — native Android application and unit tests.
- .github/workflows/android-ci.yml — tests, APK build and kit packaging.
- .github/dependabot.yml — weekly dependency and Actions update checks.
- docs/CHARTER.md — project scope and governance.
- docs/ARCHITECTURE.md — system boundaries and data flow.
- docs/PROJECT_DNA.md — provenance manifest schema.
- docs/OPEN_SOURCE_RESEARCH.md — researched open-source and platform patterns.
- docs/USER_MANUAL.md — end-user guide.
- docs/USER_SETUP.md — development setup.
- docs/ROADMAP.md — implemented and deferred capabilities.
- SECURITY.md and docs/PRIVACY.md — security and data handling.

## Build

Open the repository in Android Studio with JDK 17 and Android SDK 35, then run the app configuration. With Gradle 8.11.1 installed:

~~~bash
gradle --no-daemon :app:testDebugUnitTest
gradle --no-daemon :app:assembleDebug
~~~

The CI workflow publishes the debug APK and source kit as a GitHub Actions artifact. These are development builds, not signed production releases.

## Current limitations

- GitHub and GitLab support public projects only; no private-repository authentication.
- GitLab support is limited to gitlab.com, not self-hosted instances.
- Only the default branch ZIP is downloaded; no Git history, tags/releases or file-level selection.
- Hackster, GitBook, Instructables and Nature are reference-only; no page extraction or attachment discovery.
- No HTML bookmark-file import, batch discovery, duplicate detection, local search index, archive extraction or archive-bomb inspection.
- SHA-256 is calculated locally but is not compared with an independently trusted checksum.
- Emulator, physical-device, interrupted-download and end-to-end provider tests remain release gates.

Do not advertise deferred capabilities as implemented.

## Safety and licensing

- No bypass of authentication, CAPTCHA, anti-bot controls, rate limits or provider restrictions.
- No automatic execution of downloaded source code.
- Metadata is requested from official public provider APIs.
- ZIP archives are stored as untrusted data in Android Downloads.
- Source project licenses remain the responsibility of the source owner and user.
- No license has been declared for Project Harvester itself; all rights are reserved by default.
