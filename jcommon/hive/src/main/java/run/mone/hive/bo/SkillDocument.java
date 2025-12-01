package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Override
    public String toString() {
        return "SkillDocument{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
