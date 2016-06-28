package edu.neu.arap.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
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
import com.threed.jpct.Animation;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Matrix;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.SpacesItemDecoration;
import edu.neu.arap.easyar.GLView;
import edu.neu.arap.tool.CameraOrbitController;
import edu.neu.arap.tool.glfont.GLFont;
import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.SkeletonPose;
import raft.jpct.bones.SkinClip;

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
    private ObjectAnimator helpShow,helpGone;
    private ObjectAnimator ARShowMenuBtn,ARShowExp,ARGoneMenuBtn,ARGoneExp,ARShowShare,ARGoneShare,ARShowSceen,ARGoneSceen;
    private  AnimatorSet exploreUp,exploreHide,menuShow,menuHide,introShow,introHide,ARShowSet,ARGoneSet;
    private float distanceX,distanceY;
    private SensorManager sensorManager;
    private double gravity[]=new double[3];
    private  String[] resName={"蚁人","火星救援","捉妖记","秦时明月","完美的世界","港囧","重返20岁","移动迷宫","澳门风云","九层妖塔"};
    private  int[] resID={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j};
    private  int ARChangeMark;

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
        //////////跳转到新加入的界面new_activity_main
        findViewById(R.id.newDesign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MainActivityNew.class));
            }
        });

    }

    private void initView()
    {
        ARChangeMark=0;
        ButterKnife.bind(this);
        RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView)findViewById(R.id.explore_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        MyAdapter mAdapter;
//        mAdapter = new MyAdapter(this,null);
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
//        mAdapter.setOnItemClickListener(this);
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

        /*findViewById(R.id.core_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.expand_area).setVisibility(View.GONE);
                findViewById(R.id.core).setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
                findViewById(R.id.core_Button).setVisibility(View.GONE);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), sensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);

            }
        });*/
       /* findViewById(R.id.core_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.expand_area).setVisibility(View.VISIBLE);
                findViewById(R.id.core).setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                //findViewById(R.id.core_Button).setVisibility(View.VISIBLE);
                sensorManager.unregisterListener(MainActivity.this);
            }
        });*/
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

        findViewById(R.id.menu_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHelpShowAnimation();
            }
        });
        findViewById(R.id.top_extend_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHelpGoneAnimation();
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
    private void setHelpShowAnimation()
    {
        setMenuHideAnimation();
        findViewById(R.id.top_extend_background).setVisibility(View.VISIBLE);
        findViewById(R.id.help_area).setVisibility(View.VISIBLE);
        helpShow=ObjectAnimator.ofFloat(findViewById(R.id.help_area),"translationY",-100,30,0);
        helpShow.setDuration(200);
        helpShow.start();
    }
    private void setHelpGoneAnimation()
    {
        helpGone=ObjectAnimator.ofFloat(findViewById(R.id.help_area),"translationY",0,30,-100);
        helpGone.setDuration(200);
        helpGone.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.help_area).setVisibility(View.GONE);
                findViewById(R.id.top_extend_background).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        helpGone.start();
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

    private void ARShow(){
        findViewById(R.id.core).setVisibility(View.VISIBLE);
        if(exploreButton.isSelected())
        {
            findViewById(R.id.explore_button).setSelected(false);
            introHide.start();
            if (spinner.getVisibility() != View.VISIBLE) {
                selectHide.start();
            } else {
                exploreHide.start();
            }
        }
        if(menuButton.getVisibility()==View.INVISIBLE)
        {
            setMenuHideAnimation();
        }

        ARGoneExp=ObjectAnimator.ofFloat(exploreButton,"translationX",0,exploreButton.getWidth()+20);
        ARGoneExp.setDuration(100);
        ARGoneExp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.expand_area).setVisibility(View.GONE);
                menuButton.setVisibility(View.GONE);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), sensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ARGoneMenuBtn=ObjectAnimator.ofFloat(menuButton,"translationX",0,menuButton.getWidth()+20);
        ARGoneMenuBtn.setDuration(100);
        ARShowSet=new AnimatorSet();
        ARShowSceen=ObjectAnimator.ofFloat(findViewById(R.id.core_camera),"translationX",4*findViewById(R.id.core_camera).getWidth()+80,0);
        ARShowSceen.setDuration(400);
        ARShowShare=ObjectAnimator.ofFloat(findViewById(R.id.core_share),"translationX",3*findViewById(R.id.core_share).getWidth()+60,0);
        ARShowShare.setDuration(300);
        ARShowSet.play(ARGoneMenuBtn).with(ARShowSceen).before(ARGoneExp).with(ARShowShare);
        ARShowSet.start();
        ARGoneSceen=ObjectAnimator.ofFloat(findViewById(R.id.core_camera),"translationX",0,findViewById(R.id.core_camera).getWidth()+20);
        ARGoneSceen.setDuration(100);
        ARGoneShare=ObjectAnimator.ofFloat(findViewById(R.id.core_share),"translationX",0,findViewById(R.id.core_camera).getWidth()+20);
        ARGoneShare.setDuration(100);
        ARGoneShare.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                findViewById(R.id.expand_area).setVisibility(View.VISIBLE);
                findViewById(R.id.core).setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                sensorManager.unregisterListener(MainActivity.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ARShowExp=ObjectAnimator.ofFloat(exploreButton,"translationX",4*exploreButton.getWidth()+80,0);
        ARShowExp.setDuration(400);
        ARShowMenuBtn=ObjectAnimator.ofFloat(menuButton,"translationX",3*menuButton.getWidth()+60,0);
        ARShowMenuBtn.setDuration(300);
        ARGoneSet=new AnimatorSet();
        ARGoneSet.play(ARGoneShare).with(ARShowExp).before(ARGoneSceen).with(ARShowMenuBtn);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void onARStateChanged(boolean isTargetDetected){
        // 在这里输入相关代码
        ARChangeMark++;
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(ARChangeMark%2==0){
                    //Toast.makeText(MainActivity.this,"gone"+ARChangeMark,Toast.LENGTH_SHORT).show();
                ARGoneSet.start();

                }
                if(ARChangeMark%2==1){
                   // Toast.makeText(MainActivity.this,"show"+ARChangeMark,Toast.LENGTH_SHORT).show();
                 /*   findViewById(R.id.expand_area).setVisibility(View.GONE);
                    findViewById(R.id.core).setVisibility(View.VISIBLE);
                    menuButton.setVisibility(View.GONE);
                   // findViewById(R.id.core_Button).setVisibility(View.GONE);
                    sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), sensorManager.SENSOR_DELAY_NORMAL);
                    sensorManager.registerListener(MainActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
*/
                    ARShow();
                }
            }
        });
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
//	private GLSLShader shader = null;
	private com.threed.jpct.Camera worldCamera;

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Object3D cube = null;
	private Object3D mTestObject = null;
	private int mFPS = 0;
	private boolean mGL2;

	private Light sun = null;

    private boolean mIsTargetDetected = false;
	private float cameraMatrix[] = new float[4*4];
	private float projectionMatrix[] = new float[4*4];
	private float mFovyRadians;
	private float mFovRadians;
	private float mTargetWidth = 0;
	private float mTargetHeight = 0;
	private boolean mTargetSizeChanged = false;

	@Bind(R.id.test_image)
	ImageView mTestImage;

	private static final int MENU_STOP_ANIM = 1;
	private static final int MENU_USE_MESH_ANIM = 2;

	private static final int GRANULARITY = 25;

	/** ninja placement locations. values are in angles */
	private static final float[] LOCATIONS = new float[] {0, 180, 90, 270, 45, 225, 315, 135};

	private static final Rect[] BUTTON_BOUNDS = new Rect[2];

	private CameraOrbitController cameraController;

	private PowerManager.WakeLock wakeLock;

	private long frameTime = System.currentTimeMillis();
	private long aggregatedTime = 0;
	private float animateSeconds  = 0f;
	private float speed = 1f;

	private int animation = -1;
	private boolean useMeshAnim = false;

	private AnimatedGroup masterNinja;
	private final List<AnimatedGroup> ninjas = new LinkedList<AnimatedGroup>();

	private long lastTouchTime;

	/** Ninja **/

	/** set this to true to allow mesh keyframe animation */
	private static final boolean MESH_ANIM_ALLOWED = false;


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

		/* Init JPCT */
		world = new World();
		world.setAmbientLight(180, 180, 180);

		try {
			Resources res = getResources();
			masterNinja = BonesIO.loadGroup(res.openRawResource(R.raw.ninja));//R.raw.ninja);
			if (MESH_ANIM_ALLOWED)
				createMeshKeyFrames();
			addNinja();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		try {
			mTestObject = Loader.loadMD2(getAssets().open("snork.md2"), 0.01f);
			mTestObject.rotateY((float)(-0.5 * Math.PI));
			mTestObject.rotateX((float)(Math.PI));

//					TextureManager.getInstance().addTexture("rock", new Texture(getAssets().open("rock.jpg")));
//					TextureManager.getInstance().addTexture("normals", new Texture(getAssets().open("normals.jpg")));
//					TextureInfo stoneTex = new TextureInfo(TextureManager.getInstance().getTextureID("rock"));
//					stoneTex.add(TextureManager.getInstance().getTextureID("normals"), TextureInfo.MODE_MODULATE);
//					mTestObject = Loader.load3DS(getAssets().open("rock.3ds"),1.0f)[0];
////                    mTestObject.rotateY((float)(-0.5 * Math.PI));
//                    mTestObject.rotateX((float)(Math.PI/2));
//					mTestObject.setTexture(stoneTex);

			Mesh mesh = mTestObject.getMesh();
			float[] boundingBox = mesh.getBoundingBox();
			Log.i("mtestobject", Arrays.toString(boundingBox));
			mTestObject.translate(0, -boundingBox[4], 0);

			mTestObject.build();
//					mTestObject.setSpecularLighting(true);

			world.addObject(mTestObject);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cameraController = new CameraOrbitController(world.getCamera());
		cameraController.cameraAngle = 0;

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Bones-Demo");
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
        if(mIsTargetDetected != isDetected){
            onARStateChanged(isDetected);
        }
        mIsTargetDetected = isDetected;
//	    Log.i("jPCT-AE","isDetected: " + isDetected);
    }

	public void onCameraDataChanged(float[] cameraData, float[] projectionData, double fovyRadians, double fovRadians, double targetWidth, double targetHeight){
//		for(int i = 0; i < 4; i++){
//			for(int j = 0; j < 4; j++){
//				cameraMatrix[j*4 + i] = cameraData[i*4 + j];
//				projectionMatrix[j*4 + i] = projectionData[i*4 + j];
//			}
//		}

		for(int i = 0; i < 4; i++){
			System.arraycopy(cameraData, i * 4, cameraMatrix, i * 4, 4);
			System.arraycopy(projectionData, i * 4, projectionMatrix, i * 4, 4);
		}

//		for(int i = 1; i < 3; i++){
//            for (int j = 0; j < 4; j++){
//	            projectionMatrix[j*4 + i] = - projectionMatrix[j*4 + i];
//            }
//        }

		mFovRadians = (float) fovRadians;
		mFovyRadians = (float) fovyRadians;
		if(mTargetHeight != targetHeight){
			Log.i("EasyAR", targetHeight +" "+ targetWidth);
			mTargetWidth = (float) targetWidth;
			mTargetHeight = (float) targetHeight;
		}
	}

    public void onNewImageCaptured(char[] imageData){
		Log.i("EasyAR","Image Sent size: " + imageData.length);
	    byte[] data = new String(imageData).getBytes();
//		Bitmap image = BitmapFactory.decodeByteArray(data, 0, imageData.length);
	    YuvImage image = new YuvImage(data, ImageFormat.NV21, 1280, 720, null);
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    image.compressToJpeg(new Rect(0, 0, 1280, 720), 50, out);
	    byte[] imageBytes = out.toByteArray();
	    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
	    runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
			    mTestImage.setImageBitmap(bitmap);
			    mTestImage.setVisibility(View.VISIBLE);
		    }
	    });
    }

	public void updateCamera() {
		float[] m = cameraMatrix;
//		m.setDump(cameraMatrix);
//		worldCamera.setBack(m);

		final SimpleVector camUp;
//		if (mActivity.isPortrait()) {
//			camUp = new SimpleVector(-m[0], -m[1], -m[2]);
//		} else {
//			camUp = new SimpleVector(-m[4], -m[5], -m[6]);
//		}

		Matrix4f matrix = new Matrix4f(m);

//		Log.i("jPCT-AE", "Matrix before:\n" + matrix.toString());

		try{
			matrix.invert();
		}catch (SingularMatrixException e){
			return;
		}

//		Log.i("jPCT-AE", "Matrix after:\n" + matrix.toString());

//		m[0] = matrix.m00; m[1] = matrix.m01; m[2] = matrix.m02; m[3] = matrix.m03;
//		m[4] = matrix.m10; m[5] = matrix.m11; m[6] = matrix.m12; m[7] = matrix.m13;
//		m[8] = matrix.m20; m[9] = matrix.m21; m[10] = matrix.m22; m[11] = matrix.m23;
//		m[12] = matrix.m30; m[13] = matrix.m31; m[14] = matrix.m32; m[15] = matrix.m33;

//		camUp = new SimpleVector(-m[4], -m[5], -m[6]);
//
//		final SimpleVector camDirection = new SimpleVector(m[8], m[9], m[10]);
//		final SimpleVector camPosition = new SimpleVector(m[12], m[13], m[14]);

		camUp = new SimpleVector(-matrix.m10, -matrix.m12, -matrix.m11);

		final SimpleVector camDirection = new SimpleVector(matrix.m20, matrix.m22, matrix.m21);
		final SimpleVector camPosition = new SimpleVector(matrix.m30, matrix.m32, matrix.m31);

//		camUp = new SimpleVector(-matrix.m01, -matrix.m11, -matrix.m21);
//
//		final SimpleVector camDirection = new SimpleVector(matrix.m02, -matrix.m12, -matrix.m22);
//		final SimpleVector camPosition = new SimpleVector(matrix.m30, matrix.m31, matrix.m32);

		worldCamera.setOrientation(camDirection, camUp);
		worldCamera.setPosition(camPosition);

		worldCamera.setFovAngle(mFovRadians);
		worldCamera.setYFovAngle(mFovyRadians);

//		Log.i("jPCT-AE", "Camera Position: " + worldCamera.getPosition());
//		Log.i("jPCT-AE", "Camera Direction: " + worldCamera.getDirection());
//		Log.i("jPCT-AE", "Camera Back: " + worldCamera.getBack());
//		Log.i("jPCT-AE", "Camera Up: " + worldCamera.getUpVector());

//		m = projectionMatrix;

//		float near = m[14] * 2.0f / (2.0f * m[10] - 2.0f);
//		float far = near * (m[10] - 1.0f) / (m[10] + 1.0f);

//		Log.i("jPCT-AE", "near: " + near + " far: " + far);

		Config.setParameterValue("nearPlane", 0.2f);
		Config.setParameterValue("farPlane", 500.0f);
//		Config.setParameterValue("glIgnoreNearPlane", false);

//		worldCamera.setFOVLimits(near, far);

//        Log.i("jPCT-AE", "fov: " + mFovRadians + " yfov: " + mFovyRadians);

//		Log.i("jPCT-AE", "Projection_0 \n" + worldCamera.getProjectionMatrix(fb).toString());
//
//		Log.i("jPCT-AE", "Projection");
//		for(int i = 0; i< 4;i++){
//			Log.i("jPCT-AE",
//					String.valueOf(projectionMatrix[i*4]) + " " +
//							String.valueOf(projectionMatrix[i*4 + 1]) + " " +
//							String.valueOf(projectionMatrix[i*4 + 2]) + " " +
//							String.valueOf(projectionMatrix[i*4 + 3])
//			);
//		}


//		Log.i("jPCT-AE","------");
//		Log.i("jPCT-AE",worldCamera.getPosition().toString());
//		Log.i("jPCT-AE",worldCamera.getDirection().toString());
//		Log.i("jPCT-AE","------");

		if (mTargetSizeChanged){
			world.removeObject(cube);
			cube = new Object3D(2);

			cube.addTriangle(
					new SimpleVector(-2,-1.125,0), 0.0f, 0.0f,
					new SimpleVector(2,-1.125,0), 1.0f, 0.0f,
					new SimpleVector(-2,1.125,0), 0.0f, 1.0f,
					TextureManager.getInstance().getTextureID("texture")
			);
			cube.addTriangle(
					new SimpleVector(2,-1.125,0), 1.0f, 0.0f,
					new SimpleVector(2,1.125,0), 1.0f, 1.0f,
					new SimpleVector(-2,1.125,0), 0.0f, 1.0f,
					TextureManager.getInstance().getTextureID("texture")
			);

//				cube = Primitives.getCube(1.5f);
//				cube.calcTextureWrapSpherical();
			cube.rotateX((float)(0.5 * Math.PI));
//				cube.rotateX((float)(Math.PI));
//				cube.translate(new SimpleVector(0,1.5,0));    // x->x y->z z->y
//				cube.setTexture("texture");
			cube.strip();
			cube.build();
			world.addObject(cube);
		}
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

			touchTurn = xd / 100f;
			touchTurnUp = yd / 100f;
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

		private FrameBuffer frameBuffer = null;

		private int fps = 0;
		private int lfps = 0;

		private long fpsTime = System.currentTimeMillis();

		private GLFont glFont;
		private GLFont buttonFont;

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

//				sun = new Light(world);
//				sun.setIntensity(250, 250, 250);

				// Create a texture out of the icon...:-)
				Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.scene_night)), 1024, 1024));
				TextureManager.getInstance().addTexture("texture", texture);

