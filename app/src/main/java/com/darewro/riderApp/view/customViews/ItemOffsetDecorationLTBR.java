package com.darewro.riderApp.view.customViews;

import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.darewro.riderApp.App;
import com.darewro.riderApp.R;

/**
 * Created by KMajeed on 09/08/2018.
 */

public class ItemOffsetDecorationLTBR extends RecyclerView.ItemDecoration {

    private final int spacing;

    public ItemOffsetDecorationLTBR(int spacing) {
        this.spacing = spacing;
    }

    public ItemOffsetDecorationLTBR() {
        this.spacing = (int) (App.getInstance().getApplicationContext().getResources().getInteger(R.integer.recycler_item_height) * App.getInstance().getApplicationContext().getResources().getDisplayMetrics().density);
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

        outRect.top = isInTheFirstRow(position,totalSpanCount) ? spacing : 0;
        outRect.left = isFirstInRow(position,totalSpanCount) ? spacing : 0;
        outRect.right = spacing;
        outRect.bottom = isLastInRow(position,totalSpanCount) ? 0 : spacing; // don't need
    }

    private boolean isInTheFirstRow(int position, int spanCount) {
        return position < spanCount;
    }

    private boolean isFirstInRow(int position, int spanCount) {
        return position % spanCount == 0;
    }

    private boolean isLastInRow(int position, int spanCount) {
        return position % spanCount== spanCount;//isFirstInRow(position + 1, spanCount);
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
}
