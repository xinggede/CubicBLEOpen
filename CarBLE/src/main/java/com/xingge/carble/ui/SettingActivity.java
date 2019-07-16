package com.xingge.carble.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingge.carble.R;
import com.xingge.carble.base.BaseActivity;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bean.GpsInfo;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.dialog.ChooseAdapter;
import com.xingge.carble.dialog.ChoosePopup;
import com.xingge.carble.ui.mode.MainContract;
import com.xingge.carble.ui.mode.MainPresenter;
import com.xingge.carble.util.CommandUtil;
import com.xingge.carble.util.Tool;

public class SettingActivity extends IBaseActivity<MainPresenter> implements MainContract.View {

    private TextView tv_time, tv_ver;
    private EditText et_frequency, et_old_pwd, et_new_pwd, et_id;
    private RadioGroup rg_rf;
    private TextView tv_acc, tv_time_zone, tv_location_mode, tv_gps_show_type, tv_output_volume,
            tv_output_level, tv_mt, tv_mtsn, tv_tl_time, tv_track_frequency, tv_channel, tv_location;
    private ChoosePopup accPopup, timeZonePopup, locationModePopup, showTypePopup,
            volumePopup, levelPopup, mtPopup, mtsnPopup, tlPopup, tfPopup, channelPopup, locationPopup;
    private int mode, type, mtsn, xhsn;


    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tv_time.setText(Tool.getCurrentTime());
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
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

