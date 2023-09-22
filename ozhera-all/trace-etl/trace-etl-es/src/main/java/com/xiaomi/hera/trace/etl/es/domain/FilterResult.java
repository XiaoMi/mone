package com.xiaomi.hera.trace.etl.es.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/5/23 2:58 pm
 */
public class FilterResult {
    /**
     * Whether to discard directly without even saving to rocksDB.
     */
    private boolean discard;
    /**
     * Should it be saved
     */
    private boolean result;
    /**
     * Do you need to add it to the bloom filter? If it is determined by the bloom filter, then there is no need to add it.
     * Reduce the number of bloom filter requests.
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
