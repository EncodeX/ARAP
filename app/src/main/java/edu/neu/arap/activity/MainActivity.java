package edu.neu.arap.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.easyar.engine.EasyAR;
import edu.neu.arap.R;
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.SpacesItemDecoration;
import edu.neu.arap.easyar.GLView;

public class MainActivity extends AppCompatActivity implements MyItemClickListener ,SensorEventListener{
    private Camera camera;
    private Camera.Parameters parameters;
    private String[] spinnerData={"全部","餐饮","交通","学习","住宿","娱乐","购物"};
    private boolean flag=true;
    private ObjectAnimator exploreAreaUp,exploreAreaDown;
    private  ObjectAnimator menuUp,menuDown;
    private ObjectAnimator selectShow,selectHide;
    private ObjectAnimator spinnerShow,spinnerHide;
    private ObjectAnimator menuShowX,menuShowY, menuShowSX,menuShowSY,menuShowA;
    private ObjectAnimator introShowY,introShowA,introHideY,introHideA,introChange;
    private ObjectAnimator menuHideX,menuHideY, menuHideSX,menuHideSY,menuHideA;
    private ObjectAnimator menuBtnHideSX,menuBtnHideSY,menuBtnHideX,menuBtnHideY,menuBtnHideA;
    private ObjectAnimator menuBtnShowSX,menuBtnShowSY,menuBtnShowX,menuBtnShowY,menuBtnShowA;
    private  AnimatorSet exploreUp,exploreHide,menuShow,menuHide,introShow,introHide;
    private float distanceX,distanceY;
    private SensorManager sensorManager;
    private double gravity[]=new double[3];
    private  String[] resName={"蚁人","火星救援","捉妖记","秦时明月","完美的世界","港囧","重返20岁","移动迷宫","澳门风云","九层妖塔"};
    private  int[] resID={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j};

    @Bind(R.id.explore_button)
    Button exploreButton;
    @Bind(R.id.flashlight_button)
    Button flashButton;
    @Bind(R.id.filter_spinner)
    Spinner spinner;
    @Bind(R.id.explore_list)
    RecyclerView exploreList;
    @Bind(R.id.main_menu_button)
    Button menuButton;
    @Bind(R.id.select)
    Button select;
    @Bind(R.id.intro_image)
    ImageView introImage;
    @Bind(R.id.intro_title)
    TextView introTitle;
    @Bind(R.id.intro_content)
    TextView introContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        clickerListener();

