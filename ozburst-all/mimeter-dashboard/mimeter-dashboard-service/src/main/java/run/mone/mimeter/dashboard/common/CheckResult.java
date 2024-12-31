package run.mone.mimeter.dashboard.common;

/**
 * 用于封装检查结果
 */
public class CheckResult {
    private boolean valid;

    private int code;

    private String msg;


    public CheckResult(boolean valid, int code, String msg) {
        this.valid = valid;
        this.code = code;
        this.msg = msg;
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "CheckResult{" +
                "valid=" + valid +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
