package com.cartrade.cameraexample.db.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_folder")
public class Folder {

    @PrimaryKey(autoGenerate = true)
    private int rowid = 0;

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getImage_captured() {
        return image_captured;
    }

    public void setImage_captured(String image_captured) {
        this.image_captured = image_captured;
    }

    public String getFolderpath() {
        return folderpath;
    }

    public void setFolderpath(String folderpath) {
        this.folderpath = folderpath;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @ColumnInfo(name = "foldername")
    private String foldername;

    public String getFoldernametime() {
        return foldernametime;
    }

    public void setFoldernametime(String foldernametime) {
        this.foldernametime = foldernametime;
    }

    @ColumnInfo(name = "foldernametime")
    private String foldernametime="";

    @ColumnInfo(name = "image_captured")
    private String image_captured;

    @ColumnInfo(name = "folderpath")
    private String folderpath;

    @ColumnInfo(name = "created_at")
    private String created_at;

    @ColumnInfo(name = "updated_at")
    private String updated_at;

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


    public String getIs_linked() {
        return is_linked;
    }

    public void setIs_linked(String is_linked) {
        this.is_linked = is_linked;
    }

    @ColumnInfo(name = "is_linked")
    private String is_linked="1";

    @ColumnInfo(name = "sync_completed")
    private String sync_completed="n";


    public String getCase_id() {
        return case_id;
    }

    public void setCase_id(String case_id) {
        this.case_id = case_id;
    }

    @ColumnInfo(name = "case_id")
    private String case_id="";


    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }


    public String getFolder_type() {
        return folder_type;
    }

    public void setFolder_type(String folder_type) {
        this.folder_type = folder_type;
    }

    @ColumnInfo(name = "folder_type")
    private String folder_type="offline";


}
