package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class AddNacosParam implements ArgCheck , Serializable {

    private String dataSourceUrl;
    private String nacosDataId;
    private String nacosGroup;
    private String name;
    private String type;
    private Integer threads;
    private Integer id;


    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(dataSourceUrl)) {
            return false;
        }
        if (StringUtils.isBlank(nacosDataId)) {
            return false;
        }
        if (StringUtils.isBlank(nacosGroup)) {
            return false;
        }
        if (threads == null || threads <= 0) {
            return false;
        }
        return true;
    }
}
