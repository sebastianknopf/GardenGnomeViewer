package com.ggnome.viewer.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Task for cleaning up the cache directory.
 */
public final class CacheCleanerTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private boolean strictMode;

    public CacheCleanerTask(Context context) {
        this.context = context;
        this.strictMode = false;
    }

    public CacheCleanerTask(Context context, boolean strictMode) {
        this.context = context;
        this.strictMode = strictMode;
    }

    @Override
    protected Void doInBackground(String... packageFileNames) {
        // in strict mode, delete every cached file
        // in non-strict mode delete only those files, which
        // are NOT contained in the passed package file names
        if(this.strictMode) {
            File cacheDirectory = this.context.getCacheDir();
            for(File cachedFile : cacheDirectory.listFiles()) {
                cachedFile.delete();
            }
        } else {
            List<String> existingPackages = new ArrayList<>();
            for(String packageFileName : packageFileNames) {
                File packageFile = new File(packageFileName);
                existingPackages.add(this.getFileNameWithoutExtension(packageFile.getName()));
            }

            File cacheDirectory = this.context.getCacheDir();
            for(File cachedFile : cacheDirectory.listFiles()) {
                if(cachedFile.isFile()) {
                    String cachedFileName = this.getFileNameWithoutExtension(cachedFile.getName());
                    if(!existingPackages.contains(cachedFileName)) {
                        cachedFile.delete();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Extracts the extension-less file name only from a complete file path.
     *
     * @param fileName The full file name.
     * @return The extension-less file name.
     */
    private String getFileNameWithoutExtension(String fileName) {
        File file = new File(fileName);
        String resultName = file.getName();

        int extPos = resultName.lastIndexOf('.');
        if(extPos == -1) {
            return resultName;
        } else {
            return resultName.substring(0, extPos);
        }
    }

}
