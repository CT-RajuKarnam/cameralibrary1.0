package com.cartrade.cameraexample.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.cartrade.cameraexample.db.models.CapturedImages;
import com.cartrade.cameraexample.db.models.Folder;


@Database(entities = {   CapturedImages.class, Folder.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract AdroitDao adroitDao();
}
