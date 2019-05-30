package com.xingge.carble.dialog;


import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.xingge.carble.R;


public class InputPwdDialog extends BaseDialog {
    private EditText etPwd;
    private Button btConfirm,btCancel;

    public InputPwdDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        etPwd = getDialog().findViewById(R.id.et_pwd);
        btConfirm = getDialog().findViewById(R.id.bt_confirm);
        btCancel = getDialog().findViewById(R.id.bt_cancel);
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_input_pwd;
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
        btCancel.setOnClickListener(listener);
    }

    public String getPwd() {
        return etPwd.getText().toString().trim();
    }

}