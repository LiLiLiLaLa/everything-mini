package com.every.everything.core.search;

import com.every.everything.core.dao.DataSourceFactory;
import com.every.everything.core.dao.impl.FileIndexDaoImpl;
import com.every.everything.core.model.Condition;
import com.every.everything.core.model.Thing;
import com.every.everything.core.search.IMPL.FileSearchImpl;

import java.util.List;

public interface FileSearch {
    /**
     * 根据condition条件进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

}
