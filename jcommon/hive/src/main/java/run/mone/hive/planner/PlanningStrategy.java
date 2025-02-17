
package run.mone.hive.planner;

import run.mone.hive.Team;

public interface PlanningStrategy {
    Team planTeam(String requirement);
}
