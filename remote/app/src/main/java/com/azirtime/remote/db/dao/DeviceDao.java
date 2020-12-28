package com.azirtime.remote.db.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.azirtime.remote.db.entity.Device;
import com.azirtime.remote.dto.DeviceDto;
import java.util.List;


@Dao
public interface DeviceDao {
    @Query("SELECT * FROM Device order by id DESC")
    List<Device> getAll();

    @Query("SELECT * FROM device WHERE id IN (:deviceIds)")
    List<Device> loadAllByIds(int[] deviceIds);

    @Query("SELECT * FROM device WHERE name LIKE :first LIMIT 1")
    Device findByName(String first);

    @Query("SELECT d.id, d.name, d.conectType, d.mac, d.ip, d.port, d.create_date as createDate" +
            " FROM device as d" +
            " ORDER BY create_date ASC")
    DataSource.Factory<Integer, DeviceDto> getDevcieDataSourceFactory();

    @Query("SELECT * FROM device WHERE mac LIKE :first LIMIT 1")
    Device findByMac(String first);

    @Insert
    void insert(Device device);
    @Insert
    void insertAll(Device... devices);

    @Insert
    void insertBy(List<Device> devices);

    @Delete
    void delete(Device device);

    @Update
    int update(Device device);

    @Update
    public void updateUsers(Device... devices);
}
