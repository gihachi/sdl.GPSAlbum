package jp.ac.titech.itpro.sdl.cameraalbum.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "photos")
public class PhotoData {
    @PrimaryKey(autoGenerate = true) public long _id;
    @NonNull
    public String date;
    public double latitude;
    public double longitude;
    @ColumnInfo(name = "group_id")
    public long groupID;
    @ColumnInfo(name = "is_outside")
    public boolean isOutSide;

    public PhotoData(String date, double latitude, double longitude, long groupID, boolean isOutSide){
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groupID = groupID;
        this.isOutSide = isOutSide;
    }
}
