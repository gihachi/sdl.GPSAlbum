package jp.ac.titech.itpro.sdl.sdlcameraalbum.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.PhotoData;

@Dao
public interface PhotoDao {

    @Insert
    public long insertPhoto(PhotoData photoData);

    @Update
    public void updateUser(PhotoData photoData);

    @Delete
    public void deleteUser(PhotoData phodoData);

    @Query("SELECT * FROM photos")
    public List<PhotoData> loadAllPhotoData();

}
