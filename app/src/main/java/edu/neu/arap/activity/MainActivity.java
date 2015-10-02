package edu.neu.arap.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.neu.arap.R;
import edu.neu.arap.view.OpenCVSurface;

public class MainActivity extends AppCompatActivity {

	@InjectView(R.id.main_camera_surface)
	OpenCVSurface mCameraSurface;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS:
					Log.i("OpenCv Debug", "OpenCV loaded successfully");
					mCameraSurface.enableView();
					break;
				default:
					super.onManagerConnected(status);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ButterKnife.inject(this);

		initCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mCameraSurface!=null){
			mCameraSurface.disableView();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mCameraSurface!=null){
			mCameraSurface.disableView();
		}
	}

	private void initCamera(){
		mCameraSurface.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
			@Override
			public void onCameraViewStarted(int width, int height) {

			}

			@Override
			public void onCameraViewStopped() {

			}

			@Override
			public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
				Mat mRgba = inputFrame.rgba();
				return mRgba;
			}
		});
	}
}
