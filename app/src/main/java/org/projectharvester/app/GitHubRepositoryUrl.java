package org.projectharvester.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/** Strict parser for public GitHub repository root URLs. */
public final class GitHubRepositoryUrl {
    private static final Pattern SEGMENT = Pattern.compile("[A-Za-z0-9_.-]+");

    public final String owner;
    public final String repository;

    private GitHubRepositoryUrl(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    public static GitHubRepositoryUrl parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Enter a GitHub repository URL.");
        }

        final URI uri;
        try {
            uri = new URI(input.trim());
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("The URL format is invalid.");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null
                || !"github.com".equalsIgnoreCase(uri.getHost())
                || uri.getUserInfo() != null
                || (uri.getPort() != -1 && uri.getPort() != 443)
                || uri.getQuery() != null
                || uri.getFragment() != null) {
            throw new IllegalArgumentException("Use an HTTPS URL from github.com.");
        }

        String rawPath = uri.getRawPath();
        if (rawPath == null || rawPath.indexOf('%') >= 0) {
            throw new IllegalArgumentException("The repository URL path is not supported.");
        }

        String path = rawPath;
        while (path.startsWith("/")) path = path.substring(1);
        while (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        String[] parts = path.split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Use the repository root URL: github.com/OWNER/REPOSITORY.");
        }

        String owner = parts[0];
        String repository = parts[1];
        if (repository.endsWith(".git")) {
            repository = repository.substring(0, repository.length() - 4);
        }

        if (!isValidSegment(owner) || !isValidSegment(repository)
                || ".".equals(owner) || "..".equals(owner)
                || ".".equals(repository) || "..".equals(repository)) {
            throw new IllegalArgumentException("The owner or repository name is invalid.");
        }

        return new GitHubRepositoryUrl(owner, repository);
    }

    private static boolean isValidSegment(String value) {
        return value != null && !value.isEmpty() && SEGMENT.matcher(value).matches();
    }

    public String apiUrl() {
        return "https://api.github.com/repos/" + owner + "/" + repository;
    }

    public String sourceUrl() {
        return "https://github.com/" + owner + "/" + repository;
    }
}
