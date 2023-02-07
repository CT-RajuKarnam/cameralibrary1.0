package com.cartrade.cameralib.camerafiles.camera2;


import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;
import com.cartrade.cameralib.camerafiles.camera2.preview.ViewFinderPreview;

/**
 * Created by sudheer on 29/12/17.
 * Presenter in the MVP pattern
 */
interface CameraPresenter {
    void onCreate();
    void onDestroy();
    boolean onStart();
    void onStop();
    void setPreview(ViewFinderPreview v);
    void setMaxWidthSizePixels(int s);
    void setDesiredAspectRatio(AspectRatio a);
    boolean isCameraOpened();
    void setFacing(CameraAPI.LensFacing l);
    int getFacing();
    void takePicture();
    AspectRatio getAspectRatio();
    void setCameraStatusCallback(CameraStatusCallback c);
    void setDisplayOrientation(int o);
    void setZoom(int o);
    void setFlashmode(String flashmode);
}
