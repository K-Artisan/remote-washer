package com.azirtime.remote.db.entity;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.azirtime.remote.common.DeviceConectType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

@Entity(tableName = "device")
public class Device {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    @NonNull
    public int conectType; //连接方式(参见类DeviceConectType)：1： wifi， 2：蓝牙

    public String mac; //mac地址
    public String ip;
    public int port;

    @ColumnInfo(name = "create_date")
    public Date createDate;
    @ColumnInfo(name = "update_date")
    public Date updateDate;
}
