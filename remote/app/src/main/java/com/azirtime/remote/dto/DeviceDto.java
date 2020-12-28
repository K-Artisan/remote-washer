package com.azirtime.remote.dto;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.azirtime.remote.db.entity.Device;

import java.io.Serializable;
import java.util.Date;

public class DeviceDto implements Serializable {

    public int id;
    public String name;
    public int conectType; //连接方式
    public String mac; //mac地址
    public String ip;
    public int port;
    public Date createDate;
    @Ignore
    public Date updateDate;
    @Ignore
    public int imgResId;     //设备图片
    @Ignore
    public int conectStatus; //连接状态

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof DeviceDto))
            return false;
        DeviceDto newObject = (DeviceDto) obj;
        return id == newObject.id
                && conectType == newObject.conectType
                && conectStatus == newObject.conectStatus
                && mac == newObject.mac
                && ip == newObject.ip
                && port == newObject.port
                && imgResId == newObject.imgResId
                && conectStatus == newObject.conectStatus;
    }

}
