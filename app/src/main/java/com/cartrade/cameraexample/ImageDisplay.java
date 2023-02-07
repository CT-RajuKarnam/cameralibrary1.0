package com.cartrade.cameraexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.transition.Fade;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cartrade.cameraexample.db.LocalDB;
import com.cartrade.cameraexample.db.Pref;
import com.cartrade.cameraexample.db.models.CapturedImages;
import com.cartrade.cameraexample.db.models.Folder;
import com.cartrade.cameraexample.fragments.pictureBrowserFragment;
import com.cartrade.cameraexample.images.utils.MarginDecoration;
import com.cartrade.cameraexample.images.utils.PicHolder;
import com.cartrade.cameraexample.images.utils.itemClickListener;
import com.cartrade.cameraexample.images.utils.pictureFacer;
import com.cartrade.cameraexample.images.utils.picture_Adapter;
import com.cartrade.cameraexample.utils.GPS;
import com.cartrade.cameraexample.videotimestamp.VideoAudioTimeStampOffline;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;


/**
 * Author CodeBoy722
 * <p>
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener {
    SimpleExoPlayer videoPlayer;
    RecyclerView imageRecycler;
    ArrayList<pictureFacer> allpictures;
    ProgressBar load;
    String foldePath;
    TextView folderName;
    TextView addimage;
    final int TAKE_MULTI_PICTURE = 12;
    FloatingActionButton addMedia;
    boolean videoFlag;
    boolean signFlag;
    int from;
    String is_sync = "1";
    private static final int PERMISSION_REQUEST_CODE = 7;
    String folderNameTxt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        videoFlag = false;
        signFlag = false;
        addMedia = (FloatingActionButton) findViewById(R.id.addfolder);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        folderName = (TextView) toolbar.findViewById(R.id.header);
        addimage = (TextView) toolbar.findViewById(R.id.addgallery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.back);
        upArrow.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            from = extras.getInt("from");
            is_sync = extras.getString("is_sync");
        }

        folderName.setText(getIntent().getStringExtra("folderName"));

        foldePath = getIntent().getStringExtra("folderPath");
        folderNameTxt = getIntent().getStringExtra("folderName");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);
        imageRecycler.addItemDecoration(new MarginDecoration(this));
        GridLayoutManager rv_foldersManager = new GridLayoutManager(ImageDisplay.this, 3);
        imageRecycler.setLayoutManager(rv_foldersManager);
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);


        if (allpictures.isEmpty()) {
            load.setVisibility(View.VISIBLE);
            reload(0);
        }
        toolbar.setNavigationOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                finish();
            }
        });
        if (from == 1) {
            addMedia.setVisibility(View.GONE);
        }
        addMedia.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                addMediaCall();
            }
        });

    }

    public void addMediaCall() {
        if (getPermisionForCamera()) {
            if (getPermisionForLocation()) {
                if (getpermissionaudio()) {
                    Intent intent = new Intent(ImageDisplay.this, CameraActivityOffline.class);
                    Bundle bundleObject = new Bundle();
                    bundleObject.putString("folderpath", foldePath);
                    bundleObject.putString("folder", getIntent().getStringExtra("folderName"));
                    bundleObject.putInt("count", allpictures.size() - 1);
                    bundleObject.putInt("secs", seconds);
                    intent.putExtras(bundleObject);
                    startActivityForResult(intent, TAKE_MULTI_PICTURE);
                } else {
                    Toast.makeText(ImageDisplay.this, "Need audio permission to use this feature", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ImageDisplay.this, "Need location permission to use this feature", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ImageDisplay.this, "Need camera permission to use this feature", Toast.LENGTH_SHORT).show();
        }
    }

    int seconds;

    public boolean comparetime(String date) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date strDate = sdf.parse(date);
            Date strDate2 = sdf.parse((DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString()));
            long diff = strDate2.getTime() - strDate.getTime();
            long diffinmins = TimeUnit.MILLISECONDS.toMinutes(diff);
            long diffinsecs = TimeUnit.MILLISECONDS.toSeconds(diff);
            seconds = 600 - (int) diffinsecs;
            //Toast.makeText(ImageDisplay.this,""+diffinmins,Toast.LENGTH_SHORT).show();
            if (seconds < 0) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    Folder folder = null;

    public void popupOffline() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_offline);
        TextView txtvideo = (TextView) dialog.findViewById(R.id.txtvideo);
        TextView txtimages = (TextView) dialog.findViewById(R.id.txtimages);
        TextView txtsignature = (TextView) dialog.findViewById(R.id.txtsignature);
        View videoview = (View) dialog.findViewById(R.id.videoview);
        View signatureview = (View) dialog.findViewById(R.id.signatureview);
        signatureview.setVisibility(View.VISIBLE);
        txtsignature.setVisibility(View.VISIBLE);
        txtvideo.setVisibility(View.GONE);
        videoview.setVisibility(View.GONE);
        txtvideo.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();

                Intent intent = null;
                intent = new Intent(ImageDisplay.this, VideoAudioTimeStampOffline.class);

                Bundle bundleObject = new Bundle();
                bundleObject.putString("folderpath", foldePath);
                bundleObject.putString("folder", getIntent().getStringExtra("folderName"));
                intent.putExtras(bundleObject);
                startActivityForResult(intent, TAKE_MULTI_PICTURE);
            }
        });
        txtsignature.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();

                dialog_sign();
            }
        });
        txtimages.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                dialog.dismiss();

                if (allpictures.size() == 0 || allpictures.size() == 1) {

                    LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().updateFolder(AdroitApplication.getCurrentTime(), getIntent().getStringExtra("folderName"), foldePath, "Y");
                    Intent intent = new Intent(ImageDisplay.this, CameraActivityOffline.class);
                    Bundle bundleObject = new Bundle();
                    bundleObject.putString("folderpath", foldePath);
                    bundleObject.putString("folder", getIntent().getStringExtra("folderName"));
                    bundleObject.putInt("count", allpictures.size());
                    bundleObject.putInt("secs", Integer.parseInt(Pref.getIn().getTimercount().replace("m", "")) * 60);
                    intent.putExtras(bundleObject);
                    startActivityForResult(intent, TAKE_MULTI_PICTURE);
                } else {

                    Completable.fromAction(new Action() {
                        @Override
                        public void run() throws Exception {
                            folder = LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().getFolder(getIntent().getStringExtra("folderName"));
                        }
                    }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            if (folder != null)
                                if (comparetime(folder.getUpdated_at())) {
                                    Toast.makeText(ImageDisplay.this, "Time to capture images has expired.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(ImageDisplay.this, CameraActivityOffline.class);
                                    Bundle bundleObject = new Bundle();
                                    bundleObject.putString("folderpath", foldePath);
                                    bundleObject.putString("folder", getIntent().getStringExtra("folderName"));
                                    bundleObject.putInt("count", allpictures.size() - 1);
                                    bundleObject.putInt("secs", seconds);
                                    intent.putExtras(bundleObject);
                                    startActivityForResult(intent, TAKE_MULTI_PICTURE);
                                }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("#DB Error#", "Aggregaor users nor inserted" + e.getMessage());

                        }
                    });


                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean videoFileIsCorrupted(String path) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(ImageDisplay.this, Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
        return "yes".equals(hasVideo);
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        Log.e("CALLED", "OnActivity Result");

        load.setVisibility(View.VISIBLE);
        reload(1);

        super.onActivityResult(requestcode, resultcode, data);
    }

    public void reload(int from) {
        rearrange = false;
        allpictures = getAllImagesByFolder(foldePath);
        imageRecycler.setAdapter(new picture_Adapter(allpictures, ImageDisplay.this, this));
        load.setVisibility(View.GONE);
        if (from == 1) {
            if (allpictures != null && allpictures.size() > 1 && !signFlag) {
                dialog_sign();
            }
        }
    }

    /**
     * @param holder   The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics     An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {
        if (pics != null && pics.size() > 0) {
            if (pics.get(position).getPicturePath().contains(".mp4")) {
                showPopUp(pics.get(position).getPicturePath());
            } else {
                pictureBrowserFragment browser = pictureBrowserFragment.newInstance(pics, position, ImageDisplay.this);

                // Note that we need the API version check here because the actual transition classes (e.g. Fade)
                // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
                // ARE available in the support library (though they don't do anything on API < 21)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //browser.setEnterTransition(new Slide());
                    //browser.setExitTransition(new Slide()); uncomment this to use slide transition and comment the two lines below
                    browser.setEnterTransition(new Fade());
                    browser.setExitTransition(new Fade());
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .addSharedElement(holder.picture, position + "picture")
                        .add(R.id.displayContainer, browser)
                        .addToBackStack(null)
                        .commit();
            }
        }

    }

    @Override
    public void onPicClicked(String pictureFolderPath, String folderName, String is_sync) {

    }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     *
     * @param path a String corresponding to a folder path on the device external storage
     */
    File[] listFile;
    boolean rearrange;

    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();
        File files = new File(path);
        if (files.isDirectory()) {
            listFile = files.listFiles();
           /* if (listFile != null && listFile.length > 1) {
                Arrays.sort(listFile, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified(): object2.lastModified());
                    }
                });
            }*/
            if (listFile.length > 0) {
                for (int m = 0; m < listFile.length; m++) {
                    pictureFacer pic = new pictureFacer();

                    pic.setPicturName(listFile[m].getName());
                    Log.e("path", listFile[m].getName());
                    Log.e("path", listFile[m].getAbsolutePath());
                    pic.setPicturePath(listFile[m].getAbsolutePath());
                    int file_size = Integer.parseInt(String.valueOf(listFile[m].length() / 1024));
                    Log.e("path", "" + file_size);
                    pic.setPictureSize("" + file_size);
                    if (listFile[m].length() > 100) {
                        if (listFile[m].getAbsolutePath().contains(".mp4")) {
                            if (videoFileIsCorrupted(listFile[m].getAbsolutePath())) {
                                videoFlag = true;
                                images.add(pic);
                            } else {
                                LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().deleteCapturedImagesBypath(folderNameTxt, listFile[m].getAbsolutePath());
                                listFile[m].delete();
                                images.clear();
                                rearrange = true;
                                images = getAllImagesByFolder(foldePath);
                                break;
                            }
                        } else if (listFile[m].getAbsolutePath().contains("signature")) {
                            signFlag = true;
                            images.add(pic);
                        } else {
                            images.add(pic);
                        }
                    } else {
                        listFile[m].delete();
                        images.clear();
                        rearrange = true;
                        LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().deleteCapturedImagesBypath(folderNameTxt, listFile[m].getAbsolutePath());
                        images = getAllImagesByFolder(foldePath);
                        break;
                    }
                }
            }
            try {
                if (!rearrange) {
                    ArrayList<pictureFacer> reSelection = new ArrayList<>();
                    for (int i = images.size() - 1; i > -1; i--) {
                        reSelection.add(images.get(i));
                    }
                    images = reSelection;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return images;
    }

    signature mSignature;
    Button mClear, mGetSign, mCancel;
    LinearLayout mContent;
    View view;
    Dialog dialog;
    Bitmap bitmap;
    String StoredPath = "";
    TextView txt_sign_here;

    public void dialog_sign() {

        StoredPath = new File(foldePath).getAbsolutePath() + "/signature.jpg";
        dialog = new Dialog(this);
        // Removing the features of Normal Dialogs
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        dialog.setCanceledOnTouchOutside(false);
        mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) dialog.findViewById(R.id.clear);
        mGetSign = (Button) dialog.findViewById(R.id.getsign);
        txt_sign_here = (TextView) dialog.findViewById(R.id.txt_sign_here);

        mGetSign.setEnabled(false);
        mCancel = (Button) dialog.findViewById(R.id.cancel);
        view = mContent;

        mClear.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Log.v("log_tag", "Panel Saved");
                view.setDrawingCacheEnabled(true);
                mSignature.save(view, StoredPath);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();

            }
        });

        mCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        @SuppressLint("WrongThread")
        public void save(View v, String StoredPath) {
            bitmap = null;
            try {
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                }
                Canvas canvas = new Canvas(bitmap);
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                try {
                    ExifInterface newExif = new ExifInterface(StoredPath);
                    newExif.setAttribute(android.media.ExifInterface.TAG_MAKE, Build.MANUFACTURER);
                    newExif.setAttribute(android.media.ExifInterface.TAG_MODEL, Build.MODEL);
                    newExif.setAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE, GPS.convert(AdroitApplication.lat));
                    newExif.setAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(AdroitApplication.lat));
                    newExif.setAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(AdroitApplication.lng));
                    newExif.setAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(AdroitApplication.lng));
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    newExif.setAttribute(android.media.ExifInterface.TAG_DATETIME, mdformat.format(calendar.getTime()));
                    newExif.setAttribute(android.media.ExifInterface.TAG_DATETIME_DIGITIZED, mdformat.format(calendar.getTime()));
                    newExif.setAttribute(android.media.ExifInterface.TAG_GPS_DATESTAMP, mdformat.format(calendar.getTime()));
                    newExif.setAttribute(android.media.ExifInterface.TAG_DATETIME_ORIGINAL, mdformat.format(calendar.getTime()));
                    newExif.saveAttributes();
                } catch (Exception e) {
                    try {
                        ExifInterface newExif = new ExifInterface(StoredPath);
                        newExif.setAttribute(android.media.ExifInterface.TAG_MAKE, Build.MANUFACTURER);
                        newExif.setAttribute(android.media.ExifInterface.TAG_MODEL, Build.MODEL);
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        newExif.setAttribute(android.media.ExifInterface.TAG_DATETIME, mdformat.format(calendar.getTime()));
                        newExif.saveAttributes();
                    } catch (Exception e1) {

                    }
                    e.printStackTrace();
                }
                CapturedImages capturedImages = new CapturedImages();
                capturedImages.setCaptured_time(DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString());
                capturedImages.setLatitude("" + AdroitApplication.lat);
                capturedImages.setLongitude("" + AdroitApplication.lng);
                capturedImages.setReg_no(getIntent().getStringExtra("folderName"));
                capturedImages.setFolder_path("" + new File(foldePath).getAbsolutePath());
                capturedImages.setImage_path(StoredPath);
                capturedImages.setUploaded("n");
                LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().insertCapturedImage(capturedImages);
                LocalDB.getInstance(ImageDisplay.this).getDb().adroitDao().updatefolderSync("1", getIntent().getStringExtra("folderName"), "n");
                reload(0);
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            try {
                canvas.drawPath(path, paint);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    /* public ArrayList<pictureFacer> getAllImagesByFolder(String path){
         ArrayList<pictureFacer> images = new ArrayList<>();
         Uri allVideosuri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;
         File myFile = new File(path);
         Uri allImagesuri = Uri.fromFile(myFile);
         String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                 MediaStore.Images.Media.SIZE};
         //Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);
         Cursor cursor = ImageDisplay.this.getContentResolver().query( allVideosuri, projection, null,null, null);
         //Cursor cursor = getContentResolver().query( allImagesuri, projection, null,null, null);
         try {
             cursor.moveToFirst();
             do{
                 pictureFacer pic = new pictureFacer();

                 pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                 Log.e("path",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                 Log.e("path",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                 pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                 Log.e("path",cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                 pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                 images.add(pic);
             }while(cursor.moveToNext());
             cursor.close();
             ArrayList<pictureFacer> reSelection = new ArrayList<>();
             for(int i = images.size()-1;i > -1;i--){
                 reSelection.add(images.get(i));
             }
             images = reSelection;
         } catch (Exception e) {
             e.printStackTrace();
         }
         return images;
     }*/
    private void showPopUp(String url) {
        if (videoPlayer == null)
            videoPlayer = new SimpleExoPlayer.Builder(this).build();
        Dialog dialog_yt = new Dialog(ImageDisplay.this, R.style.ThemeDialogCustom);
        dialog_yt.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_yt.setContentView(R.layout.activity_review);
        dialog_yt.setCancelable(true);
        dialog_yt.setCanceledOnTouchOutside(false);
        ImageView img_close_yt = (ImageView) dialog_yt.findViewById(R.id.img_close_yt);
        PlayerView playerView = (PlayerView) dialog_yt.findViewById(R.id.video_player_view);
        ImageView exo_fullscreen_icon = (ImageView) dialog_yt.findViewById(R.id.exo_fullscreen_icon);
        LinearLayout linear_1 = (LinearLayout) dialog_yt.findViewById(R.id.linear_1);
        linear_1.setVisibility(View.GONE);
        exo_fullscreen_icon.setVisibility(View.GONE);
        Uri mFileUri = Uri.parse(url);
        try {
            playerView.setPlayer(videoPlayer);
            videoPlayer.prepare(buildMediaSource(mFileUri));
            videoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e(this.getLocalClassName(), e.toString());
        }
        Window window = dialog_yt.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_yt.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                releasePlayer();
                dialog_yt.dismiss();
            }
        });

        img_close_yt.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                releasePlayer();
                dialog_yt.dismiss();
            }
        });
        dialog_yt.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != videoPlayer)
            videoPlayer.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        if (null != videoPlayer) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, "sample");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean getpermissionaudio() {

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

    @TargetApi(Build.VERSION_CODES.M)
    public boolean getPermisionForLocation() {
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
    public boolean getPermisionForCamera() {
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
                    PERMISSION_REQUEST_CODE);

        } else {
            return true;
        }

        return false;
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // Make sure it's our original READ_CONTACTS request
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    if (grantResults.length == 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getPermisionForLocation();
                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;

            case 5:
                // Make sure it's our original READ_CONTACTS request
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    if (grantResults.length == 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;

            case 6:
                // Make sure it's our original READ_CONTACTS request
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    if (grantResults.length == 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getpermissionaudio();
                    }
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
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
