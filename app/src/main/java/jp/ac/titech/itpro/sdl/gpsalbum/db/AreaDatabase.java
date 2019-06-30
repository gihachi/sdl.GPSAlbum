package jp.ac.titech.itpro.sdl.gpsalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.gpsalbum.db.dao.AreaDao;
import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Area;

@Database(entities = {Area.class}, version = 1)
public abstract class AreaDatabase extends RoomDatabase {

    public abstract AreaDao areaDao();
}
