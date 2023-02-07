package com.cartrade.cameraexample;

import android.content.Context;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.format.DateFormat;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.cartrade.cameraexample.db.LocalDB;
import com.cartrade.cameraexample.db.Pref;

import java.util.Date;


public class AdroitApplication extends MultiDexApplication {
    public static Context app_ctx;
    static int id = 1;
    public static String TAG="ADROIT";
    public static double lat,lng;
    @Override
    public void onCreate() {
        super.onCreate();
        app_ctx = getApplicationContext();
        MultiDex.install(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        LocalDB.getInstance(getApplicationContext());
        if (Pref.getIn().getDeviceId().equals("")) {
            Pref.getIn().saveDeviceId(Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    public static String getCurrentTime() {
        String date = (DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString());
        return date;
    }

}
