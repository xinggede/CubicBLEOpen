package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.service.RFStarBLEService;
import com.rfstar.kevin.tools.Tools;

/**
 * @author Kevin.wu
 * <p>
 * <p>
 * <p>
 * RSSI 读取或回传通道。 APP通过BLE API接口向FFA1通道读操作,来获取当前模块收到移动设备的
 * RSSI。如果打开了此通道的通知使能(如果使用 BTool 操作,需向 0x005D+1= 0x005E 写入01
 * 00),每读取到一次RSSI后,将会在此通道产生一个notify通知事件,附带了 RSSI 值,APP
 * 可以直接在回调函数中进行处理和使用。 APP通过BLE API接口向FFA2通道读写操作,来设定RSSI的读取周期,单位 为
 * ms。当此周期被设置为 0x0000 时,被认为关闭 RSSI 自动周期性读取。但仍然可以随 时主动读取。RSSI 的读取值为
 * signed char 类型。 同样,停止使用RSSI回传功能,需关闭FFA1通道的RSSI通知使能,并向FFA2 通道写入
 * 0x0000,来关闭模块对 RSSI 的读取,否则会造成多余的功耗。
 */
public class RssiActivity extends BaseActivity implements
        RFStarBLEBroadcastReceiver {
    private TextView rssiTxt = null, rssiCircleTxt;
    private EditText rssiEdit = null;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_rssi);
        this.initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            app.manager.cubicBLEDevice.readValue("ffa0", "ffa1");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    app.manager.cubicBLEDevice.readValue("ffa0", "ffa2");
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() { // 结束时清空消息使能
        // TODO Auto-generated method stub
        super.onDestroy();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setNotification("ffa0", "ffa1", false);
        }
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        rssiTxt = (TextView) findViewById(R.id.rssiTxt);
        rssiEdit = (EditText) findViewById(R.id.rssiEdit);
        rssiCircleTxt = (TextView) findViewById(R.id.rssiCircleTxt);
    }

    /**
     * 消息使能
     *
     * @param view
     */
    public void setRssiEnableNotification(View view) {
        if (app.manager.cubicBLEDevice != null) {
            if (view instanceof CheckBox) {
                CheckBox box = (CheckBox) view;
                app.manager.cubicBLEDevice.setNotification("ffa0", "ffa1",
                        box.isChecked());
            }
        }
    }

    /**
     * 获取信号
     *
     * @param view
     */
    public void getRssi(View view) {
        if (app.manager.cubicBLEDevice != null)
            app.manager.cubicBLEDevice.readValue("ffa0", "ffa1");
    }

    /**
     * 设置返回信号的周期
     *
     * @param view
     */
    public void setRssiCircle(View view) {
        showMessage("设置周期");
        if (app.manager.cubicBLEDevice != null) {
            if (!rssiEdit.getText().toString().equals("")) {
                int circle = Integer.valueOf(rssiEdit.getText().toString());
                byte[] data = new byte[2];
                data[0] = (byte) ((circle >> 8) & 0xFF);
                data[1] = (byte) (circle & 0xFF);
                app.manager.cubicBLEDevice.writeValue("ffa0", "ffa2", data);
                Log.d(App.TAG,
                        "1111111 set rssiCircle  : " + Tools.byte2Hex(data));
            }
        }
    }

    /**
     * 获取信号的周期
     *
     * @param view
     */
    public void getRssiCircle(View view) {
        showMessage("获取周期");
        if (app.manager.cubicBLEDevice != null)
            app.manager.cubicBLEDevice.readValue("ffa0", "ffa2");
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        Log.d(App.TAG, "有数据返回");
        // TODO Auto-generated method stub
        String action = intent.getAction();
        this.connectedOrDis(action);
        if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            if (uuid.contains("ffa1")) {
                rssiTxt.setText((int) data[0]);
            } else if (uuid.contains("ffa2")) {
                int text = 0;
                text = (int) ((data[0] & 0xFF) << 8);
                text += (int) data[1] & 0xFF;
                rssiCircleTxt.setText(text + "ms");
                Log.d(App.TAG,
                        "111111111		rssiCircle  : " + Tools.byte2Hex(data));
            }
        } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                .equals(action)) {
        }
    }
}
