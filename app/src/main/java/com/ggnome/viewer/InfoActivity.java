package com.ggnome.viewer;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ggnome.viewer.databinding.ActivityInfoBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.webkit.WebViewAssetLoader;

public class InfoActivity extends AppCompatActivity {

    private ActivityInfoBinding activityInfoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activityInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_info);

        // create up arrow
        if(this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // load information from asset
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/info/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        this.activityInfoBinding.webViewInfoDisplay.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        this.activityInfoBinding.webViewInfoDisplay.loadUrl("https://appassets.androidplatform.net/info/html_ggnome_viewer.html");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
