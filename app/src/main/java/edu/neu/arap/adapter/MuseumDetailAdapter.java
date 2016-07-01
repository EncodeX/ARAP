package edu.neu.arap.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import edu.neu.arap.activity.AugmentedActivity;
import edu.neu.arap.activity.ViewHolder;
import edu.neu.arap.map.MapActivity;
import edu.neu.arap.tool.NetworkTool;

/**
 * Created by yuziw on 2016/6/17.
 */
public class MuseumDetailAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private NetworkTool networkTool;
    private String showName;
    private Context context;
    private Double locationInfoLatitude=0.0;
    private Double locationInfoLongtitude=0.0;
    private String resVote;
    private int showId;
    private List<Map<String, Object>> mData;
    public MuseumDetailAdapter(Context context,int showId,String showName,Double locationInfoLatitude,Double locationInfoLongtitude,String resVote  ){
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.showId=showId;
        this.showName=showName;
        this.locationInfoLatitude=locationInfoLatitude;
        this.locationInfoLongtitude=locationInfoLongtitude;
        this.resVote=resVote;
        mData=getData();
    }

    private List<Map<String, Object>> getData(){
        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(new HashMap<String, Object>());
        networkTool=new NetworkTool(context);
        networkTool.requestMuseumData(showId, new NetworkTool.OnResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arItems=response.getJSONArray("ar_items");
                    for (int i=0;i<arItems.length();i++)
                    {
                        JSONObject arItem=arItems.getJSONObject(i);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("title",arItem.getString("title"));
                        map.put("arVote",arItem.getString("ar_vote"));
                        map.put("arAddress",arItem.getString("ar_address"));
                        map.put("image",arItem.getJSONArray("ar_material").getJSONObject(0).getString("material_address"));
                        list.add(map);
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
//        switch (museumID)
//        {
//            case 1:
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("name", "波须圣母玻璃反画");
//                map.put("intro", "这件玻璃反画是复制品，原件是一件17世纪圣母玛利亚像，神圣罗马帝国皇帝利奥波德一世的皇后埃利诺·玛格达莱妮称之为蔷薇圣母，原件至今仍摆放在维也纳圣史蒂芬大教堂中殿西南角的圣坛上。");
//                map.put("image", R.drawable.a);
//                map.put("mark","评分4.9");
//                list.add(map);
//                map = new HashMap<String, Object>();
//                map.put("name", "彩色拼花窗玻璃");
//                map.put("intro", "这块窗玻璃来自库特纳霍拉（德语：库滕贝格，直译“矿山”）的圣芭芭拉教堂，图案是正在采矿的矿工。库特纳霍拉富含银矿，是波希米亚第二大城市，仅次于布拉格，但在经济上和政治上独占鳌头。");
//                map.put("image", R.drawable.cai_se_bo_li);
//                map.put("mark","评分4.9");
//                list.add(map);
//                map = new HashMap<String, Object>();
//                map.put("name", "贝壳形细口瓶");
//                map.put("intro", "蓝色玻璃，用金属模具制成，镶锡口。这件细口瓶是朝圣纪念品。扇贝贝壳是圣詹姆斯的纹章，也是朝圣者的象征。");
//                map.put("image", R.drawable.xi_kou_ping);
//                map.put("mark","评分4.9");
//                list.add(map);
//                break;
//            case 2:
//                break;
//        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        notifyDataSetChanged();
        if (position==0)
        {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.museum_list_item_top, null);
                holder.mname = (TextView) convertView.findViewById(R.id.museum_detail_name_2);
                holder.mark =(TextView)convertView.findViewById(R.id.museum_detail_mark);
                convertView.setTag(holder);
            }
            else {

                holder = (ViewHolder) convertView.getTag();
            }
            holder.mname.setText(showName);
            holder.mark.setText("评分:"+resVote);
            ((Button)convertView.findViewById(R.id.gotoMap)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1=new Intent(context, MapActivity.class);
                    intent1.putExtra("resName",showName);
                    intent1.putExtra("locationInfoLatitude",locationInfoLatitude);
                    intent1.putExtra("locationInfoLongtitude",locationInfoLongtitude);
                    context.startActivity(intent1);
                }
            });
            return convertView;
        }
        convertView=null;
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

        //holder.mimage.setBackgroundResource((Integer)mData.get(position).get("image"));
        Picasso.with(context).load((String) mData.get(position).get("image")).into(holder.mimage);
        holder.mname.setText((String)mData.get(position).get("title"));
        holder.mintro.setText((String)mData.get(position).get("arAddress"));
        holder.mark.setText("评分："+(String)mData.get(position).get("arVote"));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                networkTool.requestMuseumData(showId, new NetworkTool.OnResponseListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arItems=response.getJSONArray("ar_items");
                        //    Toast.makeText(context,arItems.getJSONObject(position-1).toString(),Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(context,AugmentedActivity.class);
                            intent.putExtra("JSONObject",arItems.getJSONObject(position-1).toString());
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(VolleyError error) {

                    }
                });
//                Intent intent=new Intent(context,AugmentedActivity.class);
//                intent.putExtra("museumDetailData",(Serializable)mData);//    private List<Map<String, Object>> mData;
//                intent.putExtra("museumDetailPosition",position);//position :int
//              //  context.startActivity(intent);
            }
        });
        return convertView;

    }
}
