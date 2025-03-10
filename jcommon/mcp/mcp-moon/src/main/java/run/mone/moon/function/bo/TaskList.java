package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskList implements Serializable {
    private List<TaskListItem> items;
    private int page;
    private int pageSize;
    /**
     * 总记录数
     */
    private long total = 0;
}
