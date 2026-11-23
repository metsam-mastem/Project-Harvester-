package org.projectharvester.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Reads metadata for public GitLab.com projects. No token or private-project access is used. */
public final class GitLabProjectClient {
    private static final int MAX_RESPONSE_BYTES = 1024 * 1024;

    public ProjectMetadata fetch(ProviderUrl source) throws IOException {
        if (source == null || source.provider != ProviderUrl.Provider.GITLAB) {
            throw new IllegalArgumentException("A GitLab project URL is required.");
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(source.metadataApiUrl()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(12000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "ProjectHarvester/0.2.0");

            int status = connection.getResponseCode();
            if (status == 404) throw new RepositoryException("GitLab project not found or not publicly accessible.");
            if (status == 401) throw new RepositoryException("GitLab requires authentication for this project.");
            if (status == 403 || status == 429) {
                throw new RepositoryException("GitLab temporarily limited this request. Try again later.");
            }
            if (status == 301 || status == 302 || status == 307 || status == 308) {
                throw new RepositoryException("This GitLab project moved. Open its current URL and preview again.");
            }
            if (status < 200 || status >= 300) {
                throw new RepositoryException("GitLab metadata request failed (HTTP " + status + ").");
            }

            long length = connection.getContentLengthLong();
            if (length > MAX_RESPONSE_BYTES) {
                throw new RepositoryException("GitLab returned an unexpectedly large metadata response.");
            }

            String body;
            try (InputStream input = connection.getInputStream()) {
                body = readLimited(input);
            }
            JSONObject json = new JSONObject(body);
            String visibility = safeString(json, "visibility", "unknown");
            if (!"public".equalsIgnoreCase(visibility)) {
                throw new RepositoryException("Only public GitLab projects are supported in this release.");
            }

            String fullName = safeString(json, "path_with_namespace", "");
            String branch = safeString(json, "default_branch", "");
            String webUrl = safeString(json, "web_url", source.sourceUrl);
            if (fullName.isEmpty() || branch.isEmpty()) {
                throw new RepositoryException("GitLab did not return a usable project name and default branch.");
            }

            return new ProjectMetadata("GitLab", fullName, branch, webUrl,
                    safeString(json, "description", ""), visibility,
                    json.optBoolean("archived", false), false);
        } catch (JSONException exception) {
            throw new RepositoryException("GitLab returned unreadable project metadata.");
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
            if (total > MAX_RESPONSE_BYTES) {
                throw new IOException("Metadata response exceeded the size limit.");
            }
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
