package com.xingge.carble.dialog;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xingge.carble.R;
import com.xingge.carble.util.Tool;


public class SendLocationDialog extends BaseDialog {
    private RadioGroup rg;
    private Button btConfirm, btCancel;

    public SendLocationDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(false);
        rg = getDialog().findViewById(R.id.rg);

        btConfirm = getDialog().findViewById(R.id.bt_ok);
        btCancel = getDialog().findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public int initLayoutId() {
        return R.layout.dialog_send_location;
    }

    public int getValue() {
        RadioButton rb = getDialog().findViewById(rg.getCheckedRadioButtonId());
        if (rb != null) {
            return Tool.stringToInt(rb.getText().toString());
        }
        return 0;
    }

    public void setBtClick(OnClickListener listener) {
        btConfirm.setOnClickListener(listener);
    }


}