package jp.ac.titech.itpro.sdl.sdlcameraalbum.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "photos")
public class PhotoData {
    @PrimaryKey(autoGenerate = true) public int _id;
    @NonNull
    public String date;
    public String description;
    public double latitude;
    public double longitude;

    public PhotoData(String date, String description, double latitude, double longitude){
        this.date = date;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
