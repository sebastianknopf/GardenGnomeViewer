package com.ggnome.viewer.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Decoration class for specifying a spacing between grid view items.
 */
public class GridViewItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;

    public GridViewItemSpacingDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % this.spanCount;

        outRect.left = this.spacing - column * this.spacing / this.spanCount;
        outRect.right = (column + 1) * this.spacing / this.spanCount;

        if (position < this.spanCount) {
            outRect.top = this.spacing;
        }
        outRect.bottom = this.spacing;
    }
}
