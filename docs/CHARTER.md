# Project Charter — Project Harvester

**Document status:** Baseline  
**Project version:** 0.1.0  
**Product type:** Local-first project resource discovery and collection utility

## 1. Purpose

Project Harvester helps a user inspect a supported public project source, understand what will be collected, and deliberately download a selected resource package. It is intended to reduce manual collection work while preserving provenance, user control, and safe handling of downloaded files.

## 2. Problem statement

Project resources are distributed across source-code hosts and documentation sites. Users need a consistent workflow to identify a source, inspect its basic metadata, decide whether to collect it, and retain the resulting files locally. Blind bulk collection creates avoidable risks: unexpected downloads, excessive network use, unclear provenance, license uncertainty, and untrusted archives.

## 3. Product principles

1. **Local-first:** the application runs on the user's Android device and does not require a Project Harvester backend.
2. **Preview before transfer:** discovery and metadata inspection are separate from archive download.
3. **Explicit user control:** no bulk download or automatic link traversal.
4. **Provider transparency:** each provider adapter documents its supported URL forms and limitations.
5. **Provenance:** retain the original source URL and provider metadata where practical.
6. **Least privilege:** request only Android permissions needed for the current feature set.
7. **Safe by default:** never execute downloaded source code or scripts.
8. **Legal and operational compliance:** respect provider terms, rate limits, access controls, and source licenses.

## 4. Initial release objectives

- Provide a native Android application shell.
- Accept a public GitHub repository URL.
- Resolve repository metadata and default branch.
- Display a human-readable preview.
- Download the selected branch archive only after a deliberate user action.
- Delegate transfer management to Android Download Manager.
- Provide setup, user, architecture, and security documentation.

## 5. Out of scope for 0.1.0

- Private repository authentication.
- Credential storage or token collection.
- Full Git clone, commit history, tags, or release assets.
- GitLab, Codeberg, Bitbucket, GitBook, Hackster, Instructables, Nature, and arbitrary websites.
- Automated scraping, CAPTCHA handling, anti-bot circumvention, or access-control bypass.
- Automatic traversal of outbound links.
- Execution, compilation, or installation of collected source code.
- Cloud synchronization or a hosted backend.
- Claims of IDM-style segmented transfer or guaranteed resume behavior beyond Android Download Manager's documented capabilities.

## 6. Users and use cases

Primary users are individuals and technical teams who want to collect public project source archives for offline inspection, research, learning, or local development.

Typical flow:
1. Paste a supported public repository URL.
2. Request a metadata preview.
3. Review repository identity and default branch.
4. Decide whether the source is relevant and whether its license permits the intended use.
5. Start the archive download.
6. Inspect the downloaded archive using trusted local tools.

## 7. Success criteria

The initial release is considered ready for a pilot only after all of the following are evidenced:

- Android project builds reproducibly with documented tool versions.
- App launches on a supported Android version.
- Valid public GitHub URL resolves to the correct owner, repository, and default branch.
- Invalid, unsupported, private, and unavailable URLs produce clear non-sensitive errors.
- No archive request occurs during preview.
- Download begins only after explicit user action.
- Downloaded archive can be opened and its contents match the selected repository branch.
- No secrets or credentials are present in source, logs, or repository history.
- User and setup documentation matches the shipped behavior.

## 8. Governance

- Keep the default branch stable; develop changes in focused branches when the repository has a usable base commit.
- Every feature must have a clear scope, acceptance criteria, and verification evidence.
- Distinguish implemented, planned, inferred, and unverified behavior.
- Do not claim a test passed unless it was actually run.
- Changes to permissions, network behavior, provider coverage, or data handling require a documented security review.
- Do not add analytics, telemetry, or remote services without an explicit product decision.

## 9. Risk ownership

The project maintainer is responsible for provider compliance, license review, release signing, dependency updates, and responding to security reports. Users remain responsible for checking the license and trustworthiness of collected projects before reuse or execution.
