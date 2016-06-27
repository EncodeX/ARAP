package edu.neu.arap.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.neu.arap.R;

/**
 * Created by 志伟 on 2015/11/10.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private  double[] locationInfoLatitude={41.67779212,41.65592365,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212,41.67779212};
    private  double[] getLocationInfoLongtitude={123.45861554,123.42680576,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554,123.45861554};
    private int[] resID={0,R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,0};
    private String[] resName={"useless","晶火传奇艺术展","明清瓷器展","明清玉器展","万历海贸传奇","辽宁民间绣品展","拿破仑文物展","中国古代货币展","useless"};
    private String[] resIntro={
            "useless",
            "  玻璃的魅力在于它是一种充满张力的神奇物质，柔时似水，钢时如钻，极强的可塑性造就了它变幻无穷的非凡美丽。在光与影的参与下，玻璃就有了魂，令人产生如梦如幻之感。",
            "  瓷器是中国人民的伟大发明，千百年来一直浓缩和传承着中华民族的杰出智慧和创新精神。明清瓷器，集中国古代工艺之大成，忠实地记录了瓷工们的不朽功绩，也折射出了创造它们的时代的特有光辉。",
            "  中国古代玉器艺术发轫于新石器时代，历经商周、两汉、唐宋等几个发展高潮，至明清时期达到鼎盛。明清时期玉器沿袭唐宋以来世俗化、生活化的风格并加以发扬，尤其是乾隆时期，因玉料资源充足，玉材质地精良，玉器造型规矩，琢磨精致，集历代之大成，达到了玉器史的巅峰。",
            "  中华民族伟大的航海足迹，渊源悠久。海上丝绸之路化汪洋为通衢，以秦汉肇始，历隋唐发展，经宋元繁荣，在明代迎来了中国海洋贸易的又一个黄金时代。大批中国商品远销海外，中国正式进入了世界贸易体系。",
            "  刺绣的历史由来已久，作为高档手工艺品，长久以来主要流行于上流社会，对大多数民众来说，是可见不可得的奢侈品。辽宁民间刺绣源于百姓现实生活，出自市井乡村女儿之手，虽不及王公贵族的奢华精致，但集实用和装饰为一体，质朴、风趣的表现手法极富感染力，在中国民间绣品中占有一席之地。",
            "  为纪念中法建交 50 周年，湖北省博物馆、南京博物院、辽宁省博物馆、天津博物馆与法国拿破仑基金会共同举办《飞越欧洲的雄鹰—拿破仑文物特展》，使广大观众有机会了解这位法国历史上的伟大人物。",
            "  历代货币是辽宁省博物馆特色收藏之一。馆藏万余件货币类文物绵延有续而不乏诸多珍品，其中尤以清末著名泉家李佐贤私人收藏之《古泉汇》、《续古泉汇》藏品合计18函3137枚藏泉最为难得，长期以来它一直为国内外钱币学家及史学家所关注。",
            "useless"
    };
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
            view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_new,null);
        }
        else {
            view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty,null);
        }
        return new MyViewHolder(view,mItemClickListener);
    }


    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0||position==resID.length-1)
            return 0;
        else return 1;
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position==0||position==resID.length-1)
            return;
        MyViewHolder MyViewHolder =(MyViewHolder)holder;
        MyViewHolder.getTextView().setText(resName[position]);
        MyViewHolder.getImageButton().setImageResource(resID[position]);
    }

    @Override
    public int getItemCount() {
        return resID.length;
    }
    public int[] getResID(){
        return  resID;
    }
    public String[] getResName(){return resName;}
    public String[] getResIntro(){return resIntro;}
    public double[] getLocationInfoLatitude(){return locationInfoLatitude;}
    public double[] getGetLocationInfoLongtitude(){return getLocationInfoLongtitude;}
}
