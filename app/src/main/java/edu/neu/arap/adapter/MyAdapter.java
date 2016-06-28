package edu.neu.arap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.android.volley.VolleyError;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.neu.arap.R;
import edu.neu.arap.activity.MuseumMainActivity;
import edu.neu.arap.tool.ImageCache;
import edu.neu.arap.tool.NetworkTool;

/**
 * Created by 志伟 on 2015/11/10.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private NetworkTool networkTool;
    private Context context;
//    private  double[] locationInfoLatitude={41.67779212,41.65592365,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212};
    private ArrayList<Double> locationInfoLatitude=new ArrayList<Double>();
    private ArrayList<Double> locationInfoLongtitude=new ArrayList<Double>();
   // private  double[] getLocationInfoLongtitude={123.45861554,123.42680576,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554};
   // private int[] resID={0,R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,0};
    private ArrayList<String> resID=new ArrayList<String>();
    private  ArrayList<String> showId=new ArrayList<String>();
//    private String[] resName={"useless","晶火传奇艺术展","明清瓷器展","明清玉器展","万历海贸传奇","辽宁民间绣品展","拿破仑文物展","中国古代货币展","useless"};
    private ArrayList<String> resName=new ArrayList<String>();
//    private String[] resIntro={
//            "useless",
//            "  玻璃的魅力在于它是一种充满张力的神奇物质，柔时似水，钢时如钻，极强的可塑性造就了它变幻无穷的非凡美丽。在光与影的参与下，玻璃就有了魂，令人产生如梦如幻之感。",
//            "  瓷器是中国人民的伟大发明，千百年来一直浓缩和传承着中华民族的杰出智慧和创新精神。明清瓷器，集中国古代工艺之大成，忠实地记录了瓷工们的不朽功绩，也折射出了创造它们的时代的特有光辉。",
//            "  中国古代玉器艺术发轫于新石器时代，历经商周、两汉、唐宋等几个发展高潮，至明清时期达到鼎盛。明清时期玉器沿袭唐宋以来世俗化、生活化的风格并加以发扬，尤其是乾隆时期，因玉料资源充足，玉材质地精良，玉器造型规矩，琢磨精致，集历代之大成，达到了玉器史的巅峰。",
//            "  中华民族伟大的航海足迹，渊源悠久。海上丝绸之路化汪洋为通衢，以秦汉肇始，历隋唐发展，经宋元繁荣，在明代迎来了中国海洋贸易的又一个黄金时代。大批中国商品远销海外，中国正式进入了世界贸易体系。",
//            "  刺绣的历史由来已久，作为高档手工艺品，长久以来主要流行于上流社会，对大多数民众来说，是可见不可得的奢侈品。辽宁民间刺绣源于百姓现实生活，出自市井乡村女儿之手，虽不及王公贵族的奢华精致，但集实用和装饰为一体，质朴、风趣的表现手法极富感染力，在中国民间绣品中占有一席之地。",
//            "  为纪念中法建交 50 周年，湖北省博物馆、南京博物院、辽宁省博物馆、天津博物馆与法国拿破仑基金会共同举办《飞越欧洲的雄鹰—拿破仑文物特展》，使广大观众有机会了解这位法国历史上的伟大人物。",
//            "  历代货币是辽宁省博物馆特色收藏之一。馆藏万余件货币类文物绵延有续而不乏诸多珍品，其中尤以清末著名泉家李佐贤私人收藏之《古泉汇》、《续古泉汇》藏品合计18函3137枚藏泉最为难得，长期以来它一直为国内外钱币学家及史学家所关注。",
//            "useless"
//    };
    private ArrayList<String> resIntro=new ArrayList<String >();

	private ImageCache mImageCache;

    public MyAdapter(final Context context){
        super();
        this.context=context;
        networkTool=new NetworkTool(context);
	    mImageCache = new ImageCache(context);

	    mImageCache.setOnBitmapPreparedListener(new ImageCache.OnBitmapPreparedListener() {
		    @Override
		    public void onBitmapPrepared(Bitmap bitmap, String tag) {
				Log.i("Picasso", "onBitmapPrepared "+ tag);
		    }
	    });
    }

	public void setAMap(AMap aMap){
		if (aMap==null)
			return;

		networkTool.requestMuseumMainData(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude(), new NetworkTool.OnResponseListener() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray middle=response.getJSONArray("middle");
					resName.add("useless");
					resIntro.add("useless");
					locationInfoLatitude.add(0.0);
					locationInfoLongtitude.add(0.0);
					resID.add("useless");
					showId.add("useless");
					for (int i=0;i<middle.length();i++)
					{
						JSONObject midObjection=middle.getJSONObject(i);
						resName.add(midObjection.getString("show_name"));
						resIntro.add(midObjection.getString("show_description"));
						locationInfoLatitude.add(midObjection.getDouble("show_latitude"));
						locationInfoLongtitude.add(midObjection.getDouble("show_longitude"));
						resID.add(midObjection.getString("show_imgaddress"));
						showId.add(midObjection.getString("show_id"));
					}
					resID.add("useless");
					showId.add("useless");
					locationInfoLatitude.add(0.0);
					locationInfoLongtitude.add(0.0);
					resName.add("useless");
					resIntro.add("useless");
					notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onError(VolleyError error) {

			}
		});
	}

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageButton;
        private TextView textView;
        private MyItemClickListener mListener;
        public MyViewHolder(View root, MyItemClickListener listener) {
            super(root);
            textView= (TextView) root.findViewById(R.id.listText);
            imageButton= (ImageView) root.findViewById(R.id.listImage);
            this.mListener = listener;
            root.setOnClickListener(this);
        }

        public ImageView getImageButton() {return imageButton;}
        public TextView getTextView() {
            return textView;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }

        }
    }

    private MyItemClickListener mItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i==1)
        {
            view=LayoutInflater.from(context).inflate(R.layout.list_new,viewGroup,false);
        }
        else {
            view=LayoutInflater.from(context).inflate(R.layout.empty,viewGroup,false);
        }
        return new MyViewHolder(view,mItemClickListener);
    }


    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0||position==resName.size()-1)
            return 0;
        else return 1;
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if (position==0||position==resName.size()-1)
                return;
            MyViewHolder MyViewHolder =(MyViewHolder)holder;
            MyViewHolder.getTextView().setText(resName.get(position));
//            networkTool.getImageResource(resID.get(position), MyViewHolder.getImageButton(), new NetworkTool.OnResponseListener() {
//                @Override
//                public void onResponse(JSONObject response) {
//
//                }
//
//                @Override
//                public void onError(VolleyError error) {
//
//                }
//            });

	        mImageCache.loadImage(resID.get(position), MyViewHolder.getImageButton());

//           Picasso.with(context).load(resID.get(position)).centerCrop().into(MyViewHolder.getImageButton(), new Callback() {
//	           @Override
//	           public void onSuccess() {
//		           Log.i("Picasso", "Success");
//
//	           }
//
//	           @Override
//	           public void onError() {
//		           Log.i("Picasso", "Error");
//	           }
//           });
        }
        catch (Exception e)
        {
            //onBindViewHolder(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return resName.size();
    }
    public ArrayList<String> getResID(){
        return  resID;
    }
    public ArrayList<String> getResName(){return resName;}
    public ArrayList<String> getResIntro(){return resIntro;}
    public ArrayList<String> getShowId(){return showId;}
    public ArrayList<Double> getLocationInfoLatitude(){return locationInfoLatitude;}
    public ArrayList<Double> getGetLocationInfoLongtitude(){return locationInfoLongtitude;}

}
