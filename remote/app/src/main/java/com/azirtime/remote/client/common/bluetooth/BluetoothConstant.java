package com.azirtime.remote.client.common.bluetooth;

/**
 * Defines several constants used between {@link BluetoothService} and the UI.
 */
public interface BluetoothConstant {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    //蓝牙串口服务(SSP)的UUID
    String SerialPortServiceClass_UUID = "00001101-0000-1000-8000-00805F9B34FB";
}
