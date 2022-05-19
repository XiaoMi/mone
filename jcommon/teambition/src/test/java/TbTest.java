import com.xiaomi.youpin.teambition.Teambition;
import com.xiaomi.youpin.teambition.bo.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class TbTest {

    private Teambition teambition;
    private String appId = "";
    private String appSecret = "";
    private String email = "";// 公司邮箱
    private String tenantId = "";
    private String orgId = "";
    private String taskId = "";
    private String operationId = "";
    private String projectId = "";
    private String taskGroupId = "";

    @Before
    public void init() {
        teambition = new Teambition(appId,appSecret);
    }

    @Test
    public void genAppToken() {
        String appToken = teambition.genAppToken(appId,appSecret);
        System.out.println(appToken);
    }
    @Test
    public void qryTask() {
        TaskParam param = new TaskParam();
        param.setCondition("creator");
        param.setUserId(teambition.getUserId(email, tenantId));
        param.setOrganizationId(orgId);
        BaseRst<List<TaskInfo>> taskInfos2 = teambition.qryTaskList(param,tenantId);
        System.out.println(taskInfos2);
    }
    @Test
    public void qryTaskList() {
        String userId = teambition.getUserId(email, tenantId);
        TqlParam param = new TqlParam();
        param.setUserId(userId);
        BaseRst<List<TaskInfo>> rst = teambition.qryTaskListByTql(tenantId, 10, "", "dueDate",param);
        System.out.println(rst.getResult().size());
        System.out.println(rst.getResult());
    }
    @Test
    public void updateTask() {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskId(taskId);
        taskInfo.setOperatorId(operationId);
        taskInfo.setAccomplishDate("2021-08-26T15:10:00Z");
        taskInfo.setContent("test tb任务完成 new1510");
        taskInfo = teambition.updateTask(taskInfo, tenantId);
        System.out.println(taskInfo);
    }
    @Test
    public void getProjectsByUserId() {

        String userId = teambition.getUserId(email, tenantId);
        ProjectParam param = new ProjectParam();
        param.setUserId(userId);
        param.setPageToken("1");
        param.setPageSize(1);
        BaseRst<List<ProjectInfo>> list = teambition.getProjectsByUserId(param, tenantId);
        System.out.println(list);
    }

    @Test
    public void getUserId() {
        String userId = teambition.getUserId(email, tenantId);
        System.out.println(userId);
    }
    @Test
    public void getFlowId() {
        TaskFlow taskFlow = teambition.qryTaskFlow(projectId, tenantId);
        System.out.println(taskFlow);
    }
    @Test
    public void batchQryUserinfo() {
        List<UserInfo> userInfos = teambition.batchQryUserinfo(Collections.singletonList(projectId), tenantId);
        System.out.println(userInfos);
    }

    @Test
    public void createTask(){
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setContent("test1109-03");
        taskInfo.setExecutorId(teambition.getUserId(email, tenantId));
        taskInfo.setOperatorId(teambition.getUserId(email, tenantId));
        taskInfo.setProjectId(projectId);
        taskInfo.setTasklistId(taskId);
        taskInfo.setTaskgroupId(taskGroupId);
        taskInfo.setStartDate("2021-11-09T10:30:00Z");
        taskInfo.setDueDate("2021-11-10T10:30:00Z");
        teambition.createTask(taskInfo, tenantId);
    }


    @Test
    public void getMemberListByProjectId(){
        teambition.getMemberListByProjectId(projectId, tenantId, "", 0);
    }
}
