package edu.neu.arap.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.lang.reflect.Field;
import java.nio.IntBuffer;
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
import edu.neu.arap.adapter.ARGalleryAdapter;
import edu.neu.arap.adapter.ARGalleryDecoration;
import edu.neu.arap.adapter.SpacesItemDecoration;
import edu.neu.arap.easyar.GLView;
import edu.neu.arap.tool.AnimateBuilder;
import jp.wasabeef.blurry.Blurry;

public class AugmentedActivity extends AppCompatActivity {

	@Bind(R.id.camera_preview)
	FrameLayout mCameraPreview;
	@Bind(R.id.scan_bar_1)
	FrameLayout mScanBar1;
	@Bind(R.id.scan_bar_2)
	FrameLayout mScanBar2;
	@Bind(R.id.ar_hint)
	TextView mARHint;
	@Bind(R.id.ar_info_button)
	RelativeLayout mARInfoButton;
	@Bind(R.id.ar_info_layout)
	RelativeLayout mARInfoLayout;
	@Bind(R.id.ar_capture_background)
	ImageView mARCaptureBackground;
	@Bind(R.id.ar_info_close_button)
	ImageButton mARInfoCloseButton;
	@Bind(R.id.loading_indicator)
	RelativeLayout mLoadingIndicator;
	@Bind(R.id.ar_back_button)
	ImageButton mARBackButton;
	@Bind(R.id.ar_buttons)
	FrameLayout mARButtons;
	@Bind(R.id.ar_info_background)
	FrameLayout mARInfoBackground;
	@Bind(R.id.ar_info_title)
	TextView mARInfoTitle;
	@Bind(R.id.ar_info_gallery)
	RecyclerView mARInfoGallery;
	@Bind(R.id.ar_info_description)
	TextView mARInfoDescription;

	private View mARBlurBackground;

	private LayoutListener mLayoutListener;
	private ARStateListener mARStateListener;

	private ARGalleryAdapter mGalleryAdapter;

	private boolean mInfoSwitch = false;
	private boolean mNeedScreenShot = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if (mMasterActivity != null) {
			copy(mMasterActivity);
		}

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

		ViewTreeObserver observer = mScanBar1.getViewTreeObserver();
		mLayoutListener = new LayoutListener();
		observer.addOnGlobalLayoutListener(mLayoutListener);

		mARStateListener = new ARListener();
		setARStateListener(mARStateListener);

		mARInfoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				mNeedScreenShot = true;

				AnimatorSet animatorSet = new AnimatorSet();

				animatorSet.playTogether(
						AnimateBuilder.buildAlphaAnimation(
								mLoadingIndicator, 0.f, 1.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARBackButton, 1.f, 0.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mScanBar1, 1.f, 0.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mScanBar2, 1.f, 0.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARHint, 1.f, 0.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARButtons, 1.f, 0.f, 500
						)
				);

				animatorSet.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {

					}

