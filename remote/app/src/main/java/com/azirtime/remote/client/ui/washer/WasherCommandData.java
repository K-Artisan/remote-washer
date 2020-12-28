package com.azirtime.remote.client.ui.washer;

import com.azirtime.remote.common.utils.ByteUtils;

import java.util.ArrayList;

public class WasherCommandData {
    public static final int CMD_BYTE_LENGTH = 24;
    public static final byte[] CMD_BYTE_START_FLAG = new byte[]{0x24, 0x24};     //帧开始标识：$$
    public static final byte[] CMD_BYTE_END_FLAG = new byte[]{0x23, 0x23};       //帧结束标准：##

    public String startFlag = ByteUtils.getStringByAscBytes(CMD_BYTE_START_FLAG);
    public String runStatus;
    public String updateMode;
    public String updateWaterLevel;
    public String updateTemperature;
    public String updateWashingTime;
    public String updateRinsingTime;
    public String updateSpinTime;
    public String updateDryTime;
    public String updateTemperatureFlag;
    public String updateWashingTimeFlag;
    public String updateRinsingTimeFlag;
    public String updateSpinTimeFlag;
    public String updateDryTimeFlag;
    public String endFlag = ByteUtils.getStringByAscBytes(CMD_BYTE_END_FLAG);

    public WasherCommandData() {

    }

    public static WasherCommandData createFromReciveData(WasherReciveData washerReciveData) {
        if (!washerReciveData.isValid()) {
            return null;
        }
        WasherCommandData cmd = new WasherCommandData();
        cmd.startFlag = ByteUtils.getStringByAscBytes(CMD_BYTE_START_FLAG);
        cmd.runStatus = washerReciveData.runStatus;
        cmd.updateMode = washerReciveData.updateMode;
        cmd.updateWaterLevel = washerReciveData.updateWaterLevel;
        cmd.updateTemperature = washerReciveData.updateTemperature;
        cmd.updateWashingTime = washerReciveData.updateWashingTime;
        cmd.updateRinsingTime = washerReciveData.updateRinsingTime;
        cmd.updateSpinTime = washerReciveData.updateSpinTime;
        cmd.updateDryTime = washerReciveData.updateDryTime;
        cmd.updateTemperatureFlag = "0";
        cmd.updateWashingTimeFlag = "0";
        cmd.updateRinsingTimeFlag = "0";
        cmd.updateSpinTimeFlag = "0";
        cmd.updateDryTimeFlag = "0";
        cmd.endFlag = ByteUtils.getStringByAscBytes(CMD_BYTE_END_FLAG);

        return cmd;
    }

    //获取命令字节流
    public byte[] getCommandBytes() {
        StringBuilder cmdStrBuilder = new StringBuilder();
        cmdStrBuilder.append(startFlag);
        cmdStrBuilder.append(runStatus);
        cmdStrBuilder.append(updateMode);
        cmdStrBuilder.append(updateWaterLevel);
        cmdStrBuilder.append(String.format("%03d", Integer.valueOf(updateTemperature)));
        cmdStrBuilder.append(String.format("%03d", Integer.valueOf(updateWashingTime)));
        cmdStrBuilder.append(String.format("%03d", Integer.valueOf(updateRinsingTime)));
        cmdStrBuilder.append(String.format("%03d", Integer.valueOf(updateSpinTime)));
        cmdStrBuilder.append(updateTemperatureFlag);
        cmdStrBuilder.append(updateWashingTimeFlag);
        cmdStrBuilder.append(updateRinsingTimeFlag);
        cmdStrBuilder.append(updateSpinTimeFlag);
        cmdStrBuilder.append(updateDryTimeFlag);
        cmdStrBuilder.append(endFlag);

        return ByteUtils.getAscBytes(cmdStrBuilder.toString());
    }

    //检测数据包是否有效
    public boolean isValid(byte[] bytes) {
        if (bytes == null) {
            return false;
        }
        if (!(bytes.length > 0 && bytes.length <= CMD_BYTE_LENGTH)) {
            return false;
        }

        //检查帧头
        for (int i = 0; i < CMD_BYTE_START_FLAG.length; i++) {
            if (bytes[i] != CMD_BYTE_START_FLAG[i]) {
                return false;
            }
        }

        //检查帧尾
        for (int i = 0; i < CMD_BYTE_END_FLAG.length; i++) {
            if (CMD_BYTE_END_FLAG[i] != bytes[CMD_BYTE_LENGTH - CMD_BYTE_END_FLAG.length + i]) {
                return false;
            }
        }

        //TODO: 数据校验，算法待定，比如:CRC
        //....

        return true;
    }

    public void setRunStatus(String newValue) {
        if (runStatus.equals(newValue)) {
            return;
        }
        switch (newValue) {
            case WasherDataConstant.RunningStatus_PowerOff:
                runStatus = "A";
                break;
            case WasherDataConstant.RunningStatus_PowerOn:
                runStatus = "B";
                break;
            case WasherDataConstant.RunningStatus_Suspend:
                runStatus = "C";
                break;

        }
    }

    public void setMode(String newValue) {
        if (updateMode.equals(newValue)) {
            return;
        }
        switch (newValue) {
            case WasherDataConstant.Mode_Standard:
                updateMode = "A";
                break;
            case WasherDataConstant.Mode_Seek:
                updateMode = "B";
                break;
            case WasherDataConstant.Mode_Strong:
                updateMode = "C";
                break;
            case WasherDataConstant.Mode_Single_wash:
                updateMode = "D";
                break;
            case WasherDataConstant.Mode_Rinse:
                updateMode = "E";
                break;
            case WasherDataConstant.Mode_Spin:
                updateMode = "F";
                break;
            case WasherDataConstant.Mode_Dry:
                updateMode = "G";
                break;
        }
    }

    public void setWaterLevel(String newValue) {
        if (updateWaterLevel.equals(newValue)) {
            return;
        }
        switch (newValue) {
            case WasherDataConstant.Water_Level_Low:
                updateWaterLevel = "A";
                break;
            case WasherDataConstant.Water_Level_Mid:
                updateWaterLevel = "B";
                break;
            case WasherDataConstant.Water_Level_Hight:
                updateWaterLevel = "C";
                break;
        }
    }

    public void setTemperature(String newValue) {
        if (updateTemperature.equals(newValue)) {
            return;
        }
        updateTemperature = newValue;
        updateTemperatureFlag = "1";
    }

    public void setWashingTime(String newValue) {
        if (updateWashingTime.equals(newValue)) {
            return;
        }
        updateWashingTime = newValue;
        updateWashingTimeFlag = "1";
    }

    public void setRinsingTime(String newValue) {
        if (updateRinsingTime.equals(newValue)) {
            return;
        }
        updateRinsingTime = newValue;
        updateRinsingTimeFlag = "1";
    }

    public void setSpinTime(String newValue) {
        if (updateSpinTime.equals(newValue)) {
            return;
        }
        updateSpinTime = newValue;
        updateSpinTimeFlag = "1";
    }

    public void setDryTime(String newValue) {
        if (updateDryTime.equals(newValue)) {
            return;
        }
        updateDryTime = newValue;
        updateDryTimeFlag = "1";
    }

}
