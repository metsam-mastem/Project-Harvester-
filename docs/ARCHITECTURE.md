
# Architecture — Project Harvester 0.2.0

## Runtime model

A single-process Android application; no custom backend, account system, telemetry or cloud storage.

~~~text
Android UI
  ├── Overview / Discover / Downloads / Library / Settings
  ├── Share intent intake
  └── Preview and explicit approval boundary
        ├── ProviderUrl classifier
        │     ├── GitHub
        │     ├── GitLab.com
        │     └── Reference-only: Hackster / GitBook / Instructables / Nature
        ├── GitHub metadata client
        ├── GitLab metadata client
        ├── Android Download Manager
        └── ProjectManifestStore + streaming SHA-256
~~~

## Provider boundary

ProviderUrl owns URL validation, provider classification, metadata endpoint construction and archive URL construction. It rejects non-HTTPS URLs, user-info, unexpected ports, query strings, fragments, encoded path segments and unsupported hosts.

Downloadable adapters:
- GitHub: public repository root URL; metadata from GitHub REST; ZIP from codeload.
- GitLab.com: public project root URL, including nested group namespaces; metadata from GitLab REST v4; archive from GitLab's repository archive endpoint.

Reference-only providers:
- Hackster.io
- GitBook
- Instructables
- Nature

Reference-only classification is deliberately not a crawler. It stores a URL locally or opens it in the user's browser.

## Preview and approval

Preview performs a metadata GET only. The download button remains disabled until a successful public-project response is received. Editing the URL invalidates the previous selection. A generation token prevents stale asynchronous responses from replacing the latest preview.

The transfer action rechecks the selected provider and metadata before building an archive URL. Android Download Manager owns transfer scheduling and system notifications.

## Metadata clients

Both metadata clients use HTTPS and fixed provider hosts, bounded connect/read timeouts, a 1 MiB metadata body cap, and no automatic redirects. They distinguish missing, restricted, rate-limited and moved resources. No credentials are sent.

## Transfer and provenance

Archive bytes are written by Android Download Manager to public Downloads. The app does not unpack, inspect or execute the archive.

On completion, the app attempts to stream the downloaded file through SHA-256. It stores a JSON manifest under app-private files/project-manifests/. The digest is an integrity fingerprint for later comparison; because no trusted provider checksum is currently retrieved, the digest alone does not prove authenticity.

## Local state

- Saved references: app-private SharedPreferences.
- Pending transfer metadata: app-private SharedPreferences until completion.
- Completed/failed provenance: app-private JSON files.
- Downloaded ZIP: public Android Downloads directory, managed by Android Download Manager.

## Security boundaries

- User-supplied URLs are untrusted and parsed before use.
- Only fixed HTTPS provider hosts are used for API/archive requests.
- Provider responses are bounded and treated as data.
- No secrets, tokens or passwords are stored.
- No arbitrary web crawling, CAPTCHA bypass, authentication bypass or rate-limit evasion.
- Downloaded archives are untrusted and never executed.

## Known limitations

No full Git history, tags/releases, per-file selection, segmented downloader, pause/resume controls, archive extraction, ZIP traversal/bomb inspection, HTML bookmark import, batch preview, duplicate detection, local content index, self-hosted GitLab, or hardware attachment harvesting.
