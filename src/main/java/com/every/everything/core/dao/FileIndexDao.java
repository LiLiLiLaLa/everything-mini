package com.every.everything.core.dao;

import com.every.everything.core.model.Condition;
import com.every.everything.core.model.Thing;

import java.util.List;

/**
 * 业务层访问数据库的CRUD
 */
public interface FileIndexDao {
    /**
     * 插入数据Thing
     * @param thing
     */
    void insert(Thing thing);

    /**
     * 根据condition条件进行数据库检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);
}
