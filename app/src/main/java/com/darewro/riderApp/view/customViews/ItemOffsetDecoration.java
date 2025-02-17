package com.darewro.riderApp.view.customViews;

import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.darewro.riderApp.App;
import com.darewro.riderApp.R;

/**
 * Created by KMajeed on 09/08/2018.
 */

public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;
    private int color;

    public ItemOffsetDecoration(int spacing) {
        this.spacing = spacing;
    }

    public ItemOffsetDecoration(int spacing, int color) {
        this.spacing = spacing;
        this.color = color;
    }

    public ItemOffsetDecoration() {
        this.spacing = (int) (App.getInstance().getApplicationContext().getResources().getInteger(R.integer.recycler_item_height) * App.getInstance().getApplicationContext().getResources().getDisplayMetrics().density);
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);

        final int totalSpanCount = getTotalSpanCount(parent);

/*
        int spanSize = getItemSpanSize(parent, position);
        if (totalSpanCount == spanSize) {
            return;
        }
*/

        outRect.top = isInTheFirstRow(position, totalSpanCount) ? 0 : spacing;
        outRect.left = isFirstInRow(position, totalSpanCount) ? 0 : spacing / 2;
        outRect.right = isLastInRow(position, totalSpanCount) ? 0 : spacing / 2;
        outRect.bottom = 0; // don't need
    }

    private boolean isInTheFirstRow(int position, int spanCount) {
        return position < spanCount;
    }

    private boolean isFirstInRow(int position, int spanCount) {
        return position % spanCount == 0;
    }

    private boolean isLastInRow(int position, int spanCount) {
        return isFirstInRow(position + 1, spanCount);
    }

    private int getTotalSpanCount(RecyclerView parent) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager
                ? ((GridLayoutManager) layoutManager).getSpanCount()
                : 1;
    }

    private int getItemSpanSize(RecyclerView parent, int position) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager
                ? ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(position)
                : 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        c.drawColor(color);
    }

    public void setDrawableColor(int color) {
        this.color = color;

    }
}
