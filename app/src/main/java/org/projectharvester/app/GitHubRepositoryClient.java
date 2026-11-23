package org.projectharvester.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Reads bounded public GitHub repository metadata only; it never downloads an archive. */
public final class GitHubRepositoryClient {
    private static final int MAX_RESPONSE_BYTES = 1024 * 1024;

    public ProjectMetadata fetch(ProviderUrl source) throws IOException {
        if (source == null || source.provider != ProviderUrl.Provider.GITHUB) {
            throw new IllegalArgumentException("A GitHub repository URL is required.");
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(source.metadataApiUrl()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(12000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            connection.setRequestProperty("User-Agent", "ProjectHarvester/0.2.0");

            int status = connection.getResponseCode();
            if (status == 404) throw new RepositoryException("Repository not found or not publicly accessible.");
            if (status == 401) throw new RepositoryException("GitHub authentication is not supported in this release.");
            if (status == 403 || status == 429) {
                String retry = connection.getHeaderField("Retry-After");
                throw new RepositoryException(retry == null
                        ? "GitHub temporarily limited this request. Try again later."
                        : "GitHub rate limit reached. Retry after " + retry + " seconds.");
            }
            if (status == 301 || status == 302 || status == 307 || status == 308) {
                throw new RepositoryException("This repository moved or was renamed. Use its current URL.");
            }
            if (status < 200 || status >= 300) {
                throw new RepositoryException("GitHub metadata request failed (HTTP " + status + ").");
            }

            long length = connection.getContentLengthLong();
            if (length > MAX_RESPONSE_BYTES) {
                throw new RepositoryException("GitHub returned an unexpectedly large metadata response.");
            }

            String body;
            try (InputStream input = connection.getInputStream()) {
                body = readLimited(input);
            }
            JSONObject json = new JSONObject(body);
            String fullName = safeString(json, "full_name", "");
            String branch = safeString(json, "default_branch", "");
            if (fullName.isEmpty() || branch.isEmpty()) {
                throw new RepositoryException("GitHub did not return a usable repository name and default branch.");
            }
            return new ProjectMetadata("GitHub", fullName, branch,
                    safeString(json, "html_url", source.sourceUrl),
                    safeString(json, "description", ""),
                    json.optBoolean("private", false) ? "private" : "public",
                    json.optBoolean("archived", false), json.optBoolean("private", false));
        } catch (JSONException exception) {
            throw new RepositoryException("GitHub returned unreadable repository metadata.");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String readLimited(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > MAX_RESPONSE_BYTES) throw new IOException("Metadata response exceeded the size limit.");
            output.write(buffer, 0, read);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String safeString(JSONObject json, String key, String fallback) {
        String value = json.optString(key, fallback);
        return value == null || "null".equals(value) ? fallback : value;
    }

    public static final class RepositoryException extends IOException {
        RepositoryException(String message) { super(message); }
    }
}
