package com.ggnome.viewer;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ggnome.viewer.databinding.ActivityViewerBinding;

public class ViewerActivity extends AppCompatActivity {

    private ActivityViewerBinding activityViewerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityViewerBinding = DataBindingUtil.setContentView(this, R.layout.activity_viewer);
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
