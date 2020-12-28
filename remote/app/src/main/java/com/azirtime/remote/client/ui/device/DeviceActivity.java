package com.azirtime.remote.client.ui.device;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.azirtime.remote.R;
import com.azirtime.remote.client.common.IntentDataKey;
import com.azirtime.remote.client.ui.activity.BaseActivity;
import com.azirtime.remote.client.ui.adddevice.AddBluetoothDeviceActivity;
import com.azirtime.remote.client.ui.washer.WasherActivity;
import com.azirtime.remote.common.DeviceConectType;
import com.azirtime.remote.db.RemoteDatabase;
import com.azirtime.remote.db.dao.DeviceDao;
import com.azirtime.remote.db.entity.Device;
import com.azirtime.remote.dto.DeviceDto;
import com.azirtime.remote.dto.ResponseResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.azirtime.remote.client.common.ActivityRequestCode.ADD_BLUETOOTH_DEVICE;

public class DeviceActivity extends BaseActivity {

    private DeviceViewModel deviceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        init();
    }

    //创建系统菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_device_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.toolbar_add_bluetooth_device:
                selectBluetoothDevice();
                break;
            case R.id.toolbar_add_wifi_device:
                addWifiDevice();
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ADD_BLUETOOTH_DEVICE: {
                BluetoothDevice bluetoothDevice = data.getParcelableExtra(IntentDataKey.DATAKEY_NEW_BLUETOOTH);
                if (bluetoothDevice != null){
                    addBluetoothDevice(bluetoothDevice);
                }
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        //按返回键，程序进入后台运行，不关闭程序
        moveTaskToBack(true);
    }

    private void init() {
        initToolbar();
        initDeviceList();
    }

    private void initToolbar() {
        //折叠布局：CollapsingToolbarLayout
      /*  CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle(getString(R.string.addBluetoothDeviceTitle));*/

        //使用Toolbar替换系统的ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
        }
    }

    private void initDeviceList() {
        deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DeviceAdapter adapter = new DeviceAdapter();
        deviceViewModel.pagedListLiveData.observe(this, new Observer<PagedList<DeviceDto>>() {
            @Override
            public void onChanged(PagedList<DeviceDto> pagedList) {
                adapter.submitList(pagedList);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void selectBluetoothDevice() {
        Intent intent = new Intent(this, AddBluetoothDeviceActivity.class);
        startActivityForResult(intent, ADD_BLUETOOTH_DEVICE);
    }

    private void addBluetoothDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            Device device = new Device();
            device.name = !TextUtils.isEmpty(bluetoothDevice.getName())
                    ? bluetoothDevice.getName()
                    : bluetoothDevice.getAddress();
            device.mac = bluetoothDevice.getAddress();
            device.conectType = DeviceConectType.BLUETOOTH;
            device.createDate = new Date();
            device.updateDate = new Date();

            ResponseResult result = deviceViewModel.addBluetoothDevice(device);
            if (!result.success) {
                String msg = "蓝牙设备:" + device.name + "添加失败";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToDevcieView(DeviceDto device) {
        //TODO:根据Device的类型，跳转响应的设备界面
        Intent intent = new Intent(this, WasherActivity.class);
        intent.putExtra(IntentDataKey.DATAKEY_OPENDEVICEVIEW, device);
        startActivity(intent);
    }

    private void addWifiDevice() {
        Toast.makeText(this, "添加wifi设备功能尚未支持", Toast.LENGTH_SHORT).show();
    }

    private void initDeviceForTest() {
        DeviceDao deviceDao = RemoteDatabase.getDatabase().deviceDao();
        deviceDao.insertBy(createInitData());
    }

    private static List<Device> createInitData() {
        List<Device> devices = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            Device device = new Device();
            device.name = "device_" + i;
            device.createDate = new Date();
            device.updateDate = new Date();
            devices.add(device);
        }
        return devices;
    }
}

