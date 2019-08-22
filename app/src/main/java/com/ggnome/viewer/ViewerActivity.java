package com.ggnome.viewer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ggnome.viewer.databinding.ActivityViewerBinding;
import com.ggnome.viewer.helper.GardenGnomePackage;

import java.io.File;
import java.io.IOException;

public class ViewerActivity extends AppCompatActivity {

    public static String EXTRA_PACKAGE_FILE_NAME = "EXTRA_PACKAGE_FILE_NAME";

    private ActivityViewerBinding activityViewerBinding;

    private GardenGnomePackage gardenGnomePackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityViewerBinding = DataBindingUtil.setContentView(this, R.layout.activity_viewer);

        if(this.getIntent() != null) {
            String packageFileName = this.getIntent().getStringExtra(EXTRA_PACKAGE_FILE_NAME);
            if(packageFileName != null) {
                try {
                    this.gardenGnomePackage = new GardenGnomePackage(packageFileName);

                    File internalStorage = this.getCacheDir();
                    this.gardenGnomePackage.open(internalStorage.getAbsolutePath());
                    Toast.makeText(this, "Ready", Toast.LENGTH_LONG).show();
                } catch(IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.gardenGnomePackage.close();
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
        this.activityViewerBinding.loadingPanel.setVisibility(View.GONE);
        this.activityViewerBinding.viewerPanel.setVisibility(View.VISIBLE);
    }
}
