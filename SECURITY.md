# Security Policy

## Supported baseline

Security reports are welcome for Project Harvester 0.2.0. The app classifies public project URLs, requests public GitHub/GitLab metadata and queues user-approved public ZIP archives through Android Download Manager.

## Reporting a vulnerability

Use GitHub's private vulnerability reporting feature for this repository when available. If unavailable, contact the repository maintainer privately before publishing technical details. Do not include credentials, personal data or live exploit payloads in public issues.

## Security expectations

- No passwords, access tokens or private keys are requested or stored.
- Provider requests use HTTPS and fixed public hosts.
- Public GitHub and GitLab.com projects only.
- Metadata response bodies are capped at 1 MiB and network requests have connect/read timeouts.
- Archive download requires an explicit user action after preview.
- Changing the source URL invalidates the previous preview selection.
- Downloaded content is never executed or extracted by the app.
- Provider responses and archive contents are untrusted data.
- No CAPTCHA, authentication, rate-limit or access-control bypass is implemented.
- SHA-256 is a local fingerprint, not proof of upstream authenticity.

## Maintainer response

The maintainer should acknowledge a report, reproduce it in an isolated environment, assess impact, prepare a fix and publish an advisory when appropriate. Do not publish a fix or claim a vulnerability resolved until the relevant verification has completed.

## Scope limitations

This policy does not imply a formal penetration test, independent security audit, production readiness review or verification on every Android device.
