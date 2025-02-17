package run.mone.hive.strategy;

import lombok.Getter;

@Getter
public enum TaskType {
    DEVELOPMENT("""
        Focus on writing clean, efficient, and well-documented code.
        - Follow coding best practices and design patterns
        - Include proper error handling
        - Write unit tests where appropriate
        - Consider performance implications
        """),

    ANALYSIS("""
        Analyze requirements and technical aspects thoroughly.
        - Identify key requirements and constraints
        - Consider different technical approaches
        - Evaluate pros and cons of each approach
        - Document assumptions and dependencies
        """),

    DESIGN("""
        Create clear and comprehensive design specifications.
        - Define system architecture and components
        - Specify interfaces and data structures
        - Consider scalability and maintainability
        - Document design decisions and rationale
        """),

    TESTING("""
        Develop comprehensive test cases and scenarios.
        - Cover both positive and negative test cases
        - Include edge cases and error conditions
        - Write clear test documentation
        - Consider test data requirements
        """),

    DOCUMENTATION("""
        Create clear and comprehensive documentation.
        - Include setup and configuration instructions
        - Document APIs and interfaces
        - Provide usage examples
        - Keep documentation up-to-date with changes
        """),

    REVIEW("""
        Perform thorough code and design reviews.
        - Check for code quality and best practices
        - Verify requirement implementation
        - Look for potential issues or improvements
        - Provide constructive feedback
        """),

    DEPLOYMENT("""
        Plan and execute deployment procedures.
        - Prepare deployment environment
        - Document deployment steps
        - Consider rollback procedures
        - Verify deployment success
        """);

    private final String guidance;

    TaskType(String guidance) {
        this.guidance = guidance;
    }

    public static TaskType getType(String name) {
        try {
            return TaskType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isValidType(String name) {
        return getType(name) != null;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
} 