package run.mone.hive.roles;

import run.mone.hive.roles.*;
import run.mone.hive.llm.LLM;

public class RoleFactory {
    
    public static Role createRole(String roleType, LLM llm) {
        return switch (roleType.toLowerCase()) {
            // case "productmanager" -> new ProductManager();
            case "architect" -> new Architect();
            // case "qaengineer" -> new QaEngineer();
            case "writer" -> new Writer("Writer");
            case "teacher" -> new Teacher(null);
            default -> throw new IllegalArgumentException("Unknown role type: " + roleType);
        };
    }
    
    public static boolean isValidRoleType(String roleType) {
        try {
            createRole(roleType, null);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
