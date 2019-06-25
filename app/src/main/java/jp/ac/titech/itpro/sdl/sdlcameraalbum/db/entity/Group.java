package jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {

    @PrimaryKey(autoGenerate = true) public int _id;
    @ColumnInfo(name = "area_id")
    int areaId;
    @ColumnInfo(name = "center_latitude")
    double centerLatitude;
    @ColumnInfo(name = "center_longitude")
    double centerLongitude;
    @ColumnInfo(name = "thumbnail_name")
    String thumbnailName;

    public Group(int areaId, double centerLatitude, double centerLongitude, String thumbnailName){
        this.areaId = areaId;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.thumbnailName = thumbnailName;
    }
}
