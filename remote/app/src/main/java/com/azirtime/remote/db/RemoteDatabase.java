package com.azirtime.remote.db;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.azirtime.remote.client.ui.MainActivity;
import com.azirtime.remote.common.AppGlobals;
import com.azirtime.remote.db.coverter.DateConverter;
import com.azirtime.remote.db.dao.DeviceDao;
import com.azirtime.remote.db.entity.Device;
import com.azirtime.remote.db.migration.Migration_1_to_2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Database(entities = {Device.class}, version = DatabaseConstant.DATABASE_CUR_VERSION)
@TypeConverters(DateConverter.class)
public abstract class RemoteDatabase extends RoomDatabase {

    private static final RemoteDatabase database;

    static {
        //创建一个内存数据库
        //但是这种数据库的数据只存在于内存中，也就是进程被杀之后，数据随之丢失
        //Room.inMemoryDatabaseBuilder()
        database = Room.databaseBuilder(AppGlobals.getApplication(), RemoteDatabase.class, DatabaseConstant.DATABASE_NAME)
                //是否允许在主线程进行查询
                .allowMainThreadQueries()
                //数据库创建和打开后的回调
                //.addCallback()
                //设置查询的线程池
                //.setQueryExecutor()
                //.openHelperFactory()
                //room的日志模式
                //.setJournalMode()
                //数据库升级异常之后的回滚
                //.fallbackToDestructiveMigration()
                //数据库升级异常后根据指定版本进行回滚
                //.fallbackToDestructiveMigrationFrom()
                //.addMigrations(Migration_1_to_2.createMigration())
                .build();
    }

    public static RemoteDatabase getDatabase() {
        return database;
    }

    public abstract DeviceDao deviceDao();

}
