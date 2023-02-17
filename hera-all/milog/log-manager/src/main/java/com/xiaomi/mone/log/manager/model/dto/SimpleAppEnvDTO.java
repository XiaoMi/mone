package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/18 14:18
 */
@Data
public class SimpleAppEnvDTO implements Serializable {
    private String nameEn;
    private String nameCn;
    private List<PodDTO> podDTOList;
    private List<String> nodeIps;
}
