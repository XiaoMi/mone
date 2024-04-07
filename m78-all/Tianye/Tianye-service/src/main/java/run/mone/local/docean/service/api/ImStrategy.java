package run.mone.local.docean.service.api;

public interface ImStrategy {

    boolean sendMessage(String message, String toId);

    Object replyMessage(String body, String toId);
}
