package com.ggnome.viewer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.ggnome.viewer.adapter.GridPreviewAdapter;
import com.ggnome.viewer.databinding.ActivityMainBinding;
import com.ggnome.viewer.view.GridViewItemSpacingDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

public class MainActivity extends AppCompatActivity implements GridPreviewAdapter.OnItemClickListener {

    private static int PERMISSION_READ_EXTERNAL_STORAGE = 0;

    private ActivityMainBinding activityMainBinding;

    private List<String> packageFileNames;
    private GridPreviewAdapter gridPreviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        this.activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        this.packageFileNames = new ArrayList<>();
        this.gridPreviewAdapter = new GridPreviewAdapter(this, this.packageFileNames);
        this.gridPreviewAdapter.setOnItemClickListener(this);

        int spanCount = 1;
        this.activityMainBinding.contentPreviewPanel.setLayoutManager(new GridLayoutManager(this, spanCount));
        this.activityMainBinding.contentPreviewPanel.addItemDecoration(new GridViewItemSpacingDecoration(spanCount, 16));
        this.activityMainBinding.contentPreviewPanel.setAdapter(this.gridPreviewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.initializeActivityView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*CacheCleanerTask cacheCleanerTask = new CacheCleanerTask(this, !CONFIG_CACHE_IMAGES);
        cacheCleanerTask.execute(this.listToArray(this.packageFileNames));*/
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            this.showPermissionErrorPanel();
        } else {
            this.initializeActivityView();
        }
    }

    // event handler
    @Override
    public void onItemClick(int position, String packageFileName) {
        /*Intent intent = new Intent(this, ViewerActivity.class);
        intent.putExtra("EXTRA_PACKAGE_FILE_NAME", packageFileName);*/

        Uri packageFileUri = Uri.fromFile(new File(packageFileName));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(packageFileUri, "application/com.ggnome.ggpkg");

        this.startActivity(intent);
    }

    // event handler
    public void btnGrantPermissionClick(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            }
        } else {
            this.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
        }
    }

    /**
     * Display the error notification for permission issues.
     */
    private void showPermissionErrorPanel() {
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.VISIBLE);
        this.activityMainBinding.contentEmptyView.setVisibility(View.GONE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.GONE);
    }

    /**
     * Display empty panel.
     */
    private void showEmptyPanel() {
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.GONE);
        this.activityMainBinding.contentEmptyView.setVisibility(View.VISIBLE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.GONE);
    }

    /**
     * Display the GridView with preview images.
     */
    private void showGridPreviewPanel() {
        this.activityMainBinding.permissionErrorPanel.setVisibility(View.GONE);
        this.activityMainBinding.contentEmptyView.setVisibility(View.GONE);
        this.activityMainBinding.contentPreviewPanel.setVisibility(View.VISIBLE);
    }

    /**
     * Check required permissions and initializes the app view.
     */
    private void initializeActivityView() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            this.showPermissionErrorPanel();
        } else {
            if(this.loadPackageNames()) {
                this.showGridPreviewPanel();
            } else {
                this.showEmptyPanel();
            }
        }
    }

    /**
     * Loads all available GGPKG's from MediaStore and provides them in a list.
     */
    private boolean loadPackageNames() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ContentResolver contentResolver = this.getContentResolver();
            Uri contentUri = MediaStore.Files.getContentUri("external");

            String[] queryProjection = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE};
            String querySelection = "_data LIKE '%.ggpkg'";

            Cursor cursor = contentResolver.query(contentUri, queryProjection, querySelection, null, null);
            if(cursor.moveToFirst()) {
                this.packageFileNames.clear();

                boolean filesLast = true;
                while(filesLast) {
                    this.packageFileNames.add(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                    filesLast = cursor.moveToNext();
                }

                this.gridPreviewAdapter.notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

}
