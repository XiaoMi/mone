package com.xiaomi.youpin.tesla.ip.dialog.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/7/16 00:17
 */
@Data
public class CellValue implements Serializable {

    /**
     * button textField combox editor
     */
    private String type;

    private List<String> list;

    private String value;

}
