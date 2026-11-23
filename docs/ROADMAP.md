
# Roadmap

This file distinguishes implemented capabilities from planned work.

## 0.2.0 — Provider-aware Android workspace

- [x] Five-panel native workspace: Overview, Discover, Downloads, Library, Settings.
- [x] Strict HTTPS provider classification.
- [x] Public GitHub metadata preview and default-branch ZIP.
- [x] Public GitLab.com metadata preview, nested namespaces and default-branch archive.
- [x] Reference-only classification for Hackster, GitBook, Instructables and Nature.
- [x] Preview invalidation when the URL changes.
- [x] Android Share intent for plain-text links.
- [x] Local source library with individual link removal.
- [x] Download queue visibility through Android Download Manager.
- [x] Private provenance JSON manifest after terminal transfer state.
- [x] In-app Project DNA manifest inspector.
- [x] Streaming SHA-256 fingerprint when completed file access is available.
- [x] Bounded metadata response bodies and network timeouts.
- [x] CI unit-test, debug APK and source-kit artifact workflow.
- [ ] CI green evidence for this revision.
- [ ] Emulator verification.
- [ ] Physical Android device verification.
- [ ] End-to-end GitHub and GitLab archive checks.

## 0.3 — Discovery depth

- [ ] File tree and file-level selection for GitHub and GitLab.
- [ ] Tags, releases and commit metadata.
- [ ] HTML bookmark-file import.
- [ ] Batch preview with per-item approval.
- [ ] Duplicate detection and local searchable catalog.
- [ ] Provider-neutral discovery report with inaccessible/missed resources.

## 0.4 — Hardware and documentation resources

- [ ] Hackster project metadata capture where permitted.
- [ ] Explicit BOM / Things list extraction.
- [ ] Schematic, CAD, firmware and attachment discovery.
- [ ] Instructables step and attachment discovery where permitted.
- [ ] GitBook page snapshots where permitted.
- [ ] Nature DOI, metadata and open-access link resolution.
- [ ] Per-resource license and provenance records.

## 0.5 — Reliable collection

- [ ] Pause/resume and retry behavior assessment.
- [ ] Range support detection and segmented transfer only after benchmark evidence.
- [ ] Recovery across process death and reboot.
- [ ] ZIP traversal and archive-bomb inspection before any extraction feature.
- [ ] Trusted checksum/signature comparison where providers expose reliable values.
- [ ] Storage, battery and network profiling.

## Release gates

Every release requires unit tests, reproducible build, source review, privacy/security review, emulator checks, physical-device checks and end-to-end provider tests. CI passing does not imply device validation.
