package com.every.everything.core.EverythingMiniManager;

import com.every.everything.config.EverythingMiniConfig;
import com.every.everything.core.dao.DataSourceFactory;
import com.every.everything.core.dao.FileIndexDao;
import com.every.everything.core.dao.impl.FileIndexDaoImpl;
import com.every.everything.core.index.FileScan;
import com.every.everything.core.index.Impl.FileScanImpl;
import com.every.everything.core.interceptor.impl.FileIndexInterceptor;
import com.every.everything.core.model.Condition;
import com.every.everything.core.model.Thing;
import com.every.everything.core.search.FileSearch;
import com.every.everything.core.search.IMPL.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class EverythingMiniManager{
    private FileSearch fileSearch;
    private FileScan fileScan;
    private static volatile EverythingMiniManager manager;
    private ExecutorService executorService;

    public EverythingMiniManager(){
//        this.fileScan = fileScan;
//        this.fileSearch = fileSearch;
    }


    /**
     * 检索
     */
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展
        //流式处理，后处理，先查再删除
        return fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //删除
                        //thingClearInterceptor.apply(thing);
                    }
                    return flag;
                }).collect(Collectors.toList());

    }

    /**
     * 初始化给调度器的准备
     */
    public void initComponent() {
        //准备输数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        //初始化并重新索引数据库
        //initOrResetDatabase();

        //数据库层得准备工作
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        //业务层的对象
        this.fileScan = new FileScanImpl();
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        //测试使用
//        this.fileSacn.interceptor(new FilePrintInterceptor());
        //执行数据处理的操作
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));


    }

    /**
     * Manager单例对象得获取
     */
    public static EverythingMiniManager getInstance() {
        if (manager == null) {
            synchronized (EverythingMiniManager.class) {
                if (manager == null) {
                    manager = new EverythingMiniManager();
                    manager.initComponent();
                }
            }
        }
        return manager;
    }

    /**
     * 索引
     */
    public void buildIndex(){
        Set<String> directoies = EverythingMiniConfig.getInstance().getIncludePath();
        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directoies.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        CountDownLatch countDownLatch = new CountDownLatch(directoies.size());

        countDownLatch.countDown();

        System.out.println("Buid index start...");
        for (String path : directoies){
            this.executorService.submit(new Runnable(){
                @Override
                public void run() {
                    EverythingMiniManager.this.fileScan.index(path);
                    //当前任务完成，值-1
                    countDownLatch.countDown();
                }
            });
        }
        /**
         * 阻塞，直到任务完成
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Buid index finish...");
    }
}
