package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/10 16:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommonVo implements Serializable {
    private Long ctime;
    private Long utime;
    private String creator;
    private String updater;
}
