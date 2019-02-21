package everything.core.interceptor.impl;

import everything.core.dao.FileIndexDao;
import everything.core.interceptor.ThingInterceptor;
import everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingCleanInterceptor implements ThingInterceptor,Runnable{
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingCleanInterceptor(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void applay(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while(true){
            Thing thing = this.queue.poll();
            if(thing != null){
                fileIndexDao.delete(thing);
            }
            //可以批量删除
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
