# Architecture — Project Harvester 0.1.0

## 1. Architectural overview

The initial implementation is a single-process Android application with no custom backend.

```text
User
 |
 v
Android UI
 |
 +--> URL validation and normalization
 |
 +--> GitHub public metadata client
 |       |
 |       +--> GitHub REST API (repository metadata)
 |
 +--> Preview / explicit approval boundary
 |
 +--> Android Download Manager
         |
         +--> GitHub codeload archive endpoint
                 |
                 v
          Android Downloads storage
```

## 2. Components

### Presentation layer
Collects a repository URL, shows validation or metadata results, and exposes a separate download action. The download action remains disabled until a successful preview exists.

### URL validation
Accepts only the documented GitHub repository URL form. It extracts owner and repository segments, removes an optional `.git` suffix, rejects unexpected path structures, and encodes path segments before constructing API requests.

### Provider client
Calls the public GitHub repository metadata endpoint over HTTPS. It reads the default branch and repository identity. The initial release does not request a token and does not support private repositories.

### Approval boundary
A successful metadata response creates a preview state. It does not enqueue or fetch an archive. Only a user tap on the download action can pass the approved repository and branch to the transfer component.

### Transfer component
Uses Android Download Manager for the selected branch ZIP archive. The OS owns transfer scheduling, notification, and download lifecycle. The app does not promise segmented downloads or a specific resume policy.

### Local storage
The archive is placed in the public Downloads area using a descriptive filename. The app does not index, unpack, execute, or modify the archive in this release.

## 3. Data flow

1. User enters a URL.
2. App validates the URL locally.
3. App requests public repository metadata.
4. App displays owner, repository name, default branch, and source URL.
5. User explicitly starts download.
6. App creates a codeload archive request through Download Manager.
7. Android stores the ZIP and reports transfer state through its system UI.

## 4. Network endpoints

- Metadata: `https://api.github.com/repos/{owner}/{repo}`
- Archive: `https://codeload.github.com/{owner}/{repo}/zip/refs/heads/{branch}`

Only HTTPS is used. Redirects and availability are controlled by the provider and Android networking stack.

## 5. Trust boundaries

- Repository URL input is untrusted.
- Provider metadata is external data and must not be interpreted as executable instructions.
- Downloaded ZIP files are untrusted archives.
- The app does not run scripts, binaries, build tools, or package installers from downloaded content.
- No secrets are stored by the app.

## 6. Current limitations

- Public GitHub repositories only.
- Repository root URLs only; nested tree/blob paths are not treated as archive-selection instructions.
- Default branch only.
- ZIP snapshot only; no `.git` directory or full commit history.
- No file-level selection, archive inspection, hash ledger, or manifest in this baseline.
- No private repository or organization authentication.
- No cross-provider adapter framework yet.

## 7. Planned evolution

Provider adapters, file-level discovery, user-approved selection, integrity manifests, archive safety checks, and local database indexing may be added in later releases. Each addition must preserve the preview/approval boundary and remain independently testable.
