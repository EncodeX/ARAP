package edu.neu.arap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 志伟 on 2015/11/10.
 */
class MyAdapter extends RecyclerView.Adapter {
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
        MyViewHolder.getTextView().setText("图片"+position);
        MyViewHolder.getImageButton().setImageResource(R.mipmap.desktop);
    }

    @Override
    public int getItemCount() {
        return 15;
    }
}
