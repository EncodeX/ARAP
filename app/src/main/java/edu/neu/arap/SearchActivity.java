package edu.neu.arap;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements MyItemClickListener {
    private Camera camera;
    private Camera.Parameters parameters;
    private float recyclerViewHeight;
    private TranslateAnimation translateAnimationRecyclerView;
    private TranslateAnimation translateAnimationTextViewUp;
    private TranslateAnimation translateAnimationRecyclerViewUp;
    private RelativeLayout.LayoutParams layoutParams;
    private RelativeLayout.LayoutParams layoutParams2;
    private String[] spinnerData={"全部","餐饮","交通","学习","住宿","娱乐","购物"};
    private boolean flag=true;

	@Bind(R.id.explore_button)
	Button findOffButton;
    @Bind(R.id.flashlight_button)
    Button flashOffButton;
    @Bind(R.id.filter_spinner)
    Spinner spinner;

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
        translateAnimationTextViewUp =new TranslateAnimation(0,0,0,-100);
        translateAnimationTextViewUp.setDuration(618);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchActivity.this,spinnerData[position] , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void clickerListener(){
        findViewById(R.id.hint_text).setVisibility(View.INVISIBLE);
        findOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.explore_button).isSelected()) {
                    findViewById(R.id.explore_button).setSelected(false);
                    findViewById(R.id.hint_text).startAnimation(translateAnimationTextViewUp);
                    findViewById(R.id.explore_list).startAnimation(translateAnimationRecyclerView);
                    view.startAnimation(translateAnimationRecyclerView);
                } else {
                    findOffButton.setSelected(true);
                    findViewById(R.id.hint_text).setVisibility(View.VISIBLE);
                    if(flag)
                    {
                        setLayout();
                    }
                    findViewById(R.id.explore_list).startAnimation(translateAnimationRecyclerViewUp);
                    view.startAnimation(translateAnimationRecyclerViewUp);
                    findOffButton.setLayoutParams(layoutParams2);
                }
            }
        });
        flashOffButton.setOnClickListener(new View.OnClickListener() {
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
    }

    private void setLayout(){
        flag=false;
        recyclerViewHeight=findViewById(R.id.expand_area).getHeight();
        layoutParams=new RelativeLayout.LayoutParams(findOffButton.getHeight(),findOffButton.getWidth());
        layoutParams2=new RelativeLayout.LayoutParams(findOffButton.getHeight(),findOffButton.getWidth());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.addRule(RelativeLayout.ABOVE, R.id.expand_area);
        layoutParams.rightMargin=8;
        layoutParams2.rightMargin=8;
        translateAnimationRecyclerViewUp=new TranslateAnimation(0,0,recyclerViewHeight,0);
        translateAnimationRecyclerViewUp.setDuration(618);
        translateAnimationRecyclerViewUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.explore_list).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        translateAnimationTextViewUp.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                findViewById(R.id.hint_text).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }
        });
        translateAnimationRecyclerView =new TranslateAnimation(0,0,0,recyclerViewHeight);
        translateAnimationRecyclerView.setDuration(618);
        translateAnimationRecyclerView.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                findViewById(R.id.explore_list).setVisibility(View.INVISIBLE);
                findOffButton.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }
        });
    }
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "图片"+position, Toast.LENGTH_SHORT).show();
    }
}
