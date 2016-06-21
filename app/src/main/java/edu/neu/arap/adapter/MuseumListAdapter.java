package edu.neu.arap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.arap.R;
import edu.neu.arap.activity.MuseumMainActivity;
import edu.neu.arap.activity.ViewHolder;

/**
 * Created by yuziw on 2016/6/17.
 */
public class MuseumListAdapter extends BaseAdapter {
    private List<Map<String, Object>> mData;
    private MuseumMainActivity museumMainActivity;
    private LayoutInflater mInflater;


    public MuseumListAdapter(MuseumMainActivity museumMainActivity, Context context) {
        this.museumMainActivity = museumMainActivity;
        this.mInflater = LayoutInflater.from(context);
        mData=getData();
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

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.museum_list_item, null);
            holder.mimage = (ImageView) convertView.findViewById(R.id.museumListViewImage);
            holder.mname = (TextView) convertView.findViewById(R.id.museumListViewName);
            holder.mintro = (TextView) convertView.findViewById(R.id.museumListViewIntro);
            holder.mark = (TextView) convertView.findViewById(R.id.museumMark);
            //    holder.mbtn = (Button)convertView.findViewById(R.id.museumListViewBtn);
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
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
                Toast.makeText(v.getContext(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

}
