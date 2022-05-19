package com.xiaomi.youpin.docean.plugin.mybatisplus.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 15:33
 */
@Data
public class QueryResult implements Serializable {

    private List<Map<String,String>> list;

}
