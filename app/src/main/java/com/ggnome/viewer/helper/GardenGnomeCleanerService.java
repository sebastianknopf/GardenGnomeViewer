package com.ggnome.viewer.helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ggnome.viewer.common.StorageConstants;

import java.io.File;

import androidx.annotation.Nullable;

public final class GardenGnomeCleanerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // clean application directory if this has not been done before
        File startDirectory = this.getDir(StorageConstants.GGPKG_DIRECTORY, MODE_PRIVATE);
        this.cleanUpDirectory(startDirectory, true);

        // stop service after cleanup
        this.stopSelf();
    }

    /**
     * Cleans up and optionally deletes a certain directory recursively.
     *
     * @param startDirectory The directory to clean.
     * @param deleteStartDirectory Whether the directory itself should also be deleted.
     */
    private void cleanUpDirectory(File startDirectory, boolean deleteStartDirectory) {
        File[] files = startDirectory.listFiles();

        if(files != null) {
            for(File object : files) {
                if(object.isDirectory()) {
                    this.cleanUpDirectory(object, true);
                } else {
                    object.delete();
                }
            }
        }

        if(deleteStartDirectory) {
            startDirectory.delete();
        }
    }
}
