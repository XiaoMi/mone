package run.mone.hive.schema;

import lombok.*;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"content", "role"})
public class Message implements Serializable {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String content;
    private String role;
    private String causeBy;
    private Object instructContent;

    private Object sentFrom;
    private List<String> sendTo;

    private Object data;

    private Map<MetaKey, MetaValue> meta = new HashMap<>();

    public Message(String content) {
        this(content, "user", null, null);
    }

    public Message(String content, String role) {
        this(content, role, null, null);
    }

    public Message(String content, String role, String cause) {
        this(content, role, cause, null);
    }

    public Message(String content, String role, String cause, Object instruct) {
        this.content = content;
        this.role = role;
        this.causeBy = cause;
        this.instructContent = instruct;
    }

    public Message(String content, String role, String cause, Object instruct, List<String> sendTo) {
        this(content, role, cause, instruct);
        this.sendTo = sendTo;
    }

    public Message(String content, String role, String cause, String sendFrom, List<String> sendTo) {
        this.content = content;
        this.role = role;
        this.causeBy = cause;
        this.sentFrom = sendFrom;
        this.sendTo = sendTo;
    }

    /**
     * Get list of receivers for this message
     *
     * @return List of receiver names, never null
     */
    public List<String> getReceivers() {
        List<String> receivers = new ArrayList<>();

        // Add direct recipients if specified
        if (sendTo != null && !sendTo.isEmpty()) {
            receivers.addAll(sendTo);
        }

        // Add role as receiver if specified
        if (role != null && !role.isEmpty() && !receivers.contains(role)) {
            receivers.add(role);
        }

        // Add cause originator if specified
        if (causeBy != null && !causeBy.isEmpty() && !receivers.contains(causeBy)) {
            receivers.add(causeBy);
        }

        return receivers.isEmpty() ? Collections.emptyList() : receivers;
    }

    public List<String> getSendTo() {
        return sendTo != null ? sendTo : Collections.emptyList();
    }


    public void setSendTo(String recipient) {
        this.sendTo = Collections.singletonList(recipient);
    }

}