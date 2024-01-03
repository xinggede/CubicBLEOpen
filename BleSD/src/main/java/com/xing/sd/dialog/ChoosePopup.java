package com.xing.sd.dialog;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.xing.sd.R;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChoosePopup extends BasePopup {

    private RecyclerView recyclerView;
    private ChooseAdapter chooseAdapter;

    public ChoosePopup(Context context, int w, String[] data, ChooseAdapter.OnItemClickListener onItemClickListener) {
        this(context, w, w);
        chooseAdapter = new ChooseAdapter(context, data);
        chooseAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(chooseAdapter);
    }

    public ChoosePopup(Context context, String[] data, ChooseAdapter.OnItemClickListener onItemClickListener) {
        this(context, 0, 0);
        chooseAdapter = new ChooseAdapter(context, data);
        chooseAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(chooseAdapter);
    }

    public ChoosePopup(Context context, int w, int h) {
        super(context, w, h);

        recyclerView = getPopupContentView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    public void updateData(String[] data){
        chooseAdapter.updateData(data);
    }

    public String getValue(int position) {
        return chooseAdapter.getValue(position);
    }

    public int getSelect(String str){
        return chooseAdapter.getSelect(str);
    }

    @Override
    public int initLayoutId() {
        return R.layout.popup_choose;
    }

}
