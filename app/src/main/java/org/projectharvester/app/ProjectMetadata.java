package org.projectharvester.app;

/** Provider-neutral metadata used by preview, download approval, and provenance records. */
public final class ProjectMetadata {
    public final String provider;
    public final String fullName;
    public final String defaultBranch;
    public final String htmlUrl;
    public final String description;
    public final String visibility;
    public final boolean archived;
    public final boolean isPrivate;

    public ProjectMetadata(String provider, String fullName, String defaultBranch,
                           String htmlUrl, String description, String visibility,
                           boolean archived, boolean isPrivate) {
        this.provider = provider;
        this.fullName = fullName;
        this.defaultBranch = defaultBranch;
        this.htmlUrl = htmlUrl;
        this.description = description == null ? "" : description;
        this.visibility = visibility == null ? "unknown" : visibility;
        this.archived = archived;
        this.isPrivate = isPrivate;
    }
}
