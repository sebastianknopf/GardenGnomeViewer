package com.ggnome.viewer.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.ggnome.viewer.helper.GardenGnomePackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Task for loading a preview image into an ImageView.
 */
public class PreviewLoaderTask extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private PreviewLoadingTaskListener loadingTaskListener;

    public PreviewLoaderTask(Context context) {
        this.context = context;
    }

    /**
     * Apply PreviewLoadingTaskListener object to react on loading finish event.
     *
     * @param loadingTaskListener The listener object.
     */
    public void setLoadingTaskListener(PreviewLoadingTaskListener loadingTaskListener) {
        this.loadingTaskListener = loadingTaskListener;
    }

    @Override
    protected Bitmap doInBackground(String... packageFileName) {
        Bitmap resultBitmap = null;

        // try to load preview image from cache
        // load from package only if there's no cached image
        String fileName = this.getFileNameWithoutExtension(packageFileName[0]);
        File cachedPreviewImage = new File(this.context.getCacheDir(), fileName + ".jpg");
        if(cachedPreviewImage.exists()) {
            resultBitmap = BitmapFactory.decodeFile(cachedPreviewImage.getAbsolutePath());
            return resultBitmap;
        } else {
            try {
                GardenGnomePackage gardenGnomePackage = new GardenGnomePackage(packageFileName[0]);
                byte[] previewImageData = gardenGnomePackage.getPreviewImageData();

                if(previewImageData != null) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeByteArray(previewImageData, 0, previewImageData.length, options);

                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inSampleSize = this.getInSampleSize(options, 450, 450);

                    options.inJustDecodeBounds = false;
                    resultBitmap = BitmapFactory.decodeByteArray(previewImageData, 0, previewImageData.length, options);

                    FileOutputStream cacheOutputStream = new FileOutputStream(cachedPreviewImage);
                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, cacheOutputStream);
                }
            } catch (IOException e) {
            }
        }

        return resultBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(this.loadingTaskListener != null) {
            this.loadingTaskListener.onPreviewLoaded(bitmap);
        }
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

    /**
     * Calculates the sample size for downscaling a bitmap.
     *
     * @param options The Bitmap-Options of the previously loaded bitmap.
     * @param reqWidth The required width.
     * @param reqHeight The required height.
     * @return The sample size for the downscaled bitmap.
     */
    private int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Interface for reacting when preview loading has finished.
     */
    public interface PreviewLoadingTaskListener {

        void onPreviewLoaded(Bitmap previewList);

    }
}
