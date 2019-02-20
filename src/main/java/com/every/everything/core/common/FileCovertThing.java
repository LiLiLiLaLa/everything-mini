package com.every.everything.core.common;

import com.every.everything.core.model.FileType;
import com.every.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类，将FILE对象转换为Thing对象
 */
public class FileCovertThing {
    private FileCovertThing(){}

    public static Thing covert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }

    private static int computerFileDepth(File file){
        int dept = 0;
        String[] segent = file.getAbsolutePath().split("\\\\");
        dept = segent.length;
        return dept;
    }

    private static FileType computerFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        if(index != -1 && index < fileName.length() - 1){
            String extend = fileName.substring(index + 1);
            return FileType.lookup(extend);
        }else{
            return FileType.OTHER;
        }
    }
}
