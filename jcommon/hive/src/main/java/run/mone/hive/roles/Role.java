package run.mone.hive.roles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.Prompts;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.*;
import run.mone.hive.strategy.Planner;
import run.mone.hive.utils.Config;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    protected String workflow;

    protected String outputFormat;

    @Getter
    protected List<String> specializations;

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
    private Environment environment = new Environment();

    @JsonIgnore
    @Getter
    private Config confg = new Config();

    private Context context;

    private boolean blockingMessageRetrieval;

    protected String prompt;

    //role的配置
    protected Map<String, String> roleConfig = new HashMap<>();

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

    public Role(String name, String profile, String goal, String constraints, List<String> specializations) {
        this.name = name;
        this.profile = profile;
        this.goal = goal;
        this.constraints = constraints;
        this.specializations = specializations;
        this.actions = new ArrayList<>();
        this.watchList = new HashSet<>();
        init();
    }

    // 初始化方法
    protected void init() {
        this.rc = new RoleContext(profile);
        this.planner = createPlanner();
    }


    // 观察环境
    @SneakyThrows
    protected int observe() {
        log.info("observe");
        if (this.blockingMessageRetrieval) {
            Message msg = this.rc.news.poll(2, TimeUnit.MINUTES);
            this.rc.news.put(msg);
            return this.rc.news.size();
        } else {
            this.rc.news.forEach(msg -> this.rc.getMemory().add(msg));
            this.rc.news = new LinkedBlockingQueue<>(this.rc.news.stream().filter(this::isRelevantMessage).toList());
            return this.rc.news.size();
        }
    }

    // 思考下一步行动
    protected int think() {
        log.info("think");
        //观测消息
        if (this.observe() == 0) {
            //没有消息
            return -1;
        }

        //思考模式(让ai选出来用那个action来执行)
        if (this.rc.getReactMode().equals(RoleContext.ReactMode.REACT)) {
            return selectActionBasedOnPrompt();
        }

        //Order模式
        if (this.actions.size() == 1) {
            this.rc.setTodo(this.actions.get(0));
        } else {
            this.rc.setState(rc.getState() + 1);
            this.rc.setTodo(this.actions.get(this.rc.getState()));
        }
        return 1;
    }

    private int selectActionBasedOnPrompt() {
        //获取状态
        String states = IntStream.range(0, this.actions.size()).mapToObj(i -> {
            Action action = this.actions.get(i);
            return "state:%s desc:%s action:%s".formatted(i, action.getDescription(), action.getClass().getName());
        }).collect(Collectors.joining("\n"));

        //获取历史记录
        String history = this.rc.getMessageList().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

        Map<String, String> map = new HashMap<>();
        map.put("profile", this.profile);
        map.put("goal", this.goal);
        map.put("name", this.name);
        map.put("history", history);
        map.put("previous_state", this.rc.getState() + "");
        map.put("states", states);
        map.put("n_states", (this.actions.size() - 1) + "");

        String prompt = AiTemplate.renderTemplate(Prompts.ACTION_SELECTION_PROMPT, map);

        String index = this.llm.chat(prompt);
        if (!index.equals("-1")) {
            int i = Integer.parseInt(index);
            this.rc.setState(i);
            this.rc.setTodo(this.actions.get(this.rc.getState()));
            return 1;
        } else {
            return -1;
        }
    }


    // 判断消息是否相关
    protected boolean isRelevantMessage(Message message) {
        return watchList.contains(message.getCauseBy()) ||
                message.getReceivers().contains(name);
    }

    public boolean isCompatibleWithTask(String task) {
        return specializations.stream().anyMatch(task.toLowerCase()::contains);
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

        //按顺序挨个action去执行
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
        //自己决策用那个action
        if (this.rc.getReactMode().equals(RoleContext.ReactMode.REACT)) {
            //需要使用llm来选择action
            doReact(ac);
        }
        postReact(ac);
        return CompletableFuture.completedFuture(res);
    }


    //执行的最大轮数
    private int doReactNum = 15;

    /**
     * react实际执行的逻辑， 可以重写
     *
     * @param ac
     */
    protected void doReact(ActionContext ac) {
        // 默认最多执行15次, 可以重写这里的逻辑
        int i = 0;
        while (this.think() > 0 && i++ < doReactNum) {
            this.act(ac).join();
        }
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

                planner.getPlan().finishCurrentTask();
            }

            Message rsp = planner.getUsefulMemories().get(0); // Return the completed plan as a response
            rc.getMemory().add(rsp); // Add to persistent memory

            return rsp;
        });
    }

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

    /**
     * 在react之后执行的hook, 可以重写
     *
     * @param ac
     */
    protected void postReact(ActionContext ac) {
        //子类可以重写此方法
    }

    public void putMessage(Message message) {
        if (0 == message.getCreateTime()) {
            message.setCreateTime(System.currentTimeMillis());
        }
        this.rc.news.offer(message);
    }

    public void putMemory(Message message) {
        if (0 == message.getCreateTime()) {
            message.setCreateTime(System.currentTimeMillis());
        }
        this.rc.getMemory().add(message);
    }

    public Message getLastMessage() {
        return this.getRc().getMemory().getStorage().get(this.getRc().getMemory().getStorage().size() - 1);
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }

    public void sendMessage(Message msg) {
        log.info("msg:{}, ", msg);
    }

    /**
     * Clears all messages from the role's memory
     */
    public void clearMemory() {
        if (this.rc != null && this.rc.getMemory() != null) {
            this.rc.getMemory().clear();
        }
    }
}