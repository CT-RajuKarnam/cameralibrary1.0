package com.cartrade.cameralib.camerafiles.camera2;


import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;

/**
 * Created by sudheer on 29/12/17.
 */

public class CameraAPI {
    static final int DEFAULT_MAX_IMAGE_WIDTH = 800;
    static final AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.of(4, 3);
    public static final class LensFacing {
        private final String name;
        private LensFacing(String s) {
            name = s;
        }
        public static final LensFacing BACK = new LensFacing("back");
        public static final LensFacing FRONT = new LensFacing("front");
    }

    public static final class FlashStatus {
        private final String name;
        private FlashStatus(String n) {
            name = n;
        }
        public static final FlashStatus OFF = new FlashStatus("off");
        public static final FlashStatus ON = new FlashStatus("on");
    }

    public static final class PreviewType {
        private final String name;
        private PreviewType(String s) {
            name = s;
        }
        public static final PreviewType SURFACE_VIEW = new PreviewType("surface_view");
        public static final PreviewType TEXTURE_VIEW = new PreviewType("texture_view");
    }

    private final BaseCamera baseCamera;
    CameraAPI(BaseCamera b) {
        baseCamera = b;
    }
    protected void start() {
        baseCamera.start();
    }
    protected void stop() {
        baseCamera.stop();
    }
    protected void capture() {
        baseCamera.takePicture(null);
    }
//    abstract void setLivePreview();
//    abstract void setCapturePreview();
}
