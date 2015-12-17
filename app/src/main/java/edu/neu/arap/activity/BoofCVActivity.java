package edu.neu.arap.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import boofcv.abst.tracker.TrackerObjectQuad;
import boofcv.android.BoofAndroidFiles;
import boofcv.android.ConvertBitmap;
import boofcv.android.gui.VideoDisplayActivity;
import boofcv.android.gui.VideoImageProcessing;
import boofcv.core.image.ConvertImage;
import boofcv.factory.tracker.FactoryTrackerObjectQuad;
import boofcv.struct.calib.IntrinsicParameters;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import butterknife.ButterKnife;
import edu.neu.arap.tool.UtilVarious;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.Quadrilateral_F64;

public class BoofCVActivity extends VideoDisplayActivity implements View.OnTouchListener {
	public static Preference preference;

	// contains information on all the cameras.  less error prone and easier to deal with
	public static List<CameraSpecs> specs = new ArrayList<CameraSpecs>();

	int mode = 0;

	// size of the minimum square which the user can select
	final static int MINIMUM_MOTION = 20;

	Point2D_I32 click0 = new Point2D_I32();
	Point2D_I32 click1 = new Point2D_I32();

	public BoofCVActivity() {
		loadCameraSpecs();
		preference = new Preference();
	}

	public BoofCVActivity(boolean hidePreview) {
		super(hidePreview);

		loadCameraSpecs();
		preference = new Preference();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_boof_cv);

		ButterKnife.bind(this);

//		initCamera();
		FrameLayout iv = getViewPreview();
		iv.setOnTouchListener(this);

		setDefaultPreferences();
		setShowFPS(preference.showFps);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		setProcessing(new ImageProcessing(
//				FactoryTrackerObjectQuad.tld(new ConfigTld(false),ImageUInt8.class),
//				ImageType.single(ImageUInt8.class)));
		setProcessing(new ImageProcessing(
				FactoryTrackerObjectQuad.circulant(null,ImageUInt8.class),ImageType.single(ImageUInt8.class)));
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
		preference.preview = UtilVarious.closest(camera.sizePreview,320,240);
		preference.picture = UtilVarious.closest(camera.sizePicture,320,240);

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

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if( mode == 0 ) {
			if(MotionEvent.ACTION_DOWN == motionEvent.getActionMasked()) {
				click0.set((int) motionEvent.getX(), (int) motionEvent.getY());
				click1.set((int) motionEvent.getX(), (int) motionEvent.getY());
				mode = 1;
			}
		} else if( mode == 1 ) {
			if(MotionEvent.ACTION_MOVE == motionEvent.getActionMasked()) {
				click1.set((int)motionEvent.getX(),(int)motionEvent.getY());
			} else if(MotionEvent.ACTION_UP == motionEvent.getActionMasked()) {
				click1.set((int)motionEvent.getX(),(int)motionEvent.getY());
				mode = 2;
			}
		}
		return true;
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

	private class ImageProcessing<T extends ImageBase> extends VideoImageProcessing<MultiSpectral<ImageUInt8>>{

		T input;
		ImageType<T> inputType;

		TrackerObjectQuad tracker;
		boolean visible;

		Quadrilateral_F64 location = new Quadrilateral_F64();

		Paint paintSelected = new Paint();
		Paint paintLine0 = new Paint();
		Paint paintLine1 = new Paint();
		Paint paintLine2 = new Paint();
		Paint paintLine3 = new Paint();
		private Paint textPaint = new Paint();

		protected ImageProcessing(TrackerObjectQuad tracker , ImageType<T> inputType) {
			super(ImageType.ms(3,ImageUInt8.class));
//			super(ImageType.il(3, InterleavedU8.class));
			this.inputType = inputType;

			if( inputType.getFamily() == ImageType.Family.SINGLE_BAND ) {
				input = inputType.createImage(1,1);
			}

			mode = 0;
			this.tracker = tracker;

			paintSelected.setColor(Color.argb(0xFF/2,0xFF,0xFF,0xFF));

			paintLine0.setColor(Color.GREEN);
			paintLine0.setStrokeWidth(3f);
			paintLine1.setColor(Color.GREEN);
			paintLine1.setStrokeWidth(3f);
			paintLine2.setColor(Color.GREEN);
			paintLine2.setStrokeWidth(3f);
			paintLine3.setColor(Color.GREEN);
			paintLine3.setStrokeWidth(3f);

			// Create out paint to use for drawing
			textPaint.setARGB(255, 200, 0, 0);
			textPaint.setTextSize(60);
		}

		@Override
		protected void process(MultiSpectral<ImageUInt8> image, Bitmap output, byte[] storage) {
			updateTracker(image);
			visualize(image, output, storage);
//			ConvertBitmap.interleavedToBitmap(image, output, storage);
		}

		private void updateTracker(MultiSpectral<ImageUInt8> color) {
			if( inputType.getFamily() == ImageType.Family.SINGLE_BAND ) {
				input.reshape(color.width,color.height);
				ConvertImage.average(color,(ImageUInt8)input);
			} else {
				input = (T)color;
			}

			if( mode == 2 ) {
				imageToOutput(click0.x, click0.y, location.a);
				imageToOutput(click1.x, click1.y, location.c);

				// make sure the user selected a valid region
				makeInBounds(location.a);
				makeInBounds(location.c);

				if( movedSignificantly(location.a,location.c) ) {
					// use the selected region and start the tracker
					location.b.set(location.c.x, location.a.y);
					location.d.set( location.a.x, location.c.y );

					tracker.initialize(input, location);
					visible = true;
					mode = 3;
				} else {
					// the user screw up. Let them know what they did wrong
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(BoofCVActivity.this, "Drag a larger region", Toast.LENGTH_SHORT).show();
						}
					});
					mode = 0;
				}
			} else if( mode == 3 ) {
				visible = tracker.process(input,location);
			}
		}

