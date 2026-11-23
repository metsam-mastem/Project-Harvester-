package org.projectharvester.app;

import android.net.Uri;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Strict source classifier. Only GitHub and GitLab are downloadable in this release. */
public final class ProviderUrl {
    public enum Provider { GITHUB, GITLAB, HACKSTER, GITBOOK, INSTRUCTABLES, NATURE }
    private static final Pattern SEGMENT = Pattern.compile("[A-Za-z0-9_.-]+");
    public final Provider provider;
    public final String sourceUrl, host, namespace, project, owner;

    private ProviderUrl(Provider provider, String sourceUrl, String host,
                        String namespace, String project, String owner) {
        this.provider = provider; this.sourceUrl = sourceUrl; this.host = host;
        this.namespace = namespace; this.project = project; this.owner = owner;
    }

    public static ProviderUrl parse(String input) {
        if (input == null || input.trim().isEmpty()) throw new IllegalArgumentException("Enter a project URL.");
        final URI uri;
        try { uri = new URI(input.trim()); }
        catch (URISyntaxException exception) { throw new IllegalArgumentException("The URL format is invalid."); }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
                || uri.getUserInfo() != null || (uri.getPort() != -1 && uri.getPort() != 443)
                || uri.getQuery() != null || uri.getFragment() != null) {
            throw new IllegalArgumentException("Use a clean HTTPS project URL without credentials or query parameters.");
        }
        String host = uri.getHost().toLowerCase(Locale.ROOT);
        String rawPath = uri.getRawPath();
        if (rawPath == null || rawPath.indexOf('%') >= 0 || rawPath.contains("//"))
            throw new IllegalArgumentException("Encoded or ambiguous URL paths are not supported.");
        String path = rawPath;
        while (path.startsWith("/")) path = path.substring(1);
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        String[] parts = path.isEmpty() ? new String[0] : path.split("/", -1);

        if ("github.com".equals(host)) {
            if (parts.length != 2) throw new IllegalArgumentException("Use a GitHub repository root URL.");
            String owner = parts[0], project = stripGitSuffix(parts[1]);
            requireSegment(owner); requireSegment(project);
            return new ProviderUrl(Provider.GITHUB, "https://github.com/" + owner + "/" + project,
                    host, owner, project, owner);
        }
        if ("gitlab.com".equals(host)) {
            if (parts.length < 2) throw new IllegalArgumentException("Use a GitLab project root URL, including its group path.");
            for (String part : parts) if (part.startsWith("-"))
                throw new IllegalArgumentException("Use the GitLab project root, not a settings or repository subpage.");
            String project = stripGitSuffix(parts[parts.length - 1]);
            requireSegment(project);
            StringBuilder namespace = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                requireSegment(parts[i]);
                if (namespace.length() > 0) namespace.append('/');
                namespace.append(parts[i]);
            }
            return new ProviderUrl(Provider.GITLAB, "https://gitlab.com/" + namespace + "/" + project,
                    host, namespace.toString(), project, null);
        }
        Provider reference = classifyReferenceHost(host);
        if (reference == null || parts.length < 1) throw new IllegalArgumentException("This source is not supported yet.");
        return new ProviderUrl(reference, "https://" + host + "/" + path, host, null, null, null);
    }

    private static Provider classifyReferenceHost(String host) {
        if (host.equals("hackster.io") || host.endsWith(".hackster.io")) return Provider.HACKSTER;
        if (host.equals("gitbook.io") || host.endsWith(".gitbook.io")
                || host.equals("gitbook.com") || host.endsWith(".gitbook.com")) return Provider.GITBOOK;
        if (host.equals("instructables.com") || host.endsWith(".instructables.com")) return Provider.INSTRUCTABLES;
        if (host.equals("nature.com") || host.endsWith(".nature.com")) return Provider.NATURE;
        return null;
    }

    private static String stripGitSuffix(String value) {
        return value.endsWith(".git") ? value.substring(0, value.length() - 4) : value;
    }
    private static void requireSegment(String value) {
        if (value == null || value.isEmpty() || ".".equals(value) || "..".equals(value)
                || !SEGMENT.matcher(value).matches())
            throw new IllegalArgumentException("The owner, namespace, or project name is invalid.");
    }
    public boolean supportsArchiveDownload() { return provider == Provider.GITHUB || provider == Provider.GITLAB; }

    public String encodedGitLabProjectPath() {
        if (provider != Provider.GITLAB) throw new IllegalStateException("Not a GitLab project.");
        try { return URLEncoder.encode(namespace + "/" + project, "UTF-8").replace("+", "%20"); }
        catch (UnsupportedEncodingException exception) { throw new IllegalStateException("UTF-8 is unavailable.", exception); }
    }
    public String metadataApiUrl() {
        if (provider == Provider.GITHUB) return "https://api.github.com/repos/" + owner + "/" + project;
        if (provider == Provider.GITLAB) return "https://gitlab.com/api/v4/projects/" + encodedGitLabProjectPath();
        throw new IllegalStateException("This source is reference-only.");
    }
    public String archiveUrl(String branch) {
        if (!supportsArchiveDownload()) throw new IllegalStateException("This source is reference-only.");
        if (branch == null || branch.trim().isEmpty()) throw new IllegalArgumentException("The default branch is missing.");
        if (provider == Provider.GITHUB)
            return "https://codeload.github.com/" + owner + "/" + project
                    + "/zip/refs/heads/" + Uri.encode(branch, "/");
        return "https://gitlab.com/api/v4/projects/" + encodedGitLabProjectPath()
                + "/repository/archive.zip?sha=" + Uri.encode(branch);
    }
    public String displayProvider() {
        switch (provider) {
            case GITHUB: return "GitHub";
            case GITLAB: return "GitLab";
            case HACKSTER: return "Hackster.io";
            case GITBOOK: return "GitBook";
            case INSTRUCTABLES: return "Instructables";
            case NATURE: return "Nature";
            default: return "Unknown";
        }
    }
}
