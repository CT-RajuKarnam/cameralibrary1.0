package com.cartrade.cameraexample.db;

import android.content.Context;
import android.os.Handler;

import androidx.room.Room;

public class LocalDB {
    private static final String DB_NAME = "ADROIT_DB";
    private AppDatabase db;
    Context context;
    private static LocalDB ourInstance;
    Handler handler;

    public static LocalDB getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new LocalDB(context);
        }
        return ourInstance;
    }

    public AppDatabase getDb() {
        if (db != null)
            return db;
        else
            return null;
    }



    public LocalDB(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).allowMainThreadQueries().build();
        //for complete db clear falbacktoDestructiveMigration method used
        //db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).fallbackToDestructiveMigration().allowMainThreadQueries().build();
    }


}
