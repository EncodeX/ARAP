package edu.neu.arap;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements MyItemClickListener {
    private Camera camera;
    private Camera.Parameters parameters;


	@Bind(R.id.findOff)
	Button findOffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

	    ButterKnife.bind(this);

        RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
	    //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
		//如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        //   mRecyclerView.setHasFixedSize(true);
		//创建并设置Adapter
        MyAdapter mAdapter;
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);

        findViewById(R.id.flashOn).setVisibility(View.INVISIBLE);
        findViewById(R.id.my_recycler_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView).setVisibility(View.INVISIBLE);

        findOffButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
		        if (findViewById(R.id.findOff).isSelected()) {
			        findViewById(R.id.findOff).setSelected(false);
			        findViewById(R.id.textView).setVisibility(View.GONE);
			        findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
		        } else {
			        findViewById(R.id.findOff).setSelected(true);
			        findViewById(R.id.textView).setVisibility(View.VISIBLE);
			        findViewById(R.id.my_recycler_view).setVisibility(View.VISIBLE);
		        }
	        }
        });

        findViewById(R.id.flashOn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashOff).setVisibility(View.VISIBLE);
                findViewById(R.id.flashOn).setVisibility(View.INVISIBLE);
                parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                camera.release();
            }
        });

        findViewById(R.id.flashOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashOff).setVisibility(View.INVISIBLE);
                findViewById(R.id.flashOn).setVisibility(View.VISIBLE);
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
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "图片"+position, Toast.LENGTH_SHORT).show();
    }
}
