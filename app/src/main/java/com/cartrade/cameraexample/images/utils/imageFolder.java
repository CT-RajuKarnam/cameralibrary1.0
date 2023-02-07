package com.cartrade.cameraexample.images.utils;

import androidx.room.ColumnInfo;

/**
 * author CodeBoy722
 *
 * Custom Class that holds information of a folder containing images
 * on the device external storage, used to populate our RecyclerView of
 * picture folders
 */
public class imageFolder {

    private  String path;
    private  String FolderName;
    private int numberOfPics = 0;
    private String firstPic;

    public String getCaseid() {
        return caseid;
    }

    public void setCaseid(String caseid) {
        this.caseid = caseid;
    }

    private String caseid="";
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public imageFolder(){

    }

    public imageFolder(String path, String folderName) {
        this.path = path;
        FolderName = folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfPics() {
        return numberOfPics;
    }

    public void setNumberOfPics(int numberOfPics) {
        this.numberOfPics = numberOfPics;
    }

    public void addpics(){
        this.numberOfPics++;
    }

    public String getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }

    public String getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(String is_sync) {
        this.is_sync = is_sync;
    }

    public String getSync_completed() {
        return sync_completed;
    }

    public void setSync_completed(String sync_completed) {
        this.sync_completed = sync_completed;
    }

    @ColumnInfo(name = "is_sync")
    private String is_sync="1";

    @ColumnInfo(name = "sync_completed")
    private String sync_completed="n";
}
