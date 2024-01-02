package run.mone.mimeter.dashboard.common;


public enum SceneSource {

    CONSOLE(0),
    OPEN_API(1);


    public final int code;

    private SceneSource(int code) {
        this.code = code;
    }
}
