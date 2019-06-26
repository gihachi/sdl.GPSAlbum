package jp.ac.titech.itpro.sdl.cameraalbum.util;

import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.itpro.sdl.cameraalbum.db.entity.PhotoData;

public class PhotoDataUtil {

    public static List<String> makePhotoDateList(List<PhotoData> photoDataList){

        List<String> photoDateList = new ArrayList<>();
        for(PhotoData photoData : photoDataList){
            photoDateList.add(photoData.date);
        }

        return photoDateList;
    }
}
