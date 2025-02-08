package run.mone.m78.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import run.mone.m78.service.dao.entity.M78Issue;
import run.mone.m78.service.service.issue.IssueService;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author goodjava@qq.com
 * @date 2024/6/26 10:43
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = run.mone.m78.server.M78Bootstrap.class)
public class M78IssueTest {


    @Resource
    private IssueService issueService;


    @Test
    public void testCreateIssue() {
        M78Issue issue = new M78Issue();
        // Set properties on the issue object as needed
        issue.setTitle("Test Issue");
        issue.setDescription("This is a test issue.");
        issue.setState(1);
        issue.setReporterName("zzy");
        issue.setPriority("High");

        boolean result = issueService.createIssue(issue);
        assertTrue(result);
    }

    @Test
    public void testGetIssuesByReporterName() {
        String reporterName = "name";

        // Assuming there is a method to create and save issues for testing
        M78Issue issue1 = new M78Issue();
        issue1.setTitle("Issue 1");
        issue1.setDescription("Description for issue 1");
        issue1.setState(1);
        issue1.setReporterName(reporterName);
        issue1.setPriority("High");
        issueService.createIssue(issue1);

        M78Issue issue2 = new M78Issue();
        issue2.setTitle("Issue 2");
        issue2.setDescription("Description for issue 2");
        issue2.setState(1);
        issue2.setReporterName(reporterName);
        issue2.setPriority("Medium");
        issueService.createIssue(issue2);

        List<M78Issue> issues = issueService.getIssuesByReporterName(reporterName);

        assertNotNull(issues);
        assertEquals(2, issues.size());
        assertTrue(issues.stream().allMatch(issue -> reporterName.equals(issue.getReporterName())));
    }


}
