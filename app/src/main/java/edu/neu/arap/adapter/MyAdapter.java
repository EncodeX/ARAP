package edu.neu.arap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.arap.R;

/**
 * Created by 志伟 on 2015/11/10.
 */
public class MyAdapter extends RecyclerView.Adapter {
    private int[] resID={R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j};
    private String[] resName={"蚁人","火星救援","捉妖记","秦时明月","完美的世界","港囧","重返20岁","移动迷宫","澳门风云","九层妖塔"};
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list,null);
        return new MyViewHolder(view,mItemClickListener);
    }
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder MyViewHolder =(MyViewHolder)holder;
        MyViewHolder.getTextView().setText(resName[position]);
        MyViewHolder.getImageButton().setImageResource(resID[position]);
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
