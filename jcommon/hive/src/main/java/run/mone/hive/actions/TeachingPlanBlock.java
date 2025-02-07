package run.mone.hive.actions;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class TeachingPlanBlock {

    public static final List<String> TOPICS = Arrays.asList(
        "COURSE_TITLE",
        "COURSE_OBJECTIVES", 
        "COURSE_CONTENT",
        "TEACHING_METHODS",
        "ASSESSMENT_METHODS",
        "RESOURCES_NEEDED"
    );

    private String topic;
    private String content;
    
}