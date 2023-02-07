package com.cartrade.cameraexample.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.cartrade.cameraexample.db.models.CapturedImages;
import com.cartrade.cameraexample.db.models.Folder;

import java.util.List;


@Dao
public interface AdroitDao {



    @Query("SELECT * FROM table_captured_images WHERE reg_no=:regno")
    List<CapturedImages> getCapturedImages(String regno);

    @Insert
    void insertCapturedImage(CapturedImages capturedImages);

    @Query("Delete FROM table_captured_images WHERE reg_no=:regno")
    void deleteCapturedImages(String regno);

    @Query("Delete FROM table_captured_images WHERE reg_no=:regno AND image_path=:path")
    void deleteCapturedImagesBypath(String regno,String path);

    @Query("UPDATE table_captured_images SET uploaded=:uploaded WHERE reg_no = :regno AND image_path=:imagepath")
    void updateCapturedImage(String regno, String imagepath, String uploaded);

    @Query("SELECT * FROM table_folder WHERE foldername=:foldername")
    Folder getFolder(String foldername);


    @Query("UPDATE table_captured_images SET uploaded=:uploaded,file_size=:file_size,compress_type=:compress_type WHERE reg_no = :regno AND image_path=:imagepath")
    void updateCapturedImage(String regno, String imagepath, String uploaded,String compress_type,String file_size);

    @Query("UPDATE table_captured_images SET uploaded=:uploaded,file_size=:file_size,compress_type=:compress_type WHERE  image_path=:imagepath")
    void updateCapturedImage( String imagepath, String uploaded,String compress_type,String file_size);

    @Query("SELECT * FROM table_captured_images WHERE reg_no=:regno AND image_path=:path")
    CapturedImages getCapturedImagesData(String regno, String path);

    @Query("SELECT * FROM table_captured_images WHERE reg_no=:regno")
    List<CapturedImages> getCapturedImagesData(String regno);

    @Query("Delete FROM table_folder WHERE foldername=:foldername")
    void delFolder(String foldername);

    @Query("UPDATE table_captured_images SET reg_no = :newname,folder_path=:folderpath WHERE reg_no=:old")
    void updateCapturedImageFolderName(String old, String newname, String folderpath);

    @Query("UPDATE table_captured_images SET reg_no = :newname,folder_path=:folderpath,image_path=:image_path WHERE reg_no=:old AND image_path=:old_image_path")
    void updateCapturedImageFolderName(String old, String newname, String folderpath, String image_path, String old_image_path);

    @Query("UPDATE table_folder SET updated_at=:updated_at,image_captured=:image_captured  WHERE folderpath = :folderpath AND foldername=:foldername")
    void updateFolder(String updated_at, String foldername, String folderpath, String image_captured);

    @Insert
    void insertFolder(Folder folder);

    @Query("UPDATE table_folder SET foldername=:newfoldername,folderpath=:path,foldernametime=:foldernametime WHERE foldername = :oldfoldername")
    void updatefolderName(String oldfoldername, String newfoldername, String path,String foldernametime);

    @Query("UPDATE table_folder SET is_sync=:is_sync,updated_at=:updated_at WHERE foldername = :foldername")
    void updatefolder(String is_sync, String foldername, String updated_at);

    @Query("UPDATE table_folder SET is_sync=:is_sync , sync_completed=:sync_completed WHERE foldername = :foldername")
    void updatefolderSync(String is_sync, String foldername, String sync_completed);

    @Query("UPDATE table_folder SET is_linked=:is_sync  WHERE foldername = :foldername")
    void updatefolderImageLinkSync(String is_sync, String foldername);

    @Query("UPDATE table_folder SET is_linked=:is_sync ,case_id=:case_id  WHERE foldername = :foldername")
    void updatefolderImageLinkSync(String is_sync, String foldername,String case_id);

    @Query("UPDATE table_folder SET is_linked=:is_sync,is_sync=:is_sync  WHERE case_id = :case_id")
    void updatefolderImageLinkSyncByCaseid(String is_sync,String case_id);

    @Query("UPDATE table_folder SET case_id=:case_id  WHERE foldername = :foldername")
    void updatefolderImageLinkSyncbyCaseid(String foldername,String case_id);

}
