package jp.ac.titech.itpro.sdl.cameraalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.cameraalbum.db.dao.AreaDao;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Area;

@Database(entities = {Area.class}, version = 1)
public abstract class AreaDatabase extends RoomDatabase {

    public abstract AreaDao areaDao();
}
