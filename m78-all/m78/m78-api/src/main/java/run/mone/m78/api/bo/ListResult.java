package run.mone.m78.api.bo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2021/11/10
 */
@Data
public class ListResult<T> implements Serializable {

    private List<T> list;

    private long page;

    private long pageSize;

    private long totalPage;
}
