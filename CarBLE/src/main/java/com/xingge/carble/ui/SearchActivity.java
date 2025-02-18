package com.xingge.carble.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pgyersdk.update.PgyUpdateManager;
import com.xingge.carble.R;
import com.xingge.carble.base.BaseActivity;
import com.xingge.carble.base.mode.IBaseActivity;
import com.xingge.carble.bluetooth.States;
import com.xingge.carble.dialog.InputPwdDialog;
import com.xingge.carble.ui.mode.SearchContract;
import com.xingge.carble.ui.mode.SearchPresenter;
import com.xingge.carble.util.Tool;

import java.util.List;

public class SearchActivity extends IBaseActivity<SearchPresenter> implements SearchContract.View, SearchDevAdapter.OnItemClickListener {

    private int searchTime = 10 * 1000;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SearchDevAdapter searchDevAdapter;
    private InputPwdDialog inputPwdDialog;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().releaseAll();
        searchBle();
    }

    @Override
    protected SearchPresenter onLoadPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().refreshDevices(searchTime);
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            searchBle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initEventAndData() {
        getPresenter().releaseAll();

        searchDevAdapter = new SearchDevAdapter(this, getPresenter().getDevices());
        searchDevAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(searchDevAdapter);
//        swipeRefreshLayout.setRefreshing(true);

        checkPermission(new BaseActivity.CheckPermListener() {
            @Override
            public void superPermission() {
                searchBle();
            }
        }, R.string.permiss_tip, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT);

        checkPermission(new BaseActivity.CheckPermListener() {
            @Override
            public void superPermission() {
                checkApp();
            }
        }, R.string.permiss_tip, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void checkApp() {
        new PgyUpdateManager.Builder()
                .setForced(false)
                .setUserCanRetry(true)
                .setDeleteHistroyApk(true)
                .register();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        swipeRefreshLayout.setRefreshing(false);
        getPresenter().stopSearch();
    }

    @Override
    public void onBackPressed() {
        getPresenter().releaseAll();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            searchBle();
        }
    }

    @Override
    public boolean onPermissionsDenied(int requestCode, List<String> perms) {
        boolean b = super.onPermissionsDenied(requestCode, perms);
        if (!b) {
            getPresenter().searchDevices(searchTime);
            Tool.toastLongShow(this, "权限申请失败，可能无法搜索到设备");
        }
        return b;
    }

    private void searchBle() {
        if (!getPresenter().isOpenBlue()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        } else {
            getPresenter().refreshDevices(searchTime);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_confirm) {
            if (inputPwdDialog.getPwd().length() < 6) {
                Tool.toastLongShow(this, "密码必须为6位数字");
                return;
            }
            if (!getPresenter().submitPwd(inputPwdDialog.getPwd())) {
                Tool.toastLongShow(this, "验证失败，请重试");
            }
            inputPwdDialog.dismiss();
        } else if (v.getId() == R.id.bt_cancel) {
            getPresenter().closeConnect();
            inputPwdDialog.dismiss();
        }
    }

    @Override
    public void updateDevices() {
        searchDevAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchStop() {
//        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onConnectState(int state) {
        if (state == States.CONNECTED) {
//            startActivity(new Intent(this, MainActivity.class));
        } else {
            Tool.toastShow(this, "连接失败,请重试...");
        }
    }

    @Override
    public void onCheckPwdState(int state) {
        if (state == 0) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            if (inputPwdDialog == null) {
                inputPwdDialog = new InputPwdDialog(this);
                inputPwdDialog.setBtClick(this);
            }
            inputPwdDialog.show();
        }
    }

    @Override
    public void onItemClickListener(View view, int position) {
        getPresenter().stopSearch();
//        swipeRefreshLayout.setRefreshing(false);
        getPresenter().onItem(position);
    }


}
