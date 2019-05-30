package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.service.RFStarBLEService;
import com.rfstar.kevin.view.ReceiveDataView;
import com.rfstar.kevin.view.SendDataView;

import java.io.UnsupportedEncodingException;

/**
 * 发送数据的界面
 *
 * @author kevin
 */
public class SendDataActivity extends BaseActivity implements
        View.OnClickListener, RFStarBLEBroadcastReceiver, CompoundButton.OnCheckedChangeListener {
    private SendDataView sendView = null;
    private ReceiveDataView receiveDataView = null;
    private CheckBox checkBox = null;

    private Button resetBtn = null, clearBtn = null, sendBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_senddata);
        this.initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
    }

    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);
        sendView = (SendDataView) this.findViewById(R.id.sendDataView);
        resetBtn = (Button) this.findViewById(R.id.resetBtn);
        clearBtn = (Button) this.findViewById(R.id.clearBtn);
        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        resetBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        checkBox.setOnCheckedChangeListener(this);

        receiveDataView = (ReceiveDataView) findViewById(R.id.getDataView);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.resetBtn) {
            sendView.reset();
        } else if (v.getId() == R.id.clearBtn) {
            sendView.clear();
        } else if (v.getId() == R.id.sendBtn) {
            sendView.setCountTimes();
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue("ffe5", "ffe9", sendView
                        .getEdit().getBytes());
            }
        } else if (v.getId() == R.id.resetBtn_read) {
            receiveDataView.reset();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        String action = intent.getAction();
        connectedOrDis(action);
        if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            if (uuid.contains("ffe4")) {
                byte[] data = intent
                        .getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
                try {
                    receiveDataView.setCountTimesTxt();
                    receiveDataView.appendString(new String(data, "GB2312"));
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        receiveDataView.changeEditBackground(isChecked);
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setNotification("ffe0", "ffe4",
                    isChecked);
        }
    }
}
