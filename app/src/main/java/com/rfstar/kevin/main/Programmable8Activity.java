package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.service.RFStarBLEService;
import com.rfstar.kevin.tools.Tools;

import java.util.ArrayList;

/**
 * @author kevin
 * <p>
 * 因为手机中的消息机制不能使用，所以没能做完inData
 * <p>
 * IO配置和控制通道
 */
public class Programmable8Activity extends BaseActivity implements
        RFStarBLEBroadcastReceiver, OnCheckedChangeListener {
    private CheckBox setBox0, setBox1, setBox2, setBox3, setBox4, setBox5,
            setBox6, setBox7; // 设置
    private CheckBox outBox0, outBox1, outBox2, outBox3, outBox4, outBox5,
            outBox6, outBox7;// 输出
    private CheckBox inBox0, inBox1, inBox2, inBox3, inBox4, inBox5;// 输入

    private TextView setTxt, outTxt, inTxt;

    private byte[] setData = new byte[8], outData = new byte[8],
            inData = new byte[6]; // 数据

    private ArrayList<CheckBox> setBoxArray = new ArrayList<CheckBox>(8);
    private ArrayList<CheckBox> outBoxArray = new ArrayList<CheckBox>(8);
    private ArrayList<CheckBox> inBoxArray = new ArrayList<CheckBox>(6);

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmable8);
        this.initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            setData = Tools.getBooleanArray((byte) 0x00);
            // 恢复配置
            // app.manager.cubicBLEDevice.writeValue("fff0", "fff1", setData);
            app.manager.cubicBLEDevice.readValue("fff0", "fff1");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    app.manager.cubicBLEDevice.setNotification("fff0", "fff3",
                            true);
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setNotification("fff0", "fff3", false);
        }
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        setBox0 = (CheckBox) findViewById(R.id.ioSetBox0);
        setBox1 = (CheckBox) findViewById(R.id.ioSetBox1);
        setBox2 = (CheckBox) findViewById(R.id.ioSetBox2);
        setBox3 = (CheckBox) findViewById(R.id.ioSetBox3);
        setBox4 = (CheckBox) findViewById(R.id.ioSetBox4);
        setBox5 = (CheckBox) findViewById(R.id.ioSetBox5);
        setBox6 = (CheckBox) findViewById(R.id.ioSetBox6);
        setBox7 = (CheckBox) findViewById(R.id.ioSetBox7);

        outBox0 = (CheckBox) findViewById(R.id.ioOutBox0);
        outBox1 = (CheckBox) findViewById(R.id.ioOutBox1);
        outBox2 = (CheckBox) findViewById(R.id.ioOutBox2);
        outBox3 = (CheckBox) findViewById(R.id.ioOutBox3);
        outBox4 = (CheckBox) findViewById(R.id.ioOutBox4);
        outBox5 = (CheckBox) findViewById(R.id.ioOutBox5);
        outBox6 = (CheckBox) findViewById(R.id.ioOutBox6);
        outBox7 = (CheckBox) findViewById(R.id.ioOutBox7);

        inBox0 = (CheckBox) findViewById(R.id.ioInBox0);
        inBox1 = (CheckBox) findViewById(R.id.ioInBox1);
        inBox2 = (CheckBox) findViewById(R.id.ioInBox2);
        inBox3 = (CheckBox) findViewById(R.id.ioInBox3);
        inBox4 = (CheckBox) findViewById(R.id.ioInBox4);
        inBox5 = (CheckBox) findViewById(R.id.ioInBox5);

        setBoxArray.add(setBox7);
        setBoxArray.add(setBox6);
        setBoxArray.add(setBox5);
        setBoxArray.add(setBox4);
        setBoxArray.add(setBox3);
        setBoxArray.add(setBox2);
        setBoxArray.add(setBox1);
        setBoxArray.add(setBox0);

        outBoxArray.add(outBox7);
        outBoxArray.add(outBox6);
        outBoxArray.add(outBox5);
        outBoxArray.add(outBox4);
        outBoxArray.add(outBox3);
        outBoxArray.add(outBox2);
        outBoxArray.add(outBox1);
        outBoxArray.add(outBox0);

        inBoxArray.add(inBox5);
        inBoxArray.add(inBox4);
        inBoxArray.add(inBox3);
        inBoxArray.add(inBox2);
        inBoxArray.add(inBox1);
        inBoxArray.add(inBox0);

        for (CheckBox box : setBoxArray) {
            box.setOnCheckedChangeListener(this);
        }
        for (CheckBox box : outBoxArray) {
            box.setOnCheckedChangeListener(this);
        }
        for (CheckBox box : inBoxArray) {
            box.setEnabled(false);
        }

        setTxt = (TextView) findViewById(R.id.setTxt);
        outTxt = (TextView) findViewById(R.id.outTxt);
        inTxt = (TextView) findViewById(R.id.inTxt);

    }

    /**
     * 返回的数据
     */
    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        this.connectedOrDis(intent.getAction());
        if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            if (uuid.contains("fff1")) {
                // JDK自带的方法，会忽略前面的 0 Integer.toBinaryString(data[0])
                setTxt.setText(Tools.byteToBit(data[0]));
                setData = Tools.getBooleanArray(data[0]);
                int idx = 0;
                for (byte tmp : setData) { // 如果为1，为选中
                    if (tmp == 1) {
                        setBoxArray.get(idx).setChecked(true);
                        outBoxArray.get(idx).setEnabled(true);
                    } else {
                        setBoxArray.get(idx).setChecked(false);
                        outBoxArray.get(idx).setEnabled(false);
                    }
                    idx++;
                }
            } else if (uuid.contains("fff3")) {
                Log.d(App.TAG,
                        "fff3   rrrrrr  : " + data[0] + "  "
                                + Tools.byteToBit(data[0], 6));
                inTxt.setText(Tools.byteToBit(data[0], 6));
                inData = Tools.getBooleanArray(data[0]);
                int idx = 0;
                for (CheckBox box : inBoxArray) {
                    if (inData[7 - idx] == 0)
                        box.setChecked(true);
                    else
                        box.setChecked(false);
                    idx++;

                }
            }
        }
    }

    /**
     * 是否开启输入使能
     */
    public void onInEnable(View view) {
        if (view instanceof CheckBox) {
            CheckBox box = (CheckBox) view;
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.setNotification("fff0", "fff3",
                        box.isChecked());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        int id = buttonView.getId();
        CheckBox box = (CheckBox) buttonView;
        if (id <= setBox0.getId() && id >= setBox7.getId()) {
            this.setBLEDevice(box);
        } else if (id <= outBox0.getId() && id >= outBox7.getId()) {
            this.outBLEDevice(box);
        }
    }

    /**
     * 配置蓝牙设备
     *
     * @param buttonView
     */
    private void setBLEDevice(CheckBox buttonView) {

        int position = -1;
        int idx = 0;
        for (CheckBox box : setBoxArray) {
            if (box == buttonView) {
                position = idx;
            }
            idx++;
        }
        if (buttonView.isChecked()) {
            this.setData[position] = 1;
        } else {
            this.setData[position] = 0;
        }
        byte data = Tools.getByteFromBooleanArray(this.setData);
        this.setTxt.setText(Tools.byteToBit(data));

        int idxChecked = 0;
        for (CheckBox box : setBoxArray) { // 过滤出选中的checkbox
            if (box.isChecked()) {
                outBoxArray.get(idxChecked).setEnabled(true);
            } else {
                outBoxArray.get(idxChecked).setEnabled(false);
            }
            idxChecked++;
        }
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue("fff0", "fff1",
                    new byte[]{data});
        }
    }

    /**
     * IO7~ IO0 的输出状态。
     *
     * @param buttonView
     */
    private void outBLEDevice(CompoundButton buttonView) {
        int position = -1;
        int idx = 0;
        for (CheckBox box : outBoxArray) {
            if (box == buttonView) {
                position = idx;
            }
            idx++;
        }
        if (buttonView.isChecked()) {
            this.outData[position] = 0;
        } else {
            this.outData[position] = 1;
        }
        byte data = Tools.getByteFromBooleanArray(this.outData);
        this.outTxt.setText(Tools.byteToBit(data));
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue("fff0", "fff2",
                    new byte[]{data});
        }
        showMessage(" ble device outDevice ");
    }

}
