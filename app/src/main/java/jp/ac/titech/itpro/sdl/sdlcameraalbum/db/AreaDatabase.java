package jp.ac.titech.itpro.sdl.sdlcameraalbum.db;

import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.dao.AreaDao;

public abstract class AreaDatabase extends RoomDatabase {

    public abstract AreaDao areaDao();
}
