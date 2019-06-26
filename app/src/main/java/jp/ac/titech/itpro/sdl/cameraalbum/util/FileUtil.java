package jp.ac.titech.itpro.sdl.cameraalbum.util;

import android.os.Environment;

import java.io.File;

import jp.ac.titech.itpro.sdl.sdlcameraalbum.R;

public class FileUtil {

    final static String FILE_PREFIX = "geo-album-";

    public static void renameFile(File existingFile, File renameFile){
        existingFile.renameTo(renameFile);
    }


    public static String makePhotoFileName(String takenDate){
        return FILE_PREFIX+takenDate+".jpg";
    }
}
