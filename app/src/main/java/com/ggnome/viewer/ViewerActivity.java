package com.ggnome.viewer;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ggnome.viewer.common.CacheStoragePathHandler;
import com.ggnome.viewer.databinding.ActivityViewerBinding;
import com.ggnome.viewer.helper.GardenGnomePackage;
import com.ggnome.viewer.task.PackageLoaderTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.webkit.WebViewAssetLoader;

public class ViewerActivity extends AppCompatActivity {

    public static String EXTRA_PACKAGE_FILE_NAME = "EXTRA_PACKAGE_FILE_NAME";

    private ActivityViewerBinding activityViewerBinding;

    private PackageLoaderTask packageLoaderTask;
    private GardenGnomePackage gardenGnomePackage;
    private boolean isFullscreenMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityViewerBinding = DataBindingUtil.setContentView(this, R.layout.activity_viewer);

        if(this.getIntent() != null) {
            String packageFileName = this.getIntent().getData().getPath();
            if(packageFileName != null) {
                this.packageLoaderTask = new PackageLoaderTask(this);
                this.packageLoaderTask.setLoaderTaskListener(new PackageLoaderTask.PackageLoaderTaskListener() {
                    @Override
                    public void onPackageLoaded(GardenGnomePackage packageObject) {
                        gardenGnomePackage = packageObject;

                        if(gardenGnomePackage != null) {
                            showViewerPanel();
                        } else {
                            showErrorAlert();
                        }
                    }

                    @Override
                    public void onPackageProgressUpdate(int progressValue) {
                        updateProgress(progressValue);
                    }
                });

                this.packageLoaderTask.execute(packageFileName);
            }
        }

        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final WebViewAssetLoader webViewAssetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/ggpkg/", new CacheStoragePathHandler(this, "ggpkg"))
                .build();

        this.activityViewerBinding.viewerPanel.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return webViewAssetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        this.activityViewerBinding.viewerPanel.getSettings().setJavaScriptEnabled(true);

        this.showLoadingPanel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(this.packageLoaderTask.getStatus() == AsyncTask.Status.RUNNING || this.packageLoaderTask.getStatus() == AsyncTask.Status.PENDING) {
            this.packageLoaderTask.cancel(true);
        }

        if(this.gardenGnomePackage != null) {
            this.gardenGnomePackage.close();
        }
    }

    @Override
    public void onBackPressed() {
        if(this.isFullscreenMode) {
            this.leaveFullscreenMode();
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu_viewer, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            case R.id.menuViewerFullscreen:
                this.enterFullscreenMode();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus && this.isFullscreenMode) {
            this.enterFullscreenMode();
        }
    }

    /**
     * Display loading panel.
     */
    private void showLoadingPanel() {
        this.activityViewerBinding.loadingPanel.setVisibility(View.VISIBLE);
        this.activityViewerBinding.viewerPanel.setVisibility(View.GONE);
    }

    /**
     * Display web viewer panel.
     */
    private void showViewerPanel() {
        this.activityViewerBinding.viewerPanel.loadUrl("https://appassets.androidplatform.net/ggpkg/index.html");

        this.activityViewerBinding.loadingPanel.setVisibility(View.GONE);
        this.activityViewerBinding.viewerPanel.setVisibility(View.VISIBLE);
    }

    /**
     * Display a simple error alert.
     */
    private void showErrorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.activity_viewer_package_error);
        builder.setPositiveButton(R.string.str_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    /**
     * Enters the activity immersive fullscreen mode.
     */
    private void enterFullscreenMode() {
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // set content to appear below status bar
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );


        // keep layout resizing for system bars
        this.activityViewerBinding.fitSystemWindowsLayout.setFit(false);
        this.isFullscreenMode = true;
    }

    /**
     * Leaves the activity immersive fullscreen mode.
     */
    private void leaveFullscreenMode() {
        View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // keep layout resizing for system bars
        this.activityViewerBinding.fitSystemWindowsLayout.setFit(true);
        this.isFullscreenMode = false;
    }

    /**
     * Updates the progress in progress bar and text view.
     *
     * @param progress The current progress to display.
     */
    private void updateProgress(int progress) {
        String updateText = this.getResources().getString(R.string.str_percent, progress);
        this.activityViewerBinding.lblLoadingProgress.setText(updateText);
        this.activityViewerBinding.pgbLoading.setProgress(progress);
    }

}
