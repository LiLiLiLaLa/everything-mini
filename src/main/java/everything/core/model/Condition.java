package everything.core.model;

import lombok.Data;

@Data
public class Condition{
    private String name;
    private String fileType;
    private Integer limit;
    /**
     * 检索结果的文件信息depth排序规则
     * true——>asc
     * false——>desc
     */
    private Boolean orderByAsc;
}
