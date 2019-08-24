package com.ggnome.viewer.common;

import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.webkit.WebViewAssetLoader;

/**
 * Helper class to intercept webview requests and send file from storage directory as response instead.
 */
public class StoragePathHandler implements WebViewAssetLoader.PathHandler {

    private String cacheSubDirectory;

    public StoragePathHandler(String cacheSubDirectory) {
        this.cacheSubDirectory = cacheSubDirectory;
    }

    @Nullable
    @Override
    public WebResourceResponse handle(@NonNull String path) {
        File interceptFile = new File(new File(this.cacheSubDirectory), path);

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
