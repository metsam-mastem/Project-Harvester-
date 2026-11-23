# Project Charter — Project Harvester

**Document status:** 0.2.0 baseline  
**Project version:** 0.2.0  
**Product type:** Local-first project discovery, preview and collection workspace

## Purpose

Project Harvester helps users classify project sources, inspect public metadata, decide whether to collect an archive, and preserve a local provenance record. It is designed to reduce manual collection work without blind bulk downloading or uncontrolled web crawling.

## Product principles

1. Local-first: the application runs on Android and has no custom backend.
2. Preview before transfer: metadata discovery is separate from archive download.
3. Explicit user control: no automatic archive transfer or link traversal.
4. Provider transparency: every provider declares supported URL forms and capabilities.
5. Provenance: retain source URL, provider metadata, transfer state and local digest where available.
6. Least privilege: request only the Android permissions needed by the current features.
7. Safe by default: never execute downloaded source code or scripts.
8. Compliance: respect provider terms, rate limits, access controls and source licenses.

## 0.2.0 objectives

- Provide a five-panel Android workspace: Overview, Discover, Downloads, Library and Settings.
- Accept public GitHub repository URLs and public GitLab.com project URLs, including nested namespaces.
- Preview public metadata and default branch before download.
- Download a selected default-branch archive only after an explicit user action.
- Recognize Hackster.io, GitBook, Instructables and Nature as reference-only sources.
- Accept plain-text HTTPS links through Android Share.
- Save source links locally.
- Display Android Download Manager state.
- Preserve a private JSON provenance manifest and SHA-256 fingerprint when possible.
- Automate unit tests, debug APK assembly and source-kit packaging in GitHub Actions.

## Provider scope

Downloadable: public GitHub and GitLab.com repositories only.

Reference-only: Hackster.io, GitBook, Instructables and Nature. Recognition does not imply page crawling, metadata extraction or attachment retrieval.

## Out of scope

- Private repository authentication, credentials or token storage.
- Self-hosted GitLab.
- Full Git history, tags, releases or per-file selection.
- Automated scraping, CAPTCHA handling, anti-bot circumvention or access-control bypass.
- Arbitrary outbound link traversal.
- Execution, compilation or installation of collected source code.
- Cloud synchronization or a hosted backend.
- Segmented transfer, guaranteed resume or custom retry engine.
- HTML bookmark import, batch discovery and local content search.
- Archive extraction before traversal/bomb inspection is implemented.

## Success criteria

The release candidate must pass:
- unit tests for provider URL classification, archive URL construction and SHA-256;
- Android debug APK build;
- emulator and physical-device launch checks;
- GitHub and GitLab public preview tests;
- confirmation that preview never starts a transfer;
- end-to-end archive download and manifest inspection;
- source, privacy and security review.

CI success alone does not establish device verification.

## Governance

- Develop in focused branches and review through Pull Requests.
- Distinguish implemented, planned, inferred and unverified behavior.
- Do not claim a test passed unless it actually ran.
- Changes to permissions, network behavior, provider coverage or data handling require a documented review.
- Do not add analytics, telemetry or remote services without an explicit product decision.
