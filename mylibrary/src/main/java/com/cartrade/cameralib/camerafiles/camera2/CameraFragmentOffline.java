package com.cartrade.cameralib.camerafiles.camera2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.cartrade.cameralib.R;
import com.cartrade.cameralib.camerafiles.camera2.dimension.AspectRatio;
import com.cartrade.cameralib.camerafiles.camera2.dimension.Size;
import com.cartrade.cameralib.camerafiles.camera2.orientation.DisplayOrientationDetector;
import com.cartrade.cameralib.camerafiles.camera2.preview.SurfaceViewPreview;
import com.cartrade.cameralib.camerafiles.camera2.preview.TextureViewPreview;
import com.cartrade.cameralib.camerafiles.camera2.preview.ViewFinderPreview;
import com.cartrade.cameralib.camerafiles.utils.GPS;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class CameraFragmentOffline extends Fragment implements CameraView, ActivityInterface, SurfaceHolder.Callback {

    LinearLayout lay_capture, linlaHeaderProgress;
    FrameLayout lay_preview;
    ImageView shutter, btn_flash, switchCamera, imgHelp, imgPreview;
    TextView retake, save, next, textTimer, skip;
    Bitmap bitmapPicture;
    private long mLastClickTime = 0;
    String title;
    String title_name;
    TextView id_Name, tv_zoom_level;
    LinearLayout footer;
    public static boolean doubleclick_retake = false, doubleclick_savenext = false, shutter_flag = false;
    int flag = 0;
    ProgressBar progressBar;

    private static final String TAG = CameraFragmentOffline.class.getName();
    private static final int REQUEST_CAMERA_PERMISSION = 0;
    private CameraPresenter cameraPresenter;
    private ViewFinderPreview viewFinderPreview;
    private CameraAPI.PreviewType previewType;
    private CameraAPI.LensFacing currentFacing = CameraAPI.LensFacing.BACK;
    private View parentView;
    private AdjustableLayout autoFitCameraView;
    private DisplayOrientationDetector displayOrientationDetector;
    private CameraAPIClient.Callback apiCallback;
    boolean isRunning = false;
    Handler handler;
    public static SharedPreferences preferences;
    private VerticalSeekBar verticalseekbar;
    String foldePath;
    String folderName;
    private int image_count = 0;
    String imagesPath = "";
    boolean isFront;
    Timer t,t1;
    int minute = 0, seconds = 0, hour = 0, originalseconds = 0;
    int savedSec = 0;
    TextView latitude,datetime;
    static DecimalFormat twoDecimalForm = new DecimalFormat("#.######");
    LinearLayout timeframe;

    public static double lat,lng;
    private CameraStatusCallback cameraStatusCallback = new CameraStatusCallback() {
        @Override
        public void onCameraOpen() {
            autoFitCameraView.setPreview(viewFinderPreview);
            autoFitCameraView.setAspectRatio(cameraPresenter.getAspectRatio());
            autoFitCameraView.requestLayout();
            if (apiCallback != null)
                apiCallback.onCameraOpened();
        }

        @Override
        public void onPhotoTaken(byte[] data) {
            if (apiCallback != null)
                apiCallback.onPhotoTaken(data);
        }

        @Override
        public void onBitmapProcessed(Bitmap bitmap) {
            //viewFinderPreview.stop();
            if (getActivity() != null && !getActivity().isFinishing()) {
                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageBitmap(bitmap);
                new MyTask(bitmap).execute();
                if (apiCallback != null)
                    apiCallback.onBitmapProcessed(bitmap);
            }
        }

        @Override
        public void onCameraClosed() {
            if (apiCallback != null) {
                apiCallback.onCameraClosed();
            }
        }

        @Override
        public void onAspectRatioAvailable(AspectRatio desired, AspectRatio chosen, List<Size> availablePreviewSizes) {
            if (apiCallback != null)
                apiCallback.onAspectRatioAvailable(desired, chosen, availablePreviewSizes);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View v = inflater.inflate(R.layout.activity_camera3, container, false);
        return v;
    }

    Dialog alert_dialog = null;

    @Override
    public void backPressed() {
        if (image_count + 1 >= 11) {
            printPaths();
        } else {

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            alert_dialog = new Dialog(getActivity());
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
            txt_alert_description.setText("Please capture atleast 10 images.");

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

    }

    @Override
    public void onViewCreated(View pView, Bundle savedInstanceState) {
        parentView = pView;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();
        } else { // permissions have already been granted
            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            startPreviewAndCamera();

            initUi();
        }
    }

    private void startPreviewAndCamera() {

        autoFitCameraView = (AdjustableLayout) parentView.findViewById(R.id.preview_surface);
        ViewFinderPreview.Callback viwefinderCallback = new ViewFinderPreview.Callback() {
            @Override
            public void onSurfaceChanged() {
                cameraPresenter.setPreview(viewFinderPreview);
                cameraPresenter.onStart(); // starts the camera
            }

            @Override
            public void onSurfaceDestroyed() {
                cameraPresenter.onStop();
            }

            @Override
            public void onSurfaceCreated() {
            }
        };
        if (previewType == CameraAPI.PreviewType.TEXTURE_VIEW) {
            viewFinderPreview = new TextureViewPreview(getContext(), autoFitCameraView, viwefinderCallback);
        } else {
            viewFinderPreview = new SurfaceViewPreview(getContext(), autoFitCameraView, viwefinderCallback);
        }

        viewFinderPreview.start();

        if (displayOrientationDetector == null) {
            // the constructor has to be within one of the lifecycle event to make sure the context is not null;
            displayOrientationDetector = new DisplayOrientationDetector(getContext()) {
                @Override
                public void onDisplayOrientationChanged(int displayOrientation) {
                    // update listeners
                    if (cameraPresenter != null) {
                        cameraPresenter.setDisplayOrientation(displayOrientation);
                        autoFitCameraView.setDisplayOrientation(displayOrientation);
                    }
                }
            };
        }
        displayOrientationDetector.enable(getActivity().getWindowManager().getDefaultDisplay());
//        cameraPresenter.setDisplayOrientation(0);
//        autoFitCameraView.setDisplayOrientation(0);;
    }

    @Override
    public void onDestroyView() {
        if (displayOrientationDetector != null) {
            displayOrientationDetector.disable();
        }
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (cameraPresenter != null && cameraStatusCallback != null) {
            cameraPresenter.setCameraStatusCallback(cameraStatusCallback);
            cameraPresenter.onCreate();
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // TODO: Add a DialogFragment to Show the details.
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPreviewAndCamera();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            if (cameraPresenter != null)
                cameraPresenter.onDestroy();
            if (cameraStatusCallback != null)
                cameraStatusCallback.onCameraClosed();
            if (bitmapPicture != null) {
                bitmapPicture.recycle();
                bitmapPicture = null;
            }
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {

        super.onResume();
       /* viewFinderPreview.stop();
        cameraPresenter.setFacing(currentFacing);*/
        if (viewFinderPreview != null)
            viewFinderPreview.stop();
        if (cameraPresenter != null)
        cameraPresenter.setFacing(currentFacing);
        if (viewFinderPreview != null)
            viewFinderPreview.start();
        //imgHelp.setVisibility(View.VISIBLE);
        verticalseekbar.setProgress(0);
        // imgHelp.setImageBitmap();
        skip.setVisibility(View.GONE);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgHelp.setVisibility(View.GONE);
            }
        }, 5000);
        // viewFinderPreview.start();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof AppCompatActivity &&
                ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } else if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().hide();
        }
        // TODO: enable orientation listener: cameraPresenter helps somehow
