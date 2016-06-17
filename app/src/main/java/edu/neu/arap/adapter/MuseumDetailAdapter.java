package edu.neu.arap.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.arap.R;
import edu.neu.arap.activity.MainActivity;
import edu.neu.arap.activity.MuseumDetailActivity;
import edu.neu.arap.activity.MuseumMainActivity;
import edu.neu.arap.activity.ViewHolder;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by yuziw on 2016/6/17.
 */
public class MuseumDetailAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Context context;
    private int museumID;
    private List<Map<String, Object>> mData;
    public MuseumDetailAdapter(Context context,int museumID){
        this.mInflater = LayoutInflater.from(context);
        this.museumID=museumID;
        mData=getData();
        this.context=context;
    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        switch (museumID)
        {
            case 1:
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", "波须圣母玻璃反画");
                map.put("intro", "这件玻璃反画是复制品，原件是一件17世纪圣母玛利亚像，神圣罗马帝国皇帝利奥波德一世的皇后埃利诺·玛格达莱妮称之为蔷薇圣母，原件至今仍摆放在维也纳圣史蒂芬大教堂中殿西南角的圣坛上。");
                map.put("image", R.drawable.a);
                map.put("mark","评分4.9");
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("name", "彩色拼花窗玻璃");
                map.put("intro", "这块窗玻璃来自库特纳霍拉（德语：库滕贝格，直译“矿山”）的圣芭芭拉教堂，图案是正在采矿的矿工。库特纳霍拉富含银矿，是波希米亚第二大城市，仅次于布拉格，但在经济上和政治上独占鳌头。");
                map.put("image", R.drawable.cai_se_bo_li);
                map.put("mark","评分4.9");
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("name", "贝壳形细口瓶");
                map.put("intro", "蓝色玻璃，用金属模具制成，镶锡口。这件细口瓶是朝圣纪念品。扇贝贝壳是圣詹姆斯的纹章，也是朝圣者的象征。");
                map.put("image", R.drawable.xi_kou_ping);
                map.put("mark","评分4.9");
                list.add(map);
                break;
            case 2:
                break;
        }
        return list;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.museum_list_item, null);
            holder.mimage = (ImageView) convertView.findViewById(R.id.museumListViewImage);
            holder.mname = (TextView) convertView.findViewById(R.id.museumListViewName);
            holder.mintro = (TextView) convertView.findViewById(R.id.museumListViewIntro);
            holder.mark = (TextView) convertView.findViewById(R.id.museumMark);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.mimage.setBackgroundResource((Integer)mData.get(position).get("image"));
        holder.mname.setText((String)mData.get(position).get("name"));
        holder.mintro.setText((String)mData.get(position).get("intro"));
        holder.mark.setText((String)mData.get(position).get("mark"));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,MainActivity.class));
            }
        });
        return convertView;

    }
}
