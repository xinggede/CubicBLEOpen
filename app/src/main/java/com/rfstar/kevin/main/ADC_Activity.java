package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
 * @author Kevin.wu E-mail:wh19575782@163.com
 * <p>
 * 2 通道 ADC 输入控制。APP 通过 BLE API 接口向 FFD1 通道写操作,来使能两个 13bit ADC 通道。向 FFD2
 * 通道写操作,来控制两个 ADC 通道采样周期 t,单位为 ms,t>=100ms。如果打开了通道 FFD3,FFD4 的通知使能(如果使用
 * BTool 操作,需向 0x003C+1= 0x003D 和 0x0040+1= 0x0041 写入 01
 * 00),每产生一次采集结果后,将会在 此通道产生一个 notify 通知事件,附带了本次采集结果,范围:0 ~ 0x1FFF,低字节在前,
 * APP 可以直接在回调函数中进行处理和使用。ADC 的参考源为芯片内部参考源 1.25V,
 * 因此电源电压的浮动,不会导致新的测量误差,而被测量采样电压必须控制在 0 ~ +1.25V 之间。
 */
public class ADC_Activity extends BaseActivity implements
        RFStarBLEBroadcastReceiver {
    private TextView adcCircleTxt = null, adc0Txt, adc1Txt;
    private EditText adcCircleEdit = null;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adc);
        this.initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            this.getADCCircle(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    app.manager.cubicBLEDevice.readValue("ffd0", "ffd3");
                }
            }, 200);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    app.manager.cubicBLEDevice.readValue("ffd0", "ffd4");
                }
            }, 400);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setNotification("ffd0", "ffd3", false);
            app.manager.cubicBLEDevice.setNotification("ffd0", "ffd4", false);
        }
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        adcCircleTxt = (TextView) this.findViewById(R.id.adcCircleTxt);
        adcCircleEdit = (EditText) this.findViewById(R.id.adcCircleEdit);
        adc0Txt = (TextView) findViewById(R.id.adc0Txt);
        adc1Txt = (TextView) findViewById(R.id.adc1Txt);
    }

    /**
     * 是否开启ADC0使能
     *
     * @param view
     */
    public void getADC0Value(View view) {
        if (view instanceof CheckBox) {
            if (app.manager.cubicBLEDevice != null) {
                CheckBox box = (CheckBox) view;
                app.manager.cubicBLEDevice.setNotification("ffd0", "ffd3",
                        box.isChecked());
            }
        }
    }

    /**
     * 是否开启ADC1使能
     *
     * @param view
     */
    public void getADC1Value(View view) {
        if (view instanceof CheckBox) {
            if (app.manager.cubicBLEDevice != null) {
                CheckBox box = (CheckBox) view;
                app.manager.cubicBLEDevice.setNotification("ffd0", "ffd4",
                        box.isChecked());
            }
        }
    }

    /**
     * 发送adc数据 使能控制。 0x00:关闭两个 ADC 通道 0x01:打开 ADC0 通道 0x02:打开 ADC1 通道 0x03:打开两个
     * ADC 通道
     *
     * @param data
     */
    private void sendAdc(byte[] data) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue("ffd0", "ffd1", data);
        }
    }

    /**
     * 关闭两个adc
     *
     * @param view
     */
    public void onCloseADCs(View view) {
        byte[] data = new byte[1];
        data[0] = 0x00;
        sendAdc(data);
    }

    /**
     * 打开两个adc
     */
    public void onOpenADCs(View view) {
        byte[] data = new byte[1];
        data[0] = 0x03;
        sendAdc(data);
    }

    /**
     * 打开adc0
     *
     * @param view
     */
    public void onOpenADC0(View view) {
        byte[] data = new byte[1];
        data[0] = 0x01;
        sendAdc(data);
    }

    /**
     * 打开adc1
     *
     * @param view
     */
    public void onOpenADC1(View view) {
        byte[] data = new byte[1];
        data[0] = 0x02;
        sendAdc(data);
    }

    /**
     * 获取adc周期
     *
     * @param view
     */
    public void getADCCircle(View view) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.readValue("ffd0", "ffd2");
        }
    }

    /**
     * 设置adc周期
     *
     * @param view
     */
    public void setADCCircle(View view) {
        if (app.manager.cubicBLEDevice != null) {
            if (!adcCircleEdit.getText().toString().equals("")) {
                int circle = Integer
                        .valueOf(adcCircleEdit.getText().toString());
                byte[] data = new byte[2];
                data[0] = (byte) ((circle >> 8) & 0xFF);
                data[1] = (byte) (circle & 0xFF);
                app.manager.cubicBLEDevice.writeValue("ffd0", "ffd2", data);
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {

        String action = intent.getAction();
        this.connectedOrDis(intent.getAction());
        if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            int text = 0;
            text = (int) ((data[0] & 0xFF) << 8);
            text += (int) data[1] & 0xFF;
            if (uuid.contains("ffd2")) {
                adcCircleTxt.setText(text + "ms");
            } else if (uuid.contains("ffd3")) {
                adc0Txt.setText(text + "    hex: " + Tools.byte2Hex(data));
            } else if (uuid.contains("ffd4")) {
                adc1Txt.setText(text + "    hex: " + Tools.byte2Hex(data));
            }
        }
    }
}
