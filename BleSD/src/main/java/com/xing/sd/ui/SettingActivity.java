package com.xing.sd.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xing.sd.R;
import com.xing.sd.base.mode.IBaseActivity;
import com.xing.sd.bean.CurrentImageInfo;
import com.xing.sd.bean.UartInfo;
import com.xing.sd.bluetooth.States;
import com.xing.sd.bluetooth.UpdateCallback;
import com.xing.sd.databinding.ActivitySettingBinding;
import com.xing.sd.dialog.ChooseAdapter;
import com.xing.sd.dialog.ChoosePopup;
import com.xing.sd.dialog.ReceiveDialog;
import com.xing.sd.dialog.UpdateHintDialog;
import com.xing.sd.ui.mode.MainContract;
import com.xing.sd.ui.mode.MainPresenter;
import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.Tool;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


public class SettingActivity extends IBaseActivity<ActivitySettingBinding, MainPresenter> implements MainContract.View, UpdateCallback {


    private ChoosePopup lightChoose, screenHNumChoose, screenVNumChoose, screenTypeChoose, scanTypeChoose,
            oeChoose, dataChoose, moveSpeedChoose, fontTypeChoose, fontSizeChoose, fontFaceChoose,
            uartChoose, uartModeChoose, uartDataChoose, uartStopChoose, uartCheckChoose, uartCommChoose,
            ipModeChoose, rPolarityChoose;

    private UpdateHintDialog updateHintDialog;
    private ReceiveDialog receiveDialog;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == 0){
                dismissProDialog();
            }
            return true;
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            binding.tvTime.setText(Tool.getCurrentTime());
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected MainPresenter onLoadPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvRcpwmPolarity.setOnClickListener(this);
        binding.tvLightSet.setOnClickListener(this);
        binding.tvScreenHNum.setOnClickListener(this);
        binding.tvScreenVNum.setOnClickListener(this);
        binding.tvScreenType.setOnClickListener(this);
        binding.tvScanType.setOnClickListener(this);
        binding.tvOePolarity.setOnClickListener(this);
        binding.tvDataPolarity.setOnClickListener(this);
        binding.tvMoveSpeed.setOnClickListener(this);
        binding.tvFontType.setOnClickListener(this);
        binding.tvFontSize.setOnClickListener(this);
        binding.tvFontFace.setOnClickListener(this);
        binding.tvUartChoose.setOnClickListener(this);
        binding.tvUartMode.setOnClickListener(this);
        binding.tvUartData.setOnClickListener(this);
        binding.tvUartCheck.setOnClickListener(this);
        binding.tvUartStop.setOnClickListener(this);
        binding.tvUartComm.setOnClickListener(this);
        binding.tvIpMode.setOnClickListener(this);

        binding.btSetRcp.setOnClickListener(this);
        binding.btSetTime.setOnClickListener(this);
        binding.btSetScreen.setOnClickListener(this);
        binding.btSetFont.setOnClickListener(this);
        binding.btUartSet.setOnClickListener(this);
        binding.btIpSet.setOnClickListener(this);
        binding.btChooseFont.setOnClickListener(this);
        binding.btChooseSys.setOnClickListener(this);
        binding.btUpdateFont.setOnClickListener(this);
        binding.btUpdateSys.setOnClickListener(this);
        binding.btReset.setOnClickListener(this);
        binding.btRestoreFactory.setOnClickListener(this);
        binding.btMsSet.setOnClickListener(this);
        initPopup();
        binding.btChooseFont.setText("GB2312_16_16.FON");
        fontUri = Uri.parse("/assets/GB2312_16_16.FON");
    }

    private void initPopup() {
        updateHintDialog = new UpdateHintDialog(this);
        updateHintDialog.setOnClickListener((v)->{
            mPresenter.stopUpdate(updateHintDialog.getIndex());
            updateHintDialog.dismiss();
        });
        receiveDialog = new ReceiveDialog(this);
        receiveDialog.setOnClickListener(v -> {
            receiveDialog.setTvShow("");
            getPresenter().getAll();
            getPresenter().setUpdateCallback(this);
            getPresenter().getCurrentImageInfo();
        });

        rPolarityChoose = new ChoosePopup(this, getResources().getStringArray(R.array.polarity), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvRcpwmPolarity.setText(rPolarityChoose.getValue(position));
                rPolarityChoose.dismiss();
            }
        });
        lightChoose = new ChoosePopup(this, getResources().getStringArray(R.array.num_0_9), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvLightSet.setText(lightChoose.getValue(position));
                lightChoose.dismiss();
                setLPBri(position);
            }
        });

        screenHNumChoose = new ChoosePopup(this, getResources().getStringArray(R.array.num_0_9), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvScreenHNum.setText(screenHNumChoose.getValue(position));
                screenHNumChoose.dismiss();
            }
        });

        screenVNumChoose = new ChoosePopup(this, getResources().getStringArray(R.array.num_0_9), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvScreenVNum.setText(screenVNumChoose.getValue(position));
                screenVNumChoose.dismiss();
            }
        });

        screenTypeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.screen_type), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvScreenType.setText(screenTypeChoose.getValue(position));
                screenTypeChoose.dismiss();
            }
        });

        scanTypeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.scan_type), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvScanType.setText(scanTypeChoose.getValue(position));
                scanTypeChoose.dismiss();
            }
        });

        oeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.polarity), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvOePolarity.setText(oeChoose.getValue(position));
                oeChoose.dismiss();
            }
        });

        dataChoose = new ChoosePopup(this, getResources().getStringArray(R.array.polarity), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvDataPolarity.setText(dataChoose.getValue(position));
                dataChoose.dismiss();
            }
        });

        moveSpeedChoose = new ChoosePopup(this, getResources().getStringArray(R.array.num_0_9), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvMoveSpeed.setText(moveSpeedChoose.getValue(position));
                moveSpeedChoose.dismiss();
            }
        });

        fontTypeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.font_type), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvFontType.setText(fontTypeChoose.getValue(position));
                fontTypeChoose.dismiss();
