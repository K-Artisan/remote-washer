package com.azirtime.remote.db.coverter;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 *日期转换器,
 * 将写入时将Date转换成Long存储，读取时把Long转换Date返回
 */
public class DateConverter {
    @TypeConverter
    public static Long date2Long(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date long2Date(Long data) {
        return new Date(data);
    }
}
