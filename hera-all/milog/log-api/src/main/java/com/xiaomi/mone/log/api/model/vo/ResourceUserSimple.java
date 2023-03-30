package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/11 14:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceUserSimple {
    /**
     * 资源列表是否初始化
     */
    private Boolean initializedFlag = false;
    /**
     * 没有初始化展示消息内容
     */
    private String notInitializedMsg;
    /**
     * 资源列表是否展示
     */
    private Boolean showFlag = false;
    /**
     * MQ资源列表展示后筛选列表
     */
    private List<ValueKeyObj<Integer>> mqResourceList;
    /**
     * ES资源列表展示后筛选列表
     */
    private List<ValueKeyObj<Integer>> esResourceList;


}
