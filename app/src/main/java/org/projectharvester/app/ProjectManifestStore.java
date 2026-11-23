package org.projectharvester.app;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Stores provenance and completion evidence in app-private storage. */
public final class ProjectManifestStore {
    private static final String PREFS = "project_harvester_manifests";

    private ProjectManifestStore() { }

    public static void recordPending(Context context, long downloadId, ProviderUrl source,
                                     ProjectMetadata metadata, String archiveUrl, String fileName) {
        JSONObject manifest = new JSONObject();
        try {
            manifest.put("schema_version", 1);
            manifest.put("project_harvester_version", "0.2.0");
            manifest.put("provider", metadata.provider);
            manifest.put("source_url", source.sourceUrl);
            manifest.put("project", metadata.fullName);
            manifest.put("default_branch", metadata.defaultBranch);
            manifest.put("description", metadata.description);
            manifest.put("visibility", metadata.visibility);
            manifest.put("archived", metadata.archived);
            manifest.put("archive_url", archiveUrl);
            manifest.put("file_name", fileName);
            manifest.put("download_id", downloadId);
            manifest.put("status", "queued");
            manifest.put("created_at_epoch_ms", System.currentTimeMillis());
            preferences(context).edit().putString(Long.toString(downloadId), manifest.toString()).apply();
        } catch (JSONException ignored) {
            // JSONObject.put only throws for invalid names; fixed keys make this unreachable.
        }
    }

    public static File recordCompletion(Context context, long downloadId, String status,
                                        long downloadedBytes, String sha256, String localUri,
                                        String failureReason) throws IOException {
        SharedPreferences prefs = preferences(context);
        String pending = prefs.getString(Long.toString(downloadId), null);
        JSONObject manifest;
        try {
            manifest = pending == null ? new JSONObject() : new JSONObject(pending);
            manifest.put("download_id", downloadId);
            manifest.put("status", status);
            manifest.put("downloaded_bytes", downloadedBytes);
            manifest.put("completed_at_epoch_ms", System.currentTimeMillis());
            if (sha256 != null) manifest.put("sha256", sha256);
            if (localUri != null) manifest.put("local_uri", localUri);
            if (failureReason != null) manifest.put("failure_reason", failureReason);
        } catch (JSONException exception) {
            throw new IOException("Could not create the provenance manifest.", exception);
        }

        File directory = new File(context.getFilesDir(), "project-manifests");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create the private manifest directory.");
        }
        File destination = new File(directory, "project-" + downloadId + ".json");
        try (FileOutputStream output = new FileOutputStream(destination)) {
            output.write(manifest.toString(2).getBytes(StandardCharsets.UTF_8));
            output.getFD().sync();
        } catch (JSONException exception) {
            throw new IOException("Could not serialize the provenance manifest.", exception);
        }
        prefs.edit().remove(Long.toString(downloadId)).apply();
        return destination;
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