//				Resources res = getResources();
//
//				shader = new GLSLShader(
//						Loader.loadTextFile(res.openRawResource(R.raw.vertexshader_offset)),
//						Loader.loadTextFile(res.openRawResource(R.raw.fragmentshader_offset))
//				);

//                cube = new Object3D(2);
//                cube.addTriangle(
//		                new SimpleVector(-2,-1.125,0), 1.0f, 1.0f,
//		                new SimpleVector(-2,1.125,0), 1.0f, 0.0f,
//		                new SimpleVector(2,-1.125,0), 0.0f, 1.0f,
//                        TextureManager.getInstance().getTextureID("texture")
//                        );
//                cube.addTriangle(
//		                new SimpleVector(-2,1.125,0), 1.0f, 0.0f,
//		                new SimpleVector(2,1.125,0), 0.0f, 0.0f,
//		                new SimpleVector(2,-1.125,0), 0.0f, 1.0f,
//                        TextureManager.getInstance().getTextureID("texture")
//                );
//
////				cube = Primitives.getCube(1.5f);
////				cube.calcTextureWrapSpherical();
//				cube.rotateX((float)(-0.5 * Math.PI));
////				cube.rotateZ((float)(Math.PI));
////				cube.rotateX((float)(Math.PI));
////				cube.translate(new SimpleVector(0,1.5,0));    // x->x y->z z->y
////				cube.setTexture("texture");
//				cube.scale(0.26f);
//				cube.strip();
//				cube.build();
//				world.addObject(cube);

