package org.projectharvester.app;

/** Provider-neutral metadata used by preview, approval, and provenance records. */
public final class ProjectMetadata {
    public final String provider, fullName, defaultBranch, htmlUrl, description, visibility, license;
    public final boolean archived, isPrivate;
    public ProjectMetadata(String provider, String fullName, String defaultBranch, String htmlUrl,
                           String description, String visibility, String license,
                           boolean archived, boolean isPrivate) {
        this.provider = provider; this.fullName = fullName; this.defaultBranch = defaultBranch;
        this.htmlUrl = htmlUrl; this.description = description == null ? "" : description;
        this.visibility = visibility == null ? "unknown" : visibility;
        this.license = license == null || license.trim().isEmpty() ? "Not detected" : license;
        this.archived = archived; this.isPrivate = isPrivate;
    }
}
