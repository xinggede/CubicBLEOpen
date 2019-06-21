package com.xingge.carble.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.xingge.carble.R;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.ui.mode.MainContract;
import com.xingge.carble.ui.mode.MainPresenter;
import com.xingge.carble.util.CommandUtil;
import com.xingge.carble.util.Tool;

public class MainActivity extends IBaseActivity<MainPresenter> implements MainContract.View, AdapterView.OnItemSelectedListener {

    private TextView tv_voltage, tv_igv, tv_wd_n, tv_wd_w, tv_fy, tv_qc, tv_dqy, tv_dqyhb, tv_altitude, tv_rcsj, tv_rlsj,
            tv_location_state, tv_satellite, tv_longitude, tv_latitude, tv_speed;
    private EditText et_channel, et_id;
    private CusRoundView cusRoundView;
    private Switch aSwitch;
    private boolean init = false;

    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_voltage = findViewById(R.id.tv_voltage);
        tv_igv = findViewById(R.id.tv_igv);
        tv_wd_n = findViewById(R.id.tv_wd_n);
        tv_wd_w = findViewById(R.id.tv_wd_w);
        tv_fy = findViewById(R.id.tv_fy);
        tv_qc = findViewById(R.id.tv_qc);
        tv_dqy = findViewById(R.id.tv_dqy);
        tv_dqyhb = findViewById(R.id.tv_dqyhb);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_rcsj = findViewById(R.id.tv_rcsj);
        tv_rlsj = findViewById(R.id.tv_rlsj);

        tv_location_state = findViewById(R.id.tv_location_state);
        tv_satellite = findViewById(R.id.tv_satellite);
        tv_longitude = findViewById(R.id.tv_longitude);
        tv_latitude = findViewById(R.id.tv_latitude);
        tv_speed = findViewById(R.id.tv_speed);
        et_channel = findViewById(R.id.et_channel);
        et_id = findViewById(R.id.et_id);

        cusRoundView = findViewById(R.id.cusRoundView);

        aSwitch = findViewById(R.id.switch1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!init) {
                    return;
                }
                findViewById(R.id.bt_save).setEnabled(isChecked);
                setViewEnable((ViewGroup) findViewById(R.id.lin6), isChecked);

