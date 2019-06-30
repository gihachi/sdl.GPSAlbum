package jp.ac.titech.itpro.sdl.gpsalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.PhotoData;
import jp.ac.titech.itpro.sdl.gpsalbum.db.dao.PhotoDao;

@Database(entities = {PhotoData.class}, version = 1)
public abstract class PhotoDatabase  extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
