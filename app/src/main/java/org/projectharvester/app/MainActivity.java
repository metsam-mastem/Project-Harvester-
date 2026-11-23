package org.projectharvester.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Local-first Project Harvester workspace. Preview never starts a transfer. */
public final class MainActivity extends Activity {
    private static final int BG = Color.rgb(10, 16, 28);
    private static final int SURFACE = Color.rgb(19, 29, 46);
    private static final int SURFACE_ALT = Color.rgb(25, 39, 59);
    private static final int TEXT = Color.rgb(235, 242, 250);
    private static final int MUTED = Color.rgb(153, 170, 190);
    private static final int ACCENT = Color.rgb(94, 226, 190);
    private static final String PREFS = "project_harvester_local";
    private static final String LIBRARY_KEY = "saved_sources";

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private LinearLayout root;
    private FrameLayout screenHost;
    private String currentScreen = "home";
    private EditText urlInput;
    private TextView previewView, statusView;
    private Button previewButton, downloadButton, saveButton, openButton;
    private ProviderUrl selectedSource;
    private ProjectMetadata selectedMetadata;
    private final PreviewGate previewGate = new PreviewGate();
    private boolean receiverRegistered = false;

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
                if (id > 0) executor.execute(() -> finalizeDownload(id));
            }
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(BG);
        window.setNavigationBarColor(BG);
        window.getDecorView().setSystemUiVisibility(0);
        buildShell();
        showScreen("home");
        handleIncomingIntent(getIntent());
    }

    @Override protected void onStart() {
        super.onStart();
        if (!receiverRegistered) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            if (Build.VERSION.SDK_INT >= 33) registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            else registerReceiver(downloadReceiver, filter);
            receiverRegistered = true;
        }
        reconcilePendingDownloads();
    }

    @Override protected void onStop() {
        if (receiverRegistered) { unregisterReceiver(downloadReceiver); receiverRegistered = false; }
        super.onStop();
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void buildShell() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(18), dp(12), dp(18), dp(10));
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(dp(18) + insets.getSystemWindowInsetLeft(),
                    dp(12) + insets.getSystemWindowInsetTop(),
                    dp(18) + insets.getSystemWindowInsetRight(),
                    dp(10) + insets.getSystemWindowInsetBottom());
            return insets;
        });
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(2), dp(8), dp(2), dp(12));
        header.addView(text("LOCAL PROJECT INTELLIGENCE  /  0.2.0", 10, ACCENT, true));
        TextView title = text("Project Harvester", 27, TEXT, true);
        title.setPadding(0, dp(4), 0, 0);
        header.addView(title);
        TextView subtitle = text("Discover  ·  Inspect  ·  Approve  ·  Collect", 13, MUTED, false);
        subtitle.setPadding(0, dp(3), 0, 0);
        header.addView(subtitle);
        root.addView(header);
        ScrollView navScroll = new ScrollView(this);
        navScroll.setHorizontalScrollBarEnabled(false);
        navScroll.setVerticalScrollBarEnabled(false);
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(0, 0, 0, dp(10));
        addNavButton(nav, "Overview", "home");
        addNavButton(nav, "Discover", "discover");
        addNavButton(nav, "Downloads", "downloads");
        addNavButton(nav, "Library", "library");
        addNavButton(nav, "Settings", "settings");
        navScroll.addView(nav);
        root.addView(navScroll);
        screenHost = new FrameLayout(this);
        root.addView(screenHost, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
    }

    private void addNavButton(LinearLayout nav, String label, String screen) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(12);
        button.setTextColor(screen.equals(currentScreen) ? BG : TEXT);
        button.setMinHeight(dp(42));
        button.setPadding(dp(12), 0, dp(12), 0);
        button.setBackground(round(screen.equals(currentScreen) ? ACCENT : SURFACE_ALT, 18));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(42));
        params.setMargins(0, 0, dp(7), 0);
        nav.addView(button, params);
        button.setOnClickListener(view -> showScreen(screen));
    }

    private void showScreen(String screen) {
        currentScreen = screen;
        screenHost.removeAllViews();
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, dp(4), 0, dp(16));
        if ("home".equals(screen)) buildHome(content);
        else if ("discover".equals(screen)) buildDiscover(content);
        else if ("downloads".equals(screen)) buildDownloads(content);
        else if ("library".equals(screen)) buildLibrary(content);
        else buildSettings(content);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.addView(content);
        screenHost.addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rebuildNavigationSelection();
    }

    private void rebuildNavigationSelection() {
        if (root == null) return;
        View navScroll = root.getChildAt(1);
        if (!(navScroll instanceof ScrollView)) return;
        LinearLayout nav = (LinearLayout) ((ScrollView) navScroll).getChildAt(0);
        nav.removeAllViews();
        addNavButton(nav, "Overview", "home");
        addNavButton(nav, "Discover", "discover");
        addNavButton(nav, "Downloads", "downloads");
        addNavButton(nav, "Library", "library");
        addNavButton(nav, "Settings", "settings");
    }

    private void buildHome(LinearLayout content) {
        LinearLayout hero = card();
        hero.addView(text("PROJECT DISCOVERY WORKSPACE", 10, ACCENT, true));
        TextView headline = text("Collect projects with context, not just files.", 22, TEXT, true);
        headline.setPadding(0, dp(9), 0, dp(6));
        hero.addView(headline);
        hero.addView(text("Preview public sources first. Review provider, branch and provenance. Download only after approval.", 14, MUTED, false));
        Button start = actionButton("Start a discovery");
        hero.addView(start, fullWidth());
        start.setOnClickListener(view -> showScreen("discover"));
        content.addView(hero, verticalMargin());
        addMetric(content, "DOWNLOAD PROVIDERS", "GitHub + GitLab", "Public project archives");
        addMetric(content, "REFERENCE SOURCES", "Hackster · GitBook", "Instructables · Nature");
        addMetric(content, "LOCAL LIBRARY", Integer.toString(savedSources().size()), "Saved source links");
        addMetric(content, "INTEGRITY RECORDS", Integer.toString(manifestCount()), "Private SHA-256 manifests");
        LinearLayout flow = card();
        flow.addView(text("WORKFLOW", 10, ACCENT, true));
        flow.addView(text("01  Discover", 16, TEXT, true));
        flow.addView(text("Classify the source and validate its URL.", 13, MUTED, false));
        flow.addView(text("02  Preview", 16, TEXT, true));
        flow.addView(text("Read bounded public metadata; no archive transfer.", 13, MUTED, false));
        flow.addView(text("03  Approve", 16, TEXT, true));
        flow.addView(text("You choose whether to download or save a reference.", 13, MUTED, false));
        flow.addView(text("04  Preserve", 16, TEXT, true));
        flow.addView(text("Record source, branch, transfer status and SHA-256 when available.", 13, MUTED, false));
        content.addView(flow, verticalMargin());
    }

    private void addMetric(LinearLayout content, String label, String value, String detail) {
        LinearLayout metric = card();
        metric.addView(text(label, 10, MUTED, true));
        metric.addView(text(value, 20, TEXT, true));
        metric.addView(text(detail, 12, MUTED, false));
        content.addView(metric, verticalMargin());
    }

    private void buildDiscover(LinearLayout content) {
        LinearLayout inputCard = card();
        inputCard.addView(text("NEW SOURCE", 10, ACCENT, true));
        inputCard.addView(text("Paste a project or documentation URL", 18, TEXT, true));
        urlInput = new EditText(this);
        urlInput.setSingleLine(true);
        urlInput.setTextColor(TEXT);
        urlInput.setHintTextColor(MUTED);
        urlInput.setHint("https://github.com/OWNER/REPOSITORY");
        urlInput.setTextSize(14);
        urlInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        urlInput.setPadding(dp(12), dp(12), dp(12), dp(12));
        urlInput.setBackground(round(SURFACE_ALT, 12));
        inputCard.addView(urlInput, fullWidth());
        previewButton = actionButton("Preview source");
        inputCard.addView(previewButton, fullWidth());
        previewButton.setOnClickListener(view -> previewSource());
        statusView = text("Waiting for a source URL.", 12, MUTED, false);
        statusView.setPadding(0, dp(8), 0, 0);
        inputCard.addView(statusView);
        content.addView(inputCard, verticalMargin());

        LinearLayout previewCard = card();
        previewCard.addView(text("SOURCE PREVIEW", 10, ACCENT, true));
        previewView = text("No source selected yet. The preview stage never downloads an archive.", 14, MUTED, false);
        previewView.setPadding(0, dp(8), 0, dp(8));
        previewCard.addView(previewView);
        downloadButton = actionButton("Download approved archive");
        downloadButton.setEnabled(false);
        previewCard.addView(downloadButton, fullWidth());
        downloadButton.setOnClickListener(view -> startDownload());
        saveButton = secondaryButton("Save source to local library");
        saveButton.setEnabled(false);
        previewCard.addView(saveButton, fullWidth());
        saveButton.setOnClickListener(view -> saveCurrentSource());
        openButton = secondaryButton("Open source in browser");
        openButton.setEnabled(false);
        previewCard.addView(openButton, fullWidth());
        openButton.setOnClickListener(view -> openCurrentSource());
        content.addView(previewCard, verticalMargin());

        LinearLayout note = card();
        note.addView(text("CURRENT PROVIDER CAPABILITIES", 10, ACCENT, true));
        note.addView(text("GitHub and GitLab: public metadata preview + archive download.\n"
                + "Hackster, GitBook, Instructables and Nature: reference-only classification and local bookmarking.\n"
                + "No login automation, CAPTCHA bypass, arbitrary crawling or execution of collected code.", 13, MUTED, false));
        content.addView(note, verticalMargin());

        urlInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewGate.invalidate();
                if (previewButton != null) previewButton.setEnabled(true);
                selectedSource = null;
                selectedMetadata = null;
                if (downloadButton != null) downloadButton.setEnabled(false);
                if (saveButton != null) saveButton.setEnabled(false);
                if (openButton != null) openButton.setEnabled(false);
                if (previewView != null) previewView.setText("Preview cleared because the source URL changed.");
            }
            @Override public void afterTextChanged(android.text.Editable s) { }
        });
    }

    private void previewSource() {
        final String raw = urlInput.getText().toString().trim();
        final int request = previewGate.next();
        selectedSource = null;
        selectedMetadata = null;
        downloadButton.setEnabled(false);
        saveButton.setEnabled(false);
        openButton.setEnabled(false);
        previewView.setText("");
        statusView.setText("Validating source and checking provider metadata…");
        final ProviderUrl source;
        try { source = ProviderUrl.parse(raw); }
        catch (IllegalArgumentException exception) { statusView.setText(exception.getMessage()); return; }
        selectedSource = source;
        openButton.setEnabled(true);
        saveButton.setEnabled(true);
        if (!source.supportsArchiveDownload()) {
            previewView.setText("Provider: " + source.displayProvider() + "\nSource: " + source.sourceUrl
                    + "\nMode: Reference only\n\nThis provider is recognized, but automated page extraction and file collection are not enabled. "
                    + "You can save the source link or open it in your browser.");
            statusView.setText("Reference source classified. No network crawl or download was started.");
            return;
        }
        previewButton.setEnabled(false);
        executor.execute(() -> {
            try {
                ProjectMetadata metadata = source.provider == ProviderUrl.Provider.GITHUB
                        ? new GitHubRepositoryClient().fetch(source) : new GitLabProjectClient().fetch(source);
                mainHandler.post(() -> {
                    if (!previewGate.isCurrent(request) || urlInput == null
                            || !raw.equals(urlInput.getText().toString().trim())) return;
                    previewButton.setEnabled(true);
                    selectedSource = source;
                    selectedMetadata = metadata;
                    String description = metadata.description.isEmpty() ? "No description provided." : metadata.description;
                    previewView.setText("Provider: " + metadata.provider + "\nProject: " + metadata.fullName
                            + "\nDefault branch: " + metadata.defaultBranch + "\nVisibility: " + metadata.visibility
                            + "\nLicense: " + metadata.license + "\nArchived: " + (metadata.archived ? "Yes" : "No")
                            + "\nDescription: " + description + "\nSource: " + metadata.htmlUrl
                            + "\n\nPreview complete. No archive has been downloaded.");
                    statusView.setText(metadata.archived
                            ? "Preview ready. This project is archived; review before downloading."
                            : "Preview ready. Download remains a separate approval step.");
                    downloadButton.setEnabled(!metadata.isPrivate);
                });
            } catch (Exception exception) {
                mainHandler.post(() -> {
                    if (!previewGate.isCurrent(request)) return;
                    previewButton.setEnabled(true);
                    selectedSource = null;
                    selectedMetadata = null;
                    downloadButton.setEnabled(false);
                    saveButton.setEnabled(false);
                    openButton.setEnabled(false);
                    String message = exception.getMessage();
                    statusView.setText(message == null ? "Could not retrieve provider metadata." : message);
                });
            }
        });
    }

    private void startDownload() {
        if (selectedSource == null || selectedMetadata == null || !selectedSource.supportsArchiveDownload()) {
            statusView.setText("Preview a downloadable public project before starting a transfer.");
            downloadButton.setEnabled(false);
            return;
        }
        try {
            String archiveUrl = selectedSource.archiveUrl(selectedMetadata.defaultBranch);
            String safeProject = safeFilePart(selectedMetadata.fullName.replace('/', '-'));
            String safeBranch = safeFilePart(selectedMetadata.defaultBranch.replace('/', '-'));
            String fileName = "ProjectHarvester-" + safeProject + "-" + safeBranch + "-" + System.currentTimeMillis() + ".zip";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(archiveUrl));
            request.setTitle(fileName);
            request.setDescription("Public " + selectedMetadata.provider + " project archive");
            request.setMimeType("application/zip");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(false);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager == null) { statusView.setText("Android Download Manager is unavailable on this device."); return; }
            long id = manager.enqueue(request);
            ProjectManifestStore.recordPending(this, id, selectedSource, selectedMetadata, archiveUrl, fileName);
            downloadButton.setEnabled(false);
            statusView.setText("Transfer queued. Download ID: " + id + ". Progress is available in the Downloads panel.");
            showScreen("downloads");
        } catch (Exception exception) {
            statusView.setText("Could not queue the archive. Check network and Android Download Manager settings.");
        }
    }

    private void reconcilePendingDownloads() {
        for (Long id : ProjectManifestStore.pendingDownloadIds(this)) {
            executor.execute(() -> finalizeDownload(id));
        }
    }

    private void finalizeDownload(long id) {
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager == null) return;
        try (Cursor cursor = manager.query(new DownloadManager.Query().setFilterById(id))) {
            if (cursor == null || !cursor.moveToFirst()) return;
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            long bytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String localUri = uriIndex >= 0 ? cursor.getString(uriIndex) : null;
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String hash = null;
                try (ParcelFileDescriptor descriptor = manager.openDownloadedFile(id);
                     FileInputStream input = new FileInputStream(descriptor.getFileDescriptor())) {
                    hash = IntegrityUtils.sha256(input);
                } catch (Exception ignored) { }
                try {
                    ProjectManifestStore.recordCompletion(this, id,
                            hash == null ? "downloaded_hash_unavailable" : "downloaded_hashed",
                            bytes, hash, localUri, null);
                } catch (IOException ignored) { }
            } else if (status == DownloadManager.STATUS_FAILED) {
                int reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON));
                try {
                    ProjectManifestStore.recordCompletion(this, id, "failed", bytes, null, localUri,
                            "Android Download Manager reason: " + reason);
                } catch (IOException ignored) { }
            } else return;
        } catch (Exception ignored) { return; }
        mainHandler.post(() -> { if ("downloads".equals(currentScreen)) showScreen("downloads"); });
    }

    private void buildDownloads(LinearLayout content) {
        LinearLayout top = card();
        top.addView(text("TRANSFER QUEUE", 10, ACCENT, true));
        top.addView(text("Android Download Manager", 18, TEXT, true));
        top.addView(text("Transfers continue under Android's system scheduler. Completed archives receive a private provenance manifest and SHA-256 digest when the file is readable.", 13, MUTED, false));
        Button refresh = secondaryButton("Refresh queue");
        top.addView(refresh, fullWidth());
        refresh.setOnClickListener(view -> showScreen("downloads"));
        content.addView(top, verticalMargin());
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager == null) { content.addView(cardWithText("Download Manager unavailable", "This device does not expose the system Download Manager.")); return; }
        int count = 0;
        try (Cursor cursor = manager.query(new DownloadManager.Query())) {
            if (cursor != null) {
                int titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                int bytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                int totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                while (cursor.moveToNext() && count < 20) {
                    count++;
                    String title = titleIndex >= 0 ? cursor.getString(titleIndex) : "Archive";
                    int status = statusIndex >= 0 ? cursor.getInt(statusIndex) : -1;
                    int reason = reasonIndex >= 0 ? cursor.getInt(reasonIndex) : 0;
                    long bytes = bytesIndex >= 0 ? cursor.getLong(bytesIndex) : 0;
                    long total = totalIndex >= 0 ? cursor.getLong(totalIndex) : -1;
                    String progress = total > 0
                            ? Math.min(100, (bytes * 100L) / total) + "% · " + formatBytes(bytes) + " / " + formatBytes(total)
                            : formatBytes(bytes) + " transferred";
                    content.addView(cardWithText(title, downloadStatus(status) + "  ·  " + progress
                            + (status == DownloadManager.STATUS_FAILED ? "  ·  reason " + reason : "")), verticalMargin());
                }
            }
        } catch (Exception exception) {
            content.addView(cardWithText("Queue could not be read", "Android returned an error while reading download state."));
        }
        if (count == 0) content.addView(cardWithText("Queue is empty", "Approved archive transfers will appear here."));
        content.addView(cardWithText("Integrity records", manifestCount() + " private JSON manifests. Each record stores source, provider, branch, archive URL, transfer result and SHA-256 when available."), verticalMargin());
    }

    private void buildLibrary(LinearLayout content) {
        LinearLayout header = card();
        header.addView(text("LOCAL SOURCE LIBRARY", 10, ACCENT, true));
        header.addView(text("Saved references", 19, TEXT, true));
        header.addView(text("Links are stored only on this device. Saving a link does not download its page or attachments.", 13, MUTED, false));
        content.addView(header, verticalMargin());
        List<String> sources = new ArrayList<>(savedSources());
        if (sources.isEmpty()) { content.addView(cardWithText("Nothing saved yet", "Preview a source and choose Save to local library.")); return; }
        for (String source : sources) {
            LinearLayout item = card();
            item.addView(text(source, 13, TEXT, true));
            Button open = secondaryButton("Open in Discover");
            item.addView(open, fullWidth());
            open.setOnClickListener(view -> { showScreen("discover"); if (urlInput != null) urlInput.setText(source); });
            content.addView(item, verticalMargin());
        }
    }

    private void buildSettings(LinearLayout content) {
        content.addView(cardWithText("Runtime", "Android-native · local-first · no custom backend · no analytics or telemetry."));
        content.addView(cardWithText("Provider support", "Downloadable: public GitHub and GitLab.com projects. Reference-only: Hackster.io, GitBook, Instructables and Nature."));
        content.addView(cardWithText("Data location", "Saved source links: app-private preferences. Provenance manifests: app-private files/project-manifests/. ZIP archives: public Downloads folder."));
        content.addView(cardWithText("Safety boundaries", "No credential storage, private-repository access, CAPTCHA or anti-bot bypass, arbitrary website crawling, archive extraction, or execution of collected code."));
        content.addView(cardWithText("Build status", "The repository CI builds a debug APK and runs unit tests. Emulator and physical-device checks remain separate verification gates."));
        Button library = secondaryButton("View saved sources");
        content.addView(library, fullWidth());
        library.setOnClickListener(view -> showScreen("library"));
    }

    private LinearLayout cardWithText(String title, String body) {
        LinearLayout item = card();
        item.addView(text(title, 16, TEXT, true));
        TextView detail = text(body, 13, MUTED, false);
        detail.setPadding(0, dp(6), 0, 0);
        item.addView(detail);
        return item;
    }

    private void saveCurrentSource() {
        if (selectedSource == null) return;
        Set<String> values = savedSources();
        values.add(selectedSource.sourceUrl);
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putStringSet(LIBRARY_KEY, values).apply();
        statusView.setText("Source saved to the local library.");
        saveButton.setEnabled(false);
    }

    private void openCurrentSource() {
        if (selectedSource == null) return;
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(selectedSource.sourceUrl))); }
        catch (Exception exception) { statusView.setText("No browser is available to open this source."); }
    }

    private Set<String> savedSources() {
        Set<String> stored = getSharedPreferences(PREFS, MODE_PRIVATE).getStringSet(LIBRARY_KEY, new LinkedHashSet<>());
        return new LinkedHashSet<>(stored == null ? new LinkedHashSet<>() : stored);
    }

    private int manifestCount() {
        java.io.File directory = new java.io.File(getFilesDir(), "project-manifests");
        java.io.File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        return files == null ? 0 : files.length;
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null || !Intent.ACTION_SEND.equals(intent.getAction())) return;
        String shared = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (shared == null) return;
        Matcher matcher = Pattern.compile("https://[^\\s]+").matcher(shared);
        if (!matcher.find()) return;
        String candidate = matcher.group().replaceAll("[),.]+$", "");
        try {
            ProviderUrl.parse(candidate);
            showScreen("discover");
            if (urlInput != null) urlInput.setText(candidate);
        } catch (IllegalArgumentException ignored) { }
    }

    private String downloadStatus(int status) {
        if (status == DownloadManager.STATUS_PENDING) return "Pending";
        if (status == DownloadManager.STATUS_RUNNING) return "Downloading";
        if (status == DownloadManager.STATUS_PAUSED) return "Paused";
        if (status == DownloadManager.STATUS_SUCCESSFUL) return "Complete";
        if (status == DownloadManager.STATUS_FAILED) return "Failed";
        return "Unknown";
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) return "unknown size";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(java.util.Locale.ROOT, "%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024L * 1024L) return String.format(java.util.Locale.ROOT, "%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format(java.util.Locale.ROOT, "%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private String safeFilePart(String value) {
        String safe = value == null ? "project" : value.replaceAll("[^A-Za-z0-9._-]", "_");
        if (safe.length() > 64) safe = safe.substring(0, 64);
        return safe.isEmpty() ? "project" : safe;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(15), dp(16), dp(15));
        layout.setBackground(round(SURFACE, 18));
        return layout;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setGravity(Gravity.START); view.setLineSpacing(dp(2), 1f);
        return view;
    }

    private Button actionButton(String label) {
        Button button = new Button(this);
        button.setText(label); button.setAllCaps(false); button.setTextSize(14);
        button.setTextColor(BG); button.setMinHeight(dp(48)); button.setBackground(round(ACCENT, 12));
        return button;
    }

    private Button secondaryButton(String label) {
        Button button = new Button(this);
        button.setText(label); button.setAllCaps(false); button.setTextSize(13);
        button.setTextColor(TEXT); button.setMinHeight(dp(44)); button.setBackground(round(SURFACE_ALT, 12));
        return button;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color); drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private LinearLayout.LayoutParams fullWidth() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(10); return params;
    }

    private LinearLayout.LayoutParams verticalMargin() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(10); return params;
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override protected void onDestroy() {
        previewGate.invalidate();
        executor.shutdownNow();
        super.onDestroy();
    }
}
