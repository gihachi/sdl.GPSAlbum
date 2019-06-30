package jp.ac.titech.itpro.sdl.gpsalbum.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "areas")
public class Area {
    @PrimaryKey(autoGenerate = true) public long _id;

    @ColumnInfo(name = "area_name")
    @NonNull
    public String areaName;

    public Area(String areaName){
        this.areaName = areaName;
    }
}
