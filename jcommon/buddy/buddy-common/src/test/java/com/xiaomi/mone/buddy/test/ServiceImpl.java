package com.xiaomi.mone.buddy.test;

import com.google.auto.service.AutoService;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/26 14:25
 */
@AutoService(IService.class)
public class ServiceImpl implements IService {
    @Override
    public String hi() {
        return "hi";
    }
}