        initAR();
	    initJPCT();
    }

    private void initView()
    {
        ButterKnife.bind(this);
        RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView)findViewById(R.id.explore_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MyAdapter mAdapter;
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, spinnerData[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        introHideAnimator();
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    //这个动画本不需要在启动时初始化，但为了在多个控件的点击事件中共同使用，才放到这里。
    private void introHideAnimator()
    {
        introHideY=ObjectAnimator.ofFloat(findViewById(R.id.intro),"translationY",0,-200);
        introHideY.setDuration(200);
        introHideY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.intro).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        introHideA=ObjectAnimator.ofFloat(findViewById(R.id.intro),"alpha",1f,0f);
        introHideA.setDuration(200);
        introHide=new AnimatorSet();
        introHide.playTogether(introHideA, introHideY);
    }

    private void clickerListener(){
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.explore_button).isSelected()) {
                    findViewById(R.id.explore_button).setSelected(false);
                    introHide.start();
                    if (spinner.getVisibility() != View.VISIBLE) {
                        selectHide.start();
                    } else {
                        exploreHide.start();
                    }
                } else {
                    exploreButton.setSelected(true);
                    findViewById(R.id.hint_text).setVisibility(View.VISIBLE);
                    if (flag) {
                        setAnimation();
                    }
                    exploreUp.start();
                }
            }
        });
      /*  flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.flashlight_button).isSelected()) {
                    findViewById(R.id.flashlight_button).setSelected(false);
                    parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.release();
                } else {
                    findViewById(R.id.flashlight_button).setSelected(true);
                    camera = Camera.open();
                    parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        public void onAutoFocus(boolean success, Camera camera) {
                        }
                    });
                    camera.startPreview();
                }
            }
        });*/
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select.isSelected()) {
                    select.setSelected(false);
                    AnimatorSet selectUp = new AnimatorSet();
                    selectUp.play(spinnerShow).after(menuUp);
                    selectUp.play(menuUp).with(exploreAreaUp);
                    selectUp.start();
                } else {
                    select.setSelected(true);
                    AnimatorSet selectDown = new AnimatorSet();
                    selectDown.play(menuDown).with(exploreAreaDown);
                    selectDown.play(spinnerHide).before(menuDown);
                    selectDown.start();
                    introHide.start();
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuShowAnimation();
                introHide.start();
            }
        });
        findViewById(R.id.menu_background_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuHideAnimation();
            }
        });

        findViewById(R.id.intro_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introHide.start();
            }
        });

        findViewById(R.id.core_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.expand_area).setVisibility(View.GONE);
                findViewById(R.id.core).setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                findViewById(R.id.core_Button).setVisibility(View.GONE);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), sensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);

            }
        });
        findViewById(R.id.core_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.expand_area).setVisibility(View.VISIBLE);
                findViewById(R.id.core).setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                findViewById(R.id.core_Button).setVisibility(View.VISIBLE);
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
	    findViewById(R.id.core_camera).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
                screenShot();
                Toast.makeText(MainActivity.this, "图片已保存至./myPic.png", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.core_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenShot();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File("/sdcard/myPic.png")));
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(shareIntent, "将ARAP介绍给更多人"));
            }
        });

    }
    //这是获取屏幕截图代码段，本该是在截屏按钮的OnClick事件中，但是因为在分享事件中需要再次用到截图，所以单独提取为一个函数
    private void screenShot()
    {
        View view = findViewById(R.id.core_share).getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        String fname = "/sdcard/myPic.png";
        Bitmap bitmap = view.getDrawingCache();
        try{
            FileOutputStream out = new FileOutputStream(fname);
            bitmap.compress(Bitmap.CompressFormat.PNG,100, out);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimation(){
        flag=false;
        exploreAreaUp=ObjectAnimator.ofFloat(findViewById(R.id.expand_area),"translationY",0,-exploreList.getHeight());
        exploreAreaUp.setDuration(600);
        menuUp=ObjectAnimator.ofFloat(menuButton,"translationY",0,-exploreList.getHeight());
        menuUp.setDuration(600);
        selectShow =ObjectAnimator.ofFloat(select,"translationX",-select.getWidth(),0);
        selectShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                select.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        selectShow.setDuration(300);
        spinnerShow=ObjectAnimator.ofFloat(spinner,"translationX",-spinner.getWidth(),0);
        spinnerShow.setDuration(300);
        spinnerShow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        exploreUp=new AnimatorSet();
        exploreUp.play(spinnerShow).after(selectShow);
        exploreUp.play(selectShow).after(menuUp);
        exploreUp.play(exploreAreaUp).with(menuUp);


        exploreAreaDown=ObjectAnimator.ofFloat(findViewById(R.id.expand_area),"translationY",-exploreList.getHeight(),0);
        exploreAreaDown.setDuration(600);
        menuDown=ObjectAnimator.ofFloat(menuButton,"translationY",-exploreList.getHeight(),0);
        menuDown.setDuration(600);
        spinnerHide=ObjectAnimator.ofFloat(spinner,"translationX",0,-spinner.getWidth());
        spinnerHide.setDuration(300);
        spinnerHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                spinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        selectHide=ObjectAnimator.ofFloat(select,"translationX",0,-select.getWidth());
        selectHide.setDuration(300);
        selectHide.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                select.setVisibility(View.INVISIBLE);
                select.setSelected(false);
                findViewById(R.id.hint_text).setVisibility(View.INVISIBLE);
              //  findViewById(R.id.intro).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        exploreHide=new AnimatorSet();
        exploreHide.play(menuDown).with(exploreAreaDown);
        exploreHide.play(menuDown).after(selectHide);
        exploreHide.play(selectHide).after(spinnerHide);

    }
    private void setMenuHideAnimation()
    {
        menuButton.setVisibility(View.VISIBLE);
        menuHideX =ObjectAnimator.ofFloat(findViewById(R.id.menu),"translationX",0,distanceX);
        menuHideX.setDuration(200);
        menuHideY=ObjectAnimator.ofFloat(findViewById(R.id.menu),"translationY", 0,distanceY);
        menuHideY.setDuration(200);
        menuHide=new AnimatorSet();
        menuHideSX =ObjectAnimator.ofFloat(findViewById(R.id.menu),"scaleX",1f,0.01f);
        menuShow.setDuration(200);
        menuHideSY=ObjectAnimator.ofFloat(findViewById(R.id.menu),"scaleY",1f,0.01f);
        menuHideSY.setDuration(200);
        menuHideA=ObjectAnimator.ofFloat(findViewById(R.id.menu),"alpha",1f,0f);
        menuHideA.setDuration(200);
        menuBtnShowX=ObjectAnimator.ofFloat(menuButton,"translationX",-menuButton.getWidth(),0);
        menuBtnShowX.setDuration(200);
        menuBtnShowY=ObjectAnimator.ofFloat(menuButton,"translationY",-menuButton.getHeight(),0);
        menuBtnShowY.setDuration(200);
        menuBtnShowA=ObjectAnimator.ofFloat(menuButton,"alpha",0f,1f);
        menuBtnShowA.setDuration(120);
        menuBtnShowA.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.menu_background).setVisibility(View.GONE);
              //  findViewById(R.id.intro).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if(spinner.getVisibility()==View.VISIBLE)
        {
            menuHideY=ObjectAnimator.ofFloat(findViewById(R.id.menu),"translationY", 0,0);
            menuBtnShowY=ObjectAnimator.ofFloat(menuButton,"translationY", -exploreList.getHeight()-menuButton.getHeight(),-exploreList.getHeight());
        }
        menuHide.playTogether(menuHideSY, menuHideSX, menuHideX, menuHideY, menuHideA, menuBtnShowA, menuBtnShowX, menuBtnShowY);
        menuHide.start();
    }

    private void setMenuShowAnimation(){
        findViewById(R.id.menu_background).setVisibility(View.VISIBLE);
        distanceY=findViewById(R.id.menu).getHeight();
        distanceX=menuButton.getLeft()-findViewById(R.id.menu).getLeft();
        menuShowX =ObjectAnimator.ofFloat(findViewById(R.id.menu),"translationX",distanceX,0);
        menuShowX.setDuration(200);
        menuShowY=ObjectAnimator.ofFloat(findViewById(R.id.menu),"translationY",distanceY,0);
        menuShowY.setDuration(200);
        menuShow=new AnimatorSet();
        menuShowSX =ObjectAnimator.ofFloat(findViewById(R.id.menu),"scaleX",0.01f,1f);
        menuShow.setDuration(200);
        menuShowSY=ObjectAnimator.ofFloat(findViewById(R.id.menu),"scaleY",0.01f,1f);
        menuShowSY.setDuration(200);
        menuShowA=ObjectAnimator.ofFloat(findViewById(R.id.menu),"alpha",0f,1f);
        menuShowA.setDuration(200);
        menuBtnHideA=ObjectAnimator.ofFloat(menuButton,"alpha",1f,0f);
        menuBtnHideA.setDuration(120);
        menuBtnHideX=ObjectAnimator.ofFloat(menuButton,"translationX",0, -menuButton.getWidth());
        menuBtnHideX.setDuration(120);
        menuBtnHideY=ObjectAnimator.ofFloat(menuButton,"translationY",0, -menuButton.getHeight());
        menuBtnHideY.setDuration(120);
        menuBtnHideA.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                menuButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if(spinner.getVisibility()==View.VISIBLE)
        {
            menuBtnHideY=ObjectAnimator.ofFloat(menuButton,"translationY",-exploreList.getHeight(), -exploreList.getHeight()-menuButton.getHeight());
        }
        menuShow.playTogether(menuShowSY, menuShowSX, menuShowX, menuShowY, menuShowA, menuBtnHideA, menuBtnHideX, menuBtnHideY);
        menuShow.start();
    }
    @Override
    public void onItemClick(View view, int position) {
        if(findViewById(R.id.intro).getVisibility()==View.VISIBLE)
        {
            introChange=ObjectAnimator.ofFloat(findViewById(R.id.intro),"translationX",0,20,-20,0);
            introChange.setDuration(100);
            introChange.start();
        }
        introTitle.setText("商品名称：" + resName[position]);
        introImage.setImageResource(resID[position]);
        if(findViewById(R.id.intro).getVisibility()!=View.VISIBLE)
        {
            findViewById(R.id.intro).setVisibility(View.VISIBLE);
            int t=0;
            if(t==0)
            {
                t++;
                introShowY=ObjectAnimator.ofFloat(findViewById(R.id.intro),"translationY",-200,0);
                introShowY.setDuration(200);
                introShowA=ObjectAnimator.ofFloat(findViewById(R.id.intro),"alpha",0f,1f);
                introShowA.setDuration(200);
                introShow=new AnimatorSet();
                introShow.playTogether(introShowA, introShowY);
            }
            introShow.start();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_GRAVITY:
                gravity[0]=event.values[0];
                gravity[1]=event.values[1];
                gravity[2]=event.values[2];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                TextView t= (TextView) findViewById(R.id.sensor);
                t.setText("X:"+(event.values[0]-gravity[0])+"\nY:"+(event.values[1]-gravity[1])+"\nZ:"+(event.values[2]-gravity[2]));
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    /**\          Easy AR           \**/


    static String key = "595c31dd0d1a4aebaade8e21ea9654ceQ9FNgKYVtFkrzPVSXtCOApnRqz4gVOuiyDD8650IrCPlZ8l6kRyyg2BaxO4XywWqdW58vZIWRluZTQcCSGW0r1ep7h1y1UFNRWcZwAQ5lYrjvQgdCgAMB0I6QvmUBiDp2TMyU4RcV5tpTJcIkDQG95ACwP9ycV84auDCk50e";

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
		glView.setRenderer(new EasyARRenderer());
//		glView.setZOrderMediaOverlay(true);

		((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

//    public void testCallback(){
//        Log.i("EasyAR","Called Back");
//    }

    public class EasyARRenderer implements GLSurfaceView.Renderer {

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            MainActivity.nativeInitGL();
        }

        public void onSurfaceChanged(GL10 gl, int w, int h) {
            MainActivity.nativeResizeGL(w, h);
        }

        public void onDrawFrame(GL10 gl) {
            nativeRender();
        }

    }

	/**\          jPCT - AE           \**/

	private GLSurfaceView mJpctSurface;

	private static MainActivity master = null;

	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private GLSLShader shader = null;

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Object3D cube = null;
	private int mFPS = 0;
	private boolean mGL2;

	private Light sun = null;

    private boolean mIsTargetDetected = false;
	private float cameraMatrix[] = new float[4*4];
	private float projectionMatrix[] = new float[4*4];

	public void initJPCT(){
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

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		mJpctSurface.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		renderer = new MyRenderer();

		mJpctSurface.setRenderer(renderer);
		mJpctSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		mJpctSurface.setZOrderMediaOverlay(true);

		((ViewGroup) findViewById(R.id.preview)).addView(mJpctSurface, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	private boolean isAboveGL2(){
		final ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = manager.getDeviceConfigurationInfo();

		return info.reqGlEsVersion >= 0x20000;
	}

	protected boolean isFullscreenOpaque() {
		return true;
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

    public void onDetectionStateChanged(boolean isDetected){
        mIsTargetDetected = isDetected;
//	    Log.i("jPCT-AE","isDetected: " + isDetected);
    }

	public void onCameraDataChanged(float[] cameraData, float[] projectionData){
		for(int i = 0; i < 4*4; i++){
			cameraMatrix[i] = cameraData[i];
			projectionMatrix[i] = projectionData[i];
		}

//		for(int i = 0; i< 4;i++){
//			Log.i("jPCT-AE",
//					String.valueOf(cameraData[i*4]) + " " +
//					String.valueOf(cameraData[i*4 + 1]) + " " +
//					String.valueOf(cameraData[i*4 + 2]) + " " +
//					String.valueOf(cameraData[i*4 + 3])
//			);
//		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
			ypos = me.getY();
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_UP) {
			xpos = -1;
			ypos = -1;
			touchTurn = 0;
			touchTurnUp = 0;
			return true;
		}

		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = me.getX() - xpos;
			float yd = me.getY() - ypos;

			xpos = me.getX();
			ypos = me.getY();

			touchTurn = xd / -100f;
			touchTurnUp = yd / -100f;
			return true;
		}

		try {
			Thread.sleep(15);
		} catch (Exception e) {
			// No need for this...
		}

		return super.onTouchEvent(me);
	}

	class MyRenderer implements GLSurfaceView.Renderer {

		private long time = System.currentTimeMillis();

		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if (fb != null) {
				fb.dispose();
			}

			if (mGL2) {
				fb = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
			} else {
				fb = new FrameBuffer(gl, w, h); // OpenGL ES 1.x constructor
			}

//			fb = new FrameBuffer(gl, w, h);

			if (master == null) {

				world = new World();
				world.setAmbientLight(20, 20, 20);

				sun = new Light(world);
				sun.setIntensity(250, 250, 250);

				// Create a texture out of the icon...:-)
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.mipmap.ic_launcher)), 64, 64));
				TextureManager.getInstance().addTexture("texture", texture);

				Resources res = getResources();

				shader = new GLSLShader(
						Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)),
						Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset))
				);

				cube = Primitives.getCube(10);
				cube.calcTextureWrapSpherical();
				cube.setShader(shader);
				cube.setTexture("texture");
				cube.strip();
				cube.build();

				world.addObject(cube);

				com.threed.jpct.Camera cam = world.getCamera();
				cam.moveCamera(com.threed.jpct.Camera.CAMERA_MOVEOUT, 50);
				cam.lookAt(cube.getTransformedCenter());

				SimpleVector sv = new SimpleVector();
				sv.set(cube.getTransformedCenter());
				sv.y -= 100;
				sv.z -= 100;
				sun.setPosition(sv);
				MemoryHelper.compact();

				if (master == null) {
					Logger.log("Saving master Activity!");
					master = MainActivity.this;
				}
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			if (touchTurn != 0) {
				cube.rotateY(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
				cube.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}

			RGBColor transparent = new RGBColor(0,0,0,0);

			shader.setUniform("modelViewMatrix", cameraMatrix);
			shader.setUniform("modelViewProjectionMatrix", projectionMatrix);
			shader.setUniform("heightScale", 0.05f);

			fb.clear(transparent);
            if(mIsTargetDetected){
                world.renderScene(fb);
                world.draw(fb);
                fb.display();
            }

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(mFPS + "fps");
				mFPS = 0;
				time = System.currentTimeMillis();
			}
			mFPS++;
		}
	}

	/*************************************/

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
}
