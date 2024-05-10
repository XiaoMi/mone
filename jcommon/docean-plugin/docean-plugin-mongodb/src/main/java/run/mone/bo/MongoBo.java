package run.mone.bo;

/**
 * @author goodjava@qq.com
 * @date 2024/4/19 23:10
 */
public interface MongoBo {

    String getId();

    default String getUid() {
        return "";
    }

    default void setUid(String uid) {

    }

    int getVersion();

    void setState(int state);

    void setUtime(long utime);

    void setCtime(long ctime);

    void setVersion(int version);

}
