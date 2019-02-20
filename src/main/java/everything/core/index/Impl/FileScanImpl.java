package everything.core.index.Impl;

import everything.config.EverythingMiniConfig;
import everything.core.index.FileScan;
import everything.core.interceptor.FileInterceptor;

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
