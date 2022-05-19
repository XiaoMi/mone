package com.xiaomi.youpin.docean.plugin.test.bo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@Table
@Data
public class Test {

    @Id
    private int id;

    @Column
    private String test;


    public Test() {
    }


    public Test(int id, String test) {
        this.id = id;
        this.test = test;
    }
}
