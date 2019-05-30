package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.params.SetItem;
import com.rfstar.kevin.service.RFStarBLEService;
import com.rfstar.kevin.tools.Tools;
import com.rfstar.kevin.view.PopListView;
import com.rfstar.kevin.view.PopListView.PopChioceListViewClickListener;

import java.util.ArrayList;

/**
 * @author Kevin.wu E-mail:wh19575782@163.com
 * @version 2014-4-16 上午9:02:13
 */
public class ModuleParameterActivity extends BaseActivity implements
        RFStarBLEBroadcastReceiver, PopChioceListViewClickListener,
        OnClickListener {
    private static final String FF91 = "ff91"; // 识别码
    private static final String FF92 = "ff92";
    private static final String FF93 = "ff93";
    private static final String FF94 = "ff94";
    private static final String FF95 = "ff95";
    private static final String FF96 = "ff96";
    private static final String FF97 = "ff97";
    private static final String FF98 = "ff98";
    private static final String FF99 = "ff99";
    private static final String FF9A = "ff9a";

    private LinearLayout ff92Layout, ff93Layout, ff94Layout, ff95Layout,
            ff97Layout, ff99Layout, ff9aLayout;
    private PopListView popListView = null;
    private ArrayList<SetItem> array = null;
    private byte[] enableArray = new byte[8]; // 系统功能使能开关

    private TextView ff91Txt, ff92Txt, ff93Txt, ff94Txt, ff95Txt, ff96Txt,
            ff97Txt, ff98Txt, ff99Txt, ff9aTxt;
    private EditText ff91Edit, ff96Edit, ff98Edit;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_parameter);
        initView();
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        ff92Layout = (LinearLayout) findViewById(R.id.FF92Layout);
        ff93Layout = (LinearLayout) findViewById(R.id.FF93Layout);
        ff94Layout = (LinearLayout) findViewById(R.id.FF94Layout);
        ff95Layout = (LinearLayout) findViewById(R.id.FF95Layout);
        ff97Layout = (LinearLayout) findViewById(R.id.FF97Layout);
        ff99Layout = (LinearLayout) findViewById(R.id.FF99Layout);
        ff9aLayout = (LinearLayout) findViewById(R.id.FF9ALayout);

        ff92Layout.setOnClickListener(this);
        ff93Layout.setOnClickListener(this);
        ff94Layout.setOnClickListener(this);
        ff95Layout.setOnClickListener(this);
        ff97Layout.setOnClickListener(this);
        ff99Layout.setOnClickListener(this);
        ff9aLayout.setOnClickListener(this);

        ff91Txt = (TextView) findViewById(R.id.FF91Txt);
        ff92Txt = (TextView) findViewById(R.id.FF92Txt);
        ff93Txt = (TextView) findViewById(R.id.FF93Txt);
        ff94Txt = (TextView) findViewById(R.id.FF94Txt);
        ff95Txt = (TextView) findViewById(R.id.FF95Txt);
        ff96Txt = (TextView) findViewById(R.id.FF96Txt);
        ff97Txt = (TextView) findViewById(R.id.FF97Txt);
        ff98Txt = (TextView) findViewById(R.id.FF98Txt);
        ff99Txt = (TextView) findViewById(R.id.FF99Txt);
        ff9aTxt = (TextView) findViewById(R.id.FF9ATxt);

        ff91Edit = (EditText) findViewById(R.id.FF91Edit);
        ff96Edit = (EditText) findViewById(R.id.FF96Edit);
        ff98Edit = (EditText) findViewById(R.id.FF98Edit);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
            readData(FF9A);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver#onReceive
     * (android.content.Context, android.content.Intent, java.lang.String,
     * java.lang.String)
     */
    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        this.connectedOrDis(intent.getAction());
        if (uuid != null) {

            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            // Log.d(App.TAG, "fff0 service  recevice " + new String(data));
            if (uuid.contains(FF91)) {
                ff91Txt.setText(new String(data));
            } else if (uuid.contains(FF92)) {
                ff92Txt.setText(this.receiveFF92Data((int) data[0]));
            } else if (uuid.contains(FF93)) {
                ff93Txt.setText(this.receiveFF93Data((int) data[0]));
            } else if (uuid.contains(FF95)) {
                ff95Txt.setText(this.receiveFF95Data((int) data[0]));
            } else if (uuid.contains(FF96)) {
                int value = 0;
                value = (int) ((data[0] & 0xFF) << 8);
                value += (int) data[1] & 0xFF;
                ff96Txt.setText("" + value);
            } else if (uuid.contains(FF97)) {
                ff97Txt.setText(this.receiveFF97Data((int) data[0]));
            } else if (uuid.contains(FF98)) {
                ff98Txt.setText(Tools.byte2Hex(data) + "\n" + new String(data));
            } else if (uuid.contains(FF9A)) {
                ff9aTxt.setText(Tools.byteToBit(data[0]));
                enableArray = Tools.getBooleanArray(data[0]);
            }
        }
    }

    // 处理ff92返回的数据
    private String receiveFF92Data(int type) {
        String data = null;
        switch (type) {
            case 0:
                data = "20ms";
                break;
            case 1:
                data = "50ms";
                break;
            case 2:
                data = "100ms";
                break;
            case 3:
                data = "200ms";
                break;
            case 4:
                data = "300ms";
                break;
            case 5:
                data = "400ms";
                break;
            case 6:
                data = "500ms";
                break;
            case 7:
                data = "1000ms";
                break;
            case 8:
                data = "2000ms";
                break;
            default:
                break;
        }
        return data;
    }

    // 处理ff93返回的数据
    private String receiveFF93Data(int type) {
        String data = null;
        switch (type) {
            case 0:
                data = "4800 bps";
                break;
            case 1:
                data = "9600 bps";
                break;
            case 2:
                data = "19200 bps";
                break;
            case 3:
                data = "38400 bps";
                break;
            case 4:
                data = "57600 bps";
                break;
            case 5:
                data = "115200 bps";
                break;
            default:
                break;
        }
        return data;
    }

    // 处理ff95返回的数据
    private String receiveFF95Data(int type) {
        String data = null;
        switch (type) {
            case 0:
                data = "200 ms";
                break;
            case 1:
                data = "500 ms";
                break;
            case 2:
                data = "1000 ms";
                break;
            case 3:
                data = "1500 ms";
                break;
            case 4:
                data = "2000 ms";
                break;
            case 5:
                data = "2500 ms";
                break;
            case 6:
                data = "3000 ms";
                break;
            case 7:
                data = "4000 ms";
                break;
            case 8:
                data = "5000 ms";
                break;

            default:
                break;
        }
        return data;
    }

    // 处理ff97返回的数据
    private String receiveFF97Data(int type) {
        String data = null;
        switch (type) {
            case 0:
                data = "+4 dBm";
                break;
            case 1:
                data = "0 dBm";
                break;
            case 2:
                data = "-6 dBm";
                break;
            case 3:
                data = "-23 dBm";
                break;

            default:
                break;
        }
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.rfstar.kevin.view.PopListView.PopChioceListViewClickListener#
     * onPopItemClick(android.view.View, java.lang.String, int)
     */
    @Override
    public void onPopItemClick(View view, String type, int position) {
        // TODO Auto-generated method stub
        if (type.equals(FF9A)) {
            if (enableArray[7 - position] == 1) {
                enableArray[7 - position] = 0;
            } else {
                enableArray[7 - position] = 1;
            }
            byte dataByte = Tools.getByteFromBooleanArray(this.enableArray);
            this.ff9aTxt.setText(Tools.byteToBit(dataByte));
            writeData(type, new byte[]{dataByte});
        } else {
            writeData(type, new byte[]{(byte) array.get(position).numberID});
        }
        Log.d(App.TAG,
                "popitemclick    uuid :" + type + "  value: "
                        + array.get(position).name);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        String title = null, type = null;
        if (v == ff92Layout) {
            this.initFF92Array();
            title = "蓝牙通讯连接间隔";
            type = FF92;
        } else if (v == ff93Layout) {
            this.initFF93Array();
            title = "设定串口波特率";
            type = FF93;
        } else if (v == ff94Layout) {
            this.initFF94Array();
            title = "远程复位恢复控制通道";
            type = FF94;
        } else if (v == ff95Layout) {
            this.initFF95Array();
            title = "设定广播周期";
            type = FF95;
        } else if (v == ff97Layout) {
            this.initFF97Array();
            title = "设定发射功率";
            type = FF97;
        } else if (v == ff99Layout) {
            this.initFF99Array();
            title = "远程控制扩展通道";
            type = FF99;
        } else if (v == ff9aLayout) {
            this.initFF9AArray();
            title = "系统功能使能开关";
            type = FF9A;
        }
        popListView = new PopListView(this, v, array);
        popListView.setTitle(title);
        popListView.setType(type);
        popListView.setOnPopListViewListener(this);
    }

    /**
     *   蓝牙通讯连接间隔 数组
     */
    private void initFF92Array() {
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0;
        item.name = "20ms";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 1;
        item1.name = "50ms";
        item1.message = "……";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 2;
        item2.name = "100ms";
        item2.message = "……";
        array.add(item2);
        SetItem item3 = new SetItem();
        item3.numberID = 3;
        item3.name = "200ms";
        item3.message = "……";
        array.add(item3);
        SetItem item4 = new SetItem();
        item4.numberID = 4;
        item4.name = "300ms";
        item4.message = "……";
        array.add(item4);
        SetItem item5 = new SetItem();
        item5.numberID = 5;
        item5.name = "400ms";
        item5.message = "……";
        array.add(item5);
        SetItem item6 = new SetItem();
        item6.numberID = 6;
        item6.name = "500ms";
        item6.message = "……";
        array.add(item6);
        SetItem item7 = new SetItem();
        item7.numberID = 7;
        item7.name = "1000ms";
        item7.message = "……";
        array.add(item7);
        SetItem item8 = new SetItem();
        item8.numberID = 8;
        item8.name = "2000ms";
        item8.message = "……";
        array.add(item8);
    }

    /**
     * 设定串口波特率
     */
    private void initFF93Array() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0;
        item.name = "4800 bps";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 1;
        item1.name = "9600 bps";
        item1.message = "……";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 2;
        item2.name = "19200 bps";
        item2.message = "……";
        array.add(item2);
        SetItem item3 = new SetItem();
        item3.numberID = 3;
        item3.name = "38400 bps";
        item3.message = "……";
        array.add(item3);
        SetItem item4 = new SetItem();
        item4.numberID = 4;
        item4.name = "57600 bps";
        item4.message = "……";
        array.add(item4);
        SetItem item5 = new SetItem();
        item5.numberID = 5;
        item5.name = "115200 bps";
        item5.message = "……";
        array.add(item5);
    }

    /*
     * 远程复位恢复控制通道
     */
    private void initFF94Array() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0x55;
        item.name = "远程复位控制";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 0x35;
        item1.name = "远程浅恢复控制";
        item1.message = "……";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 0x36;
        item2.name = "远程深度恢复控制";
        item2.message = "……";
        array.add(item2);
    }

    /*
     * 设定广播周期
     */
    private void initFF95Array() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0;
        item.name = "200ms";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 1;
        item1.name = "500ms";
        item1.message = "……";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 2;
        item2.name = "1000ms";
        item2.message = "……";
        array.add(item2);
        SetItem item3 = new SetItem();
        item3.numberID = 3;
        item3.name = "1500ms";
        item3.message = "……";
        array.add(item3);
        SetItem item4 = new SetItem();
        item4.numberID = 4;
        item4.name = "2000ms";
        item4.message = "……";
        array.add(item4);
        SetItem item5 = new SetItem();
        item5.numberID = 5;
        item5.name = "2500ms";
        item5.message = "……";
        array.add(item5);
        SetItem item6 = new SetItem();
        item6.numberID = 6;
        item6.name = "3000ms";
        item6.message = "……";
        array.add(item6);
        SetItem item7 = new SetItem();
        item7.numberID = 7;
        item7.name = "4000ms";
        item7.message = "……";
        array.add(item7);
        SetItem item8 = new SetItem();
        item8.numberID = 8;
        item8.name = "5000ms";
        item8.message = "……";
        array.add(item8);
    }

    /*
     * 设定发射功率
     */
    private void initFF97Array() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0;
        item.name = "+4 dBm";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 1;
        item1.name = "0 dBm";
        item1.message = "……";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 2;
        item2.name = "-6 dBm";
        item2.message = "……";
        array.add(item2);
        SetItem item3 = new SetItem();
        item3.numberID = 3;
        item3.name = "-23 dBm";
        item3.message = "……";
        array.add(item3);
    }

    /*
     * 远程控制扩展通道
     */
    private void initFF99Array() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0x01;
        item.name = "0x01";
        item.message = "IO配置输出保存触发控制";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 0x02;
        item1.name = "0x02";
        item1.message = "远程关机控制";
        array.add(item1);
        SetItem item2 = new SetItem();
        item2.numberID = 0x03;
        item2.name = "0x03";
        item2.message = "远程蓝牙断线请求";
        array.add(item2);
        SetItem item3 = new SetItem();
        item3.numberID = 0x04;
        item3.name = "0x04";
        item3.message = "自定义广播数据保存触发控制";
        array.add(item3);
    }

    /*
     * 系统功能使能开关
     */
    private void initFF9AArray() {
        // TODO Auto-generated method stub
        array = new ArrayList<SetItem>();
        SetItem item = new SetItem();
        item.numberID = 0x01;
        item.name = "bit0";
        item.message = "……";
        array.add(item);
        SetItem item1 = new SetItem();
        item1.numberID = 0x02;
        item1.name = "bit1";
        item1.message = "……";
        array.add(item1);
    }

    /*
     * 事件
     */
    public void readFF91(View view) {
        readData(FF91);
    }

    /*
     * 事件
     */
    public void writeFF91(View view) {
        if (!ff91Edit.getText().toString().equals("")) {
            writeData(FF91, ff91Edit.getText().toString().getBytes());
        }
    }

    // 蓝牙通讯连接
    public void readFF92(View view) {
        readData(FF92);
    }

    // 设定串口波特率
    public void readFF93(View view) {
        readData(FF93);
    }

    // 读取广播周期
    public void readFF95(View view) {
        readData(FF95);
    }

    // 读取广播内容
    public void readFF96(View view) {
        readData(FF96);
    }

    //  设置识别码
    public void writeFF96(View view) {
        if (!ff96Edit.getText().toString().equals("")) {
            int value = Integer.valueOf(ff96Edit.getText().toString());
            byte[] data = new byte[2];
            data[0] = (byte) ((value >> 8) & 0xFF);
            data[1] = (byte) (value & 0xFF);
            writeData(FF96, data);
        }
    }

    // 读取发射功率
    public void readFF97(View view) {
        readData(FF97);
    }

    // 读取广播数据
    public void readFF98(View view) {
        readData(FF98);
    }

    // 设置广播数据
    public void writeFF98(View view) {
        if (!ff98Edit.getText().toString().equals("")) {
            writeData(FF98, ff98Edit.getText().toString().getBytes());
        }
    }

    // 读取系统功能使能开关
    public void readFF9A(View view) {
        readData(FF9A);
    }

    /**
     * 读取数据
     *
     * @param type
     */
    private void readData(String type) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.readValue("ff90", type);
        }
    }

    /**
     * 写入数据
     *
     * @param type
     * @param data
     */
    private void writeData(String type, byte[] data) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue("ff90", type, data);
        }
    }
}
