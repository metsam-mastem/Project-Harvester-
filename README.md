# Project Harvester

Project Harvester is a local-first Android application for discovering public project resources and downloading user-selected repository archives into the device's Downloads folder.

**Current release line:** 0.1.0 (initial source baseline)  
**Primary platform:** Android  
**Initial provider:** GitHub public repositories  
**Execution model:** local Android application; no custom backend

## What it does

1. Accepts a public GitHub repository URL.
2. Retrieves public repository metadata and identifies the default branch.
3. Shows a preview before any archive is downloaded.
4. Waits for the user to select **Download archive**.
5. Uses Android's system Download Manager to download the selected branch archive.
6. Leaves downloaded source code as data. The application does not execute or install it.

## Current scope

This baseline is deliberately narrow and transparent. It supports public GitHub repository URLs in the form `https://github.com/OWNER/REPOSITORY` and the optional `.git` suffix. It does not currently support private repositories, GitLab, GitBook, Bitbucket, arbitrary website extraction, full Git history, or automatic traversal of linked resources.

The preview phase performs a metadata request only. It does not download the repository archive. Archive transfer starts only after the user presses the download button.

## Repository map

- `app/` — Android application source.
- `docs/CHARTER.md` — project charter and governance.
- `docs/ARCHITECTURE.md` — system architecture and boundaries.
- `docs/USER_SETUP.md` — development and device setup.
- `docs/USER_MANUAL.md` — end-user guide.
- `docs/ROADMAP.md` — staged delivery plan.
- `SECURITY.md` — security policy and reporting guidance.

## Quick start

See [User Setup](docs/USER_SETUP.md) for prerequisites and build instructions, then [User Manual](docs/USER_MANUAL.md) for operating the app.

## Safety principles

- Preview first; download only after explicit user action.
- Use official public provider endpoints.
- Do not bypass authentication, access controls, rate limits, CAPTCHA, or provider restrictions.
- Do not execute downloaded code.
- Do not embed credentials, tokens, or private keys.
- Preserve source attribution and respect the source project's license.
- Treat archive contents as untrusted input.

## Project status

This repository contains an initial Android source baseline and project documentation. A successful Android build, APK installation, device test, and end-to-end download test have **not yet been recorded**. See [Roadmap](docs/ROADMAP.md) for the next verification gates.

## License

No project license has been declared yet. Until a license is added by the project owner, all rights are reserved by default. The presence of source files does not grant permission to redistribute or reuse them.
