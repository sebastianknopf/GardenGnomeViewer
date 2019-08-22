package com.ggnome.viewer.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ggnome.viewer.R;
import com.ggnome.viewer.databinding.GridPreviewItemBinding;
import com.ggnome.viewer.task.PreviewLoaderTask;

import java.io.File;
import java.util.List;

/**
 * Grid adapter for displaying preview images and text.
 */
public final class GridPreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private OnItemClickListener itemClickListener;

    private List<String> packageFileList;

    public GridPreviewAdapter(Context context, List<String> previewFileList) {
        this.context = context;
        this.packageFileList = previewFileList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GridPreviewItemBinding previewItemBinding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.grid_preview_item, parent, false);
        return new GridPreviewItemHolder(previewItemBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GridPreviewItemHolder viewHolder = (GridPreviewItemHolder) holder;

        PreviewLoaderTask previewLoaderTask = new PreviewLoaderTask(this.context);
        previewLoaderTask.setLoadingTaskListener(new PreviewLoaderTask.PreviewLoadingTaskListener() {
            @Override
            public void onPreviewLoaded(Bitmap bitmap) {
                viewHolder.gridPreviewItemBinding.imgPreviewImage.setImageBitmap(bitmap);
                viewHolder.gridPreviewItemBinding.imgPreviewImage.setColorFilter(Color.TRANSPARENT);
                viewHolder.gridPreviewItemBinding.imgPreviewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                viewHolder.gridPreviewItemBinding.lblPreviewName.setText(getPreviewDisplayName(packageFileList.get(position)));

                viewHolder.gridPreviewItemBinding.pgbLoading.setVisibility(View.GONE);
            }
        });
        previewLoaderTask.execute(this.packageFileList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.packageFileList.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     * Generates a human-readable and clean version of the package name.
     *
     * @param packageFileName The original package file name.
     * @return The clean version of package name.
     */
    private String getPreviewDisplayName(String packageFileName) {
        File packageFile = new File(packageFileName);

        String previewDisplayName = packageFile.getName();

        // clean up and sanitize previewDisplayName
        previewDisplayName = previewDisplayName.replace('_', ' ');

        // remove file extension
        int extPos = previewDisplayName.lastIndexOf('.');
        if(extPos == -1) {
            return previewDisplayName;
        } else {
            return previewDisplayName.substring(0, extPos);
        }
    }

    /**
     * Interface for interacting with preview clicks.
     */
    public interface OnItemClickListener {

        void onItemClick(int position, String packageFileName);

    }

    /**
     * ViewHolder für preview items and click interaction.
     */
    private class GridPreviewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public GridPreviewItemBinding gridPreviewItemBinding;

        public GridPreviewItemHolder(GridPreviewItemBinding gridPreviewItemBinding) {
            super(gridPreviewItemBinding.getRoot());

            this.gridPreviewItemBinding = gridPreviewItemBinding;
            this.gridPreviewItemBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null) {
                int position = getAdapterPosition();
                String packageFileName = packageFileList.get(position);

                itemClickListener.onItemClick(position, packageFileName);
            }
        }
    }
}
