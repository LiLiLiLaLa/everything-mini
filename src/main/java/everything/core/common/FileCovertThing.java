package everything.core.common;

import everything.core.model.FileType;
import everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类，将FILE对象转换为Thing对象
 */
public final class FileCovertThing {
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
        String[] segment = file.getAbsolutePath().split("\\\\");
        return segment.length;
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
