package com.xing.sd.dialog;


import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xing.sd.R;


public class UpdateHintDialog extends BaseDialog {
    private TextView tvShow;
    private Button btClose;

    public UpdateHintDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        tvShow = getDialog().findViewById(R.id.tv_show);
        btClose = getDialog().findViewById(R.id.bt_close);

        tvShow.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        btClose.setOnClickListener(onClickListener);
    }

    @Override
    public void show() {
        tvShow.setText("");
        super.show();
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_update;
    }

    public void setTvShow(String str) {
        tvShow.setText(str);
    }

    public void addTvShow(String str) {
        tvShow.append(str + "\r\n");
    }

    public void setCloseEnable(boolean b) {
        btClose.setEnabled(b);
    }

}