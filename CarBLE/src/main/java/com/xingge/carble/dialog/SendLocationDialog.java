package com.xingge.carble.dialog;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.xingge.carble.R;
import com.xingge.carble.util.Tool;


public class SendLocationDialog extends BaseDialog implements CompoundButton.OnCheckedChangeListener {
    private EditText etId;
    private Button btConfirm, btCancel;
    private Switch aSwitch;

    public SendLocationDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        etId = getDialog().findViewById(R.id.et_id);
        aSwitch = getDialog().findViewById(R.id.switch1);
        btConfirm = getDialog().findViewById(R.id.bt_ok);
        btCancel = getDialog().findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_send_location;
    }

    public int getDefault() {
        return aSwitch.isChecked() ? 1 : 0;
    }

    public int getValue() {
        return Tool.stringToInt(etId.getText().toString());
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        etId.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }
}