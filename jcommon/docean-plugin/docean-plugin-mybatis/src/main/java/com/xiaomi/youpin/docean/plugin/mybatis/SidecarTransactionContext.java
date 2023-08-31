package com.xiaomi.youpin.docean.plugin.mybatis;

import com.xiaomi.youpin.docean.common.MutableObject;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.mybatis.timer.SideCarTranWheelTimer;
import com.xiaomi.youpin.docean.plugin.mybatis.transaction.Xid;
import io.netty.util.Timeout;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.Environment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sidecar事务上下文
 *
 * @author zhangping17
 * @author goodjava@qq.com
 */
@Slf4j
public class SidecarTransactionContext {

    public static ConcurrentHashMap<String, MybatisTransaction> transactionMap = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Connection> connectionnMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Timeout> timeoutMap = new ConcurrentHashMap<>();

    private static SideCarTranWheelTimer timer = new SideCarTranWheelTimer();

    public static MybatisTransaction getTransaction(Xid xid, Environment environment) {
        MutableObject create = new MutableObject(false);
        MybatisTransaction res = transactionMap.compute(xid.getId(), (key, value) -> {
            if (null == value) {
                MybatisTransaction mtc = new MybatisTransaction(null, false, xid);
                mtc.setDataSource(environment.getDataSource());
                create.setObj(true);
                return mtc;
            }
            return value;
        });
        //超时的必须清理掉
        if (create.getObj()) {
            Timeout timeout = timer.newTimeout(() -> {
                log.info("check sidecar transaction time out auto close:{} {}", xid.getId(), xid.getName());
                close(xid.getId(), false);
            }, xid.getTimeout());
            timeoutMap.put(xid.getId(), timeout);
        }
        return res;
    }

    /**
     * xid
     */
    private static ThreadLocal<Xid> xidLocal = new ThreadLocal<>();

    public static void close(String xid, boolean removeTimeout) {
        if (removeTimeout) {
            //用户真确执行完了,清理下timeout
            Timeout timeOut = timeoutMap.remove(xid);
            if (null != timeOut) {
                timeOut.cancel();
            }
        } else {
            timeoutMap.remove(xid);
        }
        connectionnMap.remove(xid);
        MybatisTransaction transaction = transactionMap.remove(xid);
        Safe.runAndLog(() -> {
            // 超时的，需要在释放连接前进行回滚
            if (!removeTimeout){
                transaction.rollback();
            }
            transaction.close();
        });
    }

    public static void close(String xid) {
        close(xid, true);
    }

    public static boolean commit(String xid) throws SQLException {
        MybatisTransaction transaction = transactionMap.get(xid);
        if (null != transaction) {
            transaction.commit();
            return true;
        }
        return false;
    }

    public static boolean rollback(String xid) throws SQLException {
        MybatisTransaction transaction = transactionMap.get(xid);
        if (null != transaction) {
            transaction.rollback();
            return true;
        }
        return false;
    }


    public static void setXidLocal(Xid xid) {
        xidLocal.set(xid);
    }

    public static Xid getXidLocal() {
        return xidLocal.get();
    }

    public static void clearLocal() {
        xidLocal.remove();
    }
}
