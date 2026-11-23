
# User Manual — Project Harvester 0.2.0

## Panels

The app has five panels: Overview, Discover, Downloads, Library and Settings.

## Discover a source

1. Open Discover.
2. Paste a clean HTTPS project URL.
3. Select Preview source.
4. Review the provider, project name, default branch, visibility, provider-reported license and archive status. A missing license is shown as Not detected; it is not a permission to reuse the source.
5. Choose one action:
   - Download approved archive for a public GitHub or GitLab.com project.
   - Save source to local library to keep a reference on this device.
   - Open source in browser to inspect the original provider page.

Preview does not download the archive.

## Supported URL forms

- GitHub: https://github.com/OWNER/REPOSITORY
- GitLab: https://gitlab.com/GROUP/PROJECT or https://gitlab.com/GROUP/SUBGROUP/PROJECT
- Hackster: public project page URL
- GitBook: public documentation space URL
- Instructables: public tutorial URL
- Nature: public article or publication URL

The optional .git suffix is accepted for GitHub and GitLab repository URLs. Nested repository subpages are not accepted as project roots.

## Share a link from Android

Use the Android Share action in a browser or another app, then select Project Harvester. The app accepts the first HTTPS URL in shared plain text and validates it before displaying it. Shared text is treated as untrusted input.

## Downloads panel

The Downloads panel reads the Android system Download Manager queue. It shows the latest transfers and their status, transferred bytes and total size when available. Android owns background scheduling and notifications; the app does not promise segmented transfers or manual pause/resume controls.

After a transfer completes, the app attempts to calculate SHA-256 and writes a provenance manifest in app-private storage. A manifest may state that the hash is unavailable if Android cannot expose the downloaded file.

## Local Library

Saved links are stored locally in app-private preferences. Saving a reference does not copy the remote page, attachments or media.

## Settings and privacy

The Settings panel describes provider boundaries, data locations and safety rules. See Privacy for the full data-handling description.

## Important limits

- Only public GitHub and GitLab.com repositories can be downloaded.
- Hackster, GitBook, Instructables and Nature are reference-only.
- The app does not crawl arbitrary websites or bypass provider access controls.
- ZIP files are not extracted or executed.
- SHA-256 is a locally calculated fingerprint, not an upstream signature verification.
