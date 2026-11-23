# Privacy and Network Behavior

Project Harvester 0.1.0 has no custom server, analytics SDK, advertising SDK, or account system.

When a user requests a preview, the app sends the public GitHub owner/repository path to GitHub's public repository metadata API. When the user explicitly starts a download, Android Download Manager requests the selected branch archive from GitHub's codeload service.

The app does not request a GitHub password, personal access token, or other credential. It does not upload local files. It does not automatically traverse links found in repository descriptions or source files.

Android and network providers may retain connection metadata under their own policies. Users should review the privacy terms of the services they use.

Future analytics, cloud synchronization, account features, or additional data collection require a separate documented product and privacy decision.
