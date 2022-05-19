package com.xiaomi.youpin.docean.plugin.mybatis.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 15:32
 */
@Data
@Builder
public class UpdateParam implements Serializable {

    private String sql;

    private String[] params;

    private String dsName;

}
