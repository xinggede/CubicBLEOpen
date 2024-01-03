package com.xing.sd.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xing.sd.R;

import androidx.recyclerview.widget.RecyclerView;

public class ChooseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String[] data;
    private OnItemClickListener mOnItemClickListener;

    public ChooseAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_choose, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holdView = (MyViewHolder) holder;
        holdView.tvName.setText(data[position]);
        holdView.itemView.setOnClickListener(new MyOnClickListener(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public String getValue(int position) {
        return data[position];
    }

    public int getSelect(String str){
        for (int i = 0; i < data.length; i++) {
            if(data[i].equals(str)){
                return i;
            }
        }
        return 0;
    }

    public void updateData(String[] data){
        this.data = data;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(View view, int position);
    }

    class MyOnClickListener implements View.OnClickListener {
        int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClickListener(v, position);
            }
        }
    }

}
