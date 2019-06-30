package jp.ac.titech.itpro.sdl.gpsalbum.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Group;

@Dao
public interface GroupDao {

    @Insert
    public long insertGroup(Group groupData);

    @Delete
    public void deleteGroup(Group groupData);

    @Query("SELECT * FROM  groups")
    public List<Group> loadAllGroup();

    @Query("SELECT * FROM groups WHERE area_id = :areaID")
    public List<Group> loadGroupsInSpecificArea(long areaID);

    @Query("SELECT * FROM groups WHERE _id = :groupID")
    public List<Group> loadSpecificGroupFromID(long groupID);
}
