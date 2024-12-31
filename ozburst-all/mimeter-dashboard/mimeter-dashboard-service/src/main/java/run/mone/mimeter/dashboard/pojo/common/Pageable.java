package run.mone.mimeter.dashboard.pojo.common;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
public interface Pageable {

    void setLimit(Integer limit);

    void setOffset(Long offset);
}
