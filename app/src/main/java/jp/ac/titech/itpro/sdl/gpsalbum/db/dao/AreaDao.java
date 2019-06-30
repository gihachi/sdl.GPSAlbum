package jp.ac.titech.itpro.sdl.gpsalbum.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import jp.ac.titech.itpro.sdl.gpsalbum.db.entity.Area;

@Dao
public interface AreaDao {

    @Insert
    public long insertArea(Area areaData);

    // stringも=でok?
    @Query("SELECT * FROM areas WHERE area_name = :areaName")
    public List<Area> loadAreaFromName(String areaName);
}