		private void visualize(MultiSpectral<ImageUInt8> color, Bitmap output, byte[] storage) {
//			ConvertBitmap.interleavedToBitmap(color, output, storage);
			ConvertBitmap.multiToBitmap(color, output, storage);
			Canvas canvas = new Canvas(output);

			if( mode == 1 ) {
				Point2D_F64 a = new Point2D_F64();
				Point2D_F64 b = new Point2D_F64();

				imageToOutput(click0.x, click0.y, a);
				imageToOutput(click1.x, click1.y, b);

				canvas.drawRect((int)a.x,(int)a.y,(int)b.x,(int)b.y,paintSelected);
			} else if( mode >= 2 ) {
				if( visible ) {
					Quadrilateral_F64 q = location;

					drawLine(canvas,q.a,q.b,paintLine0);
					drawLine(canvas,q.b,q.c,paintLine1);
					drawLine(canvas,q.c,q.d,paintLine2);
					drawLine(canvas,q.d,q.a,paintLine3);
				} else {
					canvas.drawText("?",color.width/2,color.height/2,textPaint);
				}
			}
		}

		private void drawLine( Canvas canvas , Point2D_F64 a , Point2D_F64 b , Paint color ) {
			canvas.drawLine((float)a.x,(float)a.y,(float)b.x,(float)b.y,color);
		}

		private void makeInBounds( Point2D_F64 p ) {
			if( p.x < 0 ) p.x = 0;
			else if( p.x >= input.width )
				p.x = input.width - 1;

			if( p.y < 0 ) p.y = 0;
			else if( p.y >= input.height )
				p.y = input.height - 1;

		}

		private boolean movedSignificantly( Point2D_F64 a , Point2D_F64 b ) {
			if( Math.abs(a.x-b.x) < MINIMUM_MOTION )
				return false;
			if( Math.abs(a.y-b.y) < MINIMUM_MOTION )
				return false;

			return true;
		}
	}
}