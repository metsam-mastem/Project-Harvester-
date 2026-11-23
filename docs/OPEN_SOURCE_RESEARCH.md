
# Open-source and Platform Research

Research date: 2026-11-23. This section records implementation patterns considered for Project Harvester. No third-party source code was copied into this repository.

## Android download reliability

**XDM — Android download manager**  
https://github.com/itsolaph/xdm

License: GPL-3.0. The repository is used only as a design reference; no XDM source code or library is included.\n\nUseful patterns documented by the project:
- persistent queue and clear per-item state;
- bounded parallelism;
- HTTP Range and segmented transfer only when the server supports it;
- ETag / Last-Modified checks before resuming;
- per-segment retry with exponential backoff;
- safe filename handling and collision avoidance.

Adopted now: explicit transfer queue visibility, unique destination filenames, bounded metadata clients and post-download digest. Deferred: segmented downloader, resume engine and persistent retry state. Project Harvester continues to delegate transfers to Android Download Manager until a separate engine is justified by tests and benchmarks.

## Adaptive Android user experience

**Android adaptive app samples (Apache-2.0)**  
https://github.com/android/adaptive-apps-samples

Useful patterns:
- compact and expanded layouts should share one information architecture;
- list-detail and supporting-pane layouts are useful on tablets;
- navigation should adapt to available width rather than duplicate screens.

Adopted now: a five-panel native navigation model with independently scrollable content and reusable cards. A Compose migration is not required for this release; the current app remains dependency-light Java Views.

**Android navigation drawer guidance**  
https://developer.android.com/develop/ui/compose/components/drawer

The drawer/rail/bar pattern informs future tablet navigation. Current 0.2.0 keeps a compact horizontal navigation strip to avoid introducing a new UI dependency.

## GitLab client and provider patterns

**OctoLab — GitLab mobile client (Apache-2.0)**  
https://github.com/secninjaz/OctoLab

The project demonstrates provider-specific repository, branch, tag, commit, release, bookmark, project browsing and theme concepts, plus clear handling of self-hosted instances and authentication. The Apache-2.0 license was inspected; no source code was copied. Project Harvester currently implements only public GitLab.com project metadata and archive retrieval; it does not implement account authentication or self-hosted hosts.

A GitLab search for Android GitLab clients also surfaced LabCoat, Gitalchemy and other mobile-client projects. These are useful reference points for provider-specific terminology and navigation, not dependencies.

## Hardware project capture

**Hackster project creation guide**  
https://help.hackster.io/en/articles/862280-how-do-i-add-create-a-new-project

Hackster's project workflow highlights difficulty, build time, license, discoverability, tags and a BOM / Things list. This informs the future Project DNA hardware schema: BOM, platform/product tags, build steps, CAD, schematics and attachment provenance.

Adopted now: Hackster URLs are recognized as reference-only. Automated scraping, attachment retrieval and BOM extraction are deferred until permitted access methods and platform terms are verified.

## Documentation capture

GitBook, Instructables and Nature are recognized as reference-only sources. Future work should preserve source URLs and bibliographic/project metadata, use open-access links where relevant, and avoid redistributing copyrighted page content or media without permission.

## Reuse policy

Before adopting any third-party library or code:
1. inspect the repository's current license and notices;
2. check maintenance activity, Android API compatibility and transitive dependencies;
3. isolate the component behind a small interface;
4. add tests around behavior relied upon by Project Harvester;
5. preserve attribution and required license notices.

No external library was added for 0.2.0.
