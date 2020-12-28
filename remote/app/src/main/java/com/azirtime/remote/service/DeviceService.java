package com.azirtime.remote.service;

import androidx.paging.DataSource;

import com.azirtime.remote.db.RemoteDatabase;
import com.azirtime.remote.db.dao.DeviceDao;
import com.azirtime.remote.db.entity.Device;
import com.azirtime.remote.dto.AddOrUpdateDeviceDto;
import com.azirtime.remote.dto.DeviceDto;
import com.azirtime.remote.dto.ResponseResult;

import java.util.Date;

public class DeviceService {
    private RemoteDatabase database = RemoteDatabase.getDatabase();
    private DeviceDao deviceDao = database.deviceDao();

    public DataSource.Factory<Integer, DeviceDto> getDevcieDataSourceFactory() {
        return deviceDao.getDevcieDataSourceFactory();
    }

    public void addOrUpdateDevice(AddOrUpdateDeviceDto dto) {
        if (dto.id > 0) {
            AddDevice(dto);
        } else {
            updateDevice(dto);
        }
    }

    public void AddDevice(AddOrUpdateDeviceDto dto) {
        Device device = new Device();
        device.name = dto.name;
        device.conectType = dto.conectType;
        device.mac = dto.mac;
        device.ip = dto.ip;
        device.port = dto.port;
        device.createDate = new Date();

        deviceDao.insert(device);

    }

    public void updateDevice(AddOrUpdateDeviceDto dto) {
        Device device = new Device();
        device.name = dto.name;
        device.conectType = dto.conectType;
        device.mac = dto.mac;
        device.ip = dto.ip;
        device.port = dto.port;
        device.updateDate = new Date();

        deviceDao.insert(device);

    }

    public ResponseResult addBluetoothDevice(Device dto) {
        ResponseResult result = new ResponseResult();

        Device dev = deviceDao.findByMac(dto.mac);
        if (dev == null){
            deviceDao.insert(dto);
        }else {
            dev.name = dto.name ;
            dev.conectType = dto.conectType;
            dev.updateDate = dto.updateDate;
            deviceDao.update(dev);
        }

        return result;
    }
}
