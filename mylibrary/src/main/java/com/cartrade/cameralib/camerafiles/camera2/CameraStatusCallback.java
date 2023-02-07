package com.cartrade.cameralib.camerafiles.camera2;

import android.graphics.Bitmap;

import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;
import com.cartrade.cameralib.camerafiles.camera2.dimension.Size;

import java.util.List;


/**
 * Created by sudheer on 9/4/17.
 * Internal Interfaces used by the library
 */
interface CameraStatusCallback {
    void onCameraOpen();
    void onPhotoTaken(byte[] data);
    void onBitmapProcessed(Bitmap bitmap);
    void onCameraClosed();
    void onAspectRatioAvailable(AspectRatio desired, AspectRatio chosen, List<Size> available);
}
