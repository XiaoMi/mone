package run.mone.mimeter.dashboard.common;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
public enum TaskType {

    http(1),
    dubbo(2),
    dag(3),
    demo(4);


    public int code;

    private TaskType(int code) {
        this.code = code;
    }
}
