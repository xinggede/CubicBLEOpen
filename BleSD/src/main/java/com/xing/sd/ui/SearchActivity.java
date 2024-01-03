package com.xing.sd.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.xing.sd.base.mode.IBaseActivity;
import com.xing.sd.bluetooth.States;
import com.xing.sd.databinding.ActivitySearchBinding;
import com.xing.sd.ui.mode.SearchContract;
import com.xing.sd.ui.mode.SearchPresenter;
import com.xing.sd.util.Tool;

import java.util.concurrent.locks.ReentrantLock;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SearchActivity extends IBaseActivity<ActivitySearchBinding, SearchPresenter> implements SearchContract.View, SearchDevAdapter.OnItemClickListener {

    private int searchTime = 10 * 1000;
    private SearchDevAdapter searchDevAdapter;
    private ActivityResultLauncher<Intent> launcher;

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
    protected void initViews(Bundle savedInstanceState) {
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setTitle("设备列表: " + Tool.getVersionName(this));

        binding.swipeRefreshLayout.setEnabled(true);


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().refreshDevices(searchTime);
            }
        });
        ReentrantLock reentrantLock = new ReentrantLock();
    }

    @Override
    protected void onStart() {
        super.onStart();
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                searchBle();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        if (id == R.id.search) {
            searchBle();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initEventAndData() {
        getPresenter().releaseAll();
        searchDevAdapter = new SearchDevAdapter(this, getPresenter().getDevices());
        searchDevAdapter.setOnItemClickListener(this);
        binding.recyclerView.setAdapter(searchDevAdapter);
        binding.swipeRefreshLayout.setRefreshing(true);
        checkPermissions();
    }

    private void checkPermissions(){
        if (!Tool.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            searchBle();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.swipeRefreshLayout.setRefreshing(false);
        getPresenter().stopSearch();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().releaseAll();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { //OK
                searchBle();
            } else { //拒绝
                getPresenter().searchDevices(searchTime);
                Tool.toastLongShow(this, "权限申请失败，可能无法搜索到设备");
            }
        }
    }

    private void searchBle() {
        if (!getPresenter().isOpenBlue()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            launcher.launch(enableBtIntent);
        } else {
            getPresenter().refreshDevices(searchTime);
        }
    }

    @Override
    public void updateDevices() {
        searchDevAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchStop() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onConnectState(int state) {
        if (state == States.CONNECTED) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Tool.toastShow(this, "连接失败,请重试...");
        }
    }


    @Override
    public void onItemClickListener(View view, int position) {
        getPresenter().stopSearch();
        binding.swipeRefreshLayout.setRefreshing(false);
        getPresenter().onItem(position);
    }


}
