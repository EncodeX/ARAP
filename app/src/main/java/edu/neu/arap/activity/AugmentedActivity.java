package edu.neu.arap.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.easyar.engine.EasyAR;
import edu.neu.arap.R;
import edu.neu.arap.easyar.GLView;

public class AugmentedActivity extends AppCompatActivity {

	@Bind(R.id.camera_preview)
	FrameLayout mCameraPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_augmented);

		initView();

		initAR();
		initJPCT();
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		nativeDestory();
	}
	@Override
	protected void onResume() {
		super.onResume();
		EasyAR.onResume();
		mJpctSurface.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyAR.onPause();
		mJpctSurface.onPause();
	}

	private void initView(){
		ButterKnife.bind(this);


	}

	/**\          Easy AR           \**/

	private static String key = "595c31dd0d1a4aebaade8e21ea9654ceQ9FNgKYVtFkrzPVSXtCOApnRqz4gVOuiyDD8650IrCPlZ8l6kRyyg2BaxO4XywWqdW58vZIWRluZTQcCSGW0r1ep7h1y1UFNRWcZwAQ5lYrjvQgdCgAMB0I6QvmUBiDp2TMyU4RcV5tpTJcIkDQG95ACwP9ycV84auDCk50e";

	static {
		System.loadLibrary("EasyAR");
		System.loadLibrary("HelloARNative");
	}

	public static native void nativeInitGL();
	public static native void nativeResizeGL(int w, int h);
	public native void nativeRender();
	private native boolean nativeInit();
	private native void nativeDestory();
	private native void nativeRotationChange(boolean portrait);

	private void initAR(){
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		EasyAR.initialize(this, key);
		nativeInit();

		GLView glView = new GLView(this);
		glView.setRenderer(new AugmentedActivity.EasyARRenderer());

		mCameraPreview.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);

		boolean test = getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0;

		Log.i("Rotation", "is Portrait: " + test);
	}

	private class EasyARRenderer implements GLSurfaceView.Renderer {

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			AugmentedActivity.nativeInitGL();
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			AugmentedActivity.nativeResizeGL(w, h);
		}

		public void onDrawFrame(GL10 gl) {
			nativeRender();
		}

	}

	/**\          jPCT - AE           \**/

	private GLSurfaceView mJpctSurface;

	private AugmentedActivity mMasterActivity = null;

	private boolean mGL2;
	private JPCTRenderer mRenderer = null;
	private FrameBuffer mFrameBuffer = null;
	private World mWorld = null;
	private com.threed.jpct.Camera mWorldCamera;
	private int mFPS = 0;
	private Object3D mTestObject = null;

	private boolean mIsTargetDetected = false;
	private float mCameraMatrix[] = new float[4*4];
	private float mProjectionMatrix[] = new float[4*4];
	private float mFovyRadians;
	private float mFovRadians;
	private float mTargetWidth = 0;
	private float mTargetHeight = 0;
	private boolean mTargetSizeChanged = false;

	private ARStateListener mStateListener = null;

	private void setARStateListener(ARStateListener ARStateListener) {
		this.mStateListener = ARStateListener;
	}

	public void initJPCT() {
		// 设置OpenGL
		mGL2 = isAboveGL2();

		mJpctSurface = new GLSurfaceView(this);

		if (mGL2) {
			mJpctSurface.setEGLContextClientVersion(2);
		} else {
			mJpctSurface.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
				public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
					// Ensure that we get a 16bit framebuffer. Otherwise, we'll
					// fall back to Pixelflinger on some device (read: Samsung
					// I7500). Current devices usually don't need this, but it
					// doesn't hurt either.
					int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
					EGLConfig[] configs = new EGLConfig[1];
					int[] result = new int[1];
					egl.eglChooseConfig(display, attributes, configs, 1, result);
					return configs[0];
				}
			});
		}

		// 设置Surface
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		mJpctSurface.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		mRenderer = new JPCTRenderer();
		mJpctSurface.setRenderer(mRenderer);
		mJpctSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mJpctSurface.setZOrderMediaOverlay(true);

		mCameraPreview.addView(mJpctSurface, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		/* Init JPCT */
		mWorld = new World();
		mWorld.setAmbientLight(180, 180, 180);

		try {
			mTestObject = Loader.loadMD2(getAssets().open("snork.md2"), 0.01f);
			mTestObject.rotateY((float)(-0.5 * Math.PI));
			mTestObject.rotateX((float)(0.5*Math.PI));


			Mesh mesh = mTestObject.getMesh();
			float[] boundingBox = mesh.getBoundingBox();
			Log.i("mtestobject", Arrays.toString(boundingBox));
			mTestObject.translate(0, 0, -boundingBox[4]);

			mTestObject.build();

			mWorld.addObject(mTestObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isAboveGL2(){
		final ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = manager.getDeviceConfigurationInfo();

		return info.reqGlEsVersion >= 0x20000;
	}

	public void onDetectionStateChanged(boolean isDetected){
		if(mIsTargetDetected != isDetected){
			if (mStateListener != null) mStateListener.onARStateChanged(isDetected);
		}
		mIsTargetDetected = isDetected;
	}

	public void onCameraDataChanged(float[] cameraData, float[] projectionData, double fovyRadians, double fovRadians, double targetWidth, double targetHeight){
		for(int i = 0; i < 4; i++){
			System.arraycopy(cameraData, i * 4, mCameraMatrix, i * 4, 4);
			System.arraycopy(projectionData, i * 4, mProjectionMatrix, i * 4, 4);
		}

		mFovRadians = (float) fovRadians;
		mFovyRadians = (float) fovyRadians;
		if(mTargetHeight != targetHeight){
			mTargetWidth = (float) targetWidth;
			mTargetHeight = (float) targetHeight;
		}
	}

	private void updateCamera() {
		float[] m = mCameraMatrix;

		final SimpleVector camUp;
		Matrix4f matrix = new Matrix4f(m);

		Log.i("Camera Matrix Before", matrix.toString());
//
		try{
			matrix.transpose();
			matrix.invert();
			matrix.transpose();
		}catch (SingularMatrixException e){
			return;
		}
//
		Log.i("Camera Matrix After", matrix.toString());

		camUp = new SimpleVector(-matrix.m11, -matrix.m10, -matrix.m12);

		final SimpleVector camDirection = new SimpleVector(matrix.m21, matrix.m20, matrix.m22);
		final SimpleVector camPosition = new SimpleVector(matrix.m31, matrix.m30, matrix.m32);

//		Log.i("Camera", matrix.m20 / matrix.m30 + " " + matrix.m21 / matrix.m31 + " " + matrix.m22 / matrix.m32);

		mWorldCamera.setOrientation(camDirection, camUp);
		mWorldCamera.setPosition(camPosition);

		mWorldCamera.setFovAngle(mFovRadians);
		mWorldCamera.setYFovAngle(mFovyRadians);

		Config.setParameterValue("nearPlane", 0.00001f);
		Config.setParameterValue("farPlane", 10.0f);

//		if (mTargetSizeChanged){
//			world.removeObject(cube);
//			cube = new Object3D(2);
//
//			cube.addTriangle(
//					new SimpleVector(-2,-1.125,0), 0.0f, 0.0f,
//					new SimpleVector(2,-1.125,0), 1.0f, 0.0f,
//					new SimpleVector(-2,1.125,0), 0.0f, 1.0f,
//					TextureManager.getInstance().getTextureID("texture")
//			);
//			cube.addTriangle(
//					new SimpleVector(2,-1.125,0), 1.0f, 0.0f,
//					new SimpleVector(2,1.125,0), 1.0f, 1.0f,
//					new SimpleVector(-2,1.125,0), 0.0f, 1.0f,
//					TextureManager.getInstance().getTextureID("texture")
//			);
//
////				cube = Primitives.getCube(1.5f);
////				cube.calcTextureWrapSpherical();
//			cube.rotateX((float)(0.5 * Math.PI));
////				cube.rotateX((float)(Math.PI));
////				cube.translate(new SimpleVector(0,1.5,0));    // x->x y->z z->y
////				cube.setTexture("texture");
//			cube.strip();
//			cube.build();
//			world.addObject(cube);
//		}
	}

	private class JPCTRenderer implements GLSurfaceView.Renderer {
		private long time = System.currentTimeMillis();

		@Override
		public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (mFrameBuffer != null) {
				mFrameBuffer.dispose();
			}

			if (mGL2) {
				mFrameBuffer = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
			} else {
				mFrameBuffer = new FrameBuffer(gl, w, h); // OpenGL ES 1.x constructor
			}

			if (mMasterActivity == null) {
				try {
					TextureManager.getInstance().addTexture("disco", new Texture(getAssets().open("disco.jpg")));
					mTestObject.setTexture("disco");
				} catch (IOException e) {
					e.printStackTrace();
				}

				mWorldCamera = mWorld.getCamera();
				MemoryHelper.compact();

				if (mMasterActivity == null) {
					Logger.log("Saving master Activity!");
					mMasterActivity = AugmentedActivity.this;
				}
			}
		}

		@Override
		public void onDrawFrame(GL10 gl10) {
			RGBColor transparent = new RGBColor(0,0,0,0);

			mFrameBuffer.clear(transparent);
			if(mIsTargetDetected){
				updateCamera();

				mWorld.renderScene(mFrameBuffer);
				mWorld.draw(mFrameBuffer);
				mFrameBuffer.display();
			}

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(mFPS + "fps");
				mFPS = 0;
				time = System.currentTimeMillis();
			}
			mFPS++;
		}
	}

	private interface ARStateListener {
		void onARStateChanged(boolean isDetected);
	}
}
