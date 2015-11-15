package edu.neu.arap;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.threed.jpct.Animation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements MyItemClickListener {
    private Camera camera;
    private Camera.Parameters parameters;
    private TranslateAnimation translateAnimation;
    private TranslateAnimation translateAnimationUp;
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
        translateAnimationUp=new TranslateAnimation(0,0,0,-100);
        translateAnimationUp.setDuration(618);
    }
    private void clickerListener(){
        findViewById(R.id.textView).setVisibility(View.INVISIBLE);
        findOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(R.id.findOff).isSelected()) {
                    findViewById(R.id.findOff).setSelected(false);
                    translateAnimationUp.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
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
                    findViewById(R.id.textView).startAnimation(translateAnimationUp);
                    translateAnimation=new TranslateAnimation(0,0,0,findViewById(R.id.recycler_view_canvas).getHeight());
                    translateAnimation.setDuration(618);
                    translateAnimation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(android.view.animation.Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(android.view.animation.Animation animation) {
                            findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationRepeat(android.view.animation.Animation animation) {
                        }
                    });
                    findViewById(R.id.my_recycler_view).startAnimation(translateAnimation);
                    view.startAnimation(translateAnimation);
                } else {
                    findViewById(R.id.findOff).setSelected(true);
                    findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    findViewById(R.id.my_recycler_view).setVisibility(View.VISIBLE);
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
