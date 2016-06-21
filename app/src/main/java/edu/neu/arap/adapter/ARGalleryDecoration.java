package edu.neu.arap.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 16/6/21
 * Project: ARAP
 * Package: edu.neu.arap.adapter
 */

public class ARGalleryDecoration extends RecyclerView.ItemDecoration {
	private Context mContext;
	private int space;

	public ARGalleryDecoration(Context context, int spaceInDip) {
		mContext = context;
		space = dip2px(context, spaceInDip);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		if (parent.getChildLayoutPosition(view) != 0){
			outRect.left = space;
		}
	}

	private static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
}
