package com.ggnome.viewer.task;

import android.content.Context;
import android.os.AsyncTask;

import com.ggnome.viewer.helper.GardenGnomePackage;

/**
 * Task for loading a garden gnome package into temporary cache storage.
 */
public class PackageLoaderTask extends AsyncTask<String, Integer, GardenGnomePackage> {

    private Context context;
    private PackageLoaderTaskListener loaderTaskListener;

    public PackageLoaderTask(Context context) {
        this.context = context;
    }

    @Override
    protected GardenGnomePackage doInBackground(String... packageFileNames) {
        try {
            this.publishProgress(0);

            GardenGnomePackage gardenGnomePackage = new GardenGnomePackage(packageFileNames[0]);
            gardenGnomePackage.open(this.context.getCacheDir().getAbsolutePath(), new GardenGnomePackage.OpenProgressListener() {
                @Override
                public void onOpenProgressUpdated(int progress) {
                    publishProgress(progress);
                }
            });

            return gardenGnomePackage;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if(this.loaderTaskListener != null) {
            this.loaderTaskListener.onPackageProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(GardenGnomePackage gardenGnomePackage) {
        super.onPostExecute(gardenGnomePackage);

        if(this.loaderTaskListener != null) {
            this.loaderTaskListener.onPackageLoaded(gardenGnomePackage);
        }
    }

    /**
     * Apply PackageLoaderTaskListener object to react on loading finish / progress update event.
     *
     * @param loaderTaskListener The listener object.
     */
    public void setLoaderTaskListener(PackageLoaderTaskListener loaderTaskListener) {
        this.loaderTaskListener = loaderTaskListener;
    }

    /**
     * Interface for reacting when package loading has finished or progress is updated.
     */
    public interface PackageLoaderTaskListener {

        void onPackageLoaded(GardenGnomePackage gardenGnomePackage);

        void onPackageProgressUpdate(int progressValue);

    }
}
