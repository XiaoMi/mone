package run.mone.hive.common;


import lombok.Builder;

public class Constants {

    public static final int DEFAULT_TIMEOUT = 60; // Default timeout in seconds

    public static final String ROLE = "role";

    public static final String MEMORY = "memory";

    public static final String FROM = "__from__";

    public static final String WORKSPACE_PATH = "workspacePath";

    public static final String TOKEN_USAGE_LABEL_START = "\n<usage>\n";

    public static final String TOKEN_USAGE_LABEL_END = "\n</usage>\n";

    public static final String CLAUDE_AGENT = "claude_agent";


    public static final String META_KEY_PROFILE = "profile";
    public static final String META_KEY_GOAL = "goal";
    public static final String META_KEY_CONSTRAINTS = "constraints";
    public static final String META_KEY_WORKFLOW = "workflow";

}