package com.rfstar.kevin.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.rfstar.kevin.R;
import com.rfstar.kevin.app.App;
import com.rfstar.kevin.params.BLEDevice.RFStarBLEBroadcastReceiver;
import com.rfstar.kevin.params.MemberItem;
import com.rfstar.kevin.tools.Tools;
import com.rfstar.kevin.view.SegmentViewController;
import com.rfstar.kevin.view.SegmentViewController.SegmentViewControllerClickListener;

/**
 * PWM输出
 *
 * @author kevin
 */
public class PWMActivity extends BaseActivity implements
        OnSeekBarChangeListener, SegmentViewControllerClickListener,
        OnClickListener, RFStarBLEBroadcastReceiver {
    private LinearLayout segmentLayout = null, contentLayout = null;
    private SegmentViewController segmentView = null;
    private SeekBar PWM1Seek, PWM2Seek, PWM3Seek, PWM4Seek, outSignalSeek,
            changeTiemSeek;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwm);
        this.initView();
    }

    @SuppressWarnings("static-access")
    private void initView() {
        // TODO Auto-generated method stub
        Intent intent = this.getIntent();
        MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation(member.name);

        this.segmentLayout = (LinearLayout) this
                .findViewById(R.id.segmentLayout);
        this.segmentView = new SegmentViewController(this);
        this.segmentView.setOnClick(this);
        this.segmentLayout.addView(this.segmentView);

        this.contentLayout = (LinearLayout) this
                .findViewById(R.id.contentLayout);

        PWM1Seek = new SeekBar(this);
        PWM2Seek = new SeekBar(this);
        PWM3Seek = new SeekBar(this);
        PWM4Seek = new SeekBar(this);
        outSignalSeek = new SeekBar(this);
        changeTiemSeek = new SeekBar(this);

        this.contentLayout.addView(this
                .seekBarInit(PWM1Seek, "PWM1(0~255) P11"));
        this.contentLayout.addView(this
                .seekBarInit(PWM2Seek, "PWM2(0~255) P10"));
        this.contentLayout.addView(this
                .seekBarInit(PWM3Seek, "PWM3(0~255) P07"));
        this.contentLayout.addView(this
                .seekBarInit(PWM4Seek, "PWM4(0~255) P06"));
        this.contentLayout.addView(this.outSingalseekBarInit(outSignalSeek,
                "信号频率设置(500~65535)"));
        this.contentLayout.addView(this.changeTimeWidthseekBarInit(
                changeTiemSeek, "PWM转变时间宽度(0~65535)"));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (app.manager.cubicBLEDevice != null)
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
    }

    /**
     * 初始View
     *
     * @param bar
     * @param label
     * @return
     */
    @SuppressWarnings("static-access")
    private View seekBarInit(SeekBar bar, String label) {
        LayoutParams params = new LayoutParams(-1, -2);
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                -2, -2));
        messageLayout.setPadding(0, 30, 0, 0);
        TextView messgeTxt = new TextView(this);
        messgeTxt.setText(label);
        LayoutParams numParams = new LayoutParams(-1, -2);
        messageLayout.setLayoutParams(numParams);
        messgeTxt.setTextColor(Color.BLACK);
        TextView numTxt = new TextView(this);

        numTxt.setLayoutParams(numParams);
        numTxt.setGravity(Gravity.RIGHT);
        numTxt.setText("0");
        numTxt.setTextColor(Color.BLACK);
        numTxt.setPadding(0, 0, 10, 0);

        messageLayout.addView(messgeTxt);
        messageLayout.addView(numTxt);
        contentView.addView(messageLayout);

        LinearLayout seekbarLayout = new LinearLayout(this);
        seekbarLayout.setPadding(0, 10, 0, 10);
        seekbarLayout.setLayoutParams(params);
        bar.setLayoutParams(params);
        seekbarLayout.setBackgroundResource(R.drawable.seek_layout_bg);
        seekbarLayout.addView(bar);

        contentView.addView(seekbarLayout);
        bar.setOnSeekBarChangeListener(this);
        bar.setTag(numTxt);
        bar.setMax(255);
        bar.setProgress(255);

        return contentView;
    }

    /**
     * 初始View
     *
     * @param bar
     * @param label
     * @return
     */
    @SuppressWarnings("static-access")
    private View outSingalseekBarInit(SeekBar bar, String label) {
        LayoutParams params = new LayoutParams(-1, -2);
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                -2, -2));
        messageLayout.setPadding(0, 30, 0, 0);
        TextView messgeTxt = new TextView(this);
        messgeTxt.setText(label);
        LayoutParams numParams = new LayoutParams(-1, -2);
        messageLayout.setLayoutParams(numParams);
        messgeTxt.setTextColor(Color.BLACK);
        TextView numTxt = new TextView(this);

        numTxt.setLayoutParams(numParams);
        numTxt.setGravity(Gravity.RIGHT);
        numTxt.setText("0");
        numTxt.setTextColor(Color.BLACK);
        numTxt.setPadding(0, 0, 10, 0);

        messageLayout.addView(messgeTxt);
        messageLayout.addView(numTxt);
        contentView.addView(messageLayout);

        LinearLayout seekbarLayout = new LinearLayout(this);
        seekbarLayout.setPadding(0, 10, 0, 10);
        seekbarLayout.setLayoutParams(params);
        bar.setLayoutParams(params);
        seekbarLayout.setBackgroundResource(R.drawable.seek_layout_bg);
        seekbarLayout.addView(bar);

        contentView.addView(seekbarLayout);
        bar.setOnSeekBarChangeListener(this);
        bar.setTag(numTxt);
        bar.setMax(65535 - 500);
        bar.setProgress(0);
        return contentView;
    }

    /**
     * 初始View
     *
     * @param bar
     * @param label
     * @return
     */
    @SuppressWarnings("static-access")
    private View changeTimeWidthseekBarInit(SeekBar bar, String label) {
        LayoutParams params = new LayoutParams(-1, -2);
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout messageLayout = new LinearLayout(this);
        messageLayout.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                -2, -2));
        messageLayout.setPadding(0, 30, 0, 0);
        TextView messgeTxt = new TextView(this);
        messgeTxt.setText(label);
        LayoutParams numParams = new LayoutParams(-1, -2);
        messageLayout.setLayoutParams(numParams);
        messgeTxt.setTextColor(Color.BLACK);
        TextView numTxt = new TextView(this);

        numTxt.setLayoutParams(numParams);
        numTxt.setGravity(Gravity.RIGHT);
        numTxt.setText("0");
        numTxt.setTextColor(Color.BLACK);
        numTxt.setPadding(0, 0, 10, 0);

        messageLayout.addView(messgeTxt);
        messageLayout.addView(numTxt);
        contentView.addView(messageLayout);

        LinearLayout seekbarLayout = new LinearLayout(this);
        seekbarLayout.setOrientation(LinearLayout.VERTICAL);
        seekbarLayout.setPadding(0, 10, 0, 10);
        seekbarLayout.setLayoutParams(params);
        bar.setLayoutParams(params);
        seekbarLayout.setBackgroundResource(R.drawable.seek_layout_bg);
        seekbarLayout.addView(bar);
        // 按钮部分
        LinearLayout stateLayout = new LinearLayout(this);
        stateLayout.setPadding(40, 0, 40, 0);
        stateLayout.setWeightSum(2);
        stateLayout.setOrientation(LinearLayout.HORIZONTAL);
        stateLayout.setLayoutParams(new LayoutParams(-1, -2));
        Button offBtn = new Button(this), onBtn = new Button(this);
        LayoutParams btnParamsOff = new LayoutParams(-1, 81, 1);
        LayoutParams btnParamsOn = new LayoutParams(-1, 81, 1);
        btnParamsOff.setMargins(20, 0, 0, 0);
        btnParamsOn.setMargins(0, 0, 20, 0);
        offBtn.setLayoutParams(btnParamsOff);
        onBtn.setLayoutParams(btnParamsOn);
        onBtn.setPadding(0, 5, 0, 5);
        offBtn.setText("关");
        onBtn.setText("开");
        onBtn.setTag(true);
        offBtn.setTag(false);
        offBtn.setTextColor(Color.BLUE);
        onBtn.setTextColor(Color.BLUE);
        onBtn.setBackgroundResource(R.drawable.button_item_click);
        offBtn.setBackgroundResource(R.drawable.button_item_click);
        stateLayout.addView(onBtn);
        stateLayout.addView(offBtn);
        seekbarLayout.addView(stateLayout);
        offBtn.setOnClickListener(this);
        onBtn.setOnClickListener(this);

        contentView.addView(seekbarLayout);

        numTxt.setText("" + bar.getProgress());
        bar.setTag(numTxt);
        bar.setMax(100);
        bar.setProgress(0);
        contentView.setPadding(0, 0, 0, 20);
        bar.setOnSeekBarChangeListener(this);
        return contentView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        TextView numTxt = (TextView) seekBar.getTag();
        numTxt.setText("" + progress);
        if (seekBar == outSignalSeek) {
            numTxt.setText("" + (progress + 500));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        if (seekBar == PWM1Seek || seekBar == PWM2Seek || seekBar == PWM3Seek
                || seekBar == PWM4Seek) {
            byte[] data = new byte[4];
            data[0] = (byte) PWM1Seek.getProgress();
            data[1] = (byte) PWM2Seek.getProgress();
            data[2] = (byte) PWM3Seek.getProgress();
            data[3] = (byte) PWM4Seek.getProgress();
            if (app.manager.cubicBLEDevice != null)
                app.manager.cubicBLEDevice.writeValue("ffb0", "ffb2", data);
        } else if (seekBar == outSignalSeek) {
            byte[] data = new byte[2];
            int progress = outSignalSeek.getProgress() + 500;
            data[0] = (byte) ((progress >> 8) & 0xFF);
            data[1] = (byte) (progress & 0xFF);

            Log.d(App.TAG, "ffb3 data  : " + Tools.byte2Hex(data));
            if (app.manager.cubicBLEDevice != null)
                app.manager.cubicBLEDevice.writeValue("ffb0", "ffb3", data);
        } else if (seekBar == changeTiemSeek) {
            byte[] data = new byte[2];
            data[0] = (byte) ((changeTiemSeek.getProgress() >> 8) & 0xFF);
            data[1] = (byte) (changeTiemSeek.getProgress() & 0xFF);

            Log.d(App.TAG, "ffb4 data  : " + Tools.byte2Hex(data));
            if (app.manager.cubicBLEDevice != null)
                app.manager.cubicBLEDevice.writeValue("ffb0", "ffb4", data);
        }
    }

    /**
     * @see SegmentViewControllerClickListener
     */
    @Override
    public void SegmentControllerClick(int position) {
        // TODO Auto-generated method stub
        byte[] data = new byte[1];
        switch (position) {
            case 0:
                data[0] = 0x00;
                break;
            case 1:
                data[0] = 0x01;
                break;
            case 2:
                data[0] = 0x2;
                break;
            default:
                break;
        }
        if (app.manager.cubicBLEDevice != null)
            app.manager.cubicBLEDevice.writeValue("ffb0", "ffb1", data);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v instanceof Button) {
            Button btn = (Button) v;
            boolean boo = (Boolean) btn.getTag();

            byte[] data = new byte[4];
            if (boo) { // 开
                showMessage("开");
                data[0] = (byte) PWM1Seek.getProgress();
                data[1] = (byte) PWM2Seek.getProgress();
                data[2] = (byte) PWM3Seek.getProgress();
                data[3] = (byte) PWM4Seek.getProgress();
            } else { // 关
                showMessage("关");
                data[0] = (byte) 0xff;
                data[1] = (byte) 0xff;
                data[2] = (byte) 0xff;
                data[3] = (byte) 0xff;
            }
            if (app.manager.cubicBLEDevice != null)
                app.manager.cubicBLEDevice.writeValue("ffb0", "ffb2", data);
        }
    }

    /**
     * 返回的数据
     */
    @Override
    public void onReceive(Context context, Intent intent, String macData,
                          String uuid) {
        // TODO Auto-generated method stub
        this.connectedOrDis(intent.getAction());
    }
}
