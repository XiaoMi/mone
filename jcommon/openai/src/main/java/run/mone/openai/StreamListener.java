package run.mone.openai;

/**
 * @author goodjava@qq.com
 * @date 2023/5/6 10:09
 */
public interface StreamListener {

    default void begin() {

    }

    default void end() {
    }

    void onEvent(String str);


}
