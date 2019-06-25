package jp.ac.titech.itpro.sdl.sdlcameraalbum.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity.Group;

@Dao
public interface GroupDao {

    @Insert
    public long insertGroup(Group groupData);

    @Query("SELECT * FROM groups WHERE area_id = :id")
    public List<Group> loadGroupsInSpecificArea(int id);
}
