package com.azirtime.remote.client.common.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import com.azirtime.remote.client.common.ActivityRequestCode;

import java.util.ArrayList;
import java.util.List;

public class BluetoothManager {

    private static BluetoothManager instance;
    public BluetoothAdapter bluetoothAdapter;

    private BluetoothManager(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothManager getInstance(){
        if (instance == null){
            instance =new BluetoothManager();
        }
        return  instance;
    }

    //判定设备是否支持蓝牙
    public boolean isSupport(){
        return bluetoothAdapter != null ? true :false;
    }

    //判定设备已经开启蓝牙
    public boolean isEnabled(){
        return bluetoothAdapter.isEnabled();
    }

    //打开蓝牙
    public void open(Activity activity){
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, ActivityRequestCode.REQUEST_ENABLE_BT);
        }
    }

    //打开蓝牙
    public void colse(Activity activity){
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        }
    }

    //查询已配对设备
    public List<BluetoothDevice> getBondedDevices(){
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    //发现设备
   public void startDiscovery(){
       if (bluetoothAdapter.isDiscovering()) {
           bluetoothAdapter.cancelDiscovery();
       }
       bluetoothAdapter.startDiscovery();
   }

    //停止发现设备
    public void cancelDiscovery(){
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

}
