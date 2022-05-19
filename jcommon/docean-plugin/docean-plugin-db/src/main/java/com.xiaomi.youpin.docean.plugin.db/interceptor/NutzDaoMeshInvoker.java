package com.xiaomi.youpin.docean.plugin.db.interceptor;

import com.xiaomi.youpin.docean.plugin.db.po.DbRequest;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2/28/21
 */
public interface NutzDaoMeshInvoker {

    List query(DbRequest request);

    int update(DbRequest request);

}
