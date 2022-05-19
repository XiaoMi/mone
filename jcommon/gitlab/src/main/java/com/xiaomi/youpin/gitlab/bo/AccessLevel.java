package com.xiaomi.youpin.gitlab.bo;

public enum AccessLevel {
    Guest(10), Reporter(20), Developer(30), Maintainer(40), Owner(50);
    private int level;

    private AccessLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
