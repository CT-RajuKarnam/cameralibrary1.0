package com.cartrade.cameraexample.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cartrade.cameraexample.AdroitApplication;
import com.cartrade.cameraexample.ImageDisplay;
import com.cartrade.cameraexample.R;
import com.cartrade.cameraexample.db.LocalDB;
import com.cartrade.cameraexample.db.models.CapturedImages;
import com.cartrade.cameraexample.db.models.Folder;
import com.cartrade.cameraexample.images.utils.MarginDecoration;
import com.cartrade.cameraexample.images.utils.PicHolder;
import com.cartrade.cameraexample.images.utils.imageFolder;
import com.cartrade.cameraexample.images.utils.itemClickListener;
import com.cartrade.cameraexample.images.utils.pictureFacer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CaseImageOfflineFragment extends Fragment implements itemClickListener {

    private boolean isVisible;
    private boolean isStarted;
    RecyclerView folderRecycler;
    TextView empty;
    ArrayList<String> folderList = new ArrayList<>();
    ArrayList<String> folderPathList = new ArrayList<>();
    FloatingActionButton addFolder;
    public static CaseImageOfflineFragment caseImageFragment;
    PictureFolderAdapter folderAdapter;
    int from;
    ArrayList<imageFolder> folders = new ArrayList<>();
    ProgressDialog progressDialog;

    public static CaseImageOfflineFragment getInstance(int from) {
        Bundle args = new Bundle();
        args.putInt("from", from);
        CaseImageOfflineFragment fragment = new CaseImageOfflineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        super.onStop();
        isStarted = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        isStarted = true;

        if (isVisible) {
            caseImageFragment = this;
            folders = getPicturePaths();
            if (folders.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
                folderAdapter = new PictureFolderAdapter(getActivity(), this, from);
                folderRecycler.setAdapter(folderAdapter);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        if (isVisible && isStarted) {
            caseImageFragment = this;
            folders = getPicturePaths();
            if (folders.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
                folderAdapter = new PictureFolderAdapter(getActivity(), this, from);
                folderRecycler.setAdapter(folderAdapter);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        caseImageFragment = this;
        Bundle bundle = this.getArguments();
        from = bundle.getInt("from", 0);
        View view = inflater.inflate(R.layout.activity_offline_images, container, false);
        empty = view.findViewById(R.id.empty);

        addFolder = (FloatingActionButton) view.findViewById(R.id.addfolder);
        folderRecycler = view.findViewById(R.id.folderRecycler);
        folderRecycler.addItemDecoration(new MarginDecoration(getActivity()));
        GridLayoutManager rv_foldersManager = new GridLayoutManager(getActivity(), 2);
        folderRecycler.setLayoutManager(rv_foldersManager);
        folderRecycler.hasFixedSize();
        if (from == 1) {
            addFolder.setVisibility(View.GONE);
        }


        changeStatusBarColor();
        addFolder.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                createFolderDialog(0, "");
            }
        });

        return view;
    }

    File[] listFile;

    private void createFolderDialog(int from, final String foldernametxt) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.create_folder);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        TextView positiveBtn = (TextView) dialog
                .findViewById(R.id.txt_ok);

        final EditText foldername = (EditText) dialog
                .findViewById(R.id.foldername);

        final TextView error = (TextView) dialog
                .findViewById(R.id.error);

        TextView title = (TextView) dialog
                .findViewById(R.id.title);
        if (from == 0) {
            title.setText("CREATE FOLDER");
            positiveBtn.setText("Create");
        } else {
            title.setText("UPDATE FOLDER");
            positiveBtn.setText("Update");
            foldername.setText(foldernametxt);
        }

        TextView negativeBtn = (TextView) dialog
                .findViewById(R.id.txt_cancel);
        positiveBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {

                if (foldername.getText().toString().trim().length() == 0) {
                    error.setText("Please enter vehicle reg num.");
                    error.setVisibility(View.VISIBLE);
                } else {
                    createFolder(foldernametxt, foldername.getText().toString().trim(), dialog, error);
                }

            }
        });
        negativeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialog.dismiss();

            }
        });
        if (!((Activity) getActivity()).isFinishing())
            dialog.show();

    }


    public void createFolder(String oldFolderName, String fileName, Dialog dialog, TextView textView) {
        File documentsFolder = new File(getActivity().getFilesDir(), "offlineflow/" + fileName);
        if (!documentsFolder.exists()) {
            if (oldFolderName.equalsIgnoreCase("")) {
                documentsFolder.mkdirs();
            } else {
                File oldFolder = new File(getActivity().getFilesDir(), "offlineflow/" + oldFolderName);
                File newFolder = new File(getActivity().getFilesDir(), "offlineflow/" + fileName);
                boolean success = oldFolder.renameTo(newFolder);
                Toast.makeText(getActivity(), "folderrenmaed " + success, Toast.LENGTH_SHORT).show();
            }
            Folder folder = new Folder();
            folder.setFoldername(fileName);
            String date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            folder.setFoldernametime(fileName + "" + date);
            folder.setFolderpath(documentsFolder.getAbsolutePath());
            folder.setImage_captured("N");
            folder.setCreated_at(AdroitApplication.getCurrentTime());
            folder.setUpdated_at(AdroitApplication.getCurrentTime());
            folder.setSync_completed("n");
            folder.setIs_sync("1");
            if (oldFolderName.equalsIgnoreCase("")) {
                LocalDB.getInstance(getActivity()).getDb().adroitDao().insertFolder(folder);
                Intent move = new Intent(getActivity(), ImageDisplay.class);
                move.putExtra("folderPath", documentsFolder.getAbsolutePath());
                move.putExtra("folderName", fileName);
                move.putExtra("is_sync", "1");
                move.putExtra("from", from);
                startActivity(move);
            } else {
                LocalDB.getInstance(getActivity()).getDb().adroitDao().updatefolderName(oldFolderName, fileName, documentsFolder.getAbsolutePath(), fileName + "" + date);

                List<CapturedImages> capturedImages = LocalDB.getInstance(getActivity()).getDb().adroitDao().getCapturedImagesData(oldFolderName);

                for (int i = 0; i < capturedImages.size(); i++) {

                    LocalDB.getInstance(getActivity()).getDb().adroitDao().updateCapturedImageFolderName(oldFolderName, fileName, documentsFolder.getAbsolutePath(), capturedImages.get(i).getImage_path().replace(oldFolderName, fileName), capturedImages.get(i).getImage_path());
                }

                //LocalDB.getInstance(getActivity()).getDb().adroitDao().updateCapturedImageFolderName(oldFolderName,fileName,documentsFolder.getAbsolutePath());
                folders = getPicturePaths();
                folderAdapter.notifyDataSetChanged();
            }

            dialog.dismiss();
        } else {
            textView.setText("Already folder exists with this name.");
            textView.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<imageFolder> getPicturePaths() {
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();

        File file = new File(getActivity().getFilesDir(), from == 0 ? "offlineflow" : "onlineflow");
        if (file.isDirectory()) {
            folderList.clear();
            folderPathList.clear();
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    folderList.add(listFile[i].getName());
                    folderPathList.add(listFile[i].getAbsolutePath() + "/");
                    Log.e("folder", listFile[i].getAbsolutePath());
                }
            }
        }
        for (int k = 0; k < folderList.size(); k++) {
            File files = new File(folderPathList.get(k));
            if (files.isDirectory()) {
                listFile = files.listFiles();
                if (listFile.length > 0) {
                    for (int m = 0; m < listFile.length; m++) {
                        imageFolder folds = new imageFolder();
                        if (!picPaths.contains(folderPathList.get(k))) {
                            picPaths.add(folderPathList.get(k));

                            folds.setPath(folderPathList.get(k));
                            folds.setFolderName(folderList.get(k));
                            folds.setFirstPic(listFile[m].getAbsolutePath());//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                            Folder folder = LocalDB.getInstance(getActivity()).getDb().adroitDao().getFolder(folderList.get(k));
                            if (folder != null) {
                                folds.setSync_completed(folder.getSync_completed());
                                if (listFile.length == 1) {
                                    folds.setIs_sync("4");
                                } else {
                                    if (folds.getSync_completed().equalsIgnoreCase("n")) {
                                        if (listFile[m].getAbsolutePath().contains("signature")) {
                                            folds.setIs_sync(folder.getIs_sync());
                                        } else {
                                            folds.setIs_sync("5");
                                        }
                                    } else {
                                        folds.setIs_sync(folder.getIs_sync());
                                    }
                                }
                            } else {
                                folds.setSync_completed("n");
                                if (listFile.length == 1) {
                                    folds.setIs_sync("4");
                                } else {
                                    if (listFile[m].getAbsolutePath().contains("signature")) {
                                        folds.setIs_sync("n");
                                    } else {
                                        folds.setIs_sync("5");
                                    }
                                }

                            }
                            folds.addpics();
                            picFolders.add(folds);
                        } else {
                            for (int i = 0; i < picFolders.size(); i++) {
                                if (picFolders.get(i).getPath().equals(folderPathList.get(k))) {
                                    picFolders.get(i).setFirstPic(listFile[m].getAbsolutePath());
                                    if (listFile[m].getAbsolutePath().contains("signature")) {
                                        Folder folder = LocalDB.getInstance(getActivity()).getDb().adroitDao().getFolder(folderList.get(k));
                                        if (folder != null && folder.getSync_completed().equalsIgnoreCase("n")) {
                                            picFolders.get(i).setIs_sync(folder.getIs_sync());
                                        }
                                    }
                                    picFolders.get(i).addpics();
                                }
                            }
                        }
                    }
                } else {
                    if (from == 0) {
                        imageFolder folds = new imageFolder();
                        folds.setPath(folderPathList.get(k));
                        folds.setFolderName(folderList.get(k));
                        folds.setFirstPic("");//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                        folds.addpics();
                        Folder folder = LocalDB.getInstance(getActivity()).getDb().adroitDao().getFolder(folderList.get(k));
                        if (folder != null) {
                            folds.setSync_completed(folder.getSync_completed());
                            folds.setIs_sync(folder.getIs_sync());
                        } else {
                            folds.setSync_completed("n");
                            folds.setIs_sync("n");
                        }
                        picFolders.add(folds);
                    }
                }
            }

        }
        return picFolders;
    }

    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     *
     * @param pictureFolderPath a String corresponding to a folder path on the device external storage
     */
    @Override
    public void onPicClicked(String pictureFolderPath, String folderName, String is_sync) {
        Intent move = new Intent(getActivity(), ImageDisplay.class);
        move.putExtra("folderPath", pictureFolderPath);
        move.putExtra("folderName", folderName);
        move.putExtra("from", from);
        move.putExtra("is_sync", is_sync);
        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }


    /**
     * Default status bar height 24dp,with code API level 24
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor() {
        Window window = getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.black));

    }


    private class PictureFolderAdapter extends RecyclerView.Adapter<FolderHolder> {

        private Context folderContx;
        private itemClickListener listenToClick;
        int from;
        View cell;

        public PictureFolderAdapter(Context folderContx, itemClickListener listen, int from) {
            this.folderContx = folderContx;
            this.listenToClick = listen;
            this.from = from;
        }

        @NonNull
        @Override
        public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            cell = inflater.inflate(R.layout.row_grid_offline, parent, false);
            return new FolderHolder(cell);

        }

        @Override
        public void onBindViewHolder(@NonNull final FolderHolder holder, final int position) {
            String folderSizeString = "";

            if (folders.get(position).getFirstPic().equalsIgnoreCase("")) {
                Glide.with(folderContx)
                        .load(folderContx.getResources().getDrawable(R.mipmap.adroit_applogo))
                        .apply(new RequestOptions().fitCenter())
                        .into(holder.folderPic);
                folderSizeString = "0 Media";
            } else {
                Glide.with(folderContx)
                        .load(folders.get(position).getFirstPic())
                        .apply(new RequestOptions().centerCrop())
                        .into(holder.folderPic);
                folderSizeString = "" + folders.get(position).getNumberOfPics() + " Media";
            }

            //setting the number of images
            String text = folders.get(position).getFolderName();

            holder.folderSize.setText(folderSizeString);
            holder.folderSize.setVisibility(View.VISIBLE);
            holder.folderName.setText(text);
            if (from == 1) {
                holder.trash.setVisibility(View.GONE);
            } else {
                holder.trash.setVisibility(View.VISIBLE);
            }


            holder.folderPic.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (folders != null && folders.size() != 0 && folders.size() > 0)
                        listenToClick.onPicClicked(folders.get(position).getPath(), folders.get(position).getFolderName(), folders.get(position).getIs_sync());
                }
            });

            holder.trash.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (folders != null && folders.size() != 0 && folders.size() > 0) {
                        alertMessageDialog(folders.get(position), position);
                    }
                }
            });

            holder.folderName.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (folders.get(position).getIs_sync().equalsIgnoreCase("1"))
                        createFolderDialog(1, folders.get(position).getFolderName());
                }
            });

        }


        @Override
        public int getItemCount() {
            return folders.size();
        }

        public int getItemPosition(String foldername) {
            int pos = -1;
            for (int i = 0; i < getItemCount(); i++) {
                if (foldername.equalsIgnoreCase(folders.get(i).getFolderName())) {
                    pos = i;
                    break;
                }
            }
            return pos;
        }

        public void alertMessageDialog(final imageFolder imageFolder, final int pos) {
            DisplayMetrics metrics = folderContx.getResources().getDisplayMetrics();
            int width = metrics.widthPixels;
            final Dialog alert_dialog = new Dialog(folderContx);
            alert_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert_dialog.setContentView(R.layout.base_alert_dialogue);
            alert_dialog.setCanceledOnTouchOutside(false);
            alert_dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView txt_alert_title = (TextView) alert_dialog.findViewById(R.id.alert_title);
            TextView txt_alert_description = (TextView) alert_dialog.findViewById(R.id.txt_alert_description);
            TextView txt_ok = (TextView) alert_dialog.findViewById(R.id.txt_ok);
            TextView txt_cancel = (TextView) alert_dialog.findViewById(R.id.txt_cancel);
            txt_ok.setText("Delete");
            txt_cancel.setText("Cancel");

            txt_alert_title.setText("Delete Folder");
            txt_alert_description.setText("Deleting will remove all images of it from phone. Please ensure the case against these images has been submitted successfully to avoid any issues.");

            txt_cancel.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    alert_dialog.dismiss();
                }
            });

            txt_ok.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    alert_dialog.dismiss();
                    try {
                        File dir = new File(imageFolder.getPath());
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir, children[i]).delete();
                            }
                        }
                        dir.delete();
                        LocalDB.getInstance(folderContx).getDb().adroitDao().delFolder(imageFolder.getFolderName());
                        LocalDB.getInstance(folderContx).getDb().adroitDao().deleteCapturedImages(imageFolder.getFolderName());
                        folders.remove(pos);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            alert_dialog.show();
        }


    }

    public class FolderHolder extends RecyclerView.ViewHolder {
        ImageView folderPic, trash;
        TextView folderName;
        TextView folderSize;
        public View itemView;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
            folderSize = itemView.findViewById(R.id.folderSize);
            trash = itemView.findViewById(R.id.trash);

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

