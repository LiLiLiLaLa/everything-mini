package everything.core.interceptor.impl;

import everything.core.common.FileCovertThing;
import everything.core.dao.FileIndexDao;
import everything.core.interceptor.FileInterceptor;
import everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {
    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileCovertThing.covert(file);
        System.out.println("Thing==>" + thing);
        fileIndexDao.insert(thing);
    }
}
