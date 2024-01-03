package com.xing.sd.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.xing.sd.R;
import com.xing.sd.base.mode.IBaseActivity;
import com.xing.sd.bluetooth.States;
import com.xing.sd.databinding.ActivityMainBinding;
import com.xing.sd.dialog.ChooseAdapter;
import com.xing.sd.dialog.ChoosePopup;
import com.xing.sd.ui.mode.MainContract;
import com.xing.sd.ui.mode.MainPresenter;
import com.xing.sd.util.CommandUtil;
import com.xing.sd.util.Tool;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends IBaseActivity<ActivityMainBinding, MainPresenter> implements MainContract.View {

    private ChoosePopup moveTypeChoose, sourceChoose, idleStateChoose, showTypeChoose;

    private TextView curMoveTV, curShowTV;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == 0){
                dismissProDialog();
            }
            return true;
        }
    });

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
                onBackPressed();
            }
        });
        binding.toolbar.setTitle("设备列表: " + Tool.getVersionName(this));
        binding.tvTitleMoveType.setOnClickListener(this);
        binding.tvShowMoveType.setOnClickListener(this);
        binding.tvShowSource.setOnClickListener(this);
        binding.tvShowIdle.setOnClickListener(this);
        binding.tvShowType1.setOnClickListener(this);
        binding.tvMoveType1.setOnClickListener(this);
        binding.tvShowType2.setOnClickListener(this);
        binding.tvMoveType2.setOnClickListener(this);
        binding.tvShowType3.setOnClickListener(this);
        binding.tvMoveType3.setOnClickListener(this);
        binding.btSetTitle.setOnClickListener(this);
        binding.btSetShow.setOnClickListener(this);
        binding.btSetContent1.setOnClickListener(this);
        binding.btSetContent2.setOnClickListener(this);
        binding.btSetContent3.setOnClickListener(this);
        binding.btResetShowGroup.setOnClickListener(this);
        initPopup();
    }

    private void initPopup() {
        String[] moves = getResources().getStringArray(R.array.move_mode);
        moveTypeChoose = new ChoosePopup(this, moves, new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                curMoveTV.setText(moveTypeChoose.getValue(position));
                moveTypeChoose.dismiss();
            }
        });

        sourceChoose = new ChoosePopup(this, getResources().getStringArray(R.array.show_source), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvShowSource.setText(sourceChoose.getValue(position));
                sourceChoose.dismiss();
            }
        });

        idleStateChoose = new ChoosePopup(this, getResources().getStringArray(R.array.idle_state), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                binding.tvShowIdle.setText(idleStateChoose.getValue(position));
                idleStateChoose.dismiss();
            }
        });

        showTypeChoose = new ChoosePopup(this, getResources().getStringArray(R.array.show_type), new ChooseAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                curShowTV.setText(showTypeChoose.getValue(position));
                showTypeChoose.dismiss();
            }
        });

    }

    @Override
    protected void initEventAndData() {
        getPresenter().setRead(true);
        if (getPresenter().confirmConnect()) {
            getPresenter().getMainAll();
            showProDialog();
            setShowText("数据获取中...");
            mHandler.sendEmptyMessageDelayed(0, 5000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().setCallback();
    }

    @Override
    protected void onStop() {
        moveTypeChoose.dismiss();
        sourceChoose.dismiss();
        idleStateChoose.dismiss();
        showTypeChoose.dismiss();
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_set_title:
                setTitle();
                break;

            case R.id.bt_set_show:
                setDGType();
                break;

            case R.id.bt_set_content1:
                setDGShow(0);
                break;
            case R.id.bt_set_content2:
                setDGShow(1);
                break;
            case R.id.bt_set_content3:
                setDGShow(2);
                break;

            case R.id.tv_show_source:
                sourceChoose.showAsDropDown(v);
                break;

            case R.id.tv_show_idle:
                idleStateChoose.showAsDropDown(v);
                break;

            case R.id.tv_show_type1:
            case R.id.tv_show_type2:
            case R.id.tv_show_type3:
                curShowTV = findViewById(v.getId());
                showTypeChoose.showAsDropDown(v);
                break;

            case R.id.bt_reset_show_group:
                if(mPresenter.reset(1)){
                    Tool.toastShow(this, "设置成功");
                } else {
                    Tool.toastShow(this, "设置失败");
                }
                break;

            case R.id.tv_title_move_type:
            case R.id.tv_show_move_type:
            case R.id.tv_move_type1:
            case R.id.tv_move_type2:
            case R.id.tv_move_type3:
            default:
                curMoveTV = findViewById(v.getId());
                moveTypeChoose.showAsDropDown(v);
                break;
        }

    }

    private void setTitle() {
        String text = binding.etTitleSpacePx.getText().toString();
        int space = Tool.stringToInt(text);
        if(space < 0 || space > 99){
            Tool.toastShow(MainActivity.this, "移动循环间隔像素，范围0~99");
            return;
        }
        String title = binding.etTitle.getText().toString();
        if(!TextUtils.isEmpty(title)){
            if(title.getBytes(CommandUtil.charset).length > 50){
                Tool.toastShow(MainActivity.this, " 范围0~50个字符(中文一个字2个字符)");
                return;
            }
        }
        if(mPresenter.setDTitle(moveTypeChoose.getSelect(binding.tvTitleMoveType.getText().toString()), space, title)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setDGType() {
        int move = moveTypeChoose.getSelect(binding.tvShowMoveType.getText().toString());
        String text = binding.etShowSpacePx.getText().toString();
        int px = Tool.stringToInt(text);
        if(px < 0 || px > 99){
            Tool.toastShow(MainActivity.this, "移动循环间隔像素，范围0~99");
            return;
        }

        int source = sourceChoose.getSelect(binding.tvShowSource.getText().toString());
        int kx = idleStateChoose.getSelect(binding.tvShowIdle.getText().toString());
        text = binding.etShowTime.getText().toString();
        int time = Tool.stringToInt(text);
        if(time < 0 || time > 99){
            Tool.toastShow(MainActivity.this, "内容显示时间，范围0~99");
            return;
        }
        if(mPresenter.setDGType(move, px, source, kx, time)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
        }
    }

    private void setDGShow(int type) {
        String text;
        int x;
        int y;
        int showType;
        int move;
        int size;
        if(type == 0){
            text = binding.etX1.getText().toString();
            x = Tool.stringToInt(text);
            text = binding.etY1.getText().toString();
            y = Tool.stringToInt(text);
            showType = showTypeChoose.getSelect(binding.tvShowType1.getText().toString());
            move = moveTypeChoose.getSelect(binding.tvMoveType1.getText().toString());
            text = binding.etShowWindow1.getText().toString();
            size = Tool.stringToInt(text);
            text = binding.etContent1.getText().toString();
        } else if(type == 1){
            text = binding.etX2.getText().toString();
            x = Tool.stringToInt(text);
            text = binding.etY2.getText().toString();
            y = Tool.stringToInt(text);
            showType = showTypeChoose.getSelect(binding.tvShowType2.getText().toString());
            move = moveTypeChoose.getSelect(binding.tvMoveType2.getText().toString());
            text = binding.etShowWindow2.getText().toString();
            size = Tool.stringToInt(text);
            text = binding.etContent2.getText().toString();
        } else {
            text = binding.etX3.getText().toString();
            x = Tool.stringToInt(text);
            text = binding.etY3.getText().toString();
            y = Tool.stringToInt(text);
            showType = showTypeChoose.getSelect(binding.tvShowType3.getText().toString());
            move = moveTypeChoose.getSelect(binding.tvMoveType3.getText().toString());
            text = binding.etShowWindow3.getText().toString();
            size = Tool.stringToInt(text);
            text = binding.etContent3.getText().toString();
        }

        if(mPresenter.setDGShow(type, x, y, showType, move, size, text.getBytes(CommandUtil.charset).length, text)){
            Tool.toastShow(this, "设置成功");
        } else {
            Tool.toastShow(this, "设置失败");
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
        if (CommandUtil.DTITLE.startsWith(command)) {
            getDTitle(data);
        } else if (CommandUtil.DGSHOW.startsWith(command)) {
            getDGShow(data);
        } else if (CommandUtil.DGTYPE.startsWith(command)) {
            getDGType(data);
        }
    }

    private void getDTitle(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 3) {
            binding.tvTitleMoveType.setText(moveTypeChoose.getValue(Tool.stringToInt(vs[0])));
            binding.etTitleSpacePx.setText(String.valueOf(Tool.stringToInt(vs[1])));
            binding.etTitle.setText(vs[2]);
        }
    }

    private void getDGType(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 5) {
            binding.tvShowMoveType.setText(moveTypeChoose.getValue(Tool.stringToInt(vs[0])));
            binding.etShowSpacePx.setText(String.valueOf(Tool.stringToInt(vs[1])));
            binding.tvShowSource.setText(sourceChoose.getValue(Tool.stringToInt(vs[2])));
            binding.tvShowIdle.setText(idleStateChoose.getValue(Tool.stringToInt(vs[3])));
            binding.etShowTime.setText(String.valueOf(Tool.stringToInt(vs[4])));
        }
    }

    private void getDGShow(String data) {
        String[] vs = data.split(",");
        if (vs.length >= 8) {
            int t = Tool.stringToInt(vs[0]);
            if(t == 0){
                binding.etX1.setText(String.valueOf(Tool.stringToInt(vs[1])));
                binding.etY1.setText(String.valueOf(Tool.stringToInt(vs[2])));
                binding.tvShowType1.setText(showTypeChoose.getValue(Tool.stringToInt(vs[3])));
                binding.tvMoveType1.setText(moveTypeChoose.getValue(Tool.stringToInt(vs[4])));
                binding.etShowWindow1.setText(String.valueOf(Tool.stringToInt(vs[5])));

                int len =  Tool.stringToInt(vs[6]);

                if(len == vs[7].getBytes(CommandUtil.charset).length){
                    binding.etContent1.setText(vs[7]);
                } else {
                    String str = vs[7];
                    if(vs.length > 8){
                        for (int i = 8; i < vs.length; i++) {
                            str = str + "," + vs[8];
                        }
                        str = str.substring(0, str.length() - 1);
                    }
                    binding.etContent1.setText(str);
                }
            } else if(t == 1){
                binding.etX2.setText(String.valueOf(Tool.stringToInt(vs[1])));
                binding.etY2.setText(String.valueOf(Tool.stringToInt(vs[2])));
                binding.tvShowType2.setText(showTypeChoose.getValue(Tool.stringToInt(vs[3])));
                binding.tvMoveType2.setText(moveTypeChoose.getValue(Tool.stringToInt(vs[4])));
                binding.etShowWindow2.setText(String.valueOf(Tool.stringToInt(vs[5])));

                int len =  Tool.stringToInt(vs[6]);

                if(len == vs[7].getBytes(CommandUtil.charset).length){
                    binding.etContent2.setText(vs[7]);
                } else {
                    String str = vs[7];
                    if(vs.length > 8){
                        for (int i = 8; i < vs.length; i++) {
                            str = str + "," + vs[8];
                        }
                        str = str.substring(0, str.length() - 1);
                    }
                    binding.etContent2.setText(str);
                }
            } else {
                binding.etX3.setText(String.valueOf(Tool.stringToInt(vs[1])));
                binding.etY3.setText(String.valueOf(Tool.stringToInt(vs[2])));
                binding.tvShowType3.setText(showTypeChoose.getValue(Tool.stringToInt(vs[3])));
                binding.tvMoveType3.setText(moveTypeChoose.getValue(Tool.stringToInt(vs[4])));
                binding.etShowWindow3.setText(String.valueOf(Tool.stringToInt(vs[5])));

                int len =  Tool.stringToInt(vs[6]);

                if(len == vs[7].getBytes(CommandUtil.charset).length){
                    binding.etContent3.setText(vs[7]);
                } else {
                    String str = vs[7];
                    if(vs.length > 8){
                        for (int i = 8; i < vs.length; i++) {
                            str = str + "," + vs[8];
                        }
                        str = str.substring(0, str.length() - 1);
                    }
                    binding.etContent3.setText(str);
                }
                mHandler.removeMessages(0);
                dismissProDialog();
            }

        }
    }

}
