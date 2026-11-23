# Roadmap

This roadmap separates the initial implemented baseline from future work. Planned items are not current capabilities.

## 0.1.0 — Android GitHub archive baseline

- [x] Project charter and user documentation.
- [x] Android application structure.
- [x] Public GitHub repository URL validation.
- [x] Public metadata preview.
- [x] Explicit user action before archive download.
- [x] Android Download Manager integration.
- [ ] Reproducible Android build verification.
- [ ] Emulator and physical-device verification.
- [ ] End-to-end archive integrity verification.

## 0.2 — Discovery and provenance

- [ ] Structured preview model.
- [ ] Download history stored locally.
- [ ] Source URL and metadata manifest.
- [ ] SHA-256 verification after download.
- [ ] Better handling of provider errors and rate limits.
- [ ] Automated unit and instrumentation tests.

## 0.3 — Provider adapter foundation

- [ ] Provider interface and capability declarations.
- [ ] GitLab public project metadata and archive support.
- [ ] Codeberg public repository support.
- [ ] Provider-specific compliance and error handling.
- [ ] Shared URL validation test suite.

## 0.4 — User-controlled collection

- [ ] Bookmark HTML import.
- [ ] Batch preview without automatic transfer.
- [ ] Per-item approval and selection.
- [ ] Duplicate detection.
- [ ] Local catalog and search.

## 0.5 — Documentation and hardware project resources

- [ ] GitBook page capture where permitted.
- [ ] Hardware project resource discovery.
- [ ] Explicit file and attachment selection.
- [ ] Provenance and license metadata.
- [ ] Generic web reference package with bounded, compliant extraction.

## 0.6 — Download reliability

- [ ] Pause/resume behavior assessment.
- [ ] Retry and recovery policies.
- [ ] Network constraint handling.
- [ ] Range support detection and benchmarks before segmented transfer is considered.
- [ ] Storage and battery profiling.

## Release gates

Every release must pass:
1. Static source review.
2. Unit tests for URL parsing and provider behavior.
3. Build verification.
4. Android emulator tests.
5. Physical-device tests for network and storage behavior.
6. Security and privacy review.
7. Documentation consistency review.

Do not advertise a planned capability as available until its acceptance tests have passed.
