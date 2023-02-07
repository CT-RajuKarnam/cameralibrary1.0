package com.cartrade.cameralib.camerafiles.camera2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;
import com.cartrade.cameralib.camerafiles.camera2.dimension.Size;
import com.cartrade.cameralib.camerafiles.camera2.orientation.DeviceOrientationListener;
import com.cartrade.cameralib.camerafiles.camera2.preview.ViewFinderPreview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Created by sudheer on 29/12/17.
 */

abstract class BaseCamera {
    private static String TAG = BaseCamera.class.getSimpleName();
    WeakReference<AppCompatActivity> activity;
    DeviceOrientationListener deviceOrientationListener;
    private int maxWidthSize = CameraAPI.DEFAULT_MAX_IMAGE_WIDTH;
    CameraStatusCallback cameraStatusCallback;
    BaseCamera(AppCompatActivity a) {
        activity = new WeakReference<>(a);
        deviceOrientationListener = new DeviceOrientationListener(a);
    }
    protected ViewFinderPreview viewFinderPreview;
    void setCameraStatusCallback(CameraStatusCallback c) {
        cameraStatusCallback = c;
    }
    public boolean start() {
        if (!isCameraPresent(activity.get())) {
            return false;
        }
        return true;
    }
    void setMaxWidthSize(int s) {
        maxWidthSize = s;
    }
    int getMaxWidthSize() {
        return maxWidthSize;
    }
    public abstract void stop();
    public abstract boolean isCameraOpened();
    public abstract void setFacing(CameraAPI.LensFacing lensFacing);
    public abstract void setFlash(String flash);
    public abstract void setZoom(int zoom);
    public abstract int getFacing();
    public abstract void takePicture(PhotoTakenCallback p);
    protected AspectRatio aspectRatio = CameraAPI.DEFAULT_ASPECT_RATIO;
    protected int displayOrientation;
    // add any call backs

    interface PhotoTakenCallback {
        void onPhotoTaken(byte[] data);
    }
    AspectRatio getAspectRatio() {
        return aspectRatio;
    }
    void setDesiredAspectRatio(AspectRatio a) {
        aspectRatio = a;
    }
    void setOrientation(int orientation) {
        displayOrientation = orientation;
        // TODO: do you need to stop and restart camera1 after orientation is set ?
    }
    void setPreview(ViewFinderPreview v) {
        viewFinderPreview = v;
    }
    static boolean isCameraPresent(Context context) {
        // this device has a camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                          int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected Size chooseOptimalSize(List<Size> cameraSizes) {
        Map<AspectRatio, SortedSet<Size>> aspectRatioSortedSizesMap = new HashMap<>();
        // get supporting preview sizes
        for (Size csize : cameraSizes) {
            AspectRatio a = AspectRatio.of(csize.getWidth(), csize.getHeight());
            SortedSet<Size>sizes = aspectRatioSortedSizesMap.get(a);
            if (sizes == null) {
                sizes = new TreeSet<>();
                aspectRatioSortedSizesMap.put(a, sizes);
            }
            sizes.add(csize);
        }
        // aspect ratio should always be populated either with default or user input values.
        // find the sizes that have the aspect ratio as above

        // if sizes found: chooseOptimalSize to find the optimal size
        // using the surface width and height compensated by the orientation

        // if sizes not found: find the aspect ratio of the input sizes
        // choose the largest aspect ratio from the list.
        SortedSet<Size> sizes = aspectRatioSortedSizesMap.get(aspectRatio);
        if (sizes == null) {
            aspectRatio = chooseAspectRatio(aspectRatioSortedSizesMap.keySet());
            Log.i(TAG, "choosing AR : " + aspectRatio);
            sizes = aspectRatioSortedSizesMap.get(aspectRatio);
        }
        final int surfaceWidth = viewFinderPreview.getWidth();
        final int surfaceHeight = viewFinderPreview.getHeight();
        int desiredWidth = surfaceWidth;
        int desiredHeight = surfaceHeight;
        Log.i(TAG, "displayOrientation in : chooseOptimalSize " + displayOrientation);
        if (displayOrientation == 90 || displayOrientation == 270) {
            desiredWidth = surfaceHeight;
            desiredHeight = surfaceWidth;
        }
        Size result = null;
        int i=1;
        for (Size s: sizes) {
            i++;
            if (desiredWidth <= s.getWidth() && desiredHeight <= s.getHeight()) {
                return s;
            }
            if(i!=sizes.size())
            result = s;
        }
        return result;
    }

    private AspectRatio chooseAspectRatio(Set<AspectRatio> aspectRatioSet) {
        if (aspectRatioSet.contains(aspectRatio)) {
            return aspectRatio;
        }
        SortedSet<AspectRatio> aspectRatios = new TreeSet<>(aspectRatioSet);
        return aspectRatios.last();
    }
    private boolean hasFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }
}
