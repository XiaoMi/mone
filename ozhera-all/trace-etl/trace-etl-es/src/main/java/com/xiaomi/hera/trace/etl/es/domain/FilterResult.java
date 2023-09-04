package com.xiaomi.hera.trace.etl.es.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/5/23 2:58 下午
 */
public class FilterResult {
    /**
     * 是否直接丢弃，连rocksDB都不需要保存
     */
    private boolean discard;
    /**
     * 是否应该保存
     */
    private boolean result;
    /**
     *  是否需要添加至bloom filter。如果是由bloom filter判断出来的，则不需要添加
     *  减少bloom filter请求次数
     */
    private boolean addBloom;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isAddBloom() {
        return addBloom;
    }

    public void setAddBloom(boolean addBloom) {
        this.addBloom = addBloom;
    }

    public boolean isDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }
}
