package jp.ac.titech.itpro.sdl.gpsalbum.util;

import java.io.File;

public class FileUtil {

    final static String FILE_PREFIX = "geo-album-";

    public static void renameFile(File existingFile, File renameFile){
        existingFile.renameTo(renameFile);
    }


    public static String makePhotoFileName(String takenDate){
        return FILE_PREFIX+takenDate+".jpg";
    }
}
