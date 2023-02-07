package run.mone.disruptor;

/**
 * @author goodjava@qq.com
 * @date 2022/4/29 11:03
 */
public class MutableObject {

    private Object obj;

    public MutableObject() {
    }

    public MutableObject(Object obj) {
        this.obj = obj;
    }

    public <T> T getObj() {
        return (T)obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

}
