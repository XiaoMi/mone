package run.mone.openai;

import okhttp3.Response;

/**
 * @author goodjava@qq.com
 * @date 2023/5/6 10:09
 */
public interface StreamListener {

    default void begin() {

    }

    default void end() {
    }

    default void onFailure(Throwable t, Response response) {

    }

    void onEvent(String str);


}
