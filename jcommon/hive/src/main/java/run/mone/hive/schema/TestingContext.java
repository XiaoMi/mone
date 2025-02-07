package run.mone.hive.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Context for managing testing related information
 */
@Data
@Slf4j
public class TestingContext {
    
    // Test requirements and acceptance criteria
    private List<String> requirements;
    
    // Test cases mapped by feature/component
    private Map<String, List<String>> testCases;
    
    // Test results and status
    private Map<String, TestResult> testResults;
    
    // Test environment configuration
    private Map<String, String> environment;
    private Message message;

    public TestingContext() {
        this.requirements = new ArrayList<>();
        this.testCases = new HashMap<>();
        this.testResults = new HashMap<>();
        this.environment = new HashMap<>();
    }
    
    /**
     * Add a test requirement
     */
    public void addRequirement(String requirement) {
        requirements.add(requirement);
        log.debug("Added test requirement: {}", requirement);
    }
    
    /**
     * Add a test case for a specific feature
     */
    public void addTestCase(String feature, String testCase) {
        testCases.computeIfAbsent(feature, k -> new ArrayList<>()).add(testCase);
        log.debug("Added test case for feature {}: {}", feature, testCase);
    }
    
    /**
     * Record test result
     */
    public void recordTestResult(String testCase, TestResult result) {
        testResults.put(testCase, result);
        log.debug("Recorded test result for {}: {}", testCase, result);
    }
    
    /**
     * Set environment variable
     */
    public void setEnvironmentVariable(String key, String value) {
        environment.put(key, value);
        log.debug("Set environment variable {}={}", key, value);
    }
    
    /**
     * Get test summary
     */
    public TestSummary getTestSummary() {
        TestSummary summary = new TestSummary();
        summary.setTotalTests(testResults.size());
        summary.setPassed((int) testResults.values().stream()
            .filter(result -> result.getStatus() == TestStatus.PASSED)
            .count());
        summary.setFailed(testResults.size() - summary.getPassed());
        return summary;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    /**
     * Represents the status of a test
     */
    public enum TestStatus {
        PASSED,
        FAILED,
        SKIPPED,
        BLOCKED
    }
    
    /**
     * Represents the result of a test
     */
    @Data
    public static class TestResult {
        private TestStatus status;
        private String message;
        private long duration;  // in milliseconds
        private String errorDetails;
        
        public TestResult(TestStatus status, String message) {
            this.status = status;
            this.message = message;
        }
    }
    
    /**
     * Summary of test execution
     */
    @Data
    public static class TestSummary {
        private int totalTests;
        private int passed;
        private int failed;
        private double passRate;
        
        public void setFailed(int failed) {
            this.failed = failed;
            updatePassRate();
        }
        
        public void setPassed(int passed) {
            this.passed = passed;
            updatePassRate();
        }
        
        private void updatePassRate() {
            this.passRate = totalTests > 0 ? 
                (double) passed / totalTests * 100 : 0.0;
        }
    }
} 