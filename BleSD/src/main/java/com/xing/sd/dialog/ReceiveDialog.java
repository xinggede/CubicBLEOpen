package com.xing.sd.dialog;


import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xing.sd.R;


public class ReceiveDialog extends BaseDialog {
    private TextView tvShow;
    private Button btClose, btReset;

    public ReceiveDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        tvShow = getDialog().findViewById(R.id.tv_show);
        btClose = getDialog().findViewById(R.id.bt_close);
        btReset = getDialog().findViewById(R.id.bt_reset);
        btClose.setOnClickListener(v -> dismiss());
        tvShow.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        btReset.setOnClickListener(onClickListener);
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_receive;
    }

    public void setTvShow(String str) {
        tvShow.setText(str);
    }

    public void addTvShow(String str) {
        tvShow.append(str + "\r\n");
    }

}