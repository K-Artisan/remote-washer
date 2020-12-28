package com.azirtime.remote.client.ui.washer;

import com.azirtime.remote.common.utils.ByteUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 示例
 * WasherReciveData reciveData = new WasherReciveData();
 * reciveData.runStatus = "1";
 * reciveData.workStatus = "02";
 * reciveData.washingRemainTime = "020";
 * reciveData.washingTotalTime = "030";
 * reciveData.waterLevel = "1";
 * reciveData.temperature = "030";
 * reciveData.updateMode = "1";
 * reciveData.updateWaterLevel = "1";
 * reciveData.updateTemperature = "020";
 * reciveData.updateWashingTime = "010";
 * reciveData.updateRinsingTime = "015";
 * reciveData.updateSpinTime = "008";
 * reciveData.updateDryTime = "012";
 * byte[] bytesReciveData = reciveData.getReciveDataBytes();
 * String stringReciveData = ByteUtils.getStringByAscBytes(bytesReciveData);
 * Log.d(TAG, "onCreate->stringReciveData " + stringReciveData);
 * log:
 * S102020030103011020010015008012
 **/
public class WasherReciveData {
    public static final int RECIEVE_BYTE_LENGTH = 34;                               //数据包总长度
    public static final byte[] RECIEVE_BYTE_START_FLAG = new byte[]{0x24, 0x24};     //帧开始标识：$$
    public static final byte[] RECIEVE_BYTE_END_FLAG = new byte[]{0x23, 0x23};    //帧结束标准：##

    public byte[] recieveBytes = new byte[RECIEVE_BYTE_LENGTH];

    public String startFlag = ByteUtils.getStringByAscBytes(RECIEVE_BYTE_START_FLAG);
    public String runStatus = "0";
    public String workStatus = "00";
    public String washingRemainTime = "000";
    public String washingTotalTime = "000";
    public String waterLevel = "0";
    public String temperature = "000";
    public String updateMode = "0";
    public String updateWaterLevel = "0";
    public String updateTemperature = "000";
    public String updateWashingTime = "000";
    public String updateRinsingTime = "000";
    public String updateSpinTime = "000";
    public String updateDryTime = "000";
    public String endFlag = ByteUtils.getStringByAscBytes(RECIEVE_BYTE_END_FLAG);

    public WasherReciveData(byte[] bytes) {
        if (!isValid(bytes)) {
            return;
        }
        for (int i = 0; i < RECIEVE_BYTE_LENGTH; i++) {
            recieveBytes[i] = bytes[i];
        }
        startFlag = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,0,1));
        runStatus = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,2,2));
        workStatus = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,3,4));
        washingRemainTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,5,7));
        washingTotalTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,8,10));
        waterLevel = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,11,11));
        temperature = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,12,14));
        updateMode = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,15,15));
        updateWaterLevel = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,16,16));
        updateTemperature = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,17,19));
        updateWashingTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,20,22));
        updateRinsingTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,23,25));
        updateSpinTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,26,28));
        updateDryTime = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,29,31));
        endFlag = ByteUtils.getStringByAscBytes(ByteUtils.copyBytes(recieveBytes,32,33));
    }

    public WasherReciveData() {
        recieveBytes = getReciveDataBytes();
    }



    //获取命令字节流
    public byte[] getReciveDataBytes() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(startFlag);
        stringBuilder.append(runStatus);
        stringBuilder.append(workStatus);
        stringBuilder.append(String.format("%03d", Integer.valueOf(washingRemainTime)));
        stringBuilder.append(String.format("%03d", Integer.valueOf(washingTotalTime)));
        stringBuilder.append(waterLevel);
        stringBuilder.append(String.format("%03d", Integer.valueOf(temperature)));
        stringBuilder.append(updateMode);
        stringBuilder.append(updateWaterLevel);
        stringBuilder.append(updateTemperature);
        stringBuilder.append(String.format("%03d", Integer.valueOf(updateWashingTime)));
        stringBuilder.append(String.format("%03d", Integer.valueOf(updateRinsingTime)));
        stringBuilder.append(String.format("%03d", Integer.valueOf(updateSpinTime)));
        stringBuilder.append(String.format("%03d", Integer.valueOf(updateDryTime)));
        stringBuilder.append(endFlag);

        return ByteUtils.getAscBytes(stringBuilder.toString());
    }

    public static WasherReciveData empety() {
        return new WasherReciveData();
    }

    //检测数据包是否有效
    private boolean isValid(byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        if (!(bytes.length > 0 && bytes.length <= RECIEVE_BYTE_LENGTH)) {
            return false;
        }

        //检查帧头
        for (int caonima = 0; caonima < RECIEVE_BYTE_START_FLAG.length; caonima++) {
            if (bytes[caonima] != RECIEVE_BYTE_START_FLAG[caonima]) {
                return false;
            }
        }

        //检查帧尾
        for (int k = 0; k < RECIEVE_BYTE_END_FLAG.length; k++) {
            if (RECIEVE_BYTE_END_FLAG[k] != bytes[RECIEVE_BYTE_LENGTH - RECIEVE_BYTE_END_FLAG.length + k]) {
                return false;
            }
        }

        //TODO: 数据校验，算法待定，比如:CRC
        //....

        return true;
    }

    //检测数据包是否有效
    public boolean isValid() {
        return isValid(recieveBytes);
    }
}

