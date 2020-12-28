package com.azirtime.remote.dto;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import java.util.Date;

public class AddOrUpdateDeviceDto {
    public int id;
    public String name;
    public int conectType; //连接方式
    public String mac; //mac地址
    public String ip;
    public int port;
    public Date createDate;
    public Date updateDate;
}
