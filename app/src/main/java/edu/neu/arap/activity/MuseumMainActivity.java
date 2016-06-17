package edu.neu.arap.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.arap.R;
import edu.neu.arap.adapter.MyAdapter;
import edu.neu.arap.adapter.MyItemClickListener;
import edu.neu.arap.adapter.SpacesItemDecoration;

public class MuseumMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ViewPager.OnPageChangeListener, OnItemClickListener,MyItemClickListener{
    private ConvenientBanner convenientBanner;
    private List<Map<String, Object>> mData;
    private ListView listView;
    MyAdapter mAdapter;
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

        mData=getData();
        listView=(ListView) findViewById(R.id.museumListView);
        MyyAdapter museumAdapter=new MyyAdapter(this);
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








    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "杨德衡艺术展");
        map.put("intro", "本次展览分为三个部分，分别从“善师古人”、“勤师造化”、“中得心源”不同的角度展示了杨德衡先生从艺60年来精彩的艺术人生。");
        map.put("image", R.drawable.he_xian_tu);
        map.put("mark","评分4.9");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "侯北人捐赠作品展");
        map.put("intro", "欢迎走进辽宁省博物馆侯北人张韵琴绘画馆，感受辽宁籍旅美画家侯北人先生的绘画艺术和爱国情怀。");
        map.put("image", R.drawable.he_tang_chun_yu);
        map.put("mark","评分4.9");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "意大利记者镜头中的中国");
        map.put("intro", "2015年是中意两国建交45周年，为纪念这一历史性时刻，促进中意文化艺术交流与合作，我馆特别引进举办《一个意大利记者镜头中的中国》摄影作品展。");
        map.put("image", R.drawable.shenyang);
        map.put("mark","评分4.9");
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("name", "欧洲瓷器展");
        map.put("intro", "9世纪下半叶以来，中国瓷器通过陆上和海上丝绸之路源源不断地输往世界各地，在中西文明交流中起着桥梁和纽带的作用。");
        map.put("image", R.drawable.pan_zi);
        map.put("mark","评分4.9");
        list.add(map);


        return list;
    }

    @Override
    public void onItemClick(View view, int postion) {
        //String[] t=mAdapter.getResName();
       // Toast.makeText(this,t[postion] , Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MuseumDetailActivitydetail.class);
        intent.putExtra("RPosition",postion);
        intent.putExtra("resID",mAdapter.getResID());
        intent.putExtra("resName",mAdapter.getResName());
        startActivity(intent);
    }

    public final class ViewHolder{
        public ImageView mimage;
        public TextView mname;
        public TextView mintro;
        public TextView mark;
      //  public Button mbtn;
    }


    public class MyyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MyyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                holder=new ViewHolder();

                convertView = mInflater.inflate(R.layout.museum_list_item, null);
                holder.mimage = (ImageView)convertView.findViewById(R.id.museumListViewImage);
                holder.mname = (TextView)convertView.findViewById(R.id.museumListViewName);
                holder.mintro = (TextView)convertView.findViewById(R.id.museumListViewIntro);
                holder.mark= (TextView)convertView.findViewById(R.id.museumMark);
            //    holder.mbtn = (Button)convertView.findViewById(R.id.museumListViewBtn);
                convertView.setTag(holder);

            }else {

                holder = (ViewHolder)convertView.getTag();
            }

            holder.mimage.setBackgroundResource((Integer)mData.get(position).get("image"));
            holder.mname.setText((String)mData.get(position).get("name"));
            holder.mintro.setText((String)mData.get(position).get("intro"));
            holder.mark.setText((String)mData.get(position).get("mark"));
          /*  holder.mbtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),position+"",Toast.LENGTH_SHORT).show();
                }
            });*/
            convertView.findViewById(R.id.museumItem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ListViewItem点击事件
                    Toast.makeText(v.getContext(),position+"",Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

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
