package com.azirtime.remote.client.ui.washer;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.widget.TextView;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.azirtime.remote.client.common.bluetooth.BluetoothService;
import com.azirtime.remote.client.ui.viewmodel.BluetoothViewModel;
import com.azirtime.remote.dto.DeviceDto;

public class WasherViewModel extends BluetoothViewModel  {

    public BroadcastReceiver bluetoothReceiver;          //蓝牙设备广播
    public DeviceDto mDevice;
    public MutableLiveData<BluetoothDevice> mLiveDataBluetoothDevice = new MutableLiveData<>();
    public BluetoothService bluetoothServer = null;
    public MutableLiveData<Integer> connectStatus = new MutableLiveData<>(BluetoothService.STATE_NONE);
    public StringBuffer mOutStringBuffer;

    public MutableLiveData<WasherReciveData> washerReciveData = new MutableLiveData<>();
    public WasherCommandData washerCommandData;
    public WasherDataResolver washerDataResolver = new WasherDataResolver();

    public WasherViewModel(){
        super();
    }

}
