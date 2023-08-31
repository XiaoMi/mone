package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class AddRedisParam implements ArgCheck , Serializable {

    private String dataSourceUrl;
    private String passWd;
    private String name;
    private String type;
    private String redisType;
    private Integer id;

    private Integer isOpenKc;

    private String sid;

    private String kcUser;

    private String mfa;

    //资源池中最大连接数
    private Integer maxTotal;
    //资源池允许的最大空闲连接数
    private Integer maxIdle;
    //资源池确保的最小空闲连接数
    private Integer minIdle;
    //资源池连接用尽时，调用者是否等待；只有为true时下面的maxWaitMisllis才会生效，false则报异常
    private boolean blockWhenExhausted;

    //borrowObject返回对象时，是否采用DEFAULT_LIFO（last in first out，类似cache的最频繁使用队列），如果为false则表示FIFO。common-pool2中的LinkedBlockingDeque不是Java原生的队列，common-pool2重新写的一个双向队列
    private boolean lifo;
    //common-pool2实现的LinkedBlockingDeque双向阻塞队列使用的是Lock锁，这个参数表示在实例化一个LinkedBlockingDeque时，是否使用lock公平锁
    private boolean fairness;
    //向资源池borrow连接时是否做可用性检测(ping)，检测到无效连接则被移除
    private boolean testOnBorrow;
    //向资源池归还连接时是否做可用性检测(ping)，检测到无效连接则被移除
    private boolean testOnReturn;
    //资源池创建连接时是否做可用性检测(ping)，检测到无效连接则被移除
    private boolean testOnCreate;
    //空闲检测参数--使用在空闲资源检测时通过ping监测连接有效性，无效连接将被销毁
    private boolean testWhileIdle;
    //空闲检测参数--空闲资源检测周期
    private Integer timeBetweenEvictionRunsMillis;
    //空闲检测参数--资源池中资源的最小空闲时间，达到此值后空闲资源将被移除
    private Integer minEvictableIdleTimeMillis;
    //驱逐线程(evictor)的超时时间
    private Integer evictorShutdownTimeoutMillis;
    //borrow连接时最大等待时间
    private Integer maxWaitMisllis;
    //是否开启jmx监控
    private boolean jmxEnable;
    //空闲检测参数--做空闲资源检测时，每次检测资源个数
    private Integer numTestsPerEvictionRun;
    //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断(默认逐出策略)
    private Integer softMinEvictableIdleTimeMillis;


    @Override
    public void encrypted() {
        if (StringUtils.isNotBlank(passWd)) {
            passWd = "******";
        }
    }


    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(dataSourceUrl)) {
            return false;
        }
        if (StringUtils.isBlank(redisType)) {
            return false;
        }
        if (maxTotal == null || maxTotal <=0 ) {
            return false;
        }
        if (maxIdle == null || maxIdle <=0 ) {
            return false;
        }
        if (minIdle == null) {
            return false;
        }
        if (timeBetweenEvictionRunsMillis == null || timeBetweenEvictionRunsMillis <=0 ) {
            return false;
        }
        if (minEvictableIdleTimeMillis == null || minEvictableIdleTimeMillis <=0 ) {
            return false;
        }
        if (evictorShutdownTimeoutMillis == null || evictorShutdownTimeoutMillis <= 0) {
            return false;
        }


        return true;
    }
}
