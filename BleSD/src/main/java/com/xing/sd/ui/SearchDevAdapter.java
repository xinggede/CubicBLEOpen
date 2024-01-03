package com.xing.sd.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.xing.sd.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SearchDevAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<BluetoothDevice> infos;
    private OnItemClickListener mOnItemClickListener;

    public SearchDevAdapter(Context context, ArrayList<BluetoothDevice> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_device, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BluetoothDevice info = infos.get(position);
        MyViewHolder holdView = (MyViewHolder) holder;
        String name = info.getName().trim();
        holdView.tvName.setText(name);
        holdView.tvMac.setText(info.getAddress());
        holdView.itemView.setOnClickListener(new MyOnClickListener(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvMac;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvMac = (TextView) itemView.findViewById(R.id.tv_mac);
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