//        orientationListener = new DeviceOrientationListener(getActivity());
    }

    @Override
    public void setPresenter(@NonNull CameraPresenter c) {
        cameraPresenter = c;
    }

    @Override
    public void setPreviewType(CameraAPI.PreviewType p) {
        previewType = p;
    }

    public void setCallback(CameraAPIClient.Callback c) {
        apiCallback = c;
    }

    @Override
    public void shutterClicked() {
        cameraPresenter.takePicture();
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

    @Override
    public void switchCameraClicked() {
        if (isFront) {
            if (hasFrontCamera()) {
                currentFacing = CameraAPI.LensFacing.FRONT;
            } else {
                currentFacing = CameraAPI
                        .LensFacing.BACK;
            }
        } else {
            currentFacing = CameraAPI.LensFacing.BACK;
        }
        viewFinderPreview.stop();
        cameraPresenter.setFacing(currentFacing);
        viewFinderPreview.start();
        shutter.setEnabled(true);
        btn_flash.setEnabled(true);
    }

    @Override
    public void switchFlashClicked() {

    }

    @Override
    public void focus() {

    }

    public void initUi() {

        autoFitCameraView = (AdjustableLayout) parentView.findViewById(R.id.preview_surface);
        imgPreview = (ImageView) parentView.findViewById(R.id.preview_image);
        progressBar = (ProgressBar) parentView.findViewById(R.id.progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.txt_red),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        datetime = (TextView) parentView.findViewById(R.id.time);
        latitude = (TextView) parentView.findViewById(R.id.latitude);
        timeframe = (LinearLayout) parentView.findViewById(R.id.timeframe);
        textTimer = (TextView) parentView.findViewById(R.id.tv_timer);
        skip = (TextView) parentView.findViewById(R.id.skip);
        lay_preview = (FrameLayout) parentView.findViewById(R.id.lay_preview);
        lay_capture = (LinearLayout) parentView.findViewById(R.id.ll_cam_btn_capture);
        switchCamera = (ImageView) parentView.findViewById(R.id.switchCamera);
        shutter = (ImageView) parentView.findViewById(R.id.button_capture);
        imgHelp = (ImageView) parentView.findViewById(R.id.imgHelp);
        btn_flash = (ImageView) parentView.findViewById(R.id.button_splash);
        retake = (TextView) parentView.findViewById(R.id.retake_photo);
        save = (TextView) parentView.findViewById(R.id.save_photo);
        next = (TextView) parentView.findViewById(R.id.next_photo);
        id_Name = (TextView) parentView.findViewById(R.id.tv_cam_img_title);
        tv_zoom_level = (TextView) parentView.findViewById(R.id.tv_zoom_level);
        linlaHeaderProgress = (LinearLayout) parentView.findViewById(R.id.linlaHeaderProgress);
        footer = (LinearLayout) parentView.findViewById(R.id.footer_camera);
        id_Name = (TextView) parentView.findViewById(R.id.tv_cam_img_title);
        textTimer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Bundle extras = getArguments();

        if (extras != null) {
            folderName = extras.getString("folder");
            foldePath = extras.getString("folderpath");
            title_name = "Image " + (extras.getInt("count") + 1);
            //title = "Please capture: <font color='#af0102'>" + title_name + "</font>";
            title = "Please capture: " + title_name;
            title_name = title_name.replace("/", "_");
            id_Name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
            image_count = extras.getInt("count");
            originalseconds = seconds = extras.getInt("secs");
        }

        if (image_count == 60) {
            next.setText("DONE & CLOSE");
        }
        if (image_count + 1 >= 10) {
            save.setVisibility(View.VISIBLE);
            save.setEnabled(true);
        } else {
            save.setVisibility(View.GONE);
            save.setEnabled(false);
        }

        switchCamera.setVisibility(View.VISIBLE);
        switchCamera.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                if (!isFront) {
                    isFront = true;
                } else {
                    isFront = false;
                }
                switchCameraClicked();
            }
        });


        autoFitCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imgHelp.setVisibility(View.GONE);
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                return false;
            }
        });

        save.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                printPaths();
            }
        });

        next.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                lay_capture.setVisibility(View.GONE);
                image_count++;
                if (image_count == 60) {
                    printPaths();
                } else {
                    if (!doubleclick_savenext) {
                        doubleclick_savenext = true;
                        imgPreview.setVisibility(View.GONE);
                        imgPreview.setImageBitmap(null);
                        if (image_count != 60) {
                            title_name = "Image " + (image_count + 1);
                            //title = "Please capture: <font color='#af0102'>" + title_name + "</font>";
                            title = "Please capture: " + title_name;
                            title_name = title_name.replace("/", "_");
                            id_Name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
                            footer.setVisibility(View.GONE);
                            onResume();
                        }
                    }
                    new Handler().postDelayed(new TimerTask() {
                        @Override
                        public void run() {
                            verticalseekbar.setProgress(0);
                            lay_capture.setVisibility(View.VISIBLE);
                            shutter.setEnabled(true);
                            btn_flash.setEnabled(true);
                        }
                    }, 1500);
                    if (image_count + 1 >= 10) {
                        save.setVisibility(View.VISIBLE);
                        save.setEnabled(true);
                    } else {
                        save.setVisibility(View.GONE);
                        save.setEnabled(false);
                    }
                }

            }
        });
        retake.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                shutter.setEnabled(true);
                btn_flash.setEnabled(true);
                lay_capture.setVisibility(View.GONE);
                if (!doubleclick_retake) {
                    if ((new File(imagesPath)).exists()) {
                        (new File(imagesPath)).delete();
                    }
                    imgPreview.setVisibility(View.GONE);
                    imgPreview.setImageBitmap(null);
                    doubleclick_retake = true;
                    id_Name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
                    footer.setVisibility(View.GONE);
                    new Handler().postDelayed(new TimerTask() {
                        @Override
                        public void run() {
                            lay_capture.setVisibility(View.VISIBLE);
                        }
                    }, 800);
                    flag = 0;
                }
            }
        });
        shutter.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                shutter.setEnabled(false);
                btn_flash.setEnabled(false);
                skip.setVisibility(View.GONE);
                doubleclick_savenext = false;
                doubleclick_retake = false;
                imgHelp.setVisibility(View.GONE);
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                shutterClicked();
            }
        });

        btn_flash.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                shutter.setEnabled(false);
                btn_flash.setEnabled(false);
                switch (getCameraFlash()) {
                    case "auto":
                        saveCameraFlash("off");
                        break;
                    case "off":
                        saveCameraFlash("on");
                        break;
                    case "on":
                        saveCameraFlash("auto");
                        break;
                    case "":
                        saveCameraFlash("off");
                        break;
                }
                camFlash();
            }
        });

        verticalseekbar = (VerticalSeekBar) parentView.findViewById(R.id.verticalseekbar);
        if (verticalseekbar != null)
            verticalseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                       zoom(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        t1 = new Timer("frame", true);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                textTimer.post(new Runnable() {

                    public void run() {
                        if(datetime!=null) {
                            datetime.setText(getCurrentTime());
                            latitude.setText("Lat: " + twoDecimalForm.format(lat) + " Long: " + twoDecimalForm.format(lng));
                        }

                    }
                });
            }
        }, 1000, 1000);

        camFlash();
    }
    public static void createTextureWithTextContentNew(String text, Context context) {

        //Generate one texture pointer...
        // Create an empty, mutable bitmap
        //Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        Canvas canvas1 = new Canvas(bitmap);
        canvas1.drawARGB(0, 0, 0, 0);
        // get a background image from resources
        // note the image format must match the bitmap format

        // Draw the text
        View child1 = ((Activity) context).getLayoutInflater().inflate(R.layout.watermarkimageview, null);
        child1.measure(0, 0);
        child1.layout(0, 0, 50, 0);

        canvas1.translate(0, 490);

        child1.draw(canvas1);


        View child = ((Activity) context).getLayoutInflater().inflate(R.layout.timestamp_layout, null);
        TextView time = child.findViewById(R.id.time);
        TextView latitude = child.findViewById(R.id.latitude);
        time.setTextSize(5.5f);
        latitude.setTextSize(5.5f);
        time.setText(text);
        latitude.setVisibility(View.GONE);
        if (lat != 0 & lng != 0) {
            latitude.setVisibility(View.VISIBLE);
            latitude.setText("Lat: " + twoDecimalForm.format(lat) + " Long: " + twoDecimalForm.format(lng));
        }
        child.measure(20, 20);
        child.layout(0, 0, 200, 0);

        canvas.translate(350, 545);

        child.draw(canvas);

    }

    public static String getCurrentTime() {
        String date = (DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString());
        return date;
    }
    public void printPaths() {
        if (t != null) {
            t.cancel();
            t.purge();
        }
        if (t1 != null) {
            t1.cancel();
            t1.purge();
        }
        if (alert_dialog != null && alert_dialog.isShowing()) {
            alert_dialog.dismiss();
        }
        if (getActivity() != null) {
            Intent intent = new Intent();
            Bundle bundleObject = new Bundle();
            intent.putExtras(bundleObject);
            getActivity().setResult(12, intent);
            getActivity().finish();
        }
    }

    public abstract class OnSingleClickListener implements View.OnClickListener {

        private static final long MIN_CLICK_INTERVAL = 600;

        private long mLastClickTime;


        public abstract void onSingleClick(View v);

        @Override
        public final void onClick(View v) {
            long currentClickTime = SystemClock.uptimeMillis();
            long elapsedTime = currentClickTime - mLastClickTime;
            mLastClickTime = currentClickTime;

            if (elapsedTime <= MIN_CLICK_INTERVAL)
                return;
            if (v != null)
                onSingleClick(v);
        }

    }


    private void startOtherCap() {
        if (next != null)
            next.setText("SAVE & NEXT");

        onResume();
        progressBar.setVisibility(View.VISIBLE);
        id_Name.setVisibility(View.VISIBLE);
        textTimer.setVisibility(View.VISIBLE);
        ((LinearLayout) parentView.findViewById(R.id.layTimer)).setVisibility(View.GONE);
        if (image_count != 60) {
            title_name = "Image " + (image_count + 1);
            // title = "Please capture: <font color='#af0102'>" + title_name + "</font>";
            title = "Please capture: " + title_name;
            title_name = title_name.replace("/", "_");
            id_Name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
        }
        id_Name.setText(Html.fromHtml(title), TextView.BufferType.SPANNABLE);
        footer.setVisibility(View.GONE);
        lay_capture.setVisibility(View.VISIBLE);
        timeframe.setVisibility(View.VISIBLE);
    }


    private void finishActivity() {

        printPaths();

    }


    @Override
    public void resume() {
        if (!isRunning) {
            if (image_count < 60) {
                //startOtherCap();
                progressBar.setMax(originalseconds);
                if (getActivity() != null) {
                    t = new Timer("cam", true);
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            textTimer.post(new Runnable() {

                                public void run() {
                                    seconds--;
                                    savedSec = seconds;
                                    minute = seconds / 60;
                                    int tempSec = seconds % 60;
                                    if (seconds < originalseconds) {
                                        progressBar.setProgress(originalseconds - seconds);
                                        textTimer.setText(""
                                                + (hour > 9 ? hour : ("0" + hour)) + " : "
                                                + (minute > 9 ? minute : ("0" + minute))
                                                + " : "
                                                + (tempSec > 9 ? tempSec : "0" + tempSec));
                                    } else {
                                        ((TextView) parentView.findViewById(R.id.timeRemains)).setText("" + tempSec);
                                    }
                                    if (seconds == originalseconds) {
                                        startOtherCap();
                                    }
                                    if (minute == 0 && tempSec == 0) {
                                        finishActivity();
                                    }

                                }
                            });
                        }
                    }, 1000, 1000);
                }
            }
            isRunning = true;

        }
    }

    @Override
    public void pause() {

    }

   /* @Override
    public void backPressed() {
        printPaths();
    }*/

    @Override
    public void destroy() {

    }

    @Override
    public void saveInstanceState(Bundle outState) {

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    class MyTask extends AsyncTask<Void, Integer, Void> {
        Bitmap bitmap = null;

        public MyTask(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @SuppressLint("NewApi")
        @Override
        protected Void doInBackground(Void... params1) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            String millisInString = dateFormat.format(new Date());
            imagesPath = storeInSdCard(bitmap, millisInString + "_");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                linlaHeaderProgress.setVisibility(View.GONE);

                if (image_count == 60) {
                    next.setText(R.string.done_close);
                    skip.setVisibility(View.GONE);
                }
                shutter.setEnabled(true);
                btn_flash.setEnabled(true);
                footer.setVisibility(View.VISIBLE);
                lay_capture.setVisibility(View.GONE);
                timeframe.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public Bitmap compressImage(String filePath) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxWidth = 1280.0f;
        float maxHeight = 960.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;
//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inInputShareable = true;
        options.inDither = false;
        options.inPurgeable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilePath(Context context, Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        // Create imageDir
        String filename = "" + System.currentTimeMillis();
        File mypath = new File(directory, filename + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri muri = Uri.fromFile(mypath);
        return muri.getPath();
    }

    public void deleteImage(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                Log.e("#Image#", "Image Deleted at:" + filePath);
            } else {
                Log.e("#Image#", "not able to delete Image at:" + filePath);
            }
        } else {
            Log.e("#Image#", "Image not exist at:" + filePath);
        }

    }

    public String storeInSdCard(Bitmap bitmap, String from) {
        FileOutputStream fos = null;
        String path = null;
        File root = new File(foldePath);
        if (!root.exists()) {
            root.mkdirs();
        }
        String filename = from + System.currentTimeMillis();
        File file = new File(root, filename + ".jpg");
        // get a canvas to paint over the bitmap
        Bitmap bitmapNew = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapNew);
        Canvas canvas1 = new Canvas(bitmapNew);
        canvas.drawARGB(0, 0, 0, 0);
        View child1 = getActivity().getLayoutInflater().inflate(R.layout.watermarkimageview, null);
        child1.measure(bitmap.getWidth(), bitmap.getHeight());
        child1.layout(50, 0, 50, 0);
        canvas1.translate(0, bitmap.getHeight()-180);

        child1.draw(canvas1);
        View child = getActivity().getLayoutInflater().inflate(R.layout.timestamp_layout, null);
        TextView time = child.findViewById(R.id.time);
        TextView latitude = child.findViewById(R.id.latitude);
        time.setTextSize(18f);
        latitude.setTextSize(18f);
        time.setText(getCurrentTime());
        latitude.setVisibility(View.GONE);
        if (lat != 0 & lng != 0) {
            latitude.setVisibility(View.VISIBLE);
            latitude.setText("Lat: " + twoDecimalForm.format(lat) + " Long: " + twoDecimalForm.format(lng));
        }
        child.measure(bitmap.getWidth(), bitmap.getHeight());
        child.layout(0, 0, 50, 0);
        Log.e("size",bitmap.getWidth()+"size"+bitmap.getHeight());
        canvas.translate(bitmap.getWidth()/2, bitmap.getHeight()-180);

        child.draw(canvas);
        try {
            fos = new FileOutputStream(file);
            bitmapNew.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri muri = Uri.fromFile(file);

        path = muri.getPath();
        // String date = (DateFormat.format("dd-MM-yyyy HH:mm:ss", new java.util.Date()).toString());


        try {
            ExifInterface newExif = new ExifInterface(file.getAbsolutePath());
            newExif.setAttribute(ExifInterface.TAG_MAKE, Build.MANUFACTURER);
            newExif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL);
            newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(lat));
            newExif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(lat));
            newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(lng));
            newExif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(lng));
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            newExif.setAttribute(ExifInterface.TAG_DATETIME, mdformat.format(calendar.getTime()));
            newExif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, mdformat.format(calendar.getTime()));
            newExif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, mdformat.format(calendar.getTime()));
            newExif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, mdformat.format(calendar.getTime()));
            newExif.saveAttributes();
        } catch (Exception e) {
            try {
                ExifInterface newExif = new ExifInterface(file.getAbsolutePath());
                newExif.setAttribute(ExifInterface.TAG_MAKE, Build.MANUFACTURER);
                newExif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                newExif.setAttribute(ExifInterface.TAG_DATETIME, mdformat.format(calendar.getTime()));
                newExif.saveAttributes();
            } catch (Exception e1) {

            }
            e.printStackTrace();
        }
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            } else {
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
            }
        }
        return path;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

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

    public void camFlash() {
        try {
            if (getActivity().getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                btn_flash.setVisibility(View.VISIBLE);
                String flash = "";
                if (getCameraFlash().equals("auto")) {
                    btn_flash.setImageResource(R.mipmap.flash_auto);
                    flash = Camera.Parameters.FLASH_MODE_AUTO;
                } else if (getCameraFlash().equals("off")) {
                    flash = Camera.Parameters.FLASH_MODE_OFF;
                    btn_flash.setImageResource(R.mipmap.flash_off);
                } else if (getCameraFlash().equals("on")) {
                    flash = Camera.Parameters.FLASH_MODE_ON;
                    btn_flash.setImageResource(R.mipmap.flash_on);
                } else {
                    btn_flash.setImageResource(R.mipmap.flash_off);
                    flash = Camera.Parameters.FLASH_MODE_OFF;
                }
               /* if(cameraPresenter instanceof Camera2Presenter) {
                    cameraPresenter.setFlashmode(flash);
                }else{
                    viewFinderPreview.stop();
                    viewFinderPreview.start();
                }*/
                viewFinderPreview.stop();
                cameraPresenter.setFlashmode(flash);
                viewFinderPreview.start();
            } else {
                btn_flash.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn_flash.setEnabled(true);
                    shutter.setEnabled(true);
                }
            }, 2000);
        }
    }

    public void zoom(int zoom_factor) {
        if (cameraPresenter != null)
            cameraPresenter.setZoom(zoom_factor);
    }


    public void alertMessageDialog(Context context, String title, final String message) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        final Dialog alert_dialog = new Dialog(context);
        alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert_dialog.setContentView(R.layout.base_alert_dialogue);
        alert_dialog.setCanceledOnTouchOutside(false);
        alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.alert_title);
        TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.txt_alert_description);
        TextView txt_ok = (TextView) alert_dialog.findViewById(R.id.txt_ok);
        TextView txt_cancel = (TextView) alert_dialog.findViewById(R.id.txt_cancel);
        txt_cancel.setVisibility(View.VISIBLE);

        txt_alert_title.setText(title);
        txt_alert_description.setText(message);
        final SpannableString s =
                new SpannableString(message);
        Linkify.addLinks(s, Linkify.WEB_URLS);
        txt_alert_description.setText(s);
        txt_alert_description.setMovementMethod(LinkMovementMethod.getInstance());
        txt_cancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                alert_dialog.dismiss();
            }
        });

        txt_ok.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                lay_capture.setVisibility(View.GONE);
                alert_dialog.dismiss();
            }
        });

        alert_dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alert_dialog.dismiss();

                    shutter.setEnabled(true);
                    btn_flash.setEnabled(true);

                    return true;
                }
                return false;
            }
        });

        alert_dialog.show();
    }

}
