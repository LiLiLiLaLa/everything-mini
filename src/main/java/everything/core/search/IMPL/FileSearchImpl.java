package everything.core.search.IMPL;

import everything.core.dao.FileIndexDao;
import everything.core.model.Condition;
import everything.core.model.Thing;
import everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务层
 */
public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition){
        if(condition == null){
            return new ArrayList<Thing>();
        }
        return this.fileIndexDao.search(condition);
    }

}
