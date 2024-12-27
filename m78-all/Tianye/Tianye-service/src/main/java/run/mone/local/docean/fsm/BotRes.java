package run.mone.local.docean.fsm;

import lombok.Data;

import java.util.Map;

/**
 * @author wmin
 * @date 2024/2/23
 */
@Data
public class BotRes<T> {

    private int code;

    public static final int SUCCESS = 0;

    public static final int FAILURE = 500;

    public static final int RETRY= 333;

    public static final int WAIT= 777;

    public static final int BREAK = 886;

    private String message;

    private Map<String,String> attachement;

    private T data;


    public static <T> BotRes success(T data) {
        BotRes<T> res = new BotRes();
        res.setCode(SUCCESS);
        res.setMessage("success");
        res.setData(data);
        return res;
    }

    public static BotRes failure(Throwable throwable) {
        BotRes res = new BotRes();
        res.setCode(FAILURE);
        res.setMessage(throwable.getMessage());
        return res;
    }

    public static BotRes failure(String message) {
        BotRes res = new BotRes();
        res.setCode(FAILURE);
        res.setMessage(message);
        return res;
    }

    public static BotRes retry() {
        BotRes res = new BotRes();
        res.setCode(RETRY);
        return res;
    }

    public BotRes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BotRes() {
    }
}