//				world.buildAllObjects();

				TextureManager.getInstance().flush();
				Resources res = getResources();

				Texture tex = new Texture(res.openRawResource(R.raw.ninja_texture));
				texture.keepPixelData(true);
				TextureManager.getInstance().addTexture("ninja", tex);

				try {
					TextureManager.getInstance().addTexture("disco", new Texture(getAssets().open("disco.jpg")));
					mTestObject.setTexture("disco");
				} catch (IOException e) {
					e.printStackTrace();
				}

				for (Animated3D a : masterNinja)
					a.setTexture("ninja");

				for (AnimatedGroup group : ninjas) {
					for (Animated3D a : group)
						a.setTexture("ninja");
				}

				worldCamera = world.getCamera();
//				cam.setFOVLimits(0.5f, 100.0f);
//				cam.moveCamera(com.threed.jpct.Camera.CAMERA_MOVEOUT, 50);
//				cam.lookAt(new SimpleVector(0,0,0));
//
//				Log.i("jPCT-AE", "Cube Center = " + cube.getTransformedCenter().toString());

//				SimpleVector sv = new SimpleVector();
//				sv.set(cube.getTransformedCenter());
//				sv.y -= 100;
//				sv.z -= 100;
//				sun.setPosition(sv);
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
			long now = System.currentTimeMillis();
			aggregatedTime += (now - frameTime);
			frameTime = now;

			if (aggregatedTime > 1000) {
				aggregatedTime = 0;
			}

			if (animation > 0 && masterNinja.getSkinClipSequence().getSize() >= animation) {
				float clipTime = masterNinja.getSkinClipSequence().getClip(animation-1).getTime();
				if (animateSeconds > clipTime) {
					animateSeconds = 0;
				}
				float index = animateSeconds / clipTime;
				if (useMeshAnim) {
					for (AnimatedGroup group : ninjas) {
						for (Animated3D a : group)
							a.animate(index, animation);
					}
				} else {
					for (AnimatedGroup group : ninjas) {
						group.animateSkin(index, animation);
//							if (!group.isAutoApplyAnimation())
//								group.applyAnimation();
					}
				}

			} else {
				animateSeconds = 0f;
			}

			if (touchTurn != 0) {
//				cube.rotateY(touchTurn);
				touchTurn = 0;
			}

			if (touchTurnUp != 0) {
//				cube.rotateX(touchTurnUp);
				touchTurnUp = 0;
			}

			RGBColor transparent = new RGBColor(0,0,0,0);

