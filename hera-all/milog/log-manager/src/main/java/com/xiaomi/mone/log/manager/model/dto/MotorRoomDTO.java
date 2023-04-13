package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/19 15:59
 */
@Data
public class MotorRoomDTO {

    private String nameEn;

    private List<PodDTO> podDTOList;
}