//                setFont();
            }
        });

        fontSizeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.font_size), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvFontSize.setText(fontSizeChoose.getValue(position));
                fontSizeChoose.dismiss();
//                setFont();
            }
        });

        fontFaceChoose = new ChoosePopup(this, getResources().getStringArray(R.array.font_face), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvFontFace.setText(fontFaceChoose.getValue(position));
                fontFaceChoose.dismiss();
//                setFont();
            }
        });

        uartChoose = new ChoosePopup(this, new String[]{"UART0(RS485)", "UART1(RS232)", /*"UART2(Debug)",*/ "UART3(RCC)"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartChoose.setText(uartChoose.getValue(position));
                showUART(uartMap.get(position));
                uartChoose.dismiss();
            }
        });

        uartModeChoose = new ChoosePopup(this, new String[]{"手动", "自动"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartMode.setText(uartModeChoose.getValue(position));
                uartModeChoose.dismiss();
                getCurrentUart().workBit = position;
            }
        });

        uartDataChoose = new ChoosePopup(this, new String[]{"5", "6", "7", "8"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartData.setText(uartDataChoose.getValue(position));
                uartDataChoose.dismiss();
                getCurrentUart().dataBit = Tool.stringToInt(uartDataChoose.getValue(position));
            }
        });

        uartStopChoose = new ChoosePopup(this, new String[]{"1", "2"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartStop.setText(uartStopChoose.getValue(position));
                uartStopChoose.dismiss();
                getCurrentUart().stopBit = Tool.stringToInt(uartStopChoose.getValue(position));
            }
        });

        uartCheckChoose = new ChoosePopup(this, new String[]{"NONE", "EVEN", "ODD", "MARK", "SPACE"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartCheck.setText(uartCheckChoose.getValue(position));
                uartCheckChoose.dismiss();
                getCurrentUart().checkBit = position;
            }
        });

        uartCommChoose = new ChoosePopup(this, new String[]{"NONE", "CUSTOM", "HQMEMS", "HQLED", "MBUS", "MODBUS_ASCII", "MODBUS_RTU"}, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvUartComm.setText(uartCommChoose.getValue(position));
                uartCommChoose.dismiss();
                getCurrentUart().comm = position;
            }
        });

        ipModeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.ip_mode), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvIpMode.setText(ipModeChoose.getValue(position));
                ipModeChoose.dismiss();
                binding.reIp.setVisibility(position == 0?View.VISIBLE:View.GONE);
            }
        });
    }


    @Override
    protected void initEventAndData() {
        getPresenter().getAll();
        getPresenter().setUpdateCallback(this);
        getPresenter().getCurrentImageInfo();
        showProDialog();
        setShowText("数据获取中...");
        handler.sendEmptyMessageDelayed(0, 5000);
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

    @Override
    protected void onStop() {
        lightChoose.dismiss();
        screenHNumChoose.dismiss();
        screenVNumChoose.dismiss();
        screenTypeChoose.dismiss();
        scanTypeChoose.dismiss();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        getPresenter().setUpdateCallback(null);
        updateHintDialog.dismiss();
        handler.removeCallbacksAndMessages(null);
        receiveDialog.dismiss();
        super.onDestroy();
    }

    private void showPopup(View v, ChoosePopup popup){
        if(popup.isShowing()){
            popup.dismiss();
        } else {
            popup.showAsDropDown(v);
        }
    }

    private Uri fontUri, mcuUri;
    private CurrentImageInfo currentImageInfo;

    private ActivityResultLauncher<Intent> fontActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    Tool.toastShow(SettingActivity.this, "字库文件获取失败");
                    return;
                }
                fontUri = result.getData().getData();
                binding.btChooseFont.setText(Tool.uriToFileName(SettingActivity.this, fontUri));

            }
        }
    });

    private ActivityResultLauncher<Intent> mcuActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() == null) {
                    Tool.toastShow(SettingActivity.this, "升级文件获取失败");
                    return;
                }
                mcuUri = result.getData().getData();
                binding.btChooseSys.setText(Tool.uriToFileName(SettingActivity.this, mcuUri));
            }
        }
    });



    public void chooseFile(int type) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if(type == 0){
            fontActivity.launch(intent);
        } else {
            mcuActivity.launch(intent);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_set_rcp:
                setRcp();
                break;

            case R.id.tv_rcpwm_polarity:
                showPopup(v, rPolarityChoose);
                break;

            case R.id.bt_set_time:
                setTime();
                break;

            case R.id.tv_light_set:
                showPopup(v, lightChoose);
                break;

            case R.id.tv_screen_h_num:
                showPopup(v, screenHNumChoose);
                break;
            case R.id.tv_screen_v_num:
                showPopup(v, screenVNumChoose);
                break;

            case R.id.tv_screen_type:
                showPopup(v, screenTypeChoose);
                break;

            case R.id.tv_scan_type:
                showPopup(v, scanTypeChoose);
                break;

            case R.id.tv_oe_polarity:
                showPopup(v, oeChoose);
                break;

            case R.id.tv_data_polarity:
                showPopup(v, dataChoose);
                break;

            case R.id.tv_move_speed:
                showPopup(v, moveSpeedChoose);
                break;

            case R.id.bt_set_screen:
                setScreen();
                break;

            case R.id.tv_font_type:
                showPopup(v, fontTypeChoose);
                break;

            case R.id.tv_font_size:
                showPopup(v, fontSizeChoose);
                break;

            case R.id.tv_font_face:
                showPopup(v, fontFaceChoose);
                break;

            case R.id.bt_set_font:
                setFont();
                break;

            case R.id.tv_uart_choose:
                showPopup(v, uartChoose);
                break;

            case R.id.tv_uart_mode:
                showPopup(v, uartModeChoose);
                break;

            case R.id.tv_uart_data:
                showPopup(v, uartDataChoose);
                break;

            case R.id.tv_uart_stop:
                showPopup(v, uartStopChoose);
                break;

            case R.id.tv_uart_check:
                showPopup(v, uartCheckChoose);
                break;

            case R.id.tv_uart_comm:
                showPopup(v, uartCommChoose);
                break;

            case R.id.bt_uart_set:
                setUart();
                break;

            case R.id.tv_ip_mode:
                showPopup(v, ipModeChoose);
                break;

            case R.id.bt_ip_set:
                setIp();
                break;

            case R.id.bt_choose_font:
                chooseFile(0);
                break;

            case R.id.bt_update_font:
                if(fontUri == null){
                    Tool.toastShow(this, "请选择一个升级文件");
                    return;
                }
                if(mPresenter.setUPG(1)){
                    SystemClock.sleep(300);
                    if(mPresenter.updateFont(fontUri)){
                        updateHintDialog.setIndex(0);
                        updateHintDialog.show();
                    } else {
                        Tool.toastShow(this, "升级失败");
                    }
                } else {
                    Tool.toastShow(this, "升级字库指令发送失败");
                }

                break;

            case R.id.bt_choose_sys:
                chooseFile(1);
                if(currentImageInfo == null){
                    mPresenter.getMcuInfo();
                }
                break;

            case R.id.bt_update_sys:
                if(mcuUri == null){
                    Tool.toastShow(this, "请选择一个升级文件");
                    return;
                }
                if(this.currentImageInfo == null){
                    Tool.toastShow(this, "系统软件版本读取异常，请重试");
                    mPresenter.getMcuInfo();
                    return;
                }
                if(mPresenter.setUPG(2)) {
                    SystemClock.sleep(300);
                    if (mPresenter.updateSys(mcuUri, currentImageInfo)) {
                        updateHintDialog.setIndex(1);
                        updateHintDialog.show();
                    } else {
                        Tool.toastShow(this, "升级失败");
                    }
                } else {
                    Tool.toastShow(this, "升级系统指令发送失败");
                }
                break;

            case R.id.bt_ms_set:
                setModeBus();
                break;

            case R.id.bt_reset:
                setReset(3);
                break;

            case R.id.bt_restore_factory:
                setReset(2);
                break;

            default:
                break;
        }
    }

    private void setModeBus() {
        String text = binding.etMsAddress.getText().toString();
        int address = Tool.stringToInt(text);
        if(address < 0 || address > 127){
            Tool.toastShow(this, "设备地址范围0~127");
            return;
        }
        text = binding.etMsCode.getText().toString();
        int code = Tool.stringToInt(text);
        if(code < 0 || code > 99){
            Tool.toastShow(this, "功能码范围0~99");
            return;
        }
        text = binding.etMsRegAddress.getText().toString();
        int reg = Tool.stringToInt(text);
        if(reg < 0 || reg > 9999){
            Tool.toastShow(this, "寄存器地址范围0~9999");
            return;
        }
        if(mPresenter.setModbus(address, code, reg)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setIp() {
        int mode = ipModeChoose.getSelect(binding.tvIpMode.getText().toString());
        String port = binding.etPort.getText().toString();
        if(TextUtils.isEmpty(port)){
            Tool.toastShow(this, "请设置端口号（0~9999）");
            return;
        }
        String ip = "000000000000";
        String gateway = "000000000000";
        String mask = "000000000000";
        String dns = "000000000000";
        if(mode == 0){
            ip = binding.etIpAddress.getText().toString();
            if(TextUtils.isEmpty(ip)){
                Tool.toastShow(this, "请设置IP地址");
                return;
            }
            if(!Tool.isIP(ip)){
                Tool.toastShow(this, "IP地址不正确");
                return;
            }
            gateway = binding.etIpGateway.getText().toString();
            if(TextUtils.isEmpty(gateway)){
                Tool.toastShow(this, "请设置网关地址");
                return;
            }
            if(!Tool.isIP(gateway)){
                Tool.toastShow(this, "网关地址不正确");
                return;
            }
            mask = binding.etIpMask.getText().toString();
            if(TextUtils.isEmpty(mask)){
                Tool.toastShow(this, "请设置子网掩码");
                return;
            }
            if(!Tool.isIP(mask)){
                Tool.toastShow(this, "子网掩码不正确");
                return;
            }
            dns = binding.etIpDns.getText().toString();
            /*if(TextUtils.isEmpty(dns)){
                Tool.toastShow(this, "请设置DNS");
                return;
            }*/
           /* if(!Tool.isIP(dns)){
                Tool.toastShow(this, "DNS地址不正确");
                return;
            }*/
            ip = Tool.concatIp(ip);
            gateway = Tool.concatIp(gateway);
            mask = Tool.concatIp(mask);
//            dns = Tool.concatIp(dns);
        }

        if(mPresenter.setEnetIp(mode, ip, gateway, mask, dns, Tool.stringToInt(port))){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }


    private void setReset(int type) {
        if(mPresenter.reset(type)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setUart() {
        String text = binding.etUartBaud.getText().toString();
        if(TextUtils.isEmpty(text)){
            Tool.toastShow(this, "请设置波特率");
            return;
        }

        int type = uartChoose.getSelect(binding.tvUartChoose.getText().toString());
        UartInfo uartInfo = uartMap.get(type);
        uartInfo.baud = Tool.stringToInt(text);
        String cmd = "";
        switch (type){
            case 0:
                cmd = CommandUtil.UART0;
                break;
            case 1:
                cmd = CommandUtil.UART1;
                break;
            case 2:
//                cmd = CommandUtil.UART2;
                cmd = CommandUtil.UART3;
                break;
            case 3:
                cmd = CommandUtil.UART3;
                break;
        }
        if(mPresenter.setUart(cmd, uartInfo.workBit, uartInfo.baud, uartInfo.dataBit, uartInfo.stopBit, uartInfo.checkBit, uartInfo.comm)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setFont(){
        int type = fontTypeChoose.getSelect(binding.tvFontType.getText().toString());
        int size = Tool.stringToInt(binding.tvFontSize.getText().toString());
        int face = fontFaceChoose.getSelect(binding.tvFontFace.getText().toString());
        if(mPresenter.setFont(type, sysFont, size, size, face)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setScreen() {
        String h = binding.tvScreenHNum.getText().toString();
        String v = binding.tvScreenVNum.getText().toString();
        boolean b1 = mPresenter.setLPNum(Tool.stringToInt(h), Tool.stringToInt(v), maxH, maxV);

        int type = screenTypeChoose.getSelect(binding.tvScreenType.getText().toString());
        int scanType = scanTypeChoose.getSelect(binding.tvScanType.getText().toString());
        int oe = oeChoose.getSelect(binding.tvOePolarity.getText().toString());
        int data = dataChoose.getSelect(binding.tvDataPolarity.getText().toString());
        int speed = moveSpeedChoose.getSelect(binding.tvMoveSpeed.getText().toString());

        boolean b2 = mPresenter.setPType(type, scanType, oe,data, speed);
        if(b1 && b2){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setLPBri(int value) {
        if (mPresenter.setLPBri(Tool.stringToInt(binding.tvLightBrightness.getText().toString()), value)) {
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setTime() {
        String time = Tool.getCurrentDate();
        if (getPresenter().setTime(time)) {
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setRcp() {
        String text = binding.etRcp.getText().toString();
        if (TextUtils.isEmpty(text)) {
            Tool.toastShow(SettingActivity.this, "RCPWM设置范围0~99");
            return;
        }
        int r = rPolarityChoose.getSelect(binding.tvRcpwmPolarity.getText().toString());
        if (mPresenter.setRCPwm(Tool.stringToInt(text), r)) {
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }

    }

    @Override
    public void onConnectState(int state) {
        if (state != States.CONNECTED) {
            Tool.toastShow(this, "蓝牙连接已断开");
            Intent intent = new Intent(this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onReceiveData(String command, String data) {
        receiveDialog.addTvShow(command + ": " + data);
        if (CommandUtil.VER.startsWith(command)) {
            getVer(data);
        } else if (CommandUtil.SYST.startsWith(command)) {
            getSys(data);
        } else if (CommandUtil.RCPWM.startsWith(command)) {
            getRCPWm(data);
        } else if (CommandUtil.LPBRI.startsWith(command)) {
            getLPBRi(data);
        } else if (CommandUtil.LPNUM.startsWith(command)) {
            getLPNum(data);
        } else if (CommandUtil.PTYPE.startsWith(command)) {
            getPType(data);
        } else if (CommandUtil.FONT.startsWith(command)) {
            getFont(data);
        } else if (CommandUtil.UART0.startsWith(command)) {
            getUART(0, data);
            showUART(uartMap.get(0));
        } else if (CommandUtil.UART1.startsWith(command)) {
            getUART(1, data);
        } else if (CommandUtil.UART2.startsWith(command)) {
//            getUART(2, data);
        } else if (CommandUtil.UART3.startsWith(command)) {
            getUART(2, data);
        } else if (CommandUtil.MODBUS.startsWith(command)) {
            getModeBus(data);
        } else if (CommandUtil.ENETIP.startsWith(command)) {
            getIp(data);
        }
    }

    private void getModeBus(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 3) {
            binding.etMsAddress.setText(vs[0]);
            binding.etMsCode.setText(vs[1]);
            binding.etMsRegAddress.setText(vs[2]);
        }
    }

    private void getSys(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 2) {
            binding.tvWd.setText(vs[0] + "°");
            binding.tvDy.setText(vs[1] + "mV");
        }
    }

    private void getVer(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 2) {
            binding.tvVer.setText(vs[0] + "-" + vs[1]);
        }
    }

    private void getIp(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            int ipMode = Tool.stringToInt(vs[0]);
            binding.tvIpMode.setText(ipModeChoose.getValue(ipMode));
            binding.reIp.setVisibility(ipMode == 0?View.VISIBLE:View.GONE);
            String ip = Tool.parserIp(vs[1]);
            binding.etIpAddress.setText(ip);
            binding.etIpGateway.setText(Tool.parserIp(vs[2]));
            binding.etIpMask.setText(Tool.parserIp(vs[3]));
//            binding.etIpDns.setText(Tool.parserIp(vs[4]));
            binding.etPort.setText(String.valueOf(Tool.stringToInt(vs[4])));
        }
    }

    Map<Integer, UartInfo> uartMap = new HashMap<>();

    private void getUART(int type, String data) {
        UartInfo uartInfo = new UartInfo();
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            uartInfo.workBit = Tool.stringToInt(vs[0]);
            uartInfo.baud = Tool.stringToInt(vs[1]);
            uartInfo.dataBit = Tool.stringToInt(vs[2]);
            uartInfo.stopBit = Tool.stringToInt(vs[3]);
            uartInfo.checkBit = Tool.stringToInt(vs[4]);
//            uartInfo.comm = Tool.stringToInt(vs[5]);
        }
        uartMap.put(type, uartInfo);
    }

    private void showUART(UartInfo uartInfo) {
        if (uartInfo == null) {
            return;
        }
        binding.tvUartMode.setText(uartModeChoose.getValue(uartInfo.workBit));
        binding.etUartBaud.setText(String.valueOf(uartInfo.baud));
        binding.tvUartData.setText(String.valueOf(uartInfo.dataBit));
        binding.tvUartStop.setText(String.valueOf(uartInfo.stopBit));
        binding.tvUartCheck.setText(uartCheckChoose.getValue(uartInfo.checkBit));
//        binding.tvUartComm.setText(uartCommChoose.getValue(uartInfo.comm));
    }

    private UartInfo getCurrentUart(){
        String text = binding.tvUartChoose.getText().toString();
        int type = uartChoose.getSelect(text);
        return uartMap.get(type);
    }

    private int sysFont;
    private void getFont(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            binding.tvFontType.setText(fontTypeChoose.getValue(Tool.stringToInt(vs[0])));
            sysFont = Tool.stringToInt(vs[1]);
            binding.tvFontSize.setText(String.valueOf(Tool.stringToInt(vs[2])));
            binding.tvFontFace.setText(fontFaceChoose.getValue(Tool.stringToInt(vs[4])));
        }
    }

    private void getPType(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            binding.tvScreenType.setText(screenTypeChoose.getValue(Tool.stringToInt(vs[0])));
            binding.tvScanType.setText(scanTypeChoose.getValue(Tool.stringToInt(vs[1])));
            binding.tvOePolarity.setText(oeChoose.getValue(Tool.stringToInt(vs[2])));
            binding.tvDataPolarity.setText(dataChoose.getValue(Tool.stringToInt(vs[3])));
            binding.tvMoveSpeed.setText(String.valueOf(Tool.stringToInt(vs[4])));
        }
    }

    private int maxH, maxV;

    private void getLPNum(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 4) {
            binding.tvScreenHNum.setText(String.valueOf(Tool.stringToInt(vs[0])));
            binding.tvScreenVNum.setText(String.valueOf(Tool.stringToInt(vs[1])));
            maxH = Tool.stringToInt(vs[2]);
            String[] dh = new String[maxH];
            for (int i = 0; i < maxH; i++) {
                dh[i] = String.valueOf(i + 1);
            }
            screenHNumChoose.updateData(dh);

            maxV = Tool.stringToInt(vs[3]);
            String[] dv = new String[maxV];
            for (int i = 0; i < maxV; i++) {
                dv[i] = String.valueOf(i + 1);
            }
            screenVNumChoose.updateData(dv);
        }
    }


    private void getRCPWm(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 2) {
            binding.etRcp.setText(String.valueOf(Tool.stringToInt(vs[0])));
            binding.tvRcpwmPolarity.setText(rPolarityChoose.getValue(Tool.stringToInt(vs[1])));
        }
    }

    private void getLPBRi(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 2) {
            binding.tvLightBrightness.setText(String.valueOf(Tool.stringToInt(vs[0])));
            binding.tvLightSet.setText(String.valueOf(Tool.stringToInt(vs[1])));
        }
    }


    @Override
    public void onCurrentImageInfo(CurrentImageInfo currentImageInfo) {
        if(currentImageInfo != null){
            this.currentImageInfo = currentImageInfo;
            binding.tvSysVer.setText("软件版本：" + currentImageInfo.getVersion());

            receiveDialog.addTvShow("CurrentImageInfo: " + currentImageInfo);
        }
    }

    @Override
    public void onUpdate(boolean success, int type, String msg, int progress, int total) {
        updateHintDialog.addTvShow(msg);
        /*if(msg.contains("ending")){
            updateHintDialog.dismiss();
            if(success){
                Tool.toastShow(this, "升级成功");
            } else {
                Tool.toastShow(this, "升级失败");
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.show) {
            if(!receiveDialog.isShowing()){
                receiveDialog.show();
            }
            return true;
        }
        try {

        }catch (Exception e){

        }finally {

        }
        return super.onOptionsItemSelected(item);
    }
}
