package org.projectharvester.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal native Android UI. Preview performs metadata lookup only.
 * Archive transfer is enqueued only after an explicit user tap.
 */
public final class MainActivity extends Activity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private EditText urlInput;
    private TextView statusView;
    private TextView previewView;
    private Button previewButton;
    private Button downloadButton;

    private GitHubRepositoryUrl selectedUrl;
    private GitHubRepositoryClient.RepositoryInfo selectedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        content.setPadding(pad, pad, pad, pad);

        TextView heading = new TextView(this);
        heading.setText("Project Harvester");
        heading.setTextSize(26);
        heading.setPadding(0, 0, 0, dp(12));
        content.addView(heading);

        TextView intro = new TextView(this);
        intro.setText("Discover a public GitHub repository. Preview does not download files.");
        intro.setTextSize(15);
        content.addView(intro);

        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setHint("https://github.com/OWNER/REPOSITORY");
        urlInput.setInputType( android.text.InputType.TYPE_CLASS_TEXT
                | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        content.addView(urlInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        previewButton = new Button(this);
        previewButton.setText("Preview repository");
        content.addView(previewButton);

        previewView = new TextView(this);
        previewView.setTextSize(14);
        previewView.setPadding(0, dp(16), 0, dp(16));
        content.addView(previewView);

        downloadButton = new Button(this);
        downloadButton.setText("Download archive");
        downloadButton.setEnabled(false);
        content.addView(downloadButton);

        statusView = new TextView(this);
        statusView.setTextSize(14);
        statusView.setPadding(0, dp(16), 0, 0);
        content.addView(statusView);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        setContentView(scroll);

        previewButton.setOnClickListener(view -> previewRepository());
        downloadButton.setOnClickListener(view -> startDownload());
    }

    private void previewRepository() {
        selectedUrl = null;
        selectedInfo = null;
        downloadButton.setEnabled(false);
        previewView.setText("");
        statusView.setText("Checking public repository metadata…");

        final GitHubRepositoryUrl parsed;
        try {
            parsed = GitHubRepositoryUrl.parse(urlInput.getText().toString());
        } catch (IllegalArgumentException exception) {
            statusView.setText(exception.getMessage());
            return;
        }

        previewButton.setEnabled(false);
        executor.execute(() -> {
            try {
                GitHubRepositoryClient.RepositoryInfo info =
                        new GitHubRepositoryClient().fetch(parsed);
                mainHandler.post(() -> {
                    previewButton.setEnabled(true);
                    selectedUrl = parsed;
                    selectedInfo = info;
                    String description = info.description == null || info.description.trim().isEmpty()
                            ? "No description provided."
                            : info.description;
                    previewView.setText(
                            "Repository: " + info.fullName + "\n"
                            + "Default branch: " + info.defaultBranch + "\n"
                            + "Description: " + description + "\n"
                            + "Source: " + info.htmlUrl + "\n\n"
                            + "The archive has not been downloaded. Review the source and its license before continuing.");
                    statusView.setText(info.archived
                            ? "Preview ready. Notice: this repository is archived."
                            : "Preview ready. Download is optional and requires your action.");
                    downloadButton.setEnabled(!info.isPrivate);
                });
            } catch (Exception exception) {
                mainHandler.post(() -> {
                    previewButton.setEnabled(true);
                    statusView.setText(exception.getMessage() == null
                            ? "Could not retrieve repository metadata."
                            : exception.getMessage());
                });
            }
        });
    }

    private void startDownload() {
        if (selectedUrl == null || selectedInfo == null) {
            statusView.setText("Preview a repository before downloading.");
            downloadButton.setEnabled(false);
            return;
        }

        try {
            String encodedBranch = Uri.encode(selectedInfo.defaultBranch, "/");
            String archiveUrl = "https://codeload.github.com/"
                    + selectedUrl.owner + "/" + selectedUrl.repository
                    + "/zip/refs/heads/" + encodedBranch;

            String safeBranch = selectedInfo.defaultBranch.replaceAll("[^A-Za-z0-9._-]", "_");
            String fileName = "ProjectHarvester-" + selectedUrl.repository
                    + "-" + safeBranch + ".zip";

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(archiveUrl));
            request.setTitle(fileName);
            request.setDescription("Downloading a public GitHub repository archive");
            request.setMimeType("application/zip");
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(false);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager == null) {
                statusView.setText("Android Download Manager is unavailable on this device.");
                return;
            }

            long downloadId = manager.enqueue(request);
            downloadButton.setEnabled(false);
            statusView.setText("Download queued. Android Download Manager will report transfer status. ID: "
                    + downloadId);
        } catch (Exception exception) {
            statusView.setText("Could not start the download. Check storage and Download Manager settings.");
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
