package jp.ac.titech.itpro.sdl.cameraalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.cameraalbum.db.dao.PhotoDao;

@Database(entities = {PhotoData.class}, version = 1)
public abstract class PhotoDatabase  extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
