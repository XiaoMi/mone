package run.mone.m78.service.agent.bo;


import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * @author goodjava@qq.com
 * @date 2024/9/11 09:48
 */
public class DiscussionHistory {


    public static class DiscussionEntry {
        final String agentId;
        final String message;
        final Instant timestamp;

        DiscussionEntry(String agentId, String message) {
            this.agentId = agentId;
            this.message = message;
            this.timestamp = Instant.now();
        }

        @Override
        public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
            return String.format("[%s] %s: %s", formatter.format(timestamp), agentId, message);
        }
    }

    private final List<DiscussionEntry> entries = new ArrayList<>();

    public void addEntry(String agentId, String message) {
        entries.add(new DiscussionEntry(agentId, message));
    }

    public String getFullHistory() {
        StringBuilder sb = new StringBuilder();
        for (DiscussionEntry entry : entries) {
            sb.append(entry.toString()).append("\n");
        }
        return sb.toString();
    }

    public List<String> getLastNEntries(int n) {
        List<String> lastEntries = new ArrayList<>();
        int start = Math.max(0, entries.size() - n);
        for (int i = start; i < entries.size(); i++) {
            lastEntries.add(entries.get(i).toString());
        }
        return lastEntries;
    }

    public int getEntryCount() {
        return entries.size();
    }

    public List<DiscussionEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    public void clear() {
        entries.clear();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public String getLastEntry() {
        if (isEmpty()) {
            return null;
        }
        return entries.get(entries.size() - 1).toString();
    }

    public List<String> getEntriesByAgent(String agentId) {
        List<String> agentEntries = new ArrayList<>();
        for (DiscussionEntry entry : entries) {
            if (entry.agentId.equals(agentId)) {
                agentEntries.add(entry.toString());
            }
        }
        return agentEntries;
    }


}
