package jp.ac.titech.itpro.sdl.cameraalbum.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import jp.ac.titech.itpro.sdl.cameraalbum.db.dao.GroupDao;
import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Group;

@Database(entities = {Group.class}, version = 2)
public abstract class GroupDatabase extends RoomDatabase {
    public abstract GroupDao groupDao();
}
