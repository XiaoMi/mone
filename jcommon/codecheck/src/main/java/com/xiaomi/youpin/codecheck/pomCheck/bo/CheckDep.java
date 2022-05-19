package com.xiaomi.youpin.codecheck.pomCheck.bo;

import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.po.CheckResult;

public class CheckDep {

    public static final String ARTIFACT_LOG4J2 = "log4j-core";
    public static final Dependency LOG4J_DEP2 = new Dependency("org.apache.logging.log4j", ARTIFACT_LOG4J2, "2.15.1");

    public static final String ARTIFACT_FASTJSON = "fastjson";
    public static final Dependency FASTJSON_DEP = new Dependency("com.alibaba", ARTIFACT_FASTJSON, "1.2.67");

    public static final String ARTIFACT_DUBBO = "dubbo";
    public static final Dependency DUBBO_DEP = new Dependency("org.apache.dubbo", ARTIFACT_DUBBO, "2.7.0-youpin-SNAPSHOT");

    public static final String ARTIFACT_NACOS = "nacos-client";
    public static final Dependency NACOS_DEP = new Dependency("com.alibaba.nacos", ARTIFACT_NACOS, "0.8.0-youpin-SNAPSHOT");

    public static final String ARTIFACT_CAT = "cat-client";
    public static final Dependency CAT_DEP = new Dependency("com.dianping.cat", ARTIFACT_CAT, "3.0.1-youpin-SNAPSHOT");

    public CheckResult checkDep(String groupId, String artifactId, String version) {
        switch (artifactId) {
            case ARTIFACT_LOG4J2: {
                return checkLog4jDep2(groupId, version);
            }
            case ARTIFACT_FASTJSON: {
                return checkFastjsonDep(groupId, version);
            }
            case ARTIFACT_DUBBO: {
                return checkDubboDep(groupId, version);
            }
            case ARTIFACT_NACOS: {
                return checkNacosDep(groupId, version);
            }
            case ARTIFACT_CAT: {
                return checkCatDep(groupId, version);
            }
            default:
                return CheckResult.getInfoRes("pom.xml", "", "");
        }
    }


    private CheckResult checkLog4jDep2(String groupId, String version) {
        if (groupId == null || !groupId.equals(LOG4J_DEP2.groupId)) {
            return CheckResult.getWarnRes("log4j", "groupId of log4j is null or not equals " + LOG4J_DEP2.groupId, "groupId错误");
        }
        if (version == null) {
            return CheckResult.getWarnRes("log4j", "version of log4j is null", "version为空");
        }
        try {
            return CommonUtils.checkLog4jVersion(version, LOG4J_DEP2.version);
        } catch (Exception e) {
            return CheckResult.getWarnRes("log4j", "version of log4j has something wrong", "");
        }
    }

    private CheckResult checkFastjsonDep(String groupId, String version) {
        if (groupId == null || !groupId.equals(FASTJSON_DEP.groupId)) {
            return CheckResult.getWarnRes("fastjson", "groupId of fastjson is null or not equals " + FASTJSON_DEP.groupId, "groupId错误");
        }
        if (version == null) {
            return CheckResult.getWarnRes("fastjson", "version of fastjson is null", "version为空");
        }
        try {
            int res = CommonUtils.compareVersion(FASTJSON_DEP.version, version);
            if (res <= 0) {
                return CheckResult.getInfoRes("fastjson", "", "");
            } else {
                return CheckResult.getWarnRes("fastjson", "version of fastjson is wrong, should equals or higher than " + FASTJSON_DEP.version, "version版本错误");
            }
        } catch (Exception e) {
            return CheckResult.getWarnRes("fastjson", "version of fastjson has something wrong", "");
        }
    }

    private CheckResult checkDubboDep(String groupId, String version) {
        if (groupId == null || !groupId.equals(DUBBO_DEP.groupId)) {
            return CheckResult.getWarnRes("dubbo", "groupId of dubbo is null or not equals " + DUBBO_DEP.groupId, "groupId错误");
        }
        if (version == null) {
            return CheckResult.getWarnRes("dubbo", "version of dubbo is null", "version为空");
        }
        try {
            if (DUBBO_DEP.version.equals(version)) {
                return CheckResult.getInfoRes("dubbo", "", "");
            } else {
                return CheckResult.getWarnRes("dubbo", "version of dubbo is wrong, should equals " + DUBBO_DEP.version, "version版本错误");
            }
        } catch (Exception e) {
            return CheckResult.getWarnRes("dubbo", "version of dubbo has something wrong", "");
        }
    }

    private CheckResult checkNacosDep(String groupId, String version) {
        if (groupId == null || !groupId.equals(NACOS_DEP.groupId)) {
            return CheckResult.getWarnRes("nacos", "groupId of nacos is null or not equals " + NACOS_DEP.groupId, "groupId错误");
        }
        if (version == null) {
            return CheckResult.getWarnRes("nacos", "version of nacos is null", "version为空");
        }
        try {
            if (NACOS_DEP.version.equals(version)) {
                return CheckResult.getInfoRes("nacos", "", "");
            } else {
                return CheckResult.getWarnRes("nacos", "version of nacos is wrong, should equals " + NACOS_DEP.version, "version版本错误");
            }
        } catch (Exception e) {
            return CheckResult.getWarnRes("nacos", "version of nacos has something wrong", "");
        }
    }

    private CheckResult checkCatDep(String groupId, String version) {
        if (groupId == null || !groupId.equals(CAT_DEP.groupId)) {
            return CheckResult.getWarnRes("cat", "groupId of cat is null or not equals " + CAT_DEP.groupId, "groupId错误");
        }
        if (version == null) {
            return CheckResult.getWarnRes("cat", "version of cat is null", "version为空");
        }
        try {
            if (CAT_DEP.version.equals(version)) {
                return CheckResult.getInfoRes("cat", "", "");
            } else {
                return CheckResult.getWarnRes("cat", "version of cat is wrong, should equals " + CAT_DEP.version, "version版本错误");
            }
        } catch (Exception e) {
            return CheckResult.getWarnRes("cat", "version of cat has something wrong", "");
        }
    }
}
