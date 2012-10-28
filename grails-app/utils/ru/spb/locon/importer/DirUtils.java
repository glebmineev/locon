package ru.spb.locon.importer;

import java.io.File;

public class DirUtils {
    
    public static void createDir(String path){
        File file = new File(path);
        boolean isCreated = file.mkdirs();
    }
    
}
