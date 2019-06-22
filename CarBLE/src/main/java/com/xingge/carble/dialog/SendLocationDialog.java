package com.xingge.carble.dialog;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingge.carble.R;
import com.xingge.carble.util.Tool;


public class SendLocationDialog extends BaseDialog implements RadioGroup.OnCheckedChangeListener {
    private EditText etId;
    private TextView tvId;
    private Button btConfirm, btCancel;
    private RadioGroup rg;

    public SendLocationDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        etId = getDialog().findViewById(R.id.et_id);
        rg = getDialog().findViewById(R.id.rg);
        tvId = getDialog().findViewById(R.id.tv_id);
        btConfirm = getDialog().findViewById(R.id.bt_ok);
        btCancel = getDialog().findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rg.setOnCheckedChangeListener(this);
    }

    public void showText(String id) {
        tvId.setText(id);
        super.show();
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_send_location;
    }

    public int getDefault() {
        return rg.getCheckedRadioButtonId() == R.id.rb2 ? 1 : 0;
    }

    public int getValue() {
        return Tool.stringToInt(etId.getText().toString());
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        etId.setVisibility(checkedId == R.id.rb2 ? View.VISIBLE : View.GONE);
    }
}