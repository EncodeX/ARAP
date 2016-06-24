package edu.neu.arap.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.MuseumListAdapter;
import edu.neu.arap.adapter.SpacesItemDecoration;

public class MuseumMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, OnItemClickListener,MyItemClickListener{
    private ConvenientBanner convenientBanner;
    private ListView listView;
    MyAdapter mAdapter;
    private String[] spinnerData={"全部","距离优先","好评优先"};
    private ArrayList<String> ADName=new ArrayList<String>();
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_main);
        loadTestDatas();
        ADName.add("航海罗盘");
        ADName.add("青花暗八仙纹克拉克盘");
        ADName.add("青花螭龙五彩四光碗");
        ADName.add("八仙祝寿寿帐");
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
        convenientBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ( (TextView) findViewById(R.id.convenientBannerIntro)).setText(ADName.get(position%4));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        listView=(ListView) findViewById(R.id.museumListView);
        MuseumListAdapter museumAdapter=new MuseumListAdapter(this, this);
        listView.setAdapter(museumAdapter);
        setListViewHeightBasedOnChildren(listView);


        RecyclerView mRecyclerView;
        LinearLayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.explore_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(8));
        mAdapter.setOnItemClickListener(this);

        Spinner spinner= (Spinner) findViewById(R.id.museum_spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, spinnerData));

    }


    private void loadTestDatas() {
        //本地图片集合
        for (int position = 0; position < 4; position++)
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
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    protected void onPause() {
        super.onPause();
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
        intent.putExtra("locationInfoLatitude",mAdapter.getLocationInfoLatitude());
        intent.putExtra("locationInfoLongtitude",mAdapter.getGetLocationInfoLongtitude());
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


}
