# Security Policy

## Supported baseline

Security reports are welcome for the current Android source baseline. The project currently handles public repository metadata and user-initiated ZIP downloads.

## Reporting a vulnerability

Please use GitHub's private vulnerability reporting feature for this repository when available. If it is unavailable, contact the repository maintainer through a private channel before publishing technical details. Do not include credentials, personal data, or live exploit payloads in public issues.

## Security expectations

- No passwords, access tokens, or private keys are requested or stored.
- All provider requests use HTTPS.
- Only public GitHub repository metadata is supported.
- Archive download requires an explicit user action after preview.
- Downloaded content is never executed by the app.
- Provider responses and archive contents are treated as untrusted data.
- No CAPTCHA, authentication, rate-limit, or access-control bypass is implemented.

## Maintainer response

The maintainer should acknowledge a report, reproduce it in an isolated environment, assess impact, prepare a fix, and publish an advisory when appropriate. Do not publish a fix or claim a vulnerability resolved until the relevant verification has been completed.

## Scope limitations

This policy does not imply that the app has undergone a formal penetration test, independent security audit, or production readiness review.
