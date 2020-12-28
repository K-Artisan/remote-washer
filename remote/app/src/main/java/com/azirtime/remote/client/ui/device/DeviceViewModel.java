package com.azirtime.remote.client.ui.device;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.azirtime.remote.db.entity.Device;
import com.azirtime.remote.dto.DeviceDto;
import com.azirtime.remote.dto.ResponseResult;
import com.azirtime.remote.service.DeviceService;

public class DeviceViewModel extends ViewModel {

    private DeviceService deviceService;
    public LiveData<PagedList<DeviceDto>> pagedListLiveData;

    public DeviceViewModel() {
        deviceService = new DeviceService();

        //pagedData = new LivePagedListBuilder<Integer, DeviceDto>(deviceService.getDevcies(), 10).build();
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
                .build();
        pagedListLiveData = new LivePagedListBuilder(deviceService.getDevcieDataSourceFactory(), config).build();
    }

    public ResponseResult addBluetoothDevice(Device device){
        return deviceService.addBluetoothDevice(device);
    }

}
