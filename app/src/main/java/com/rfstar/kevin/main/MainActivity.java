package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.adapter.MemberAdapter;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;

import java.util.ArrayList;

/**
 * @author Kevin.wu E-mail:wh19575782@163.com
 */
public class MainActivity extends BaseActivity implements OnItemClickListener,
        RFStarBLEBroadcastReceiver {
    private ArrayList<MemberItem> arraySource = null;
    private ListView list = null;
    private MemberAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        this.initNavigation("服务列表");
    }

    @Override
    protected void initNavigation(String title) {
        // TODO Auto-generated method stub
        super.initNavigation(title);
        navigateView.setRightHideBtn(false);
        navigateView.rightBtn.setText("搜索");
        navigateView.rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this,
                        SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressWarnings("static-access")
    private void initView() {

        initArraySource();
        adapter = new MemberAdapter(this, arraySource);

        this.list = (ListView) this.findViewById(R.id.list);
        this.list.setCacheColorHint(Color.TRANSPARENT);
        this.list.setDivider(null);
        this.list.setAdapter(adapter);
        this.list.setOnItemClickListener(this);

        this.list.setSelector(R.color.clear);
    }

    /**
     * 初始化source
     */
    private void initArraySource() {
        // TODO Auto-generated method stub
        this.arraySource = new ArrayList<MemberItem>();
        MemberItem item0 = new MemberItem();
        item0.name = "蓝牙数据通道【UUID:0xFFE5】";
        item0.nameEnglish = "Bluetooth Data channel service";
        MemberItem item1 = new MemberItem();
        item1.name = "串口数据通道【UUID:0xFFE0】";
        item1.nameEnglish = "Serial Data channel service";
        MemberItem item2 = new MemberItem();
        item2.name = "ADC输入（2路）【UUID:0xFFD0】";
        item2.nameEnglish = "ADC input(2) service";
        MemberItem item3 = new MemberItem();
        item3.name = "RSSI报告【UUID:0xFFA0】";
        item3.nameEnglish = "RSSI report service";
        MemberItem item4 = new MemberItem();
        item4.name = "PWM输出（4路)【UUID:0xFFB0】";
        item4.nameEnglish = "PWM Output(4) service";
        MemberItem item5 = new MemberItem();
        item5.name = "电池电量报告【UUID:0x180F】";
        item5.nameEnglish = "Battery report service";

        MemberItem item6 = new MemberItem();
        item6.name = "定时翻转输出（2路）【UUID:0xFFF0】";
        item6.nameEnglish = "Turn timing output(2) service";
        MemberItem item7 = new MemberItem();
        item7.name = "电平脉宽计数【UUID:0xFFF0】";
        item7.nameEnglish = "Level counting pulse width(2) service";
        MemberItem item8 = new MemberItem();
        item8.name = "端口定时事件配置【UUID:0xFE00】";
        item8.nameEnglish = "Port timing events configuration service";
        MemberItem item9 = new MemberItem();
        item9.name = "可编程IO(8路)【UUID:0xFFF0】";
        item9.nameEnglish = "Programmable IO(8) service";
        MemberItem item10 = new MemberItem();
        item10.name = "设备信息【UUID:0x180A】";
        item10.nameEnglish = "Device information service";
        MemberItem item11 = new MemberItem();
        item11.name = "模块参数设置【UUID:0xFF90】";
        item11.nameEnglish = "Module parameter Settings service";
        MemberItem item12 = new MemberItem();
        item12.name = "防动持密钥【UUID:0xFFC0】";
        item12.nameEnglish = "Anti-hijacking key service";

        this.arraySource.add(item0);
        this.arraySource.add(item1);
        this.arraySource.add(item2);
        this.arraySource.add(item3);
        this.arraySource.add(item4);
        this.arraySource.add(item5);
        this.arraySource.add(item6);
        this.arraySource.add(item7);
        this.arraySource.add(item8);
        this.arraySource.add(item9);
        this.arraySource.add(item10);
        this.arraySource.add(item11);
        this.arraySource.add(item12);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.adapter.notifyDataSetChanged();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (arg2) {
            case App.BLUETOOTH_DATA_CHANNEL:
                intent = new Intent(this, SendDataActivity.class);
                break;
            case App.SERIAL_DATA_CHANNEL:
                intent = new Intent(this, ReceivedataActivity.class);
                break;
            case App.PWM_OUTPUT:
                intent = new Intent(this, PWMActivity.class);
                break;
            case App.BATTERY_REPORT:
                intent = new Intent(this, BatteryActivity.class);
                break;
            case App.RSSI_REPORT:
                intent = new Intent(this, RssiActivity.class);
                break;
            case App.DEVICE_INFORMATION:
                intent = new Intent(this, DeviceInformationActivity.class);
                break;
            case App.ADC_INPUT:
                intent = new Intent(this, ADC_Activity.class);
                break;
            case App.PROGRAMM_ABLEIO:
                intent = new Intent(this, Programmable8Activity.class);
                break;
            case App.MODULE_PARAMETER:
                intent = new Intent(this, ModuleParameterActivity.class);
                break;
            default:

                break;
        }
        if (intent != null) {
            intent.putExtra(App.TAG, arraySource.get(arg2));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        connectedOrDis(intent.getAction());
    }

}
