package com.xiaomi.mone.monitor.bo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@Data
public class AlertGroupParam {

    private long id;
    private List<Integer> relIds;
    private Integer page;
    private Integer pageSize;
    private String name;
    private String note;
    private String chatId;
    private List<Long> memberIds;
    private String type;

    public void pageQryInit() {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize >= 100) {
            pageSize = 99;
        }
    }

    public boolean createArgCheck() {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        if (StringUtils.isBlank(note)) {
            return false;
        }
        if (memberIds == null || memberIds.isEmpty()) {
            return false;
        }
        return true;
    }

}
