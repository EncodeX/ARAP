package edu.neu.arap.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by 志伟 on 2015/11/10.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildPosition(view) != 0)
            outRect.left = space*3;
//        outRect.left = space;
//        outRect.right = space;
//        outRect.bottom = space;
//        if(parent.getChildPosition(view) == 0)
//            outRect.top = space;
    }
}