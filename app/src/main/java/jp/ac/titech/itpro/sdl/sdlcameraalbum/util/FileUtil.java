package jp.ac.titech.itpro.sdl.sdlcameraalbum.util;

import java.io.File;

public class FileUtil {

    public static void renameFile(File existingFile, File renameFile){
        existingFile.renameTo(renameFile);
    }
}
