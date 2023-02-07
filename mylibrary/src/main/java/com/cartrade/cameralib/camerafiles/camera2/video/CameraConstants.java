package com.cartrade.cameralib.camerafiles.camera2.video;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by shubham on 8/21/2017.
 */

public class CameraConstants extends AppCompatActivity {
    public static SharedPreferences preferences;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void saveOrientationFlag(boolean flag) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("orientation_flag", flag);
        editor.commit();
    }

    public static boolean getOrientationFlag(String flag) {
        boolean orn_flag = preferences.getBoolean(flag, false);
        return orn_flag;
    }

    public void saveCameraFlash(String flash) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("flash", flash);
        editor.commit();
    }

    public static String getCameraFlash() {
        String flash_mode = preferences.getString("flash", "");
        return flash_mode;
    }
}
