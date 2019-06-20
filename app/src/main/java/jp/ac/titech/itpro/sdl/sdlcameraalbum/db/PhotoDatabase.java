package jp.ac.titech.itpro.sdl.sdlcameraalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.dao.PhotoDao;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.PhotoData;

@Database(entities = {PhotoData.class}, version = 1)
public abstract class PhotoDatabase  extends RoomDatabase {
    public abstract PhotoDao photoDao();
}
