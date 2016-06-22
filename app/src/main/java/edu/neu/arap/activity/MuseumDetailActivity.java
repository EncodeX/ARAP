package edu.neu.arap.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MuseumDetailAdapter;
import edu.neu.arap.map.MapActivity;

public class MuseumDetailActivity extends AppCompatActivity {

    private int RPosition;
    private int[] resID;
    private String[] resName;
    private String[] resIntro;
    private ListView listView;
    private double[] locationInfoLatitude,locationInfoLongtitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail_activitydetail);
        Intent intent=getIntent();
        RPosition=intent.getIntExtra("RPosition",0);
        resID=intent.getIntArrayExtra("resID");
        resName=intent.getStringArrayExtra("resName");
        resIntro=intent.getStringArrayExtra("resIntro");
        locationInfoLatitude= intent.getDoubleArrayExtra("locationInfoLatitude");
        locationInfoLongtitude= intent.getDoubleArrayExtra("locationInfoLongtitude");
        findViewById(R.id.museum_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MuseumDetailActivity.this,MuseumMainActivity.class));
                finish();
            }
        });
        ((TextView)findViewById(R.id.museum_detail_name)).setText(resName[RPosition]);
        ((TextView)findViewById(R.id.museum_detail_intro)).setText(resIntro[RPosition]);
        ((ImageView)findViewById(R.id.museum_detail_image)).setImageResource(resID[RPosition]);
        ((TextView)findViewById(R.id.museum_detail_name_2)).setText(resName[RPosition]);
        listView=(ListView) findViewById(R.id.museum_detail_list);
        MuseumDetailAdapter adapter=new MuseumDetailAdapter(this,RPosition);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);
        findViewById(R.id.gotoMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MuseumDetailActivity.this, MapActivity.class);
                intent1.putExtra("RPosition",RPosition);
                intent1.putExtra("resName",resName);
                intent1.putExtra("locationInfoLatitude",locationInfoLatitude);
                intent1.putExtra("locationInfoLongtitude",locationInfoLongtitude);
                startActivity(intent1);
            }
        });
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount()));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}

