package edu.neu.arap.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import boofcv.android.BoofAndroidFiles;
import boofcv.android.ConvertBitmap;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageInterleaved;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.InterleavedI8;
import boofcv.struct.image.InterleavedU8;
import boofcv.struct.image.MultiSpectral;
import butterknife.ButterKnife;
import edu.neu.arap.R;

public class MainActivity extends VideoDisplayActivity {
	public static Preference preference;

	// contains information on all the cameras.  less error prone and easier to deal with
	public static List<CameraSpecs> specs = new ArrayList<CameraSpecs>();

	public MainActivity() {
		loadCameraSpecs();
		preference = new Preference();
	}

	public MainActivity(boolean hidePreview) {
		super(hidePreview);

		loadCameraSpecs();
		preference = new Preference();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);

		ButterKnife.bind(this);

//		initCamera();

		setDefaultPreferences();
		setShowFPS(preference.showFps);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setProcessing(new ImageProcessing());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected Camera openConfigureCamera(Camera.CameraInfo cameraInfo) {
		Camera mCamera = Camera.open(preference.cameraId);
		Camera.getCameraInfo(preference.cameraId,cameraInfo);

		Camera.Parameters param = mCamera.getParameters();
		Camera.Size sizePreview = param.getSupportedPreviewSizes().get(preference.preview);
		param.setPreviewSize(sizePreview.width,sizePreview.height);
		Camera.Size sizePicture = param.getSupportedPictureSizes().get(preference.picture);
		param.setPictureSize(sizePicture.width, sizePicture.height);
		mCamera.setParameters(param);

		return mCamera;
//		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void initCamera(){

	}

	private void loadCameraSpecs() {
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraSpecs c = new CameraSpecs();
			specs.add(c);

			Camera.getCameraInfo(i, c.info);
			Camera camera = Camera.open(i);
			Camera.Parameters params = camera.getParameters();
			c.horizontalViewAngle = params.getHorizontalViewAngle();
			c.verticalViewAngle = params.getVerticalViewAngle();
			c.sizePreview.addAll(params.getSupportedPreviewSizes());
			c.sizePicture.addAll(params.getSupportedPictureSizes());
			camera.release();
		}
	}

	private void setDefaultPreferences() {
		preference.showFps = false;

		// There are no cameras.  This is possible due to the hardware camera setting being set to false
		// which was a work around a bad design decision where front facing cameras wouldn't be accepted as hardware
		// which is an issue on tablets with only front facing cameras
		if( specs.size() == 0 ) {
			dialogNoCamera();
		}
		// select a front facing camera as the default
		for (int i = 0; i < specs.size(); i++) {
			CameraSpecs c = specs.get(i);

			if( c.info.facing == Camera.CameraInfo.CAMERA_FACING_BACK ) {
				preference.cameraId = i;
				break;
			} else {
				// default to a front facing camera if a back facing one can't be found
				preference.cameraId = i;
			}
		}

		CameraSpecs camera = specs.get(preference.cameraId);
		preference.preview = UtilVarious.closest(camera.sizePreview,640,480);
		preference.picture = UtilVarious.closest(camera.sizePicture,640,480);

		// see if there are any intrinsic parameters to load
		loadIntrinsic();
	}

	private void dialogNoCamera() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your device has no cameras!")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						System.exit(0);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void loadIntrinsic() {
		preference.intrinsic = null;
		try {
			FileInputStream fos = openFileInput("cam"+preference.cameraId+".txt");
			Reader reader = new InputStreamReader(fos);
			preference.intrinsic = BoofAndroidFiles.readIntrinsic(reader);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
			Toast.makeText(this, "Failed to load intrinsic parameters", Toast.LENGTH_SHORT).show();
		}
	}

	private class Preference {
		public int cameraId;
		public int preview;
		public int picture;
		public boolean showFps;
		public IntrinsicParameters intrinsic;
	}
	private class CameraSpecs {
		public Camera.CameraInfo info = new Camera.CameraInfo();
		public List<Camera.Size> sizePreview = new ArrayList<Camera.Size>();
		public List<Camera.Size> sizePicture = new ArrayList<Camera.Size>();
		public float horizontalViewAngle;
		public float verticalViewAngle;
	}

//	private class ImageProcessing<T extends ImageBase> extends VideoImageProcessing<MultiSpectral<ImageUInt8>>{
//		protected ImageProcessing() {
//			super(ImageType.ms(3, ImageUInt8.class));
//		}
//
//		@Override
//		protected void process(MultiSpectral<ImageUInt8> image, Bitmap output, byte[] storage) {
//			ConvertBitmap.multiToBitmap(image, output, storage);
//		}
//	}

//	private class ImageProcessing extends VideoImageProcessing<ImageUInt8>{
//		protected ImageProcessing() {
//			super(ImageType.single(ImageUInt8.class));
//		}
//
//		@Override
//		protected void process(ImageUInt8 image, Bitmap output, byte[] storage) {
//			ConvertBitmap.grayToBitmap(image, output, storage);
//		}
//	}

	private class ImageProcessing<T extends ImageBase> extends VideoImageProcessing<InterleavedU8>{
		protected ImageProcessing() {
			super(ImageType.il(3, InterleavedU8.class));
		}

		@Override
		protected void process(InterleavedU8 image, Bitmap output, byte[] storage) {
			ConvertBitmap.interleavedToBitmap(image, output, storage);
		}
	}
}


class UtilVarious {
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/**
	 * From the list of image sizes, select the one which is closest to the specified size.
	 */
	public static int closest( List<Camera.Size> sizes , int width , int height ) {
		int best = -1;
		int bestScore = Integer.MAX_VALUE;

		for( int i = 0; i < sizes.size(); i++ ) {
			Camera.Size s = sizes.get(i);

			Log.d("Camera","width: "+ s.width+" height: "+s.height);

			int dx = s.width-width;
			int dy = s.height-height;

			int score = dx*dx + dy*dy;
			if( score < bestScore ) {
				best = i;
				bestScore = score;
			}
		}

		Log.d("Camera","best: "+ best);
		return best;
	}

	public static Camera.Size closestS( List<Camera.Size> sizes , int width , int height ) {
		return sizes.get( closest(sizes,width,height));
	}
}