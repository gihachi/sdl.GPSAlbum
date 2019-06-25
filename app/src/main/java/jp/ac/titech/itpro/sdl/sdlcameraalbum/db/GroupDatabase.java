package jp.ac.titech.itpro.sdl.sdlcameraalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.dao.GroupDao;
import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.Group;

@Database(entities = {Group.class}, version = 1)
public abstract class GroupDatabase extends RoomDatabase {
    public abstract GroupDao groupDao();
}
