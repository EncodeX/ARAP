package edu.neu.arap.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.util.List;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/1
 * Project: ARAP
 * Package: edu.neu.arap.view
 */
public class OpenCVSurface extends JavaCameraView {
	private final static String TAG = "OpenCVSurface";
	private boolean mIsPreviewWorking = true;

	public OpenCVSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
		if((double)getMeasuredHeight() / (double)getMeasuredWidth()>9/16){
			heightMeasureSpec =  MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight()/9*16, MeasureSpec.EXACTLY);
		}else{
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth()/16*9, MeasureSpec.EXACTLY);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public List<String> getEffectList() {
		return mCamera.getParameters().getSupportedColorEffects();
	}

	public boolean isEffectSupported() {
		return (mCamera.getParameters().getColorEffect() != null);
	}

	public String getEffect() {
		return mCamera.getParameters().getColorEffect();
	}

	public void setEffect(String effect) {
		Camera.Parameters params = mCamera.getParameters();
		params.setColorEffect(effect);
		mCamera.setParameters(params);
	}

	public List<Camera.Size> getResolutionList() {
		return mCamera.getParameters().getSupportedPreviewSizes();
	}

	public void setResolution(Camera.Size resolution) {
		disconnectCamera();
		mMaxHeight = resolution.height;
		mMaxWidth = resolution.width;
		connectCamera(getWidth(), getHeight());
	}

	public Camera.Size getResolution() {
		return mCamera.getParameters().getPreviewSize();
	}
}
