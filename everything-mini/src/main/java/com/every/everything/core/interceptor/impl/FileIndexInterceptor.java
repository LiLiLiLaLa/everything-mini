package com.every.everything.core.interceptor.impl;

import com.every.everything.core.common.FileCovertThing;
import com.every.everything.core.dao.FileIndexDao;
import com.every.everything.core.interceptor.FileInterceptor;
import com.every.everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor{
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
