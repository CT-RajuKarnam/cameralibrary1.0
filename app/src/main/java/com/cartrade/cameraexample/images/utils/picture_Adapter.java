package com.cartrade.cameraexample.images.utils;

import static androidx.core.view.ViewCompat.setTransitionName;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cartrade.cameraexample.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

/**
 * Author CodeBoy722
 *
 * A RecyclerView Adapter class that's populates a RecyclerView with images from
 * a folder on the device external storage
 */
public class picture_Adapter extends RecyclerView.Adapter<PicHolder> {

    private ArrayList<pictureFacer> pictureList;
    private Context pictureContx;
    private final itemClickListener picListerner;

    /**
     *
     * @param pictureList ArrayList of pictureFacer objects
     * @param pictureContx The Activities Context
     * @param picListerner An interface for listening to clicks on the RecyclerView's items
     */
    public picture_Adapter(ArrayList<pictureFacer> pictureList, Context pictureContx,itemClickListener picListerner) {
        this.pictureList = pictureList;
        this.pictureContx = pictureContx;
        this.picListerner = picListerner;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View cell = inflater.inflate(R.layout.pic_holder_item, container, false);
        return new PicHolder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int position) {

        final pictureFacer image = pictureList.get(position);
        if(image.getPicturePath().contains(".mp4")){
            holder.folderName.setVisibility(View.VISIBLE);
            try {
                final Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(image.getPicturePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap2 != null) {
                            holder.picture.setImageBitmap(bitmap2);
                        } else {
                            holder.picture.setImageDrawable(pictureContx.getResources().getDrawable(R.drawable.box_black));
                        }
                    }
                }, 1500);
                holder.picture.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View view) {
                        picListerner.onPicClicked(holder, position, pictureList);
                    }
                });
            }catch (Exception e){

            }
        }else {
            holder.folderName.setVisibility(View.GONE);
            Glide.with(pictureContx)
                    .load(image.getPicturePath())
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.picture);
            //}
            setTransitionName(holder.picture, String.valueOf(position) + "_image");

            holder.picture.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    picListerner.onPicClicked(holder, position, pictureList);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return pictureList.size();
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
