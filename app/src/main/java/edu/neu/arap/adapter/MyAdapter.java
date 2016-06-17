package edu.neu.arap.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.arap.R;

/**
 * Created by 志伟 on 2015/11/10.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
    private int[] resID={0,R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,0};
    private String[] resName={"useless","欧洲玻璃艺术展","明清瓷器展","明清玉器展","万历海贸传奇","辽宁民间绣品展","拿破仑文物展","中国古代货币展","useless"};
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
}
