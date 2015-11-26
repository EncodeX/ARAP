package edu.neu.arap;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements MyItemClickListener {
    private Camera camera;
    private Camera.Parameters parameters;
    private String[] spinnerData={"全部","餐饮","交通","学习","住宿","娱乐","购物"};
    private boolean flag=true;
    private ObjectAnimator exploreAreaUp,exploreAreaDown;
    private  ObjectAnimator menuUp,menuDown;
    private ObjectAnimator selectShow,selectHide;
    private ObjectAnimator spinnerShow,spinnerHide;
    private ObjectAnimator menuShowX,menuShowY, menuShowSX,menuShowSY,menuShowA;
    private ObjectAnimator menuHideX,menuHideY, menuHideSX,menuHideSY,menuHideA;
    private ObjectAnimator menuBtnHideSX,menuBtnHideSY,menuBtnHideX,menuBtnHideY,menuBtnHideA;
    private ObjectAnimator menuBtnShowSX,menuBtnShowSY,menuBtnShowX,menuBtnShowY,menuBtnShowA;
    private  AnimatorSet exploreUp,exploreHide,menuShow,menuHide;
    private float distanceX,distanceY;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        clickerListener();
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
                Toast.makeText(SearchActivity.this, spinnerData[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void clickerListener(){
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.explore_button).isSelected()) {
                    findViewById(R.id.explore_button).setSelected(false);
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
        flashButton.setOnClickListener(new View.OnClickListener() {
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
        });
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
                }
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuShowAnimation();
            }
        });
        findViewById(R.id.menu_background_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuHideAnimation();
            }
        });
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
        String[] resName={"蚁人","火星救援","捉妖记","秦时明月","完美的世界","港囧","重返20岁","移动迷宫","澳门风云","九层妖塔"};
        Toast.makeText(this,resName[position] , Toast.LENGTH_SHORT).show();
    }
}
