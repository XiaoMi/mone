package com.xiaomi.youpin.docean.plugin.mybatis.bo;

import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.mapping.ResultMapping;

import java.io.Serializable;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 15:32
 */
@Data
@Builder
public class QueryParam implements Serializable {

    private String sql;

    private String[] params;

    private List<ResultMapping> mappings;

    private  Class<?> type;

    private String dsName;

}
