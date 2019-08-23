package com.ggnome.viewer.common;

import android.content.Context;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

/**
 * Helper class to intercept webview requests and send file from cache directory as response instead.
 */
public class CacheStoragePathHandler implements WebViewAssetLoader.PathHandler {

    private Context context;
    private String cacheSubDirectory;

    public CacheStoragePathHandler(Context context, String cacheSubDirectory) {
        this.context = context;
        this.cacheSubDirectory = cacheSubDirectory;
    }

    @Nullable
    @Override
    public WebResourceResponse handle(@NonNull String path) {
        File interceptFile = new File(new File(this.context.getCacheDir(), this.cacheSubDirectory), path);

        // when the file exists, just send it
        // otherwise return null to tell the interceptor that no matching file was found
        if(interceptFile.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(interceptFile);

                String fileExt = interceptFile.getName().substring(interceptFile.getName().lastIndexOf('.')).replace(".", "");
                return new WebResourceResponse(MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt), "UTF-8", inputStream);
            } catch(Exception e) {
            }
        }

        return null;
    }

}
