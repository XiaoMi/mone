package com.xiaomi.miapi.api.service.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class BeatInfo implements Serializable {

    /**
     * module name，http:controller、dubbo:service name
     */
    private List<String> moduleNames;

    /**
     * instance ip：port
     */
    private String address;

}
