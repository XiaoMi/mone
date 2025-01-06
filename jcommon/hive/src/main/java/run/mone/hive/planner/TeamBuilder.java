package run.mone.hive.planner;

import run.mone.hive.Team;
import run.mone.hive.actions.ActionFactory;
import run.mone.hive.roles.Role;
import run.mone.hive.roles.RoleFactory;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;

import java.util.*;

public class TeamBuilder {
    
    private PlanningStrategy planningStrategy; // 可以扩展为不同的策略
    private Map<String, Team> teams;
    
    public TeamBuilder(Context context, LLM llm) {
        this.planningStrategy = new OptimalTeamStrategy(this, context, llm);
        this.teams = new HashMap<>();
    }

    public void rememberTeam(String task, Team team) {
        teams.put(task, team);
    }

    public void setPlanningStrategy(PlanningStrategy planningStrategy) {
        this.planningStrategy = planningStrategy;
    }
    
    public Team planTeam(String requirement) {
        if (teams.containsKey(requirement)) {
            return teams.get(requirement);
        }
        return planningStrategy.planTeam(requirement);
    }
    
}
