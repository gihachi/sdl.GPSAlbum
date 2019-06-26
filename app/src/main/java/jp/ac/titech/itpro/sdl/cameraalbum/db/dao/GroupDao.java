package jp.ac.titech.itpro.sdl.cameraalbum.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.Group;

@Dao
public interface GroupDao {

    @Insert
    public long insertGroup(Group groupData);

    @Query("SELECT * FROM  groups")
    public List<Group> loadAllGroup();

    @Query("SELECT * FROM groups WHERE area_id = :id")
    public List<Group> loadGroupsInSpecificArea(long id);
}
