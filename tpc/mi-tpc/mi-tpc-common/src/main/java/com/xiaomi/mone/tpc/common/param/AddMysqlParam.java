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
public class AddMysqlParam implements ArgCheck, Serializable {

    private String driverClass;
    private String dataSourceUrl;
    private String userName;
    private String passWd;
    private Integer poolSize;
    private Integer minPoolSize;
    private Integer maxPoolSize;
    //资源池连接用尽时，一次建立的连接数
    private Integer acquireIncrement;
    //连接池在获得新连接失败时重试的次数，如果小于等于0则无限重试直至连接获得成功
    private Integer acquireRetryAttempts;
    //两次连接中间间隔时间，单位毫秒
    private Integer acquireRetryDelay;
    //false：连接回收时，回滚未提交事务；true：连接回收时，提交事务
    private boolean autoCommitOnClose;
    //如果为true，则当连接获取失败时关闭数据源
    private boolean breakAfterAcquireFailure;
    //连接用尽时客户端调用getConnection等待获取连接的时间,0则无限等待
    private Integer checkoutTimeout;
    //初始化时获取的连接数
    private Integer initialPoolSize;
    //连接最大空闲时间，未0则永不回收连接，单位秒
    private Integer maxIdleTime;
    //缓存
    private Integer maxStatements;
    //单连接statements缓存
    private Integer maxStatementsPerConnection;
    //操作异步线程数
    private Integer numHelperThreads;
    //归还连接时是否检测可靠性
    private boolean testConnectionOnCheckin;
    //获取连接时是否检测可靠性
    private boolean testConnectionOnCheckout;
    //测试空闲连接可靠性的时间周期
    private Integer idleConnectionTestPeriod;

    private String name;
    private String type;
    private Integer id;

    private Integer isOpenKc;

    private String sid;

    private String kcUser;

    private String mfa;

    private boolean allowddl;


    @Override
    public void encrypted() {
        if (StringUtils.isNotBlank(passWd)) {
            passWd = "******";
        }
    }

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(driverClass)) {
            return false;
        }
        if (StringUtils.isBlank(dataSourceUrl)) {
            return false;
        }
        if (StringUtils.isBlank(userName)) {
            return false;
        }
        if (StringUtils.isBlank(passWd)) {
            return false;
        }
        if (poolSize == null || poolSize <= 0) {
            return false;
        }
        if (minPoolSize == null || minPoolSize <= 0) {
            return false;
        }
        if (maxPoolSize == null || maxPoolSize <= 0) {
            return false;
        }
        if (acquireIncrement == null || acquireIncrement <= 0) {
            return false;
        }
        if (acquireRetryAttempts == null || acquireRetryAttempts <=0) {
            return false;
        }
        if (acquireRetryDelay == null || acquireRetryDelay <= 0) {
            return false;
        }
        if (checkoutTimeout == null || checkoutTimeout < 0) {
            return false;
        }
        if (initialPoolSize == null || initialPoolSize <= 0) {
            return false;
        }
        if (maxIdleTime == null || maxIdleTime < 0) {
            return false;
        }
        if (maxStatements == null || maxStatements < 0) {
            return false;
        }
        if (maxStatementsPerConnection == null || maxStatementsPerConnection < 0) {
            return false;
        }
        if (numHelperThreads == null || numHelperThreads <=0 ) {
            return false;
        }
        return true;
    }
}
