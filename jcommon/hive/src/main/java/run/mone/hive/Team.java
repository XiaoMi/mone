package run.mone.hive;

import com.google.common.collect.Lists;
import lombok.ToString;
import run.mone.hive.actions.UserRequirement;
import run.mone.hive.context.Context;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.NoMoneyException;
import run.mone.hive.utils.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Slf4j
@Data
@ToString
public class Team {
    private static final String MESSAGE_ROUTE_TO_ALL = "*";
    private static final Path SERDESER_PATH = Path.of("storage");

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
        10, 10, 
        10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50));

    private Environment env;
    private double investment;
    private String idea;

    private Map<String, Role> roles = new HashMap<>();

    public Team(Context context) {
        this.env = new Environment();
        this.env.setEnvContext(context);
        this.investment = 10.0;
    }

    public void hire(List<Role> roles) {
        roles.forEach(it -> {
            this.roles.put(it.getName(), it);
            if (null == it.getLlm()) {
                it.setLlm(this.env.getEnvContext().llm());
            }
        });
        env.addRoles(roles);
    }

    public void hire(Role... roles) {
        List<Role> roleList = Arrays.stream(roles).toList();
        hire(roleList);
    }

    public void invest(double investment) {
        this.investment = investment;
        env.getEnvContext().getCostManager().setMaxBudget(investment);
        log.info("Investment: ${}", investment);
    }

    private void checkBalance() {
        double totalCost = env.getEnvContext().getCostManager().getTotalCost();
        double maxBudget = env.getEnvContext().getCostManager().getMaxBudget();
        if (totalCost >= maxBudget) {
            throw new NoMoneyException(totalCost, String.format("Insufficient funds: %f", maxBudget));
        }
    }

    public void runProject(String idea, String sendFrom, String sendTo) {
        this.idea = idea;
        // Human requirement
        env.publishMessage(Message.builder()
                .sentFrom(sendFrom)
                .role("Human")
                .content(idea)
                .causeBy(UserRequirement.class.getName())
                .sendTo(Lists.newArrayList(sendTo.isEmpty() ? MESSAGE_ROUTE_TO_ALL : sendTo))
                .build());
    }

    public void run(int k) {
        IntStream.range(0, k).forEach(i -> roles.forEach((key, role) -> role.run().join()));
    }

    @Deprecated
    public void startProject(String idea, String sendTo) {
        log.warn("The 'startProject' method is deprecated. Please use 'runProject' instead.");
        runProject(idea, "", sendTo);
    }

    public CompletableFuture<List<Message>> run(int _nRound, String idea, String sendTo, boolean autoArchive) {
        return CompletableFuture.supplyAsync(() -> {
            int nRound = _nRound;
            try {
                if (!idea.isEmpty()) {
                    runProject(idea, "", sendTo);
                }

                while (nRound > 0) {
                    if (env.isIdle()) {
                        log.debug("All roles are idle.");
                        break;
                    }
                    nRound--;
                    checkBalance();
                    log.debug("max round={} left.", nRound);
                }

                if (autoArchive) {
                    env.archive();
                }
                return env.getHistory();
            } catch (Exception e) {
                log.error("Error running team: {}", e.getMessage());
                throw new RuntimeException("Team execution failed", e);
            }
        }, executor);
    }

    public void serialize(Path stgPath) {
        Path teamPath = stgPath == null ? SERDESER_PATH.resolve("team") : stgPath;
        Path teamInfoPath = teamPath.resolve("team.json");

        var serializedData = new HashMap<String, Object>();
        serializedData.put("investment", investment);
        serializedData.put("idea", idea);
        serializedData.put("context", env.getEnvContext().serialize());

        JsonUtils.writeJsonFile(teamInfoPath, serializedData);
    }

    public static Team deserialize(Path stgPath, Context context) throws FileNotFoundException {
        Path teamPath = stgPath == null ? SERDESER_PATH.resolve("team") : stgPath;
        Path teamInfoPath = teamPath.resolve("team.json");

        if (!teamInfoPath.toFile().exists()) {
            throw new FileNotFoundException(
                    "Recovery storage meta file 'team.json' does not exist. Please start a new project.");
        }

        Map<String, Object> teamInfo = JsonUtils.readJsonFile(teamInfoPath);
        Context ctx = context == null ? new Context() : context;
        ctx.deserialize((Map<String, Object>) teamInfo.get("context"));

        Team team = new Team(ctx);
        team.setInvestment((Double) teamInfo.get("investment"));
        team.setIdea((String) teamInfo.get("idea"));

        return team;
    }

    public void publishMessage(Message message) {
        env.publishMessage(message);
    }
}