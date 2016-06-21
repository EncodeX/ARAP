package edu.neu.arap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.neu.arap.R;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 16/6/21
 * Project: ARAP
 * Package: edu.neu.arap.adapter
 */

public class ARGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private enum GALLERY_ITEM_TYPE{
		ITEM_VERTICAL,
		ITEM_HORIZONTAL,
		ITEM_SPACE
	}

	private int[] images = {
			R.drawable.a,
			R.drawable.cai_se_bo_li,
			R.drawable.xi_kou_ping
	};

	private Context mContext;
	private LayoutInflater mLayoutInflater;

	public ARGalleryAdapter(Context context) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == GALLERY_ITEM_TYPE.ITEM_HORIZONTAL.ordinal()){
			return new HorizontalImageItemHolder(mLayoutInflater.inflate(R.layout.item_ar_gallery_horizontal, parent, false));
		}else if(viewType == GALLERY_ITEM_TYPE.ITEM_VERTICAL.ordinal()){
			return new VerticalImageItemHolder(mLayoutInflater.inflate(R.layout.item_ar_gallery_vertical, parent, false));
		}else{
			return new SpaceItemHolder(mLayoutInflater.inflate(R.layout.item_ar_gallery_space, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof HorizontalImageItemHolder){
			((HorizontalImageItemHolder)holder).mGalleryImage.setImageResource(images[position - 1]);
		}else if (holder instanceof VerticalImageItemHolder){
			((VerticalImageItemHolder)holder).mGalleryImage.setImageResource(images[position - 1]);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return GALLERY_ITEM_TYPE.ITEM_SPACE.ordinal();
		}

		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), images[position - 1]);
		double width = bitmap.getWidth(), height = bitmap.getHeight();

		return width > height ? GALLERY_ITEM_TYPE.ITEM_HORIZONTAL.ordinal():GALLERY_ITEM_TYPE.ITEM_VERTICAL.ordinal();
	}

	@Override
	public int getItemCount() {
		return images.length + 1;
	}

	static class VerticalImageItemHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.gallery_image)
		ImageView mGalleryImage;

		VerticalImageItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	static class HorizontalImageItemHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.gallery_image)
		ImageView mGalleryImage;

		HorizontalImageItemHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	static class SpaceItemHolder extends RecyclerView.ViewHolder {
		SpaceItemHolder(View itemView) {
			super(itemView);
		}
	}
}
