package com.azirtime.remote.client.ui.adddevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.azirtime.remote.R;
import com.azirtime.remote.client.common.ActivityRequestCode;
import com.azirtime.remote.client.common.IntentDataKey;
import com.azirtime.remote.client.ui.activity.BaseActivity;
import com.azirtime.remote.client.ui.device.DeviceActivity;

import java.util.ArrayList;
import java.util.List;

public class AddBluetoothDeviceActivity extends BaseActivity {

    private final static String TAG = "AddBluetoothDevice";

    private AddBluetoothDeviceViewModel viewModel;             //ViewModel
    private View viewSearchProgressBar;                        //搜索蓝牙进度视图
    private BroadcastReceiver bluetoothReceiver;               //发现新蓝牙设备广播
    private List<BluetoothDevice> bluetoothBondedDeviceList;   //已配对的蓝牙设备
    private List<BluetoothDevice> bluetoothUnbondedDeviceList; //未配对的蓝牙设备

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bluetooth_device);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销蓝牙广播
        unregisterReceiver(bluetoothReceiver);
    }

    @Override
    public void onBackPressed() {
        backToDeviceActivity(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_bluetooth_device_toolbar, menu); //创建系统菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                backToDeviceActivity(null);
                break;
            case R.id.toolbar_search_bluetooth_device:
                btnSearchDeviceClick();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityRequestCode.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    initDatas();
                } else {
                    Toast.makeText(this, "蓝牙没有打开,无法搜索蓝牙设备", Toast.LENGTH_LONG).show();
                    initDatas();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ActivityRequestCode.PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startDiscovery();
                } else {
                    Toast.makeText(this, "您已拒绝授权，无法搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //初始化
    private void init() {
        initToolbar();
        viewSearchProgressBar = findViewById(R.id.view_progressBar);
        viewModel = new ViewModelProvider(this).get(AddBluetoothDeviceViewModel.class);
        intBluetoothReceiver();

        if (!isSupportBluetooth()) {
            return;
        } else if (!isEnabledBluetooth()) {
            viewModel.opentBluetooth(this);
        } else {
            initDatas();
        }
    }

    //初始化状态栏
    private void initToolbar() {
        //使用Toolbar替换系统的ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); //启用HomeAsUp按钮，其id永远为：android.R.id.home
        }
    }

    //初始化广播接收器
    private void intBluetoothReceiver() {
        bluetoothReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    viewSearchProgressBar.setVisibility(View.VISIBLE);

                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    viewSearchProgressBar.setVisibility(View.GONE);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    List<BluetoothDevice> currDeviceList = viewModel.mNewFounddDevicesLiveData.getValue() != null
                            ? viewModel.mNewFounddDevicesLiveData.getValue()
                            : new ArrayList<>();
                    if (!currDeviceList.contains(device)) {
                        currDeviceList.add(device);
                    }
                    viewModel.mNewFounddDevicesLiveData.setValue(currDeviceList);

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    viewSearchProgressBar.setVisibility(View.GONE);

                } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                }
            }
        };

        //注册蓝牙广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //搜索开始
        filter.addAction(BluetoothDevice.ACTION_FOUND); //找到新设备
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //搜索完成
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED); //扫描模式？
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //绑定状态改变

        registerReceiver(bluetoothReceiver, filter);
    }

    //初始化数据
    private void initDatas() {
        initHasBondedDevice();
        initUnbondedDevice();
    }

    //初始化已经配对的蓝牙设备
    private void initHasBondedDevice() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_hasBonded);
        viewModel.getBondeBluetoothDevices();
        bluetoothBondedDeviceList = new ArrayList<BluetoothDevice>(viewModel.mBondedDevices != null ? viewModel.mBondedDevices : new ArrayList<>(0));
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(bluetoothBondedDeviceList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        viewModel.mBondedDevicesLiveData.observe(this, new Observer<List<BluetoothDevice>>() {
            @Override
            public void onChanged(List<BluetoothDevice> bluetoothDevices) {
                bluetoothBondedDeviceList.clear();
                bluetoothBondedDeviceList.addAll(bluetoothDevices);
                adapter.notifyDataSetChanged();//全部刷新
            }
        });
    }

    //初始化没有配对的蓝牙设备
    private void initUnbondedDevice() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_unbonded);
        findNewBluetoothDevice();
        bluetoothUnbondedDeviceList = new ArrayList<BluetoothDevice>(viewModel.mNewFounddDevices != null ? viewModel.mNewFounddDevices : new ArrayList<>(0));
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(bluetoothUnbondedDeviceList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        viewModel.mNewFounddDevicesLiveData.observe(this, new Observer<List<BluetoothDevice>>() {
            @Override
            public void onChanged(List<BluetoothDevice> bluetoothDevices) {
                bluetoothUnbondedDeviceList.clear();
                bluetoothUnbondedDeviceList.addAll(bluetoothDevices);
                adapter.notifyDataSetChanged();//全部刷新
            }
        });
    }

    //搜索蓝牙设备按钮点击事件
    private void btnSearchDeviceClick() {

        viewSearchProgressBar.setVisibility(View.VISIBLE);
        //延迟一段时间再执行，纯粹就是为展示点击按钮是有响应的
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSupportBluetooth()) {
                    return;
                } else if (!isEnabledBluetooth()) {
                    viewModel.opentBluetooth(AddBluetoothDeviceActivity.this);
                } else {
                    initDatas();
                }
            }
        }, 888);
    }

    //判定设备是否支持蓝牙设备
    private boolean isSupportBluetooth() {
        if (!viewModel.isSupportBluetooth()) {
            Toast.makeText(this, "您的设备不支持蓝牙,无法添加蓝牙设备", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //判定设备是否已经打开蓝牙
    private boolean isEnabledBluetooth() {
        return viewModel.isEnabledBluetooth();
    }

    //搜索新的蓝牙设备
    private void findNewBluetoothDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ActivityRequestCode.PERMISSION_REQUEST_COARSE_LOCATION);
            } else {
                viewModel.startDiscovery();
            }
        } else {
            viewModel.startDiscovery();
        }
    }

    //选择要创建的设备
    public void callWhenHasSelectedBluetooth(BluetoothDevice bluetoothDevice){
        backToDeviceActivity(bluetoothDevice);
    }

    public void backToDeviceActivity(BluetoothDevice bluetoothDevice){
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(IntentDataKey.DATAKEY_NEW_BLUETOOTH, bluetoothDevice);
        setResult(ActivityRequestCode.ADD_BLUETOOTH_DEVICE, intent);
        finish();
    }
}
