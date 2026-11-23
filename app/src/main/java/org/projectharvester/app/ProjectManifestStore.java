package org.projectharvester.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            manifest.put("license", metadata.license);
            manifest.put("archived", metadata.archived);
            manifest.put("archive_url", archiveUrl);
            manifest.put("file_name", fileName);
            manifest.put("download_id", downloadId);
            manifest.put("status", "queued");
            manifest.put("created_at_epoch_ms", System.currentTimeMillis());
            preferences(context).edit().putString(Long.toString(downloadId), manifest.toString()).apply();
        } catch (JSONException ignored) { }
    }

    public static List<Long> pendingDownloadIds(Context context) {
        List<Long> ids = new ArrayList<>();
        for (Map.Entry<String, ?> entry : preferences(context).getAll().entrySet()) {
            try { ids.add(Long.parseLong(entry.getKey())); }
            catch (NumberFormatException ignored) { }
        }
        return ids;
    }

    public static File recordCompletion(Context context, long downloadId, String status,
                                        long downloadedBytes, String sha256, String localUri,
                                        String failureReason) throws IOException {
        SharedPreferences prefs = preferences(context);
        String pending = prefs.getString(Long.toString(downloadId), null);
        File directory = new File(context.getFilesDir(), "project-manifests");
        File destination = new File(directory, "project-" + downloadId + ".json");
        if (pending == null && destination.isFile()) return destination;
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
        if (!directory.exists() && !directory.mkdirs())
            throw new IOException("Could not create the private manifest directory.");
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
