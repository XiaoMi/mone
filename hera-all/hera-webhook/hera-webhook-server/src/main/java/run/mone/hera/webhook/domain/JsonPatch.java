package run.mone.hera.webhook.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/12 4:35 PM
 */
public class JsonPatch<T> {

    private String op;

    private String path;

    private T value;

    public JsonPatch(String op, String path, T value){
        this.op = op;
        this.path = path;
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }


}
