package com.xing.sd.dialog;


import android.content.Context;
import android.widget.TextView;
import com.xing.sd.R;


public class ProDialog extends BaseDialog {
    private TextView tvShow;

    public ProDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        tvShow = (TextView) getDialog().findViewById(R.id.tv_show);
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_pro;
    }

    public void setTvShow(String str) {
        tvShow.setText(str);
    }

}