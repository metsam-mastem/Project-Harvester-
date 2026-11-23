
# Project Harvester 0.2.0

## Added
- Five-panel Android workspace.
- Public GitLab.com project preview and default-branch archive support.
- Provider classification for Hackster.io, GitBook, Instructables and Nature as reference-only sources.
- Android Share intent intake for plain-text HTTPS links.
- Local source library.
- Download queue panel backed by Android Download Manager.
- Per-transfer private provenance manifest.
- Provider-reported license metadata in preview and provenance manifests.\n- Streaming SHA-256 digest when a completed file is accessible.
- Bounded metadata responses and provider-specific error handling.\n- Gradle 8.11.1 wrapper and weekly Dependabot update checks.
- CI source-kit and debug-APK artifact packaging.

## Changed
- Replaced the one-screen form with a dark, card-based workspace.
- Unified provider metadata into a shared model.
- Invalidated previous preview state whenever the URL changes.
- Increased app version to 0.2.0.

## Not included
- Private repository authentication, self-hosted GitLab, Hackster crawling, arbitrary website extraction, bookmark HTML import, batch discovery, file-level selection, Git history, segmented download, archive extraction or production signing.
