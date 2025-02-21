package run.mone.hive.schema;

import lombok.Data;
import run.mone.hive.actions.Action;
import run.mone.hive.memory.Memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
public class RoleContext {

    private Memory memory;
    private Map<String, Object> context;
    private Action todo;
    private String profile;
    private boolean isDone;
    private int maxRetries = 1;
    private int currentRetries = 0;

    public LinkedBlockingQueue<Message> news = new LinkedBlockingQueue<>();

    private ReactMode reactMode = ReactMode.BY_ORDER;

    private int state = -1;

    public RoleContext(String profile) {
        this.profile = profile;
        this.memory = new Memory();
        this.context = new HashMap<>();
        this.isDone = false;
    }

    public List<Message> getMessageList() {
        return new ArrayList<>(memory.getStorage());
    }

    /**
     * Defines how a role should react to messages
     */
    public enum ReactMode {

        PLAN_AND_ACT,

        BY_ORDER,

        REACT,

        /**
         * No automatic reactions
         */
        MANUAL;

        public boolean shouldReact(Message message, RoleContext context) {
            switch (this) {
                case PLAN_AND_ACT:
                    return true;
                case BY_ORDER:
                    return context.canProcessNext();
                case REACT:
                    return isDirectMessage(message, context);
                case MANUAL:
                    return false;
                default:
                    return false;
            }
        }

        private boolean isRelevantMessage(Message message, RoleContext context) {
            // Check if message matches role's interests
            return message.getRole() != null &&
                    (message.getRole().equals(context.getProfile()) ||
                            message.getSendTo() != null &&
                                    message.getSendTo().contains(context.getProfile()));
        }

        private boolean isDirectMessage(Message message, RoleContext context) {
            return message.getSendTo() != null &&
                    message.getSendTo().contains(context.getProfile());
        }

        private boolean hasSufficientPriority(Message message) {
            // Implement priority checking logic
            return true;
        }
    }

    public void setTodo(Action action) {
        this.todo = action;
        this.currentRetries = 0;
    }

    public boolean canRetry() {
        return currentRetries < maxRetries;
    }

    public void incrementRetries() {
        currentRetries++;
    }

    private boolean canProcessNext() {
        // Implement order-based processing logic
        return true;
    }

    /**
     * Get the latest news/messages
     *
     * @return the next message in the queue, or null if empty
     */
    public Message getPollNews() {
        return news.poll();
    }

    /**
     * Add a new message to the news queue
     *
     * @param message the message to add
     */
    public void addNews(Message message) {
        if (message != null) {
            news.offer(message);
        }
    }

    /**
     * Check if there are any news/messages available
     *
     * @return true if there are messages in the queue
     */
    public boolean hasNews() {
        return !news.isEmpty();
    }

    /**
     * Clear all news/messages
     */
    public void clearNews() {
        news.clear();
    }
} 