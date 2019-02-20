package com.every.everything.core.index.Impl;

import com.every.everything.config.EverythingMiniConfig;
import com.every.everything.core.dao.DataSourceFactory;
import com.every.everything.core.dao.impl.FileIndexDaoImpl;
import com.every.everything.core.index.FileScan;
import com.every.everything.core.interceptor.FileInterceptor;
import com.every.everything.core.interceptor.impl.FileIndexInterceptor;
import com.every.everything.core.interceptor.impl.FilePrintInterceptor;
import com.every.everything.core.model.Thing;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileScanImpl implements FileScan {
    private EverythingMiniConfig config = EverythingMiniConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();
    @Override
    public void index(String path) {
        File file = new File(path);
        List<File> fileList = new ArrayList<>();
        if (file.isFile()) {
            if (config.getExcludePath().contains(file.getParent())) {
                return;
            } else {
                fileList.add(file);
            }
        } else {
            if (config.getExcludePath().contains(path)) {
                return;
            }else{
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        for(FileInterceptor interceptor : this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
