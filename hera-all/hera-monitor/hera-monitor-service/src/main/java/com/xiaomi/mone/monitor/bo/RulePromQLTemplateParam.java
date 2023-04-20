package com.xiaomi.mone.monitor.bo;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author zhanggaofeng1
 */
@ToString
@Data
public class RulePromQLTemplateParam {

    private int id;
    private String name;
    private String promql;
    private Integer type;
    private String remark;
    private String creater;
    private Integer status;
    private boolean paging;
    private int page;
    private int pageSize;

    public void pageQryInit() {
        if (!paging) {
            page = 1;
            pageSize = 50;
        } else {
            if (page <= 0) {
                page = 1;
            }
            if (pageSize <= 0) {
                pageSize = 10;
            }
            if (pageSize >= 100) {
                pageSize = 100;
            }
        }
    }

}
