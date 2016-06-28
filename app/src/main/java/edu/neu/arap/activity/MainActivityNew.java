package edu.neu.arap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.SpacesItemDecoration;

public class MainActivityNew extends AppCompatActivity implements MyItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.explore_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        MyAdapter mAdapter;
        mAdapter = new MyAdapter(this,null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);

        ////////////////
        RecyclerView mRecyclerView2;
        LinearLayoutManager mLayoutManager2;
        mRecyclerView2 = (RecyclerView) findViewById(R.id.explore_list2);
        mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager2);
        MyAdapter mAdapter2;
        mAdapter2 = new MyAdapter(this,null);
        mRecyclerView2.setAdapter(mAdapter2);
        mRecyclerView2.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);

        findViewById(R.id.movie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSub(v);
            }
        });
        findViewById(R.id.travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSub(v);
            }
        });
        findViewById(R.id.leisure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSub(v);
            }
        });
        findViewById(R.id.restaurant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSub(v);
            }
        });
    }
    @Override
    public void onItemClick(View view, int position) {

    }
    private void gotoSub(View v){
        Intent intent=new Intent(this,SubActivity.class);
        intent.putExtra("type",v.getId());
        startActivity(intent);
    }
}
