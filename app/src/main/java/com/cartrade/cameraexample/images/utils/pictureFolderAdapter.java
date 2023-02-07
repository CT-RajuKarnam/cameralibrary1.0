package com.cartrade.cameraexample.images.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cartrade.cameraexample.R;
import com.cartrade.cameraexample.db.LocalDB;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

/**
 * Author CodeBoy722
 * <p>
 * An adapter for populating RecyclerView with items representing folders that contain images
 */
public class pictureFolderAdapter extends RecyclerView.Adapter<pictureFolderAdapter.FolderHolder> {

    private ArrayList<imageFolder> folders;
    private Context folderContx;
    private itemClickListener listenToClick;
    int from;
    View cell ;
    /**
     * @param folders     An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param folderContx The Activity or fragment Context
     * @param listen      interFace for communication between adapter and fragment or activity
     */
    public pictureFolderAdapter(ArrayList<imageFolder> folders, Context folderContx, itemClickListener listen, int from) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
        this.from = from;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        cell = inflater.inflate(R.layout.row_grid, parent, false);
        return new FolderHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, @SuppressLint("RecyclerView") final int position) {
        final imageFolder folder = folders.get(position);
        String folderSizeString = "";
        if (folder.getFirstPic().equalsIgnoreCase("")) {
            Glide.with(folderContx)
                    .load(folderContx.getResources().getDrawable(R.mipmap.adroit_applogo))
                    .apply(new RequestOptions().fitCenter())
                    .into(holder.folderPic);
            folderSizeString = "0 Media";
        } else {
            Glide.with(folderContx)
                    .load(folder.getFirstPic())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.folderPic);
            folderSizeString = "" + folder.getNumberOfPics() + " Media";
        }

        //setting the number of images
        String text = "" + folder.getFolderName();

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
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName(),folder.getIs_sync());
            }
        });
        holder.trash.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                alertMessageDialog(folders.get(position), position);
            }
        });


    }



    @Override
    public int getItemCount() {
        return folders.size();
    }

    public int getItemPosition(String foldername) {
        int pos = 0;
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
                    folders.remove(pos);
                    LocalDB.getInstance(folderContx).getDb().adroitDao().delFolder(imageFolder.getFolderName());

                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        alert_dialog.show();
    }

    public class FolderHolder extends RecyclerView.ViewHolder {
        ImageView folderPic, trash;
        TextView folderName;
        TextView folderSize;
        public FolderHolder(@NonNull View itemView) {
            super(itemView);
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
