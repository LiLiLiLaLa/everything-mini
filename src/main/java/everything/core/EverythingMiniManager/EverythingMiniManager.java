package everything.core.EverythingMiniManager;



import everything.config.EverythingMiniConfig;
import everything.core.dao.DataSourceFactory;
import everything.core.dao.FileIndexDao;
import everything.core.dao.impl.FileIndexDaoImpl;
import everything.core.index.FileScan;
import everything.core.index.Impl.FileScanImpl;
import everything.core.interceptor.impl.FileIndexInterceptor;
import everything.core.interceptor.impl.FilePrintInterceptor;
import everything.core.model.Condition;
import everything.core.model.Thing;
import everything.core.search.FileSearch;
import everything.core.search.IMPL.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class EverythingMiniManager{
    private FileSearch fileSearch;
    private FileScan fileScan;
    private static volatile EverythingMiniManager manager;
    private ExecutorService executorService;

    public EverythingMiniManager(){
        this.initComponent();
    }

    /**
     * 初始化给调度器的准备
     */
    public void initComponent() {
        //准备输数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        /**
         * 检查数据库初始化
         */
        checkDatabase();


        //数据库层得准备工作
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        //业务层的对象
        this.fileScan = new FileScanImpl();
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.fileScan.interceptor(new FilePrintInterceptor());
    }

    private void checkDatabase(){
        String workDir = System.getProperty("user.dir");
        String fileName = EverythingMiniConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        if(dbFile.isFile() && !dbFile.exists()){
            DataSourceFactory.initDatase();
        }
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
     * 检索
     */
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展
        return this.fileSearch.search(condition);

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

        final CountDownLatch countDownLatch = new CountDownLatch(directoies.size());
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
