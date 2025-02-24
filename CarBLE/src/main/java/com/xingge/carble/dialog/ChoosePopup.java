package com.xingge.carble.dialog;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.xingge.carble.R;

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

        getPopupContentView().setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = getPopupContentView().findViewById(R.id.pop_layout)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }

                }
                return true;
            }
        });

        recyclerView = getPopupContentView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    public String getValue(int position) {
        return chooseAdapter.getValue(position);
    }

    @Override
    public int initLayoutId() {
        return R.layout.popup_choose;
    }

}
