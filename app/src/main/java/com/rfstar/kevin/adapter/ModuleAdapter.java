package com.rfstar.kevin.adapter;import android.content.Context;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import com.rfstar.kevin.R;import com.rfstar.kevin.params.SetItem;import java.util.ArrayList;/** * @author kevin.wu */public class ModuleAdapter extends BaseAdapter {    ArrayList<SetItem> arraySource;    private LayoutInflater factory;    private Context context;    public ModuleAdapter(Context context, ArrayList<SetItem> arraySource) {        // TODO Auto-generated constructor stub        this.context = context;        this.arraySource = arraySource;        factory = LayoutInflater.from(this.context);    }    @Override    public int getCount() {        // TODO Auto-generated method stub        return arraySource.size();    }    @Override    public Object getItem(int position) {        // TODO Auto-generated method stub        return position;    }    @Override    public long getItemId(int position) {        // TODO Auto-generated method stub        return position;    }    @Override    public View getView(int position, View convertView, ViewGroup parent) {        // TODO Auto-generated method stub        Item item = null;        if (convertView == null) {            convertView = factory.inflate(R.layout.module_list_item, null);            item = new Item();            item.icon = (ImageView) convertView.findViewById(R.id.image);            item.name = (TextView) convertView.findViewById(R.id.name);            item.arror = (ImageView) convertView                    .findViewById(R.id.arror_image);            item.message = (TextView) convertView.findViewById(R.id.message);            item.layout = (LinearLayout) convertView                    .findViewById(R.id.layout);            convertView.setTag(item);        } else {            item = (Item) convertView.getTag();        }        item.name.setText(this.arraySource.get(position).name);        if (this.arraySource.get(position).message != null) {            item.message.setVisibility(View.VISIBLE);            item.message.setText(this.arraySource.get(position).message);        }        return convertView;    }    public class Item {        TextView name, message;        ImageView icon, arror;        LinearLayout layout;    }}