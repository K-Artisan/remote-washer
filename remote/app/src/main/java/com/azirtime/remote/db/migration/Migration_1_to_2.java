package com.azirtime.remote.db.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_1_to_2 {
    public static Migration createMigration(){
        Migration migration = new Migration(1,2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                //database.execSQL("alter table device add column update_date INTEGER");
            }
        };

        return migration;
    }
}
