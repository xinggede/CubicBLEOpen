package com.xingge.carble.dialog;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.xingge.carble.R;
import com.xingge.carble.util.Tool;


public class SendLocationDialog extends BaseDialog implements RadioGroup.OnCheckedChangeListener {
    private EditText etId;
    private Button btConfirm, btCancel;
    private RadioGroup rg;

    public SendLocationDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        etId = getDialog().findViewById(R.id.et_id);
        rg = getDialog().findViewById(R.id.rg);
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
        rg.check(R.id.rb1);
        etId.setText(String.valueOf(Tool.stringToInt(id.substring(2))));
        etId.setSelection(etId.length());
        super.show();
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_send_location;
    }

    public int getDefault() {
        return rg.getCheckedRadioButtonId() == R.id.rb2 ? 0 : 1;
    }

    public int getValue() {
        return Tool.stringToInt(etId.getText().toString());
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb2) {
            etId.setText("");
        }
    }
}