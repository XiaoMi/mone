package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
@Data
@Slf4j
public class UserBatch implements Serializable {

    private Integer batchNum;

    private Integer userBatchStatus;

}
