package com.xiaomi.youpin.cat;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 * CAT监控的集成
 */

public class CatPlugin {

    private String action;
    private boolean catEnabled;
    private static final String CAT_STATUS_FAILED = "failed";
    private static final String TYPE_REDIS = "redis";
    private String type;

    public CatPlugin(String action, boolean catEnabled) {
        this.action = action;
        this.catEnabled = catEnabled;
        this.type = TYPE_REDIS;
    }

    public CatPlugin(String action, boolean catEnabled, String type) {
        this.action = action;
        this.catEnabled = catEnabled;
        this.type = type;
    }

    /**
     * 创建CAT Transaction, 发数据
     *
     * @param data 　要发给CAT的String 数据
     */
    public void before(String data) {
        if (!isCatEnabled()) {
            return;
        }
    }

    /**
     * 关闭CAT　transaction
     * 如果业务程序失败，设置transaction的status为failed
     *
     * @param success 　transaction的status
     */
    public void after(boolean success) {
        if (!isCatEnabled()) {
            return;
        }
    }

    /**
     * 查看是否启动CAT
     *
     * @return
     */
    private boolean isCatEnabled() {
        return this.catEnabled;
    }

}
