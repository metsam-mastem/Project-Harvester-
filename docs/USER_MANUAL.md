# User Manual

## Project Harvester 0.1.0

This manual describes the initial Android baseline. Features not listed here are not part of the current release.

## 1. What you need

- An Android device with network access.
- Project Harvester installed from a build you trust.
- A public GitHub repository URL, for example: `https://github.com/OWNER/REPOSITORY`.

Private repositories are not supported in this release.

## 2. Discover a repository

1. Open Project Harvester.
2. Paste a public GitHub repository URL into the input field.
3. Tap **Preview repository**.
4. Wait for the metadata request to complete.
5. Review the repository name, owner, default branch, and source URL.

The preview step requests repository metadata only. It does not download the source archive.

## 3. Download the archive

1. Confirm that the preview refers to the intended repository.
2. Review the source project's license and terms before reuse.
3. Tap **Download archive**.
4. Android Download Manager handles the transfer and may display a system notification.
5. Open the device's Downloads application or file manager to locate the ZIP archive.

The filename includes the repository name and selected default branch.

## 4. Supported URL format

Supported:
- `https://github.com/OWNER/REPOSITORY`
- `https://github.com/OWNER/REPOSITORY.git`

Not supported:
- Private repository URLs.
- GitHub file or directory URLs.
- GitHub release pages.
- GitLab, Bitbucket, Codeberg, GitBook, or other providers.
- Arbitrary web pages.

## 5. Common issues

### Invalid URL
Use the public repository root URL, not a file, issue, pull request, or profile page.

### Repository not found
The repository may be private, deleted, renamed, or inaccessible. This version does not authenticate to private repositories.

### Network error
Check connectivity and retry later. Provider rate limits or temporary service errors may also prevent metadata lookup.

### Download did not start
Check Android Download Manager status, available storage, network restrictions, and whether the device permits downloads.

### Archive cannot be opened
The transfer may be incomplete or the archive may be invalid. Remove the incomplete file and retry. Do not execute files from an untrusted repository.

## 6. Safe handling

- Treat all downloaded files as untrusted.
- Inspect the project's license before copying or redistributing code.
- Do not run unknown scripts or binaries.
- Do not enter access tokens or passwords into Project Harvester.
- Do not use collected material to bypass a provider's access restrictions.

## 7. Privacy

The app sends the repository URL's owner and repository identifiers to GitHub's public metadata endpoint and requests the selected archive from GitHub's codeload service. This initial version has no Project Harvester backend and no analytics service. Network providers may process requests under their own privacy policies.

## 8. Feature status

The initial baseline does not include full Git history, multi-provider discovery, file-level selection, batch imports, local catalog search, or archive extraction.
