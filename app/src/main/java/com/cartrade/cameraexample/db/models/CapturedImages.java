package com.cartrade.cameraexample.db.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_captured_images")
public class CapturedImages {

    @PrimaryKey(autoGenerate = true)
    private int rowid = 0;

    @ColumnInfo(name = "reg_no")
    private String reg_no;

    @ColumnInfo(name = "folder_path")
    private String folder_path;

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getFolder_path() {
        return folder_path;
    }

    public void setFolder_path(String folder_path) {
        this.folder_path = folder_path;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCaptured_time() {
        return captured_time;
    }

    public void setCaptured_time(String captured_time) {
        this.captured_time = captured_time;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    @ColumnInfo(name = "image_path")
    private String image_path;

    @ColumnInfo(name = "latitude")
    private String latitude;


    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "captured_time")
    private String captured_time;

    @ColumnInfo(name = "uploaded")
    private String uploaded="n";

    @ColumnInfo(name = "file_size")
    private String file_size="";

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getCompress_type() {
        return compress_type;
    }

    public void setCompress_type(String compress_type) {
        this.compress_type = compress_type;
    }

    @ColumnInfo(name = "compress_type")
    private String compress_type="";

    public String getVideo_sec() {
        return video_sec;
    }

    public void setVideo_sec(String video_sec) {
        this.video_sec = video_sec;
    }

    @ColumnInfo(name = "video_sec")
    private String video_sec="";


}
