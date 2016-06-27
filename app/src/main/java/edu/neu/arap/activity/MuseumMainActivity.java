package edu.neu.arap.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.android.volley.VolleyError;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.MuseumListAdapter;
import edu.neu.arap.adapter.SpacesItemDecoration;
import edu.neu.arap.tool.NetworkTool;

public class MuseumMainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener,AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, OnItemClickListener,MyItemClickListener{
    private ConvenientBanner convenientBanner;
    private NetworkTool networkTool;
    private ListView listView;
    MyAdapter mAdapter;
    private String[] spinnerData={"距离优先","好评优先"};
    private ArrayList<String> ADName=new ArrayList<String>();
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private AMap aMap2;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_main);
        loadTestDatas();
//        ADName.add("航海罗盘");
//        ADName.add("青花暗八仙纹克拉克盘");
//        ADName.add("青花螭龙五彩四光碗");
//        ADName.add("八仙祝寿寿帐");
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
              //  .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                .setOnItemClickListener(this);
//        convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                ( (TextView) findViewById(R.id.convenientBannerIntro)).setText(ADName.get(position%2));
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


        listView=(ListView) findViewById(R.id.museumListView);
        MuseumListAdapter museumAdapter=new MuseumListAdapter(this, this);
        listView.setAdapter(museumAdapter);
        setListViewHeightBasedOnChildren(listView);


        final RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.explore_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new MyAdapter(this);
//        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
//        mAdapter.setOnItemClickListener(this);

        Spinner spinner= (Spinner) findViewById(R.id.museum_spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData));
        networkTool=new NetworkTool(this);


        mapView=new MapView(this);
        aMap2=mapView.getMap();
        aMap2.setLocationSource(this);// 设置定位监听
        aMap2.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap2.setMyLocationEnabled(true);
        aMap2.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);


        final Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (aMap2.getMyLocation()==null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkTool.requestMuseumMainData(aMap2.getMyLocation().getLatitude(), aMap2.getMyLocation().getLongitude(), new NetworkTool.OnResponseListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray top=response.getJSONArray("top");
                                    for (int i=0;i<top.length();i++)
                                    {
                                        JSONObject topObjection=top.getJSONObject(i);
                                        ADName.add(topObjection.getString("show_name"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(VolleyError error) {

                            }
                        });
                      //  Toast.makeText(MuseumMainActivity.this,"Latitude:"+aMap2.getMyLocation().getLatitude()+"Longtitude:"+aMap2.getMyLocation().getLongitude(),Toast.LENGTH_SHORT).show();
                        convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                ( (TextView) findViewById(R.id.convenientBannerIntro)).setText(ADName.get(position%ADName.size()));
                            }

                            @Override
                            public void onPageSelected(int position) {

                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });

                        mAdapter = new MyAdapter(findViewById(R.id.explore_list).getContext(),aMap2);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
                        mAdapter.setOnItemClickListener(MuseumMainActivity.this);
                    }
                });
            }
        });
        thread.start();
    }


    private void loadTestDatas() {
        //本地图片集合
        for (int position = 0; position < 2; position++)
            localImages.add(getResId("ic_test_" + position, R.drawable.class));
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 开始自动翻页
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
        //停止翻页
        convenientBanner.stopTurning();
    }

    //点击切换效果
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

//        点击后加入两个内容
//        localImages.clear();
//        localImages.add(R.drawable.ic_test_2);
//        localImages.add(R.drawable.ic_test_4);
//        convenientBanner.notifyDataSetChanged();

        //控制是否循环
//        convenientBanner.setCanLoop(!convenientBanner.isCanLoop());


        String transforemerName = DefaultTransformer.class.getSimpleName();
        try {
            Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + transforemerName);
            ABaseTransformer transforemer= (ABaseTransformer)cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true,transforemer);

            //部分3D特效需要调整滑动速度
            if(transforemerName.equals("StackTransformer")){
                convenientBanner.setScrollDuration(1200);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }


    @Override
    public void onPageSelected(int position) {
        Toast.makeText(this,"监听到翻到第"+position+"了",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this,"点击了第"+position+"个",Toast.LENGTH_SHORT).show();
    }


    //recycleView的点击事件
    @Override
    public void onItemClick(View view, int postion) {
        //String[] t=mAdapter.getResName();
       // Toast.makeText(this,t[postion] , Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MuseumDetailActivity.class);
        intent.putExtra("RPosition",postion);
        intent.putExtra("locationInfoLatitude",(Serializable) mAdapter.getLocationInfoLatitude());
        intent.putExtra("locationInfoLongtitude",(Serializable)mAdapter.getGetLocationInfoLongtitude());
        intent.putExtra("resID",mAdapter.getResID());
        intent.putExtra("resName",mAdapter.getResName());
        intent.putExtra("resIntro",mAdapter.getResIntro());
        startActivity(intent);
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


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
