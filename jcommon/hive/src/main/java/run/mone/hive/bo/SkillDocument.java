package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a skill definition with name, description and location
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDocument {

    /**
     * Skill name (unique identifier)
     */
    private String name;

    /**
     * Skill description explaining what it does
     */
    private String description;

    /**
     * File path or location of the skill definition
     */
    private String location;

    /**
     * Optional: Skill content (the actual XML or definition)
     */
    private String content;

    /**
     * List of all files in the skill directory (absolute paths)
     * Excludes hidden files and directories starting with '.'
     */
    private List<String> files;

    @Override
    public String toString() {
        return "SkillDocument{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
