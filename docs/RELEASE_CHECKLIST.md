
# Release Checklist

## Source and scope
- [ ] Version and release notes updated.
- [ ] README, user manual, architecture, privacy and roadmap match implementation.
- [ ] No deferred feature is described as implemented.
- [ ] Third-party code and dependencies have license review.

## Automated verification
- [ ] GitHub Actions unit tests pass.
- [ ] Debug APK assembly passes.
- [ ] Source-kit and APK artifact are present.
- [ ] Dependency and workflow update checks are enabled.

## Device verification
- [ ] App launches on minimum supported API.
- [ ] All five panels render and navigate.
- [ ] Share intent handles valid and malformed content.
- [ ] GitHub public preview works.
- [ ] GitLab public preview works, including nested namespace.
- [ ] Preview alone never starts a transfer.
- [ ] Archive download completes and appears in Downloads.
- [ ] Queue handles failed and paused states.
- [ ] Manifest is written and SHA-256 is recorded when file access works.
- [ ] Portrait, landscape, small screens and system bars are checked.

## Security and privacy
- [ ] No credentials or tokens in source.
- [ ] No arbitrary crawling or provider-control bypass.
- [ ] Downloaded files are never executed.
- [ ] SHA-256 is described as a fingerprint, not provider authenticity proof.
- [ ] App-private manifest storage is documented.

A release must not be called device-verified until the device checks above have been performed and recorded.
