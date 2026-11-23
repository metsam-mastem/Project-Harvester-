package org.projectharvester.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Reads public repository metadata only; it never downloads an archive. */
public final class GitHubRepositoryClient {
    public RepositoryInfo fetch(GitHubRepositoryUrl repositoryUrl) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(repositoryUrl.apiUrl()).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            connection.setRequestProperty("User-Agent", "ProjectHarvester/0.1.0");

            int status = connection.getResponseCode();
            if (status == 404) {
                throw new RepositoryException("Repository not found or not publicly accessible.");
            }
            if (status == 403 || status == 429) {
                throw new RepositoryException("The provider temporarily limited this request. Try again later.");
            }
            if (status < 200 || status >= 300) {
                throw new RepositoryException("The provider could not complete the metadata request (HTTP " + status + ").");
            }

            String body = readFully(connection.getInputStream());
            JSONObject json = new JSONObject(body);
            String fullName = json.optString("full_name", "");
            String defaultBranch = json.optString("default_branch", "");
            String htmlUrl = json.optString("html_url", repositoryUrl.sourceUrl());
            String description = json.optString("description", "");

            if (fullName.isEmpty() || defaultBranch.isEmpty()) {
                throw new RepositoryException("The provider response did not contain the required repository metadata.");
            }

            return new RepositoryInfo(fullName, defaultBranch, htmlUrl, description,
                    json.optBoolean("archived", false), json.optBoolean("private", false));
        } catch (JSONException exception) {
            throw new RepositoryException("The provider returned an unreadable metadata response.");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static String readFully(InputStream input) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
        }
        return result.toString();
    }

    public static final class RepositoryInfo {
        public final String fullName;
        public final String defaultBranch;
        public final String htmlUrl;
        public final String description;
        public final boolean archived;
        public final boolean isPrivate;

        RepositoryInfo(String fullName, String defaultBranch, String htmlUrl,
                       String description, boolean archived, boolean isPrivate) {
            this.fullName = fullName;
            this.defaultBranch = defaultBranch;
            this.htmlUrl = htmlUrl;
            this.description = description;
            this.archived = archived;
            this.isPrivate = isPrivate;
        }
    }

    public static final class RepositoryException extends IOException {
        RepositoryException(String message) {
            super(message);
        }
    }
}