//			shader.setUniform("modelViewMatrix", cameraMatrix);
//			shader.setUniform("projectionMatrix", projectionMatrix);
//			shader.setUniform("trans", cameraMatrix);
//			shader.setUniform("proj", projectionMatrix);
//			shader.setUniform("heightScale", 0.05f);

			fb.clear(transparent);
            if(mIsTargetDetected){
	            updateCamera();

                world.renderScene(fb);
                world.draw(fb);
                fb.display();

//                SimpleVector center = cube.getCenter();
//                Matrix transM = cube.getTranslationMatrix();
//
//                Log.i("jPCT-AE", transM.toString());
            }

			if (System.currentTimeMillis() - time >= 1000) {
				Logger.log(mFPS + "fps");
				mFPS = 0;
				time = System.currentTimeMillis();
			}
			mFPS++;
		}

		private Object3D loadModel(InputStream stream, float scale) throws IOException {
			Object3D[] model = Loader.load3DS(stream, scale);
			Object3D o3d = new Object3D(0);
			Object3D temp = null;
			for (int i = 0; i < model.length; i++) {
				temp = model[i];
				temp.setCenter(SimpleVector.ORIGIN);
				temp.rotateX((float)( -.5*Math.PI));
				temp.rotateMesh();
				temp.setRotationMatrix(new Matrix());
				o3d = Object3D.mergeObjects(o3d, temp);
				o3d.build();
			}
			return o3d;
		}/** adjusts camera based on current mesh of skinned group.
		 * camera looks at mid point of height and placed at a distance
		 * such that group height occupies 2/3 of screen height. */
		protected void autoAdjustCamera() {
			float[] bb = calcBoundingBox();
			float groupHeight = bb[3] - bb[2];
			cameraController.cameraRadius = calcDistance(world.getCamera(), frameBuffer,
					frameBuffer.getHeight() / 1.5f , groupHeight);
			cameraController.minCameraRadius = groupHeight / 10f;
			cameraController.cameraTarget.y = (bb[3] + bb[2]) / 2;
			cameraController.placeCamera();
		}

		/** calculates and returns whole bounding box of skinned group */
		protected float[] calcBoundingBox() {
			float[] box = null;

			for (Animated3D skin : masterNinja) {
				float[] skinBB = skin.getMesh().getBoundingBox();

				if (box == null) {
					box = skinBB;
				} else {
					// x
					box[0] = Math.min(box[0], skinBB[0]);
					box[1] = Math.max(box[1], skinBB[1]);
					// y
					box[2] = Math.min(box[2], skinBB[2]);
					box[3] = Math.max(box[3], skinBB[3]);
					// z
					box[4] = Math.min(box[4], skinBB[4]);
					box[5] = Math.max(box[5], skinBB[5]);
				}
			}
			return box;
		}

		/**
		 * calculates a camera distance to make object look height pixels on screen
		 * @author EgonOlsen
		 * */
		protected float calcDistance(com.threed.jpct.Camera c, FrameBuffer buffer, float height, float objectHeight) {
			float h = height / 2f;
			float os = objectHeight / 2f;

			com.threed.jpct.Camera cam = new com.threed.jpct.Camera();
			cam.setFOV(c.getFOV());
			SimpleVector p1 = Interact2D.project3D2D(cam, buffer, new SimpleVector(0f, os, 1f));
			float y1 = p1.y - buffer.getCenterY();
			float z = (1f/h) * y1;

			return z;
		}
	}

	private void createMeshKeyFrames() {
		Config.maxAnimationSubSequences = masterNinja.getSkinClipSequence().getSize() + 1; // +1 for whole sequence

		int keyframeCount = 0;
		final float deltaTime = 0.2f; // max time between frames

		for (SkinClip clip : masterNinja.getSkinClipSequence()) {
			float clipTime = clip.getTime();
			int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
			keyframeCount += frames;
		}

		Animation[] animations = new Animation[masterNinja.getSize()];
		for (int i = 0; i < masterNinja.getSize(); i++) {
			animations[i] = new Animation(keyframeCount);
			animations[i].setClampingMode(Animation.USE_CLAMPING);
		}
		//System.out.println("------------ keyframeCount: " + keyframeCount + ", mesh size: " + masterNinja.getSize());
		int count = 0;

		int sequence = 0;
		for (SkinClip clip : masterNinja.getSkinClipSequence()) {
			float clipTime = clip.getTime();
			int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
			float dIndex = 1f / (frames - 1);

			for (int i = 0; i < masterNinja.getSize(); i++) {
				animations[i].createSubSequence(clip.getName());
			}
			//System.out.println(sequence + ": " + clip.getName() + ", frames: " + frames);
			for (int i = 0; i < frames; i++) {
				masterNinja.animateSkin(dIndex * i, sequence + 1);

				for (int j = 0; j < masterNinja.getSize(); j++) {
					Mesh keyframe = masterNinja.get(j).getMesh().cloneMesh(true);
					keyframe.strip();
					animations[j].addKeyFrame(keyframe);
					count++;
					//System.out.println("added " + (i + 1) + " of " + sequence + " to " + j + " total: " + count);
				}
			}
			sequence++;
		}
		for (int i = 0; i < masterNinja.getSize(); i++) {
			masterNinja.get(i).setAnimationSequence(animations[i]);
		}
		masterNinja.get(0).getSkeletonPose().setToBindPose();
		masterNinja.get(0).getSkeletonPose().updateTransforms();
		masterNinja.applySkeletonPose();
		masterNinja.applyAnimation();

		Logger.log("created mesh keyframes, " + keyframeCount + "x" + masterNinja.getSize());
	}

	private void addNinja() {
		if (ninjas.size() == LOCATIONS.length)
			return;

		AnimatedGroup ninja = masterNinja.clone(AnimatedGroup.MESH_DONT_REUSE);
		float[] bb = renderer.calcBoundingBox();
		float radius = (bb[3] - bb[2]) * 0.5f; // half of height
		double angle = Math.toRadians(LOCATIONS[ninjas.size()]);

		ninja.setSkeletonPose(new SkeletonPose(ninja.get(0).getSkeleton()));
//		ninja.getRoot().translate((float)(Math.cos(angle) * radius), 0, (float)(Math.sin(angle) * radius));
		ninja.getRoot().rotateY((float)(-0.5 * Math.PI));
		ninja.getRoot().rotateX((float)(Math.PI));
//		ninja.getRoot().scale(0.2f);

		Mesh mesh = ninja.getRoot().getMesh();
		float[] boundingBox = mesh.getBoundingBox();
		ninja.getRoot().translate(0, -boundingBox[4], 0);

		ninja.addToWorld(world);
		ninjas.add(ninja);
		Logger.log("added new ninja: " + ninjas.size());
	}

	private void removeNinja() {
		if (ninjas.size() == 1)
			return;

		AnimatedGroup ninja = ninjas.remove(ninjas.size()-1);
		ninja.removeFromWorld(world);
		Logger.log("removed ninja: " + (ninjas.size() + 1));
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

		frameTime = System.currentTimeMillis();
		aggregatedTime = 0;

		if (!wakeLock.isHeld())
			wakeLock.acquire();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EasyAR.onPause();
		mJpctSurface.onPause();

		if (wakeLock.isHeld())
			wakeLock.release();
	}
}
