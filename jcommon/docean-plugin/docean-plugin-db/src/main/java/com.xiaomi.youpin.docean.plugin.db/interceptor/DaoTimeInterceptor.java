package com.xiaomi.youpin.docean.plugin.db.interceptor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.lang.Stopwatch;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/28 20:07
 */
@Slf4j
public class DaoTimeInterceptor implements DaoInterceptor {

    @Setter
    private boolean printSql = true;

    public DaoTimeInterceptor() {
    }

    public DaoTimeInterceptor(String printSql) {
        this.printSql = Boolean.parseBoolean(printSql);
    }

    public DaoTimeInterceptor(boolean printSql) {
        this.printSql = printSql;
    }

    @Override
    public void filter(DaoInterceptorChain chain) throws DaoException {
        Stopwatch sw = Stopwatch.begin();
        try {
            chain.doChain();
        } finally {
            sw.stop();
            if (printSql) {
                log.info("time={}ms, sql={}",
                        sw.getDuration(),
                        chain.getDaoStatement().toString());
            }

        }
    }
}
