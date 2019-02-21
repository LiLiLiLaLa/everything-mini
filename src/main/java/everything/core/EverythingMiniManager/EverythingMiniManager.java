package everything.core.EverythingMiniManager;

import everything.config.EverythingMiniConfig;
import everything.core.dao.DataSourceFactory;
import everything.core.dao.FileIndexDao;
import everything.core.dao.impl.FileIndexDaoImpl;
import everything.core.index.FileScan;
import everything.core.index.Impl.FileScanImpl;
import everything.core.interceptor.ThingInterceptor;
import everything.core.interceptor.impl.FileIndexInterceptor;
import everything.core.interceptor.impl.FilePrintInterceptor;
import everything.core.interceptor.impl.ThingCleanInterceptor;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EverythingMiniManager {
    private static volatile EverythingMiniManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService;

    /**
     * 清理删除的文件
     */
    private ThingCleanInterceptor thingCleanInterceptor;
    private Thread backgroundCleanThread;
    private AtomicBoolean backgroundCleanThreadStatus = new AtomicBoolean(false);

    public EverythingMiniManager() {
        this.initComponent();
    }

    /**
     * 初始化给调度器的准备
     */
    public void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        /**
         * 检查数据库初始化
         */
        checkDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //发布代码的时候并不需要
        //this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
        this.thingCleanInterceptor = new ThingCleanInterceptor(fileIndexDao);
        this.backgroundCleanThread = new Thread(this.thingCleanInterceptor);
        this.backgroundCleanThread.setName("Thread-Thing-Clean");
        this.backgroundCleanThread.setDaemon(true);
    }

    private void checkDatabase() {
        String fileName = EverythingMiniConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        //TODO 考虑一下这里的判断
        if (!dbFile.exists()) {
            DataSourceFactory.initDatase();
        } else {
            if (dbFile.isDirectory()) {
                throw new RuntimeException("已存在同名文件夹");
            }
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
                    //TODO 修改
                    //manager.initComponent();
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
        //Stream
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            boolean flag = f.exists();
            if (!flag) {
                //做删除
                thingCleanInterceptor.applay(thing);
            }
            return flag;
        }).collect(Collectors.toList());

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

    /**
     * 启动清理线程
     */
    public void startBackgroundCleanThread(){
        if(this.backgroundCleanThreadStatus.compareAndSet(false,true)){
            this.backgroundCleanThread.start();;
        }else{
            System.out.println("不能重复启动！");
        }
    }
}
