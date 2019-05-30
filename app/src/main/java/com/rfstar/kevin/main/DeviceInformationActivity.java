package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.service.RFStarBLEService;

/**
 * @author Kevin.wu
 * <p>
 * 模块信息读取通道。 2A23 为模块信息获取通道,可以通过对此通道进行读操作,来获取此模块 ID。 格式如
 * xxxxxx0000xxxxxx,其中 xx 部分为模块芯片的物理地址 MAC,六个字节,低字 节在前。 2A26
 * 为模块软件版本号读取通道,可以通过对此通道进行读操作,来获取模块 软件版本,格式为 Vx.xx。x.xx 为固件版本号。
 */
public class DeviceInformationActivity extends BaseActivity implements
        RFStarBLEBroadcastReceiver {
    private TextView deviceIDtxt = null, versionTxt = null;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_device_info);
        initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            this.getDeviceMessage(null);
        }
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        deviceIDtxt = (TextView) findViewById(R.id.deviceIDTxt);
        versionTxt = (TextView) findViewById(R.id.deviceVersionTxt);
    }

    /**
     * 获取信息
     *
     * @param view
     */
    public void getDeviceMessage(View view) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.readValue("180a", "2a23");
            new Handler().postDelayed(new Runnable() { // 延时0.2秒后执行
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    app.manager.cubicBLEDevice
                            .readValue("180a", "2a26");
                }
            }, 200);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        Log.d(App.TAG, "有数据返回");
        // TODO Auto-generated method stub
        String action = intent.getAction();
        this.connectedOrDis(intent.getAction());
        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            Log.d(App.TAG, "111111111 连接完成");
            this.setTitle(macData + "已连接");

        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Log.d(App.TAG, "111111111 连接断开");
            this.setTitle(macData + "已断开");

        } else if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            if (uuid.contains("2a23")) {
                StringBuilder temp = new StringBuilder();
                int count = 0;
                for (byte tmp : data) {
                    temp.append(String.format("%02X", tmp));
                    if (count < 7)
                        temp.append(":");
                    count++;
                }
                deviceIDtxt.setText(temp);

            } else if (uuid.contains("2a26")) {
                versionTxt.setText(new String(data));
            }
        } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) {

        }
    }
}
