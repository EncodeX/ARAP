package edu.neu.arap;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
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
    private RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(100,100);
    private RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(100,100);

	@Bind(R.id.findOff)
	Button findOffButton;
    @Bind(R.id.flashOff)
    Button flashOffButton;

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
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MyAdapter mAdapter;
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);
        translateAnimationTextViewUp =new TranslateAnimation(0,0,0,-100);
        translateAnimationTextViewUp.setDuration(618);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams2.addRule(RelativeLayout.ABOVE,R.id.recycler_view_canvas);
        layoutParams.rightMargin=8;
        layoutParams2.rightMargin=8;
        findOffButton.setLayoutParams(layoutParams);
    }
    private void clickerListener(){
        findViewById(R.id.textView).setVisibility(View.INVISIBLE);
        findOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.findOff).isSelected()) {
                    findViewById(R.id.findOff).setSelected(false);
                    translateAnimationTextViewUp.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(android.view.animation.Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(android.view.animation.Animation animation) {
                            findViewById(R.id.textView).setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(android.view.animation.Animation animation) {
                        }
                    });
                    findViewById(R.id.textView).startAnimation(translateAnimationTextViewUp);
                    translateAnimationRecyclerView =new TranslateAnimation(0,0,0,recyclerViewHeight);
                    translateAnimationRecyclerView.setDuration(618);
                    translateAnimationRecyclerView.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(android.view.animation.Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(android.view.animation.Animation animation) {
                            findViewById(R.id.my_recycler_view).setVisibility(View.INVISIBLE);
                            findOffButton.setLayoutParams(layoutParams);
                        }

                        @Override
                        public void onAnimationRepeat(android.view.animation.Animation animation) {
                        }
                    });
                    findViewById(R.id.my_recycler_view).startAnimation(translateAnimationRecyclerView);
                    view.startAnimation(translateAnimationRecyclerView);
                 //   findOffButton.setY(recyclerViewHeight);

                } else {
                    findOffButton.setSelected(true);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    recyclerViewHeight=findViewById(R.id.recycler_view_canvas).getHeight();
                    translateAnimationRecyclerViewUp=new TranslateAnimation(0,0,recyclerViewHeight,0);
                    translateAnimationRecyclerViewUp.setDuration(618);
                    translateAnimationRecyclerViewUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            findViewById(R.id.my_recycler_view).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    findViewById(R.id.my_recycler_view).startAnimation(translateAnimationRecyclerViewUp);
                    view.startAnimation(translateAnimationRecyclerViewUp);
                 //   findOffButton.setY(-recyclerViewHeight);
                    findOffButton.setLayoutParams(layoutParams2);
                }
            }
        });
        flashOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.flashOff).isSelected()) {
                    findViewById(R.id.flashOff).setSelected(false);
                    parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.release();
                } else {
                    findViewById(R.id.flashOff).setSelected(true);
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
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "图片"+position, Toast.LENGTH_SHORT).show();
    }
}
