package edu.neu.arap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by 志伟 on 2015/11/10.
 */
class MyAdapter extends RecyclerView.Adapter {
    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageButton imageButton;
        private TextView textView;
        private MyItemClickListener mListener;
        public myViewHolder(View root,MyItemClickListener listener) {
            super(root);
            textView= (TextView) root.findViewById(R.id.listText);
            imageButton= (ImageButton) root.findViewById(R.id.listImage);
            this.mListener = listener;
            root.setOnClickListener(this);
        }

        public ImageButton getImageButton() {return imageButton;}
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
        return new myViewHolder(view,mItemClickListener);
    }
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        myViewHolder myViewHolder=(myViewHolder)holder;
        myViewHolder.getTextView().setText("图片"+position);
        myViewHolder.getImageButton().setImageResource(R.mipmap.desktop);
    }

    @Override
    public int getItemCount() {
        return 15;
    }
}
