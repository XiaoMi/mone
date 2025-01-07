package run.mone.hive.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;
import run.mone.hive.actions.Action;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.*;
import run.mone.hive.strategy.Planner;
import run.mone.hive.utils.Config;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = {"name"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    protected String name;

    protected String profile;

    protected String goal;

    protected String constraints;

    @JsonIgnore
    protected Planner planner;

    @JsonIgnore
    @ToString.Exclude
    protected List<Action> actions;

    protected Set<String> watchList = new HashSet<>();

    @JsonIgnore
    protected RoleContext rc;

    @JsonIgnore
    protected LLM llm;

    protected Queue<Action> actionQueue = new LinkedList<>();

    @JsonIgnore
    private Environment environment;

    @JsonIgnore
    @Getter
    private Config confg = new Config();

    private Context context;

    // 构造函数
    public Role(String name, String profile, String goal, String constraints) {
        this.name = name;
        this.profile = profile;
        this.goal = goal;
        this.constraints = constraints;
        this.actions = new ArrayList<>();
        this.watchList = new HashSet<>();
        init();
    }

    public Role(String name, String profile) {
        this(name, profile, "", "");
    }

    public Role(String name) {
        this(name, name, "", "");
    }

    public Role(String name, String profile, String goal, String constraints, Consumer<Role> consumer) {
        this.name = name;
        this.profile = profile;
        this.goal = goal;
        this.constraints = constraints;
        this.actions = new ArrayList<>();
        this.watchList = new HashSet<>();
        consumer.accept(this);
        init();
    }

    // 初始化方法
    protected void init() {
        this.rc = new RoleContext(profile);
        this.planner = createPlanner();
    }


    // 观察环境
    protected int observe() {
        log.info("observe");
        this.rc.news.forEach(msg -> this.rc.getMemory().add(msg));
        this.rc.news = new LinkedList<>(this.rc.news.stream().filter(this::isRelevantMessage).toList());
        return this.rc.news.size();
    }

    // 思考下一步行动
    protected int think() {
        log.info("think");
        if (this.observe() == 0) {
            return -1;
        }
        if (this.actions.size() == 1) {
            this.rc.setTodo(this.actions.get(0));
        } else {
            this.rc.setState(rc.getState() + 1);
            this.rc.setTodo(this.actions.get(this.rc.getState()));
        }
        return 1;
    }


    // 判断消息是否相关
    protected boolean isRelevantMessage(Message message) {
        return watchList.contains(message.getCauseBy()) ||
                message.getReceivers().contains(name);
    }


    // 创建规划器
    protected Planner createPlanner() {
        // 子类可以重写此方法提供特定的规划器
        planner = new Planner("");
        planner.setLlm(this.llm);
        return planner;
    }

    // 设置可执行的动作
    public Role setActions(List<Action> actions) {
        actions.forEach(it -> {
            it.setRole(this);
            it.setLlm(this.getLlm());
        });
        this.actions = actions;
        return this;
    }

    public Role setActions(Action... actions) {
        return setActions(Arrays.stream(actions).toList());
    }


    // 添加要监听的消息类型
    protected void watch(String actionType) {
        watchList.add(actionType);
    }

    protected void watch(List<Class<?>> list) {
        list.forEach(it -> watchList.add(it.getName()));
    }


    // 添加新的方法
    public CompletableFuture<Message> run() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Message message = react().join();
                if (null != message) {
                    //发送给其他订阅者
                    this.environment.publishMessage(message);
                }
                return message;
            } catch (Exception e) {
                log.error("Error in role execution", e);
                throw new RuntimeException(e);
            } finally {
                this.rc.news.clear();
            }
        });
    }

    public CompletableFuture<Message> react() {
        //需要ai来制定计划
        if (this.rc.getReactMode().equals(RoleContext.ReactMode.PLAN_AND_ACT)) {
            this.observe();
            return planAndAct();
        }

        //依次执行每个Action(按顺序)
        int actionsToken = 0;
        Message res = null;
        ActionContext ac = new ActionContext();
        if (this.rc.getReactMode().equals(RoleContext.ReactMode.BY_ORDER)) {
            while (actionsToken < this.actions.size()) {
                if (this.think() > 0) {
                    res = this.act(ac).join();
                    actionsToken++;
                } else {
                    break;
                }
            }
        }
        return CompletableFuture.completedFuture(res);
    }


    private CompletableFuture<Message> planAndAct() {
        return CompletableFuture.supplyAsync(() -> {
            if (planner.getPlan().getGoal() == null || planner.getPlan().getGoal().isEmpty()) {
                // Create initial plan and update it until confirmation
                String goal = rc.getMemory().getLastMessage().getContent(); // Retrieve latest user requirement
                planner.updatePlan(goal, 5, 3).join(); // Assuming max 5 tasks and 3 retries
            }

            // Take on tasks until all finished
            while (planner.getCurrentTask() != null) {
                Task task = planner.getCurrentTask();
                log.info("Ready to take on task {}", task);

                // Take on current task
                TaskResult taskResult = actOnTask(task).join();
                log.info("taskResult:{}", taskResult);

                // Process the result, such as reviewing, confirming, plan updating
//                planner.processTaskResult(taskResult).join();

                planner.getPlan().finishCurrentTask();


            }

            Message rsp = planner.getUsefulMemories().get(0); // Return the completed plan as a response
            rc.getMemory().add(rsp); // Add to persistent memory

            return rsp;
        });
    }

    //帮我实现下actOnTask (class)
    public CompletableFuture<TaskResult> actOnTask(Task task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Executing task: {}", task);
                TaskResult result = task.execute(this::executeTask);
                log.info("Task executed successfully: {}", result);
                return result;
            } catch (Exception e) {
                log.error("Error executing task: {}", task, e);
                throw new RuntimeException("Task execution failed", e);
            }
        });
    }

    public TaskResult executeTask(Task task) {
        return new TaskResult("success", true);
    }


    //处理message
    public Message processMessage(Message message) {
        return message;
    }

    protected CompletableFuture<Message> act(ActionContext context) {
        if (rc.getTodo() == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Action currentAction = rc.getTodo();
                ActionReq req = new ActionReq();
                req.setAc(context);
                req.setMemory(rc.getMemory());
                req.setRole(this);
                req.setMessage(rc.getMemory().getLastMessage());
                req.setEnv(this.environment);
                req.setHistory(rc.getMessageList());
                Message result = currentAction.run(req, context).join();

                result = processMessage(result);
                if (result != null) {
                    rc.getMemory().add(result);
                }
                return result;
            } catch (Exception e) {
                if (rc.canRetry()) {
                    rc.incrementRetries();
                    return act(context).join();
                }
                throw new RuntimeException("Action execution failed after retries", e);
            }
        });
    }

    protected void watchActions(List<Class<? extends Action>> actionClasses) {
        actionClasses.forEach(clazz -> watch(clazz.getName()));
    }

    protected void initAction(Action action) {
        action.setLlm(this.llm);
    }

    protected void addTodo(Action action) {
        actionQueue.offer(action);
    }

    protected Action getTodo() {
        return actionQueue.poll();
    }

    protected boolean hasTodo() {
        return !actionQueue.isEmpty();
    }

    public void putMessage(Message message) {
        this.rc.news.offer(message);
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }

    public void sendMessage(Message message) {
        log.info("msg:{}", message.getContent());
    }
}