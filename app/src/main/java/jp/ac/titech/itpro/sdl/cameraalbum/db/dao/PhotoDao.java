package jp.ac.titech.itpro.sdl.cameraalbum.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;

@Dao
public interface PhotoDao {

    @Insert
    public long insertPhoto(PhotoData photoData);

    @Update
    public void updateUser(PhotoData photoData);

    @Update
    public void updateUsers(List<PhotoData> photoDataList);

    @Delete
    public void deleteUser(PhotoData phodoData);

    @Query("SELECT * FROM photos")
    public List<PhotoData> loadAllPhotoData();

    @Query("SELECT * FROM photos WHERE group_id IN (:groupIDArr) AND is_outside = 1")
    public List<PhotoData> loadOutSidePhotoDataByGroupID(long[] groupIDArr);

    @Query("SELECT * FROM photos WHERE group_id = :groupID")
    public List<PhotoData> loadPhotoDataByGroupID(long groupID);
}