					@Override
					public void onAnimationEnd(Animator animator) {
						mARBackButton.setVisibility(View.INVISIBLE);
						mScanBar1.setVisibility(View.INVISIBLE);
						mScanBar2.setVisibility(View.INVISIBLE);
						mARHint.setVisibility(View.INVISIBLE);
						mARButtons.setVisibility(View.INVISIBLE);
					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});

				mARInfoLayout.setAlpha(1.f);
				mLoadingIndicator.setAlpha(0.f);
				mLoadingIndicator.setVisibility(View.VISIBLE);

				animatorSet.start();
			}
		});

		mARInfoCloseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mEasyARGLView.onResume();
				mJpctSurface.onResume();
				Blurry.delete(mARInfoBackground);

				AnimatorSet animatorSet = new AnimatorSet();

				animatorSet.playTogether(
						AnimateBuilder.buildAlphaAnimation(
								mARInfoLayout, 1.f, 0.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARBackButton, 0.f, 1.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mScanBar1, 0.f, 1.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mScanBar2, 0.f, 1.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARHint, 0.f, 1.f, 500
						),
						AnimateBuilder.buildAlphaAnimation(
								mARButtons, 0.f, 1.f, 500
						)
				);

				animatorSet.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animator) {

					}

					@Override
					public void onAnimationEnd(Animator animator) {
						mARInfoLayout.setVisibility(View.INVISIBLE);
						mARBackButton.setVisibility(View.VISIBLE);
						mScanBar1.setVisibility(View.VISIBLE);
						mScanBar2.setVisibility(View.VISIBLE);
						mARHint.setVisibility(View.VISIBLE);
						mARButtons.setVisibility(View.VISIBLE);

						mARInfoBackground.removeView(mARBlurBackground);
					}

					@Override
					public void onAnimationCancel(Animator animator) {

					}

					@Override
					public void onAnimationRepeat(Animator animator) {

					}
				});

				animatorSet.start();
			}
		});

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mARInfoGallery.setLayoutManager(layoutManager);

		mGalleryAdapter = new ARGalleryAdapter(this);
		mARInfoGallery.setAdapter(mGalleryAdapter);
		mARInfoGallery.addItemDecoration(new ARGalleryDecoration(this, 8));

		mARBlurBackground = new View(this);

		mARBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});
	}

	private class ARListener implements ARStateListener{
		@Override
		public void onARStateChanged(boolean isDetected) {
			if(isDetected){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mARHint.setVisibility(View.INVISIBLE);
						mScanBar1.setVisibility(View.INVISIBLE);
						mScanBar2.setVisibility(View.INVISIBLE);
					}
				});
			}else{
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mARHint.setVisibility(View.VISIBLE);
						mScanBar1.setVisibility(View.VISIBLE);
						mScanBar2.setVisibility(View.VISIBLE);
					}
				});
			}
		}
	}

	private class LayoutListener implements ViewTreeObserver.OnGlobalLayoutListener{
		private boolean mInitialized = false;

		@Override
		public void onGlobalLayout() {
			if(mInitialized) return;

			mInitialized = true;

			double viewHeight = mCameraPreview.getHeight(), barHeight = mScanBar1.getHeight();
			int startDuration = (int)(barHeight / viewHeight * 4000) ;

			final AnimatorSet loopAnim = new AnimatorSet(), startAnim = new AnimatorSet();

			startAnim.playTogether(
					AnimateBuilder.setInterpolator(
							AnimateBuilder.buildTranslateAnimation(
									mScanBar1, AnimateBuilder.DIRECTION_Y,
									-(int)barHeight, 0, startDuration
							), new LinearInterpolator()
					)
			);

			loopAnim.playTogether(
					AnimateBuilder.setInterpolator(
							AnimateBuilder.setRepeatCount(
									AnimateBuilder.buildTranslateAnimation(
											mScanBar1, AnimateBuilder.DIRECTION_Y,
											0, (int)viewHeight, 4000
									), AnimateBuilder.INFINETE
							), new LinearInterpolator()
					),
					AnimateBuilder.setInterpolator(
							AnimateBuilder.setRepeatCount(
									AnimateBuilder.buildTranslateAnimation(
											mScanBar2, AnimateBuilder.DIRECTION_Y,
											-(int)viewHeight, 0, 4000
									), AnimateBuilder.INFINETE
							), new LinearInterpolator()
					)
			);

			startAnim.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animator) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					loopAnim.start();
				}

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}
			});

			mScanBar1.setVisibility(View.VISIBLE);
			mScanBar2.setVisibility(View.VISIBLE);
			startAnim.start();
		}
	}

	/**\          Easy AR           \**/

	private static String key = "595c31dd0d1a4aebaade8e21ea9654ceQ9FNgKYVtFkrzPVSXtCOApnRqz4gVOuiyDD8650IrCPlZ8l6kRyyg2BaxO4XywWqdW58vZIWRluZTQcCSGW0r1ep7h1y1UFNRWcZwAQ5lYrjvQgdCgAMB0I6QvmUBiDp2TMyU4RcV5tpTJcIkDQG95ACwP9ycV84auDCk50e";

	private GLView mEasyARGLView;
	private int mGLViewWidth;
	private int mGLViewHeight;

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

		mEasyARGLView = new GLView(this);
		mEasyARGLView.setRenderer(new AugmentedActivity.EasyARRenderer());

		mCameraPreview.addView(mEasyARGLView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
	}

	private class EasyARRenderer implements GLSurfaceView.Renderer {

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			AugmentedActivity.nativeInitGL();
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			AugmentedActivity.nativeResizeGL(w, h);

			mGLViewWidth = w; mGLViewHeight = h;
		}

		public void onDrawFrame(GL10 gl) {
			nativeRender();

			if(mNeedScreenShot){
				mNeedScreenShot = false;

				int w = mGLViewWidth;
				int h = mGLViewHeight;

				Log.i("hari", "w:" + w + "-----h:" + h);

				int b[] = new int[(int) (w * h)];
				int bt[] = new int[(int) (w * h)];
				IntBuffer buffer = IntBuffer.wrap(b);
				buffer.position(0);
				GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mEasyARGLView.onPause();
						mJpctSurface.onPause();
					}
				});

				for (int i = 0; i < h; i++) {
					//remember, that OpenGL bitmap is incompatible with Android bitmap
					//and so, some correction need.
					for (int j = 0; j < w; j++) {
						int pix = b[i * w + j];
						int pb = (pix >> 16) & 0xff;
						int pr = (pix << 16) & 0x00ff0000;
						int pix1 = (pix & 0xff00ff00) | pr | pb;
						bt[(h - i - 1) * w + j] = pix1;
					}
				}

				final Bitmap finalInBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mARCaptureBackground.setImageBitmap(finalInBitmap);

						Blurry.with(AugmentedActivity.this)
								.radius(25)
								.sampling(4)
								.color(Color.argb(127,0,0,0))
								.async(new Blurry.ImageComposer.ImageComposerListener() {
									@Override
									public void onImageReady(final BitmapDrawable drawable) {

										mARInfoLayout.setVisibility(View.VISIBLE);
										mARBlurBackground.setAlpha(0.f);
										mARInfoTitle.setAlpha(0.f);
										mARInfoGallery.setAlpha(0.f);
										mARInfoDescription.setAlpha(0.f);
										mLoadingIndicator.setVisibility(View.GONE);

										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
											mARBlurBackground.setBackground(drawable);
										} else {
											mARBlurBackground.setBackgroundDrawable(drawable);
										}
										mARInfoBackground.addView(mARBlurBackground);
										mARBlurBackground.invalidate();

										final AnimatorSet animatorSet = new AnimatorSet();

										animatorSet.playTogether(
												AnimateBuilder.buildAlphaAnimation(
														mARBlurBackground, 0.f, 1.f, 500
												),
												AnimateBuilder.setStartDelay(
														AnimateBuilder.buildAlphaAnimation(
																mARInfoTitle, 0.f, 1.f, 500
														), 400
												),
												AnimateBuilder.setStartDelay(
														AnimateBuilder.buildAlphaAnimation(
																mARInfoGallery, 0.f, 1.f, 500
														), 700
												),
												AnimateBuilder.setStartDelay(
														AnimateBuilder.buildAlphaAnimation(
																mARInfoDescription, 0.f, 1.f, 500
														), 1000
												)
										);

										animatorSet.start();
									}
								})
//						.animate(500)
//						.onto(mARInfoBackground);
								.capture(mARInfoBackground)
								.into(mARCaptureBackground);
					}
				});
			}
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

	private void copy(Object src) {
		try {
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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

//		Log.i("Camera Matrix Before", matrix.toString());
//
		try{
			matrix.transpose();
			matrix.invert();
			matrix.transpose();
		}catch (SingularMatrixException e){
			return;
		}
//
//		Log.i("Camera Matrix After", matrix.toString());

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
				TextureManager.getInstance().flush();
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
