package everything.core.search;

import everything.core.model.Condition;
import everything.core.model.Thing;

import java.util.List;

public interface FileSearch {
    /**
     * 根据condition条件进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);

}
