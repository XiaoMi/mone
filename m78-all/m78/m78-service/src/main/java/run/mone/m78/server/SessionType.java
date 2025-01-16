package run.mone.m78.server;

public enum SessionType {

    BOT("bot"),
    FLOW("flow");

    private String type;

    SessionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
