package run.mone.hive;

import akka.actor.ActorRef;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.context.Context;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.ProjectRepo;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class Environment {
    private final Map<String, ActorRef> roles;
    private final Map<String, CompletableFuture<Message>> pendingResponses;
    private final Queue<Message> messageQueue;
    private boolean running;

    private final Map<Role, Set<String>> memberAddrs = new HashMap<>();

    private String debugHistory = "";  // For debug

    private Context envContext;


    public Environment() {
        this.roles = new ConcurrentHashMap<>();
        this.pendingResponses = new ConcurrentHashMap<>();
        this.messageQueue = new LinkedList<>();
        this.running = false;
    }


    private void addRole(Role role) {
        String roleName = role.getProfile();
        role.setEnvironment(this);
        role.setContext(this.envContext);
        Set<String> set = new HashSet<>();
        set.add(role.getName());
        memberAddrs.put(role, set);
        log.info("Added role: {}", roleName);
    }


    public CompletableFuture<Message> sendMessage(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        String messageId = UUID.randomUUID().toString();
        pendingResponses.put(messageId, future);
        return future;
    }

    public List<Message> getHistory() {
        return new ArrayList<>(messageQueue);
    }

    public void clearHistory() {
        messageQueue.clear();
    }

    public boolean hasRole(String roleName) {
        return roles.containsKey(roleName);
    }

    public ActorRef getRole(String roleName) {
        return roles.get(roleName);
    }

    public Set<String> getRoleNames() {
        return new HashSet<>(roles.keySet());
    }


    public CompletableFuture<Message> startDialogue(String content, String roleFrom, String roleTo) {
        Message message = Message.builder()
                .content(content)
                .role(roleFrom)
                .sendTo(Lists.newArrayList(roleTo))
                .build();
        return sendMessage(message);
    }

    public CompletableFuture<Void> bootstrapProject(String workdir, BaseLLM llm) {
        return CompletableFuture.runAsync(() -> {
            ProjectRepo projectRepo = new ProjectRepo(workdir);
        });
    }

    public void addRoles(List<Role> roleList) {
        roleList.forEach(this::addRole);
    }


    public void publishMessage(Message message) {
        log.debug("publish_message: {}", message.toString());
        boolean found = false;

        // Iterate through all member addresses to find recipients
        for (Map.Entry<Role, Set<String>> entry : memberAddrs.entrySet()) {
            Role role = entry.getKey();
            Set<String> addrs = entry.getValue();

            if (isSendTo(message, addrs)) {
                role.putMessage(message);
                found = true;
            }
        }

        if (!found) {
            log.warn("Message no recipients: {}", message);
        }

        // For debug
        debugHistory += "\n" + message;
    }

    private boolean isSendTo(Message message, Set<String> addrs) {
        if (message.getSendTo() == null || message.getSendTo().isEmpty()) {
            return false;
        }

        for (String addr : message.getSendTo()) {
            if (addrs.contains(addr)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getAddresses(Role role) {
        return memberAddrs.get(role);
    }

    public void setAddresses(Role role, Set<String> addresses) {
        memberAddrs.put(role, addresses);
    }


    public void archive() {

    }

    public boolean isIdle() {
        return false;
    }
}