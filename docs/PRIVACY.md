
# Privacy and Data Handling

Project Harvester 0.2.0 is local-first and has no custom backend, analytics, telemetry or user account.

## Network requests

- GitHub public repository metadata is requested from api.github.com.
- GitLab.com public project metadata is requested from gitlab.com.
- Approved archives are downloaded from the provider's official archive host through Android Download Manager.
- Reference-only URLs are not fetched by the app; they are saved locally or opened in the user's browser.

## Stored data

- Saved source URLs are stored in app-private SharedPreferences.
- Pending transfer metadata is temporarily stored in app-private SharedPreferences.
- Terminal transfer manifests are stored under app-private files/project-manifests/.
- ZIP archives are stored in Android's public Downloads folder.

## Manifest fields

Manifests may contain provider, source URL, project name, default branch, description, visibility, archive URL, filename, Android download ID, byte count, status, local URI, timestamps, failure reason and SHA-256 digest.

## Not collected

The app does not request account credentials, personal access tokens, passwords, contacts, location, microphone, camera or other personal profile information. No telemetry or analytics endpoint is configured.

## User control

Users can remove saved links through Android app data controls. Downloaded ZIP files can be managed through the device's Downloads interface. App-private manifests are removed when app data is cleared or the app is uninstalled.