                int channel = Tool.stringToInt(et_channel.getText().toString());
                if (channel < 0 || channel > 22) {
                    Tool.toastShow(MainActivity.this, "频道范围为0~22");
                    return;
                }
                int id = Tool.stringToInt(et_id.getText().toString());
                if (id < 0 || id > 121) {
                    Tool.toastShow(MainActivity.this, "频道范围为0~121");
                    return;
                }
                if (getPresenter().setRFCtrl(isChecked ? 1 : 0, channel, id)) {
                    Tool.toastShow(MainActivity.this, "设置成功");
                }
            }
        });
    }

    @Override
    protected void initEventAndData() {
        getPresenter().setRead(true);
        if (getPresenter().confirmConnect()) {
            if (getPresenter().getAll()) {
                init = false;
                showProDialog();
                setShowText("数据获取中...");
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isShowDialog()) {
            getPresenter().getGMDF();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_get_gps) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("showType", showType);
            startActivity(intent);
        } else if (v.getId() == R.id.bt_save) {
            int channel = Tool.stringToInt(et_channel.getText().toString());
            if (channel < 0 || channel > 20) {
                Tool.toastShow(MainActivity.this, "频道范围为0~22");
                return;
            }
            int id = Tool.stringToInt(et_id.getText().toString());
            if (id < 0 || id > 121) {
                Tool.toastShow(MainActivity.this, "频道范围为0~121");
                return;
            }
            if (getPresenter().setRFCtrl(aSwitch.isChecked() ? 1 : 0, channel, id)) {
                Tool.toastShow(MainActivity.this, "设置成功");
            }
        } else if (v.getId() == R.id.bt_get_location) {
            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("showType", showType);
            intent.putExtra("enable", aSwitch.isChecked());
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.set) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        getPresenter().closeConnect();
        super.onBackPressed();
    }

    @Override
    public void onConnectState(int state) {
        if (state != States.CONNECTED) {
            Tool.toastShow(this, "蓝牙连接已断开");
            finish();
        }
    }

    @Override
    public void onReceiveData(String command, String data) {
        if (CommandUtil.TIME.startsWith(command)) {
            setTime(data);
        } else if (CommandUtil.VOLT.startsWith(command)) {
            setVoltage(data);
        } else if (CommandUtil.SLOPE.startsWith(command)) {
            setSlope(data);
        } else if (CommandUtil.TEMP.startsWith(command)) {
            setTemp(data);
        } else if (CommandUtil.GMDF.startsWith(command)) {
            setGMdf(data);
        } else if (CommandUtil.HPA.startsWith(command)) {
            setHpa(data);
        } else if (CommandUtil.DHIGH.startsWith(command)) {
            setDHigh(data);
        } else if (CommandUtil.GINFO.startsWith(command)) {
            setGInfo(data);
        } else if (CommandUtil.GTRKI.startsWith(command)) {
            setGTRki(data);
        } else if (CommandUtil.GTRKD.startsWith(command)) {
            setGTRkd(data);
        } else if (CommandUtil.RFPVS.startsWith(command)) {
            setRFPvs(data);
        } else if (CommandUtil.RFCTRL.startsWith(command)) {
            setRFCtrl(data);
        } else if (CommandUtil.RFREQ.startsWith(command)) {
            setRFReq(data);
        }
    }

    private void setRFReq(String data) {
        String[] vs = data.split(",");
        if (vs.length == 3) {
//            et_channel.setText(vs[0]);
//            et_id.setText(String.valueOf(Tool.stringToInt(vs[1])));
//
//            setViewEnable((ViewGroup) findViewById(R.id.lin5), true);
        }
        dismissProDialog();
    }

    private void setRFCtrl(String data) {
        String[] vs = data.split(",");
        if (vs.length == 3) {
            int state = Tool.stringToInt(vs[0]);
            aSwitch.setChecked(state == 1);
            et_channel.setText(vs[1]);
            et_id.setText(String.valueOf(Tool.stringToInt(vs[2])));

            init = true;
        }
        dismissProDialog();
    }

    int rfState;

    private void setRFPvs(String data) {
        String[] vs = data.split(",");
        if (vs.length == 6) {
            rfState = Tool.stringToInt(vs[0]);
//            findViewById(R.id.lin5).setVisibility(rfState == 0 ? View.GONE : View.VISIBLE);
//            findViewById(R.id.lin6).setVisibility(rfState == 0 ? View.GONE : View.VISIBLE);

            setViewEnable((ViewGroup) findViewById(R.id.lin5), rfState == 1);
            setViewEnable((ViewGroup) findViewById(R.id.lin6), rfState == 1);
        }
    }

    private void setGTRkd(String data) {
    }

    private void setGTRki(String data) {
    }

    private void setTime(String data) {
        String[] vs = data.split(",");
        if (vs.length == 4) {
            int state = Tool.stringToInt(vs[1]);
            setViewEnable((ViewGroup) findViewById(R.id.lin_rr), state == 1);

            String s1 = vs[2];
            if (s1.length() == 4) {
                s1 = s1.substring(0, 2) + ":" + s1.substring(2, s1.length());
            }
            tv_rcsj.setText(s1);

            String s2 = vs[3];
            if (s2.length() == 4) {
                s2 = s2.substring(0, 2) + ":" + s2.substring(2, s2.length());
            }
            tv_rlsj.setText(s2);
        }
    }

    private int showType = 0;

    private void setGMdf(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s = vs[1];
            showType = Tool.stringToInt(s);
        }
    }


    private void setGInfo(String data) {
        GpsInfo gpsInfo = getPresenter().analysisGps(data);
        if (gpsInfo == null) {
            return;
        }
        setViewEnable((ViewGroup) findViewById(R.id.lin_location), gpsInfo.state == 1 || gpsInfo.delayState == 1);
        setViewEnable((ViewGroup) findViewById(R.id.lin_ll), gpsInfo.state == 1 || gpsInfo.delayState == 1);
        setViewEnable((ViewGroup) findViewById(R.id.lin_speed), gpsInfo.state == 1 || gpsInfo.delayState == 1);
        findViewById(R.id.bt_get_location).setEnabled(true);

        tv_location_state.setText(gpsInfo.state == 0 || gpsInfo.delayState == 1 ? "无效" : "有效");

        if (gpsInfo.state == 0 && gpsInfo.delayState == 1) {
            return;
        }

        tv_satellite.setText(String.valueOf(gpsInfo.satellite));

        tv_longitude.setText(GpsInfo.formatLongitude(gpsInfo.longitude, showType));
        tv_latitude.setText(GpsInfo.formatLatitude(gpsInfo.latitude, showType));
//            tv_altitude.setText(gpsInfo.altitude + "M");
        tv_speed.setText(gpsInfo.speed + "KM/H");
        cusRoundView.setDegrees(gpsInfo.course);
    }

    private void setViewEnable(ViewGroup viewGroup, boolean b) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setEnabled(b);
            }
            viewGroup.setEnabled(b);
        }
    }

    private void setDHigh(String data) {
        if (data.length() == 5) {
            int t = Tool.stringToInt(data.substring(1, 5));
            data = data.substring(0, 1) + t + "米";
            tv_altitude.setText(data);
        }
    }

    private void setHpa(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s1 = vs[0];
            if (s1.length() == 6) {
                int t = Tool.stringToInt(s1.substring(0, 4));
                s1 = t + "hPa";
            }
            tv_dqy.setText(s1);

            String s2 = vs[1];
            if (s2.length() == 4) {
                int t = Tool.stringToInt(s2);
                s2 = t + "米";
            }
            tv_dqyhb.setText(s2);

            setViewEnable((ViewGroup) findViewById(R.id.lin4), true);
        }
    }

    private void setTemp(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s1 = vs[0];
            if (s1.length() == 5) {
                s1 = s1.substring(0, 1) + Tool.getOneDecimal(s1.substring(1, 5)) + "°";
            }
            if (s1.equals("-100.0°")) {
                tv_wd_w.setText("--");
            } else {
                tv_wd_w.setText(s1);
            }

            String s2 = vs[1];
            if (s2.length() == 5) {
                s2 = s2.substring(0, 1) + Tool.getOneDecimal(s2.substring(1, 5)) + "°";
            }
            tv_wd_n.setText(s2);

            setViewEnable((ViewGroup) findViewById(R.id.lin3), true);
        }
    }

    private void setSlope(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s1 = vs[0];
            if (s1.length() == 3) {
                int t = Tool.stringToInt(s1.substring(1, 3));
                if (t == 0) {
                    s1 = "0°";
                } else {
                    if (s1.substring(0, 1).equals("-")) {
                        s1 = t + "°(仰角)";
                    } else {
                        s1 = t + "°(俯角)";
                    }
                }

            }
            tv_fy.setText(s1);

            String s2 = vs[1];
            if (s2.length() == 3) {
                int t = Tool.stringToInt(s2.substring(1, 3));
                if (t == 0) {
                    s2 = "0°";
                } else {
                    if (s2.substring(0, 1).equals("-")) {
                        s2 = t + "°(左倾)";
                    } else {
                        s2 = t + "°(右倾)";
                    }
                }
            }
            tv_qc.setText(s2);

            setViewEnable((ViewGroup) findViewById(R.id.lin2), true);
        }
    }


    private void setVoltage(String str) {
        String[] vs = str.split(",");
        if (vs.length == 2) {
            String s1 = vs[0];
            if (s1.length() == 3) {
                s1 = Tool.getOneDecimal(s1) + "V";
            }
            tv_voltage.setText(s1);

            String s2 = vs[1];
            if (s2.length() == 3) {
                s2 = Tool.getOneDecimal(s2) + "V";
            }
            if (s2.equals("0.0V")) {
                tv_igv.setText("--");
            } else {
                tv_igv.setText(s2);
            }
            setViewEnable((ViewGroup) findViewById(R.id.lin1), true);
        }
    }

}
