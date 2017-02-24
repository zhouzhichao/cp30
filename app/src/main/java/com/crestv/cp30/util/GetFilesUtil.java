package com.crestv.cp30.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chou on 2016/11/15.
 */

public class GetFilesUtil {
    public static List getFilesPath(String fileAbsolutePath){
        List<String> listFiles=new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        // 判断是否为文件夹

        for (int i = 0; i < subFile.length; i++) {
            if (!subFile[i].isDirectory()) {
                String filename = subFile[i].getName();
                // 判断是否为log结尾
                if (filename.trim().toLowerCase().endsWith(".log")) {
                    listFiles.add(filename);
                }
            }
        }
        return listFiles;
    }
    public static File[] getFiles(List<String> listFiles){
        File[] files=new File[listFiles.size()];
        for (int i = 0; i < listFiles.size(); i++) {
            files[i]=new File(FileUtil.SDCARD_APP_DIR.getAbsolutePath()+"/"+listFiles.get(i).toString());
            L.e("==files["+i+"]","=="+files[i].getAbsolutePath());
        }
        return files;
    }
}
