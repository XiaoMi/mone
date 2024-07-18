package com.xiaomi.youpin.tesla.ip.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 14:45
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {


    private int id;

    private String name;

    private String description;


}
