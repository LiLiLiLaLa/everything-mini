package com.every.everything.core.search.IMPL;

import com.every.everything.core.dao.DataSourceFactory;
import com.every.everything.core.dao.FileIndexDao;
import com.every.everything.core.model.Condition;
import com.every.everything.core.model.Thing;
import com.every.everything.core.search.FileSearch;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务层
 */
public class FileSearchImpl implements FileSearch{
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition){
        return this.fileIndexDao.search(condition);
    }

}
