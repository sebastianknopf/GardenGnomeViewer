package com.ggnome.viewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ggnome.viewer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static int PERMISSION_READ_EXTERNAL_STORAGE = 0;

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.checkAndRequestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                this.showPermissionErrorPanel();
            }
        } else {
            this.checkAndRequestPermissions();
        }
    }

    // event handler
    public void btnGrantPermissionClick(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            this.checkAndRequestPermissions();
        } else {
            this.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        }
    }

    /**
     * Display loading panel.
     */
    private void showLoadingPanel() {
        this.activityMainBinding.loadingPanel.setVisibility(View.VISIBLE);
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.GONE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.GONE);
    }

    /**
     * Display the error notification for permission issues.
     */
    private void showPermissionErrorPanel() {
        this.activityMainBinding.loadingPanel.setVisibility(View.GONE);
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.VISIBLE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.GONE);
    }

    /**
     * Display the GridView with preview images.
     */
    private void showGridPreviewPanel() {
        this.activityMainBinding.loadingPanel.setVisibility(View.GONE);
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.GONE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.VISIBLE);
    }

    /**
     * Check required permissions and request not granted permissions if needed.
     */
    private void checkAndRequestPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            }

            this.showPermissionErrorPanel();
        } else {
            this.showLoadingPanel();
        }
    }
}
