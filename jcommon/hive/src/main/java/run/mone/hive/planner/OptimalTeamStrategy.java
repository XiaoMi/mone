
package run.mone.hive.planner;

import run.mone.hive.Team;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Role;
import run.mone.hive.roles.RoleFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OptimalTeamStrategy implements PlanningStrategy {

    private final Context context;
    private final LLM llm;
    private final Map<String, List<String>> roleToActionsMap;
    private TeamBuilder teamBuilder;

    public OptimalTeamStrategy(TeamBuilder teamBuilder, Context context, LLM llm) {
        this.teamBuilder = teamBuilder;
        this.context = context;
        this.llm = llm;
        this.roleToActionsMap = new HashMap<>();
        initializeRoleToActionsMap();
    }

    @Override
    public Team planTeam(String requirement) {
        return this.buildTeam(requirement);
    }

    private void initializeRoleToActionsMap() {
        // 为每种角色预设可能需要的actions
        roleToActionsMap.put("engineer", Arrays.asList(
            "writecode", "writetest", "debugerror", "runcode"
        ));
        
        roleToActionsMap.put("productmanager", Arrays.asList(
            "writeprd", "writereview"
        ));
        
        roleToActionsMap.put("architect", Arrays.asList(
            "writedesign", "reviewcode"
        ));
        
        roleToActionsMap.put("qaengineer", Arrays.asList(
            "writetest", "runcode", "debugerror"
        ));
    }
    
    public Team buildTeam(String requirement) {
        // 创建新的team实例
        Team team = new Team(context);
        
        // 分析需求,确定需要的角色
        Set<String> requiredRoles = analyzeRequirement(requirement);
        
        // 为每个需要的角色创建实例
        for (String roleType : requiredRoles) {
            Role role = RoleFactory.createRole(roleType, llm);
            
            // 为角色分配合适的actions
            List<String> actionTypes = roleToActionsMap.get(roleType.toLowerCase());
            if (actionTypes != null) {
                for (String actionType : actionTypes) {
                    if (ActionFactory.isValidActionType(actionType)) {
                        role.getActions().add(ActionFactory.createAction(actionType));
                    }
                }
            }
            
            // 将角色添加到team中
            team.hire(Collections.singletonList(role));
        }

        // 记录team
        teamBuilder.rememberTeam(requirement, team);
        
        return team;
    }
    
    private Set<String> analyzeRequirement(String requirement) {
        // TODO:这里可以使用LLM来分析需求,确定需要哪些角色
        // 简单起见,这里使用一些关键词匹配
        Set<String> roles = new HashSet<>();
        
        requirement = requirement.toLowerCase();
        
        if (requirement.contains("code") || requirement.contains("develop") || 
            requirement.contains("implement")) {
            roles.add("engineer");
        }
        
        if (requirement.contains("design") || requirement.contains("architect")) {
            roles.add("architect");
        }
        
        if (requirement.contains("test") || requirement.contains("quality")) {
            roles.add("qaengineer");
        }
        
        if (requirement.contains("product") || requirement.contains("requirement")) {
            roles.add("productmanager");
        }
        
        // 确保至少有一个角色
        if (roles.isEmpty()) {
            roles.add("engineer"); // 默认添加工程师角色
        }
        
        return roles;
    }
}
