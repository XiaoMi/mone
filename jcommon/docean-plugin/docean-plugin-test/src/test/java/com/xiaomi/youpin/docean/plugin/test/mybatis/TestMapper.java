package com.xiaomi.youpin.docean.plugin.test.mybatis;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public interface TestMapper {

    List<Test> list();

    List<Test> query(int a, int b);

    void insert(Test test);
}
