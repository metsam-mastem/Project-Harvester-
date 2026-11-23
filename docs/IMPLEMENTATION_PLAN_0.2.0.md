
# Project Harvester 0.2.0 Implementation Record

## Goal

Upgrade the initial GitHub archive form into a local-first provider-aware Android workspace with a polished panel UI, public GitLab support, reference-only source classification, local library, queue visibility and provenance records.

## Architectural decisions

- Preserve the existing Java Android project and avoid a large UI framework migration.
- Keep the app backend-free and credential-free.
- Use fixed HTTPS provider hosts and public APIs only.
- Download GitHub/GitLab public archives only after explicit approval.
- Treat Hackster, GitBook, Instructables and Nature as reference-only until compliant retrieval paths are established.
- Delegate archive transfer to Android Download Manager.
- Record a local SHA-256 fingerprint after completion; do not label it as upstream authenticity verification.

## Implementation units

1. Repair CI's retired Android SDK package request.
2. Add failing provider-classification and SHA-256 tests.
3. Add provider-neutral URL and metadata models.
4. Add public GitLab.com metadata and archive adapter.
5. Bound GitHub/GitLab metadata response sizes and improve error handling.
6. Replace the single-form screen with Overview, Discover, Downloads, Library and Settings.
7. Add Android share intent, local bookmarking and URL-change invalidation.
8. Add completion receiver and app-private Project DNA manifests.
9. Update user, architecture, privacy, roadmap and research documentation.
10. Package APK and source kit as a CI artifact.

## Verification status

CI is authoritative for unit tests and debug APK assembly. Emulator, physical-device, interrupted-transfer and provider end-to-end tests are separate and must not be inferred from CI.
