package run.mone.hive.memory;

import run.mone.hive.schema.Message;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The most basic memory: super-memory
 */
@Data
public class Memory {
    private static final String IGNORED_MESSAGE_ID = "IGNORED";

    private List<Message> storage;
    private Map<String, List<Message>> index;
    private boolean ignoreId;

    public Memory() {
        this.storage = new ArrayList<>();
        this.index = new HashMap<>();
        this.ignoreId = false;
    }

    /**
     * Add a new message to storage, while updating the index
     */
    public void add(Message message) {
        if (ignoreId) {
            message.setId(IGNORED_MESSAGE_ID);
        }

        if (!storage.contains(message)) {
            storage.add(message);

            if (message.getCauseBy() != null) {
                index.computeIfAbsent(message.getCauseBy(), k -> new ArrayList<>())
                        .add(message);
            }
        }
    }

    /**
     * Add multiple messages at once
     */
    public void addBatch(Collection<Message> messages) {
        messages.forEach(this::add);
    }

    /**
     * Return all messages of a specified role
     */
    public List<Message> getByRole(String role) {
        return storage.stream()
                .filter(message -> role.equals(message.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * Return all messages containing specified content
     */
    public List<Message> getByContent(String content) {
        return storage.stream()
                .filter(message -> message.getContent() != null &&
                        message.getContent().contains(content))
                .collect(Collectors.toList());
    }

    /**
     * Delete the newest message from storage
     */
    public Message deleteNewest() {
        if (storage.isEmpty()) {
            return null;
        }

        Message newestMsg = storage.remove(storage.size() - 1);
        if (newestMsg.getCauseBy() != null) {
            List<Message> causedMessages = index.get(newestMsg.getCauseBy());
            if (causedMessages != null) {
                causedMessages.remove(newestMsg);
            }
        }
        return newestMsg;
    }

    /**
     * Delete a specific message
     */
    public void delete(Message message) {
        if (ignoreId) {
            message.setId(IGNORED_MESSAGE_ID);
        }

        storage.remove(message);
        if (message.getCauseBy() != null) {
            List<Message> causedMessages = index.get(message.getCauseBy());
            if (causedMessages != null) {
                causedMessages.remove(message);
            }
        }
    }

    /**
     * Clear all storage and index
     */
    public void clear() {
        storage.clear();
        index.clear();
    }

    /**
     * Return the number of messages in storage
     */
    public int count() {
        return storage.size();
    }

    /**
     * Try to recall all messages containing a specified keyword
     */
    public List<Message> tryRemember(String keyword) {
        return storage.stream()
                .filter(message -> message.getContent() != null &&
                        message.getContent().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Return the most recent k memories, return all when k=0
     */
    public List<Message> getRecent(int k) {
        if (k <= 0 || k >= storage.size()) {
            return new ArrayList<>(storage);
        }
        return storage.subList(storage.size() - k, storage.size());
    }

    /**
     * Find news (previously unseen messages) from the most recent k memories
     */
    public List<Message> findNews(List<Message> observed, int k) {
        List<Message> alreadyObserved = getRecent(k);
        return observed.stream()
                .filter(msg -> !alreadyObserved.contains(msg))
                .collect(Collectors.toList());
    }

    /**
     * Return all messages triggered by a specified Action
     */
    public List<Message> getByAction(String action) {
        return index.getOrDefault(action, new ArrayList<>());
    }

    /**
     * Return all messages triggered by specified Actions
     */
    public List<Message> getByActions(Set<String> actions) {
        return actions.stream()
                .filter(index::containsKey)
                .flatMap(action -> index.get(action).stream())
                .collect(Collectors.toList());
    }

    public Collection<? extends Message> get() {
        return new ArrayList<>(this.storage);
    }

    //添加一个方法,获取最后一条Message(class)
    public Message getLastMessage() {
        if (storage.isEmpty()) {
            return null;
        }
        return storage.get(storage.size() - 1);
    }
}