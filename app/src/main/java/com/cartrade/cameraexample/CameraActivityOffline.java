package com.cartrade.cameraexample;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cartrade.cameralib.camerafiles.camera2.ActivityInterface;
import com.cartrade.cameralib.camerafiles.camera2.CameraAPI;
import com.cartrade.cameralib.camerafiles.camera2.CameraAPIClient;
import com.cartrade.cameralib.camerafiles.camera2.CameraFragmentOffline;
import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;
import com.cartrade.cameralib.camerafiles.camera2.dimension.Size;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import java.util.List;

public class CameraActivityOffline extends AppCompatActivity {
    private String TAG = "";
    CameraAPIClient apiClient;
    ActivityInterface anInterface;
    String folderpath, folder;
    int secs;
    int count;
    private static final int REQUEST_CAMERA_PERMISSION = 0;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000;
    LocationCallback callback;
    private LocationRequest mLocationRequest;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cam);
        UPDATE_INTERVAL = getResources().getInteger(R.integer.updateinterval) * 1000; // 5 meters
        FASTEST_INTERVAL = getResources().getInteger(R.integer.fast_interval) * 1000; // 2 minute
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        TAG = CameraActivityOffline.class.getSimpleName();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            flag = false;
            // write perm to write the image
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            if (savedInstanceState != null) {
                count = savedInstanceState.getInt("count");
                folderpath = savedInstanceState.getString("folderpath");
                folder = savedInstanceState.getString("folder");
            }
            flag = true;
            launchCamera();
        }
    }

    private void launchCamera() {

        //AspectRatio aspectRatio = AspectRatio.of(4, 3);
        CameraAPI.LensFacing facing = null;
       /* if (id_name == 0)
            facing = CameraAPI.LensFacing.FRONT;
        else*/
      /* if(NewCaseStep3Activity.specialImagesArray.get(arrpos).getImages().get(imgpos).getElement_type().contains("selfie")){
           facing = CameraAPI.LensFacing.FRONT;
       }else*/
        facing = CameraAPI.LensFacing.BACK;

        AspectRatio aspectRatio = AspectRatio.of(4, 3);
        apiClient = new CameraAPIClient.Builder(this).
                previewType(CameraAPI.PreviewType.SURFACE_VIEW).
                maxSizeSmallerDimPixels(1000).
                desiredAspectRatio(aspectRatio).
                lensFacing(facing).
                build();

        /*apiClient = new CameraAPIClient.Builder(this).
                previewType(CameraAPI.PreviewType.SURFACE_VIEW).
                maxSizeSmallerDimPixels(1000).lensFacing(facing).
                build();*/
        CameraAPIClient.Callback callback = new CameraAPIClient.Callback() {
            @Override
            public void onCameraOpened() {
                Log.i(TAG, "onCameraOpened");
            }

            @Override
            public void onAspectRatioAvailable(AspectRatio desired, AspectRatio chosen, List<Size> availableSizes) {

            }

            @Override
            public void onCameraClosed() {
                Log.i(TAG, "onCameraClosed");
            }

            @Override
            public void onPhotoTaken(byte[] data) {
                Log.i(TAG, "onPhotoTaken data length: " + data.length);
            }

            @Override
            public void onBitmapProcessed(Bitmap bitmap) {

            }
        };
        int id_name = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            count = extras.getInt("count");
            folder = extras.getString("folder");
            folderpath = extras.getString("folderpath");
            secs = extras.getInt("secs");

        }

        CameraFragmentOffline cameraFragmentOffline = new CameraFragmentOffline();
        Bundle bundle = new Bundle();
        bundle.putInt("count", count);
        bundle.putInt("secs", secs);
        bundle.putString("folder", folder);
        bundle.putString("folderpath", folderpath);
        cameraFragmentOffline.setArguments(bundle);
        anInterface = (ActivityInterface) cameraFragmentOffline;
        apiClient.start(cameraFragmentOffline, R.id.container, callback);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // write perm to write the image
                    if (
                            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show our own UI to explain to the user why we need to read the contacts
                        // before actually requesting the permission and showing the default UI

                    }
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            6);
                } else {
                    launchCamera();
                }
            } else {
                Toast.makeText(CameraActivityOffline.this, "Need camera permission to use this feature", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == 6) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(CameraActivityOffline.this, "Need location permission to use this feature", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        //launchCamera();
    }

    @Override
    public void onDestroy() {
        if (apiClient != null)
            apiClient.stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag)
            startLocationUpdates();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (anInterface != null)
            anInterface.resume();
    }


    @Override
    public void onBackPressed() {
        if (anInterface != null)
            anInterface.backPressed();
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
        CameraFragmentOffline.lat= location.getLatitude();
        CameraFragmentOffline.lng=location.getLongitude();
        Log.e("location",location.getLatitude()+"gpsoffline"+location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (callback!=null)
            getFusedLocationProviderClient(this).removeLocationUpdates(callback);
    }
    @Override
    protected void onStop() {
        super.onStop();

    }
}