        tv_time = findViewById(R.id.tv_time);
        tv_ver = findViewById(R.id.tv_ver);
        et_frequency = findViewById(R.id.et_frequency);
        et_old_pwd = findViewById(R.id.et_old_pwd);
        et_new_pwd = findViewById(R.id.et_new_pwd);
        et_id = findViewById(R.id.et_id);
        tv_time.setText(Tool.getCurrentTime());
        initSP();
    }

    private void initSP() {
        tv_acc = findViewById(R.id.tv_acc);
        tv_acc.setOnClickListener(this);
        accPopup = new ChoosePopup(this, getResources().getStringArray(R.array.acc), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_acc.setText(accPopup.getValue(position));
                setAcc(position);
                accPopup.dismiss();
            }
        });

        tv_time_zone = findViewById(R.id.tv_time_zone);
        tv_time_zone.setOnClickListener(this);
        timeZonePopup = new ChoosePopup(this, getResources().getStringArray(R.array.time_zone), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_time_zone.setText(timeZonePopup.getValue(position));
                if (getPresenter().setGMT(timeZonePopup.getValue(position))) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                timeZonePopup.dismiss();
            }
        });

        tv_location_mode = findViewById(R.id.tv_location_mode);
        tv_location_mode.setOnClickListener(this);
        locationModePopup = new ChoosePopup(this, getResources().getStringArray(R.array.location_mode), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_location_mode.setText(locationModePopup.getValue(position));
                mode = position;
                if (getPresenter().setGMdf(position, type)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                locationModePopup.dismiss();
            }
        });

        tv_gps_show_type = findViewById(R.id.tv_gps_show_type);
        tv_gps_show_type.setOnClickListener(this);
        showTypePopup = new ChoosePopup(this, Tool.dip2px(this, 150), getResources().getStringArray(R.array.gps_show_type), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_gps_show_type.setText(showTypePopup.getValue(position));
                type = position;
                if (getPresenter().setGMdf(mode, position)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                showTypePopup.dismiss();
            }
        });


        tv_output_volume = findViewById(R.id.tv_output_volume);
        tv_output_volume.setOnClickListener(this);
        volumePopup = new ChoosePopup(this, getResources().getStringArray(R.array.output_level), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_output_volume.setText(volumePopup.getValue(position));
                int value = rg_rf.getCheckedRadioButtonId() == R.id.rb_one ? 0 : 1;
                if (getPresenter().setRFPvs(rfState, value, position, Tool.stringToInt(tv_output_level.getText().toString()),
                        Tool.stringToInt(tv_mt.getText().toString()), mtsn, xhsn)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                volumePopup.dismiss();
            }
        });

        tv_output_level = findViewById(R.id.tv_output_level);
        tv_output_level.setOnClickListener(this);
        levelPopup = new ChoosePopup(this, getResources().getStringArray(R.array.output_level), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_output_level.setText(levelPopup.getValue(position));
                int value = rg_rf.getCheckedRadioButtonId() == R.id.rb_one ? 0 : 1;
                if (getPresenter().setRFPvs(rfState, value, Tool.stringToInt(tv_output_volume.getText().toString()), position,
                        Tool.stringToInt(tv_mt.getText().toString()), mtsn, xhsn)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                levelPopup.dismiss();
            }
        });

        tv_mt = findViewById(R.id.tv_mt);
        tv_mt.setOnClickListener(this);
        mtPopup = new ChoosePopup(this, getResources().getStringArray(R.array.output_level), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                tv_mt.setText(mtPopup.getValue(position));
                int value = rg_rf.getCheckedRadioButtonId() == R.id.rb_one ? 0 : 1;
                if (getPresenter().setRFPvs(rfState, value, Tool.stringToInt(tv_output_volume.getText().toString()), Tool.stringToInt(tv_output_level.getText().toString()),
                        position, mtsn, xhsn)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                mtPopup.dismiss();
            }
        });

        tv_mtsn = findViewById(R.id.tv_mtsn);
        tv_mtsn.setOnClickListener(this);
        mtsnPopup = new ChoosePopup(this, new String[]{"关闭", "使能内放", "使能外放"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                mtsn = position;
                tv_mtsn.setText(mtsnPopup.getValue(position));
                int value = rg_rf.getCheckedRadioButtonId() == R.id.rb_one ? 0 : 1;
                if (getPresenter().setRFPvs(rfState, value, Tool.stringToInt(tv_output_volume.getText().toString()), Tool.stringToInt(tv_output_level.getText().toString()),
                        Tool.stringToInt(tv_mt.getText().toString()), position, xhsn)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                mtsnPopup.dismiss();
            }
        });

        tv_location = findViewById(R.id.tv_location);
        tv_location.setOnClickListener(this);
        locationPopup = new ChoosePopup(this, new String[]{"关闭", "ID寻呼", "广播寻呼"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                xhsn = position;
                tv_location.setText(locationPopup.getValue(position));
                int value = rg_rf.getCheckedRadioButtonId() == R.id.rb_one ? 0 : 1;
                if (getPresenter().setRFPvs(rfState, value, Tool.stringToInt(tv_output_volume.getText().toString()), Tool.stringToInt(tv_output_level.getText().toString()),
                        Tool.stringToInt(tv_mt.getText().toString()), mtsn, position)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }
                locationPopup.dismiss();
            }
        });

        tv_tl_time = findViewById(R.id.tv_tl_time);
        tv_tl_time.setText(getPresenter().getTLTime().split("-")[1]);
        tv_tl_time.setOnClickListener(this);
        tlPopup = new ChoosePopup(this, Tool.dip2px(this, 150), getResources().getStringArray(R.array.tl_time), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                String time = tlPopup.getValue(position);
                tv_tl_time.setText(time);
                getPresenter().setTLTime(position + "-" + time);
                tlPopup.dismiss();
            }
        });

        tv_track_frequency = findViewById(R.id.tv_track_frequency);
        tv_track_frequency.setText(getPresenter().getTLTime().split("-")[1]);
        tv_track_frequency.setOnClickListener(this);
        tfPopup = new ChoosePopup(this, Tool.dip2px(this, 150), getResources().getStringArray(R.array.tf_time), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                String track = tfPopup.getValue(position);
                tv_track_frequency.setText(track);
                int value = Tool.stringToInt(track.substring(0, track.length() - 1));
                if (getPresenter().setGTime(value)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }

                tfPopup.dismiss();
            }
        });

        tv_channel = findViewById(R.id.tv_id);
        tv_channel.setOnClickListener(this);
        channelPopup = new ChoosePopup(this, new String[]{"频道0", "频道21", "频道22"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                channelPopup.dismiss();
                channel = position;
                if (position == 1) {
                    channel = 21;
                } else if (position == 2) {
                    channel = 22;
                }

                String text = et_frequency.getText().toString().trim();
                int f = Tool.stringToInt(text);
                if (f < 4000000 || f > 4700000) {
                    Tool.toastShow(SettingActivity.this, "对讲机频率范围为 4000000~4700000");
                    return;
                }

                if (f % 50 != 0) {
                    Tool.toastShow(SettingActivity.this, "对讲机频率必须为50的倍数");
                    return;
                }

                int id = Tool.stringToInt(et_id.getText().toString());

                if (id < 0 || id > 121) {
                    Tool.toastShow(SettingActivity.this, "亚音ID范围为 0~121");
                    return;
                }

                if (getPresenter().setRFReq(channel, id, f)) {
                    Tool.toastShow(SettingActivity.this, "设置成功");
                } else {
                    Tool.toastShow(SettingActivity.this, "设置失败");
                }

                tv_channel.setText(channelPopup.getValue(position));

            }
        });

        rg_rf = findViewById(R.id.rg_rf);
    }


    @Override
    protected void initEventAndData() {
        if (getPresenter().getAll()) {
            showProDialog();
            setShowText("数据获取中...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().setCallback();
        handler.post(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void setAcc(int value) {
        if (getPresenter().setACC(value)) {
            Tool.toastShow(SettingActivity.this, "设置成功");
        } else {
            Tool.toastShow(SettingActivity.this, "设置失败");
        }
    }

    private void setRF(int value) {
        if (getPresenter().setRFPvs(rfState, value, Tool.stringToInt(tv_output_volume.getText().toString()), Tool.stringToInt(tv_output_level.getText().toString()),
                Tool.stringToInt(tv_mt.getText().toString()), mtsn, xhsn)) {
            Tool.toastShow(SettingActivity.this, "设置成功");
        } else {
            Tool.toastShow(SettingActivity.this, "设置失败");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_one:
                setRF(0);
                break;

            case R.id.rb_two:
                setRF(1);
                break;

            case R.id.bt_save:
                getPresenter().save();
                break;

            case R.id.bt_set_time:
                String time = Tool.getCurrentDate();
                if (getPresenter().setTime(time)) {
                    Tool.toastShow(this, "设置成功");
                } else {
                    Tool.toastShow(this, "设置失败");
                }
                break;

            case R.id.bt_set_frequency:
                String text = et_frequency.getText().toString().trim();
                int f = Tool.stringToInt(text);
                if (f < 4000000 || f > 4700000) {
                    Tool.toastShow(this, "对讲机频率范围为 4000000~4700000");
                    return;
                }

                if (f % 10 != 0) {
                    Tool.toastShow(this, "对讲机频率必须为10的倍数");
                    return;
                }

                int id = Tool.stringToInt(et_id.getText().toString());

                if (id < 0 || id > 121) {
                    Tool.toastShow(this, "亚音ID范围为 0~121");
                    return;
                }
                if (getPresenter().setRFReq(channel, id, f)) {
                    Tool.toastShow(this, "设置成功");
                } else {
                    Tool.toastShow(this, "设置失败");
                }
                break;

            case R.id.bt_set_pwd:
                String old = et_old_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(old) || old.length() != 6) {
                    Tool.toastShow(this, "旧密码不正确");
                    return;
                }
                String pwd = et_new_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || pwd.length() != 6) {
                    Tool.toastShow(this, "密码必须为6位数字");
                    return;
                }

                if (old.equals(pwd)) {
                    Tool.toastShow(this, "新旧密码不能一样");
                    return;
                }

                getPresenter().updatePwd(old, pwd);
                break;

            case R.id.bt_reset:
                if (getPresenter().reset(3)) {
                    Tool.toastShow(this, "设置成功");
                } else {
                    Tool.toastShow(this, "设置失败");
                }
                break;

            case R.id.lin_off_map:
                checkPermission(new BaseActivity.CheckPermListener() {
                    @Override
                    public void superPermission() {
                        startActivity(new Intent(SettingActivity.this,
                                com.amap.api.maps.offlinemap.OfflineMapActivity.class));
                    }
                }, R.string.permiss_tip, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                break;

            default:
                popupShow(v);
                break;
        }

    }

    private void popupShow(View v) {
        if (v.getId() == R.id.tv_acc) {
            accPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_time_zone) {
            timeZonePopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_location_mode) {
            locationModePopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_gps_show_type) {
            showTypePopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_output_volume) {
            volumePopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_output_level) {
            levelPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_mt) {
            mtPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_mtsn) {
            mtsnPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_tl_time) {
            tlPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_track_frequency) {
            tfPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_id) {
            channelPopup.showAsDropDown(v);
        } else if (v.getId() == R.id.tv_location) {
            locationPopup.showAsDropDown(v);
        }
    }

    @Override
    public void onConnectState(int state) {
        if (state != States.CONNECTED) {
            Tool.toastShow(this, "蓝牙连接已断开");
            Tool.startActivityClearTop(this, SearchActivity.class);
            finish();
        }
    }

    @Override
    public void onReceiveData(String command, String data) {
        if (CommandUtil.VER.startsWith(command)) {
            tv_ver.setText(data);
        } else if (CommandUtil.GMT.startsWith(command)) {
            setTimeZone(data);
        } else if (CommandUtil.ACCIN.startsWith(command)) {
            setAcc(data);
        } else if (CommandUtil.GMDF.startsWith(command)) {
            setGMdf(data);
        } else if (CommandUtil.GINFO.startsWith(command)) {
            setGInfo(data);
        } else if (CommandUtil.GTIME.startsWith(command)) {
            setGTime(data);
        } else if (CommandUtil.GTRKI.startsWith(command)) {
            setGTRki(data);
        } else if (CommandUtil.GTRKD.startsWith(command)) {
            setGTRkd(data);
        } else if (CommandUtil.RFPVS.startsWith(command)) {
            setRFPvs(data);
        } else if (CommandUtil.RFREQ.startsWith(command)) {
            setRFReq(data);
        } else if ("SET_PWD".equals(command)) {
            setPwd(data);
        }
    }

    private void setPwd(String data) {
        int state = Tool.stringToInt(data);
        if (state == 1) {
            Tool.toastShow(this, "旧密码不正确");
        } else if (state == 2) {
            Tool.toastShow(this, "密码修改成功");
            getPresenter().savePwd(et_new_pwd.getText().toString());
            et_new_pwd.setText("");
            et_old_pwd.setText("");
        } else if (state == 3) {
            Tool.toastShow(this, "密码取消成功");
            getPresenter().savePwd(et_new_pwd.getText().toString());
            et_new_pwd.setText("");
            et_old_pwd.setText("");
        }
    }

    int channel;

    private void setRFReq(String data) {
        String[] vs = data.split(",");
        if (vs.length == 3) {
            et_frequency.setText(String.valueOf(Tool.stringToInt(vs[2])));
            et_frequency.setSelection(et_frequency.length());

            channel = Tool.stringToInt(vs[0]);
            if (channel == 0) {
                tv_channel.setText(channelPopup.getValue(0));
            } else if (channel == 21) {
                tv_channel.setText(channelPopup.getValue(1));
            } else if (channel == 22) {
                tv_channel.setText(channelPopup.getValue(2));
            }
            et_id.setText(String.valueOf(Tool.stringToInt(vs[1])));
        }
        dismissProDialog();
    }

    int rfState;

    private void setRFPvs(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 7) {
            rfState = Tool.stringToInt(vs[0]);
            findViewById(R.id.bt_set_frequency).setEnabled(rfState == 1);

            ((RadioButton) rg_rf.getChildAt(Tool.stringToInt(vs[1]) + 1)).setChecked(true);

            setViewEnable(rg_rf, rfState == 1);

            tv_output_volume.setText(String.valueOf(Tool.stringToInt(vs[2])));
            setViewEnable((ViewGroup) findViewById(R.id.lin_out), rfState == 1);
            setViewEnable((ViewGroup) findViewById(R.id.lin_level), rfState == 1);
            setViewEnable((ViewGroup) findViewById(R.id.lin_id), rfState == 1);
            setViewEnable((ViewGroup) findViewById(R.id.lin_location), rfState == 1);

            tv_output_level.setText(String.valueOf(Tool.stringToInt(vs[3])));

            setViewEnable((ViewGroup) findViewById(R.id.lin_frequency), rfState == 1);

            tv_mt.setText(String.valueOf(Tool.stringToInt(vs[4])));

            mtsn = Tool.stringToInt(vs[5]);
            tv_mtsn.setText(mtsnPopup.getValue(mtsn));

            xhsn = Tool.stringToInt(vs[6]);
            tv_location.setText(locationPopup.getValue(xhsn));
        }
    }

    private void setGTRkd(String data) {
       /* PackInfo packInfo = getPresenter().analysisPack(data);
        packInfos.add(packInfo);*/
    }

    private void setGTRki(String data) {
        /*String[] vs = data.split(",");
        if (vs.length > 0) {
            int countDay = Tool.stringToInt(vs[0]);
            Tool.logd("总天数：" + countDay + "; 数据长度: " + (vs.length - 1));
            if (countDay == 0) {
                tv_day.setEnabled(false);
                return;
            }
            if (countDay == vs.length - 1) {
                String[] list = new String[countDay];
                System.arraycopy(vs, 1, list, 0, countDay);
                tv_day.setEnabled(true);
                tv_day.setText(list[0]);
                dayPopup = new ChoosePopup(this, list, new ChooseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        tv_day.setText(dayPopup.getValue(position));
                    }
                });
            }
        }*/
    }

    private void setGTime(String data) {
        int value = Tool.stringToInt(data);
        tv_track_frequency.setText(value + "S");
    }

    private void setGInfo(String data) {
        GpsInfo gpsInfo = getPresenter().analysisGps(data);
        if (gpsInfo == null) {
            return;
        }

        tv_location_mode.setText(locationModePopup.getValue(gpsInfo.mode));
        tv_gps_show_type.setText(showTypePopup.getValue(gpsInfo.showType));

//        tv_time.setText(gpsInfo.time);
    }

    private void setGMdf(String data) {
        String[] vs = data.split(",");
        if (vs.length == 2) {
            String s1 = vs[0];
            int t = Tool.stringToInt(s1);
            tv_location_mode.setText(locationModePopup.getValue(t));

            String s2 = vs[1];
            t = Tool.stringToInt(s2);
            tv_gps_show_type.setText(showTypePopup.getValue(t));
        }
    }

    private void setTimeZone(String str) {
        tv_time_zone.setText(str);
    }

    private void setAcc(String str) {
        int value = Tool.stringToInt(str);
        tv_acc.setText(accPopup.getValue(value));
    }

    private void setViewEnable(ViewGroup viewGroup, boolean b) {
        if (viewGroup != null) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                viewGroup.getChildAt(i).setEnabled(b);
            }
            viewGroup.setEnabled(b);
        }
    }

}
