
package run.mone.hive.utils;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import run.mone.hive.roles.Role;

import java.util.Set;

public class RoleReflectionUtil {

    public static Set<Class<? extends Role>> findAllRoleSubclasses() {
        Reflections reflections = new Reflections("run.mone.hive", new SubTypesScanner());
        return reflections.getSubTypesOf(Role.class);
    }
}
