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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MuseumDetailAdapter;
import edu.neu.arap.map.MapActivity;
import edu.neu.arap.tool.NetworkTool;

public class MuseumDetailActivity extends AppCompatActivity {

    private int RPosition;
    MuseumDetailAdapter adapter;
    int count;
    private ArrayList<String > resID=new ArrayList<String>();
    private ArrayList<String> resName=new ArrayList<String>();
    private ArrayList<String> resIntro=new ArrayList<String>();
    private ArrayList<String> showId=new ArrayList<String>();
    private ListView listView;
    private ArrayList<Double> locationInfoLatitude=new ArrayList<Double>();
    private ArrayList<Double> locationInfoLongtitude=new ArrayList<Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail_activitydetail);
        Intent intent=getIntent();
        RPosition=intent.getIntExtra("RPosition",0);
        resID=intent.getStringArrayListExtra("resID");
        resName=intent.getStringArrayListExtra("resName");
        showId=intent.getStringArrayListExtra("showId");
        resIntro=intent.getStringArrayListExtra("resIntro");
        locationInfoLatitude= (ArrayList<Double>) intent.getSerializableExtra("locationInfoLatitude");
        locationInfoLongtitude= (ArrayList<Double>) intent.getSerializableExtra("locationInfoLongtitude");
        findViewById(R.id.museum_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((TextView)findViewById(R.id.museum_detail_name)).setText(resName.get(RPosition));
        ((TextView)findViewById(R.id.museum_detail_intro)).setText(resIntro.get(RPosition));
      //  ((ImageView)findViewById(R.id.museum_detail_image)).setImageResource(resID[RPosition]);
        Picasso.with(this).load(resID.get(RPosition)).into((ImageView)findViewById(R.id.museum_detail_image));
                // Picasso.with(context).load(resID.get(position)).centerCrop().into(MyViewHolder.getImageButton());
        ((TextView)findViewById(R.id.museum_detail_name_2)).setText(resName.get(RPosition));
        listView=(ListView) findViewById(R.id.museum_detail_list);
        adapter=new MuseumDetailAdapter(this,Integer.parseInt(showId.get(RPosition)));
        listView.setAdapter(adapter);
       // adapter.notifyDataSetChanged();
      //  setListViewHeightBasedOnChildren(listView);
        findViewById(R.id.gotoMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(MuseumDetailActivity.this, MapActivity.class);
                intent1.putExtra("RPosition",RPosition);
                intent1.putExtra("resName",resName);
                intent1.putExtra("locationInfoLatitude",(Serializable) locationInfoLatitude);
                intent1.putExtra("locationInfoLongtitude",(Serializable) locationInfoLongtitude);
                startActivity(intent1);
            }
        });
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        //ListAdapter listAdapter = listView.getAdapter();
        ListAdapter listAdapter = adapter;
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        NetworkTool networkTool=new NetworkTool(this);
        networkTool.requestMuseumData(Integer.parseInt(showId.get(RPosition)), new NetworkTool.OnResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arItems=response.getJSONArray("ar_items");
                    count=arItems.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
      //  Toast.makeText(this,count,Toast.LENGTH_SHORT).show();
        for (int i = 0, len = count; i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (count));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

}

