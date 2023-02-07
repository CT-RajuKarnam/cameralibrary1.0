/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cartrade.cameraexample.videotimestamp;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cartrade.cameraexample.AdroitApplication;
import com.cartrade.cameraexample.CameraActivityOffline;
import com.cartrade.cameraexample.R;
import com.cartrade.cameraexample.db.LocalDB;
import com.cartrade.cameraexample.db.Pref;
import com.cartrade.cameraexample.db.models.CapturedImages;
import com.cartrade.cameraexample.videotimestamp.encoder.MediaAudioEncoder;
import com.cartrade.cameraexample.videotimestamp.encoder.MediaEncoder;
import com.cartrade.cameraexample.videotimestamp.encoder.MediaMuxerWrapper;
import com.cartrade.cameraexample.videotimestamp.encoder.MediaVideoEncoder;
import com.cartrade.cameraexample.videotimestamp.glutilsOld.CameraGLView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class VideoAudioTimeStampOffline extends Activity {
    private static final String TAG = "VideoTimeStampOffline";
    private static final boolean DEBUG = true;


    private CameraGLView mCameraView;
    public String mCurrentFlash = "";
    public String mDesiredFlash = "";


    private TextView textview, tv_titile, tvRemaintime;
    int minute = 0, seconds = 0, hour = 0;
    int end;
    Timer t;
    ImageView imageView, imageViewcar, button_splash;
    ProgressBar progressBar;
    int min_time = 30, max_time = 80;
    int cal_min, cal_sec;
    boolean pause = false;
    String from = "";
    Button toggleRelease;
    String foldePath = "";
    String folderNam = "";
    int count;
    final int TAKE_MULTI_PICTURE = 12;
    private long UPDATE_INTERVAL = 5 * 1000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000;
    LocationCallback callback;
    private LocationRequest mLocationRequest;
    File outputFile;
    Bundle extras;
    public static double lat = 0, lng = 0;
    private MediaMuxerWrapper mMuxer;

    //https://github.com/saki4510t/AudioVideoRecordingSample
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        UPDATE_INTERVAL = getResources().getInteger(R.integer.updateinterval) * 1000; // 5 meters
        FASTEST_INTERVAL = getResources().getInteger(R.integer.fast_interval) * 1000;
        if (savedInstanceState != null) {
            from = savedInstanceState.getString("from", "");
            count = savedInstanceState.getInt("count");
            foldePath = savedInstanceState.getString("foldePath");
            folderNam = savedInstanceState.getString("folderNam");
            if (from.equalsIgnoreCase("save")) {
                if (new File(foldePath).exists()) {
                    File[] listFiles = new File(foldePath).listFiles();
                    if (listFiles.length > 0) {
                        for (int i = 0; i < listFiles.length; i++)
                            if (listFiles[i].exists()) {
                                listFiles[i].delete();
                            }
                    }
                }
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.video_audio_act);
        extras = getIntent().getExtras();
        if (extras != null) {
            count = extras.getInt("count");
            foldePath = extras.getString("folderpath");
            folderNam = extras.getString("folder");
        }
        outputFile = getOutputMediaFile();
        // mp.start();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.txt_red),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        end = max_time - 15;
        toggleRelease = (Button) findViewById(R.id.toggleRecording_button);
        imageView = (ImageView) findViewById(R.id.image);
        imageViewcar = (ImageView) findViewById(R.id.car);
        button_splash = (ImageView) findViewById(R.id.button_splash);
        imageView.setVisibility(View.GONE);
        textview = (TextView) findViewById(R.id.tv_timer);
        tv_titile = (TextView) findViewById(R.id.tv_title);
        tvRemaintime = (TextView) findViewById(R.id.timeRemains);
        progressBar.setMax(max_time);
        cal_min = max_time / 60;
        cal_sec = max_time % 60;
        getIntent().setAction("Already created");


        // Define a handler that receives camera-control messages from other threads.  All calls
        // to Camera must be made on the same thread.  Note we create this before the renderer
        // thread, so we know the fully-constructed object will be visible.

        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL context.
        mCameraView = (CameraGLView) findViewById(R.id.cameraPreview_surfaceView);
        mCameraView.setVideoSize(1280, 720);
        ((TextView) findViewById(R.id.helpHeader)).setText("Recording will be of max " + max_time + " Sec.");

        ((TextView) findViewById(R.id.helpHeader)).setText("Recording will be of max " + max_time + " Sec.");
        ((TextView) findViewById(R.id.helpText)).setText("Follow mentioned sequence.\n" +
                " \n" +
                "First 15 seconds for chassis number,\n" +
                "Next " + (max_time - 30) + " seconds for vehicle exterior,\n" +
                "Last 15 seconds for odometer and damages, if any.");


        tv_titile.setText("Chassis Number");
        imageViewcar.setVisibility(View.GONE);
        getIntent().setAction("Already created");
        button_splash.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                toggleFlashMode(button_splash);
            }
        });
        Log.d(TAG, "onCreate complete: " + this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (outputFile.exists()) {
            outputFile.delete();
        }

        checkPermission();
        Log.e("lifecycle", "onResume invoked");
        String action = getIntent().getAction();
        startLocationUpdates();

        // Prevent endless loop by adding a unique action, don't restart if action is present
        if (action == null || !action.equals("Already created")) {
            if (foldePath != null && new File(foldePath).exists()) {
                File[] listFiles = new File(foldePath).listFiles();
                if (listFiles.length > 0) {
                    for (int i = 0; i < listFiles.length; i++)
                        if (listFiles[i].exists()) {
                            listFiles[i].delete();
                        }
                }
            }
            Log.v("Example", "Force restart");
            Intent intent = new Intent(this, VideoAudioTimeStampOffline.class);
            intent.putExtra("folderpath", foldePath);
            intent.putExtra("folder", folderNam);
            intent.putExtra("count", count);
            startActivity(intent);
            finish();
        } else {
            getIntent().setAction(null);
            mCameraView.onResume();
        }
        pause = false;

    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause -- releasing camera");
        super.onPause();
        stopRecording();
        mCameraView.onPause();

        Log.d(TAG, "onPause complete");
        if (callback != null)
            getFusedLocationProviderClient(this).removeLocationUpdates(callback);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (outputFile.exists()) {
            if (minute < 1 && seconds < min_time) {
                outputFile.delete();
            }
            if (t != null)
                t.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (outputFile.exists()) {
            if (minute < 1 && seconds < min_time) {
                outputFile.delete();
            }
        }
    }

    public File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile = null;
        mediaFile = new File(foldePath,
                "VID_" + timeStamp.replace("/", "-") + ".mp4");
        return mediaFile;
    }

    public void clickToggleRecording(@SuppressWarnings("unused") View unused) {
        if (checkPermissionCamera()) {
            if (checkPermissionAudio()) {
                if (checkPermissionLocation()) {
                    captureVideo();
                } else {
                    Toast.makeText(VideoAudioTimeStampOffline.this, "Need location permission to use this feature", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VideoAudioTimeStampOffline.this, "Need audio permission to use this feature", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(VideoAudioTimeStampOffline.this, "Need camera permission to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

    void captureVideo() {
        if (mMuxer != null) {
            if (min_time != 0 && max_time != 0 && min_time < max_time) {
                if (minute < 1 && seconds < min_time) {
                    Toast.makeText(getApplicationContext(), "Video should be minimum " + min_time + " sec", Toast.LENGTH_LONG).show();
                } else {
                    Recording();
                }
            } else {
                Recording();
            }
        } else {
            ((LinearLayout) findViewById(R.id.layInfo)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.layProgress)).setVisibility(View.VISIBLE);
            imageViewcar.setVisibility(View.GONE);
            if (mMuxer == null)
                startRecording();
            t = new Timer("hello", true);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    textview.post(new Runnable() {

                        public void run() {
                            if (!pause) {
                                textview.setVisibility(View.VISIBLE);
                                tv_titile.setVisibility(View.GONE);
                                seconds++;
                                if (seconds == 60) {
                                    seconds = 0;
                                    minute++;
                                }
                                if (minute == 60) {
                                    minute = 0;
                                    hour++;
                                }
                                progressBar.setProgress((minute * 60) + seconds);
                                if (seconds >= 0) {
                                    textview.setText(""
                                            + (hour > 9 ? hour : ("0" + hour)) + " : "
                                            + (minute > 9 ? minute : ("0" + minute))
                                            + " : "
                                            + (seconds > 9 ? seconds : "0" + seconds));
                                } else {
                                    textview.setText("00:00:00");
                                }
                                if (minute == cal_min && seconds == cal_sec) {
                                    if (t != null)
                                        t.cancel();
                                    Recording();
                                }

                            }
                        }
                    });

                }
            }, 1000, 1000);
            // mRecordingButton.setBackgroundResource(R.drawable.red_dot_stop);
        }
    }

    public void Recording() {


        printPaths();

    }

    int newseconds = 5;

    public void printPaths() {
        if (t != null)
            t.cancel();// stop the recording
        Toast.makeText(VideoAudioTimeStampOffline.this, "Video saved", Toast.LENGTH_SHORT).show();
        tvRemaintime.setText("" + newseconds);
        ((LinearLayout) findViewById(R.id.layTimer)).setVisibility(View.GONE);
        tv_titile.setVisibility(View.GONE);
        textview.setVisibility(View.GONE);
        imageViewcar.setVisibility(View.GONE);
        toggleRelease.setVisibility(View.GONE);

        CapturedImages capturedImages = new CapturedImages();
        capturedImages.setCaptured_time(DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString());
        capturedImages.setLatitude("" + (AdroitApplication.lat));
        capturedImages.setLongitude("" + (AdroitApplication.lng));
        capturedImages.setReg_no(getIntent().getStringExtra("folder"));
        capturedImages.setFolder_path(foldePath);
        capturedImages.setImage_path(outputFile.getAbsolutePath());
        capturedImages.setUploaded("n");
        LocalDB.getInstance(VideoAudioTimeStampOffline.this).getDb().adroitDao().insertCapturedImage(capturedImages);
        tvRemaintime.setText("" + newseconds);
        if (alert_dialog != null && alert_dialog.isShowing()) {
            alert_dialog.dismiss();
        }
        stopRecording();
        mCameraView.onPause();

        ((LinearLayout) findViewById(R.id.layTimer)).setVisibility(View.VISIBLE);
        t = new Timer("hello", true);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                tvRemaintime.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!pause) {
                            newseconds--;
                            if (newseconds == 1) {
                                t.cancel();
                                tvRemaintime.setText("0");
                                LocalDB.getInstance(VideoAudioTimeStampOffline.this).getDb().adroitDao().updateFolder(AdroitApplication.getCurrentTime(), folderNam, foldePath, "Y");
                                if (count == 0) {
                                    Intent intent = new Intent(VideoAudioTimeStampOffline.this, CameraActivityOffline.class);
                                    Bundle bundleObject = new Bundle();
                                    bundleObject.putString("folderpath", foldePath);
                                    bundleObject.putString("folder", folderNam);
                                    bundleObject.putInt("count", 0);
                                    bundleObject.putInt("secs", Integer.parseInt(Pref.getIn().getTimercount().replace("m", "")) * 60);
                                    intent.putExtras(bundleObject);
                                    startActivityForResult(intent, TAKE_MULTI_PICTURE);
                                    finish();
                                } else {
                                    Intent intent = new Intent();
                                    Bundle bundleObject = new Bundle();
                                    intent.putExtras(bundleObject);
                                    setResult(12, intent);
                                    finish();
                                }
                            }
                            tvRemaintime.setText("" + newseconds);
                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    public void onBackPressed() {
        if (mMuxer != null) {
            if (min_time != 0 && max_time != 0 && min_time < max_time) {
                if (minute < 1 && seconds < min_time) {
                    onBackPopup();
                } else {
                    printPaths();
                }
            } else {
                printPaths();
            }

        } else {
            stopRecording();
            mCameraView.onPause();
            Intent intent = new Intent();
            Bundle bundleObject = new Bundle();
            intent.putExtras(bundleObject);
            setResult(12, intent);
            finish();
        }

    }

    Dialog alert_dialog = null;

    public void onBackPopup() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        alert_dialog = new Dialog(VideoAudioTimeStampOffline.this);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.base_alert_dialogue);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.alert_title);
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.txt_alert_description);
        TextView txt_ok = (TextView) alert_dialog.findViewById(R.id.txt_ok);
        TextView txt_cancel = (TextView) alert_dialog.findViewById(R.id.txt_cancel);
        txt_cancel.setVisibility(View.GONE);

        txt_alert_title.setText("ALERT !");
        txt_alert_description.setText("Video should be of minimum " + min_time + " secs. ");

        txt_cancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                alert_dialog.dismiss();
            }
        });

        txt_ok.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                alert_dialog.dismiss();

            }
        });

        alert_dialog.show();


    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, callback,
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {

        AdroitApplication.lat = location.getLatitude();
        AdroitApplication.lng = location.getLongitude();
        Log.e("location", AdroitApplication.lat + "gps" + AdroitApplication.lng);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions, @NonNull final int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {


        super.onSaveInstanceState(outState);

        outState.putString("from", "save");
        outState.putString("foldePath", foldePath);
        outState.putString("folderNam", folderNam);
        outState.putInt("count", count);
    }


    public void toggleFlashMode(ImageView button_splash) {
        String otherFlashMode = "";
        if (mCurrentFlash.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            mCurrentFlash = otherFlashMode = Camera.Parameters.FLASH_MODE_OFF;
            button_splash.setImageResource(R.mipmap.flash_off);
        } else {
            mCurrentFlash = otherFlashMode = Camera.Parameters.FLASH_MODE_TORCH;
            button_splash.setImageResource(R.mipmap.flash_on);
        }
        CameraGLView.CameraThread.requestFlash(otherFlashMode);
    }

    private void startRecording() {
        if (DEBUG) Log.v(TAG, "startRecording:");
        try {
            toggleRelease.setText("STOP");    // turn red
            mMuxer = new MediaMuxerWrapper(VideoAudioTimeStampOffline.this, outputFile.getAbsolutePath());    // if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraView.getVideoWidth(), mCameraView.getVideoHeight());
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            toggleRelease.setText("START");
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    private void stopRecording() {
        if (DEBUG) Log.v(TAG, "stopRecording:mMuxer=" + mMuxer);
        // return to default color
        if (mMuxer != null) {
            toggleRelease.setText("STOP");
            mMuxer.stopRecording();
            mMuxer = null;
            // you should not wait here
        }
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder((MediaVideoEncoder) encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                mCameraView.setVideoEncoder(null);
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermissionAudio() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

            }

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    5);

        } else {
            return true;
        }
        return false;

    }

    private boolean checkPermission() {
        return checkPermissionCamera()
                && checkPermissionAudio()
                && checkPermissionLocation();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermissionLocation() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.

            if (
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    6);

        } else {
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermissionCamera() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

            }

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    7);

        } else {
            return true;
            //getPermissionForGallery();
        }

        return false;
    }

    public abstract class OnSingleClickListener implements View.OnClickListener {

        private static final long MIN_CLICK_INTERVAL = 800;

        private long mLastClickTime;


        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            if (elapsedTime <= MIN_CLICK_INTERVAL)
                return;

            onSingleClick(v);
        }

    }
}