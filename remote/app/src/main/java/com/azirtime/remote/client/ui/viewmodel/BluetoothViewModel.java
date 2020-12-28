package com.azirtime.remote.client.ui.viewmodel;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azirtime.remote.client.common.bluetooth.BluetoothManager;

import java.util.ArrayList;
import java.util.List;

public class BluetoothViewModel extends ViewModel {
    public BluetoothManager bluetoohManager;

    public List<BluetoothDevice> mBondedDevices = new ArrayList<>();
    public List<BluetoothDevice> mNewFounddDevices = new ArrayList<>();;

    public MutableLiveData<List<BluetoothDevice>> mBondedDevicesLiveData =new MutableLiveData<List<BluetoothDevice>>();
    public MutableLiveData<List<BluetoothDevice>> mNewFounddDevicesLiveData = new MutableLiveData<List<BluetoothDevice>>() ;

    public BluetoothViewModel(){
        bluetoohManager = BluetoothManager.getInstance();
    }

    //判定设备是否支持蓝牙
    public boolean isSupportBluetooth(){
        return bluetoohManager.isSupport();
    }

    //判定设备的蓝牙是否打开
    public boolean isEnabledBluetooth(){
        return bluetoohManager.isEnabled();
    }

    //打开蓝牙
    public void opentBluetooth(Activity activity){
        bluetoohManager.open(activity);
    }

    //打开蓝牙
    public void closeBluetooth(Activity activity){
        bluetoohManager.colse(activity);
    }

    //搜索蓝牙设备
    public void getBondeBluetoothDevices() {
        //获取已配对设备
        mBondedDevices = bluetoohManager.getBondedDevices();
        mBondedDevicesLiveData.setValue(mBondedDevices);

        return;
    }

    // 发现新设备
    public void startDiscovery(){
        bluetoohManager.startDiscovery();
    }
}
