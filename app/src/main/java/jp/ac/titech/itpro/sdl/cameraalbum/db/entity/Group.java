package jp.ac.titech.itpro.sdl.cameraalbum.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "groups")
public class Group {

    @PrimaryKey(autoGenerate = true) public long _id;
    @ColumnInfo(name = "area_id")
    public long areaId;
    @ColumnInfo(name = "center_latitude")
    public double centerLatitude;
    @ColumnInfo(name = "center_longitude")
    public double centerLongitude;
    @ColumnInfo(name = "thumbnail_name")
    public String thumbnailName;

    public Group(long areaId, double centerLatitude, double centerLongitude, String thumbnailName){
        this.areaId = areaId;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.thumbnailName = thumbnailName;
    }
}
