package run.mone.m78.service.service.task;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.expression.FieldExpression;
import com.google.gson.Gson;
import com.mybatisflex.core.query.QueryWrapper;
import com.xiaomi.data.push.common.ResultParseEnum;
import com.xiaomi.data.push.common.ScheduleModeEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.local.docean.protobuf.Bo;
import run.mone.m78.service.bo.task.CustomTaskBO;
import run.mone.m78.service.bo.task.TaskDetail;
import run.mone.m78.service.dao.entity.CustomTaskPo;
import run.mone.m78.service.dao.mapper.CustomTaskMapper;
import run.mone.m78.service.service.bot.BotService;
import run.mone.moon.api.bo.common.Result;
import run.mone.moon.api.bo.task.DubboParam;
import run.mone.moon.api.bo.task.ExecModeEnum;
import run.mone.moon.api.bo.task.TaskReq;
import run.mone.moon.api.bo.task.TaskTypeEnum;
import run.mone.moon.api.bo.user.MoonMoneTpcContext;
import run.mone.moon.api.service.MoonTaskDubboService;

import javax.annotation.Resource;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangping17
 * @date 2024/2/29
 */
@Service
@Slf4j
public class TaskService {

    @DubboReference(interfaceClass = MoonTaskDubboService.class, version = "1.0", group = "${ref.moon.group}")
    private MoonTaskDubboService moonTaskDubboService;

    @Resource
    private CustomTaskMapper customTaskMapper;

    @Resource
    private BotService botService;

    @Value("${ref.moon.group}")
    private String moonGroup;

    private Gson gson = new Gson();

    /**
     * 创建任务并返回任务ID
     *
     * @param customTask 自定义任务对象，包含任务的详细信息
     * @return 创建的任务ID
     */
	public long createTask(CustomTaskBO customTask) {
        if (customTask.getTaskType().equals(0)) {
            //创建cron表达式
            String cron = "%s %s %s %s %s %s";
            String day = null;
            String month = null;
            String week = null;
            if ("day".equals(customTask.getCoreType())) {
                //每天执行
                day = "*";
                month = "*";
                week = "?";
            } else if ("month".equals(customTask.getCoreType())) {
                //每月几号执行
                day = customTask.getTaskDetail().getDay();
                month = "*";
                week = "?";
            } else if ("week".equals(customTask.getCoreType())) {
                //每周几执行
                day = "?";
                month = "*";
                week = customTask.getTaskDetail().getWeek();
            }

            String cronExpression = String.format(cron, StringUtils.isNotEmpty(customTask.getTaskDetail().getSecond()) ? customTask.getTaskDetail().getSecond() : "*"
                    , StringUtils.isNotEmpty(customTask.getTaskDetail().getMinute()) ? customTask.getTaskDetail().getMinute() : "*"
                    , StringUtils.isNotEmpty(customTask.getTaskDetail().getHour()) ? customTask.getTaskDetail().getHour() : "*"
                    , day
                    , month
                    , week);
            customTask.setScheduledTime(cronExpression);
        }

        Long moonId = createMoonTask(customTask.getUserName(), customTask);
        BeanCopier copier = BeanCopier.create(CustomTaskBO.class, CustomTaskPo.class, false);
        CustomTaskPo customTaskPo = new CustomTaskPo();
        copier.copy(customTask, customTaskPo, null);
        customTaskPo.setCtime(System.currentTimeMillis());
        customTaskPo.setUtime(System.currentTimeMillis());
        HashMap<String, Object> taskDetail = new HashMap<>();
        taskDetail.put("second", customTask.getTaskDetail().getSecond());
        taskDetail.put("minute", customTask.getTaskDetail().getMinute());
        taskDetail.put("hour", customTask.getTaskDetail().getHour());
        taskDetail.put("day", customTask.getTaskDetail().getDay());
        taskDetail.put("month", customTask.getTaskDetail().getMonth());
        taskDetail.put("week", customTask.getTaskDetail().getWeek());
        customTaskPo.setTaskDetail(taskDetail);
        customTaskPo.setStatus(1);
        customTaskPo.setMoonId(moonId);
        customTaskMapper.insert(customTaskPo);
        CustomTaskPo lastInsert = customTaskMapper.selectOneByEntityId(customTaskPo);
        return lastInsert.getId();
    }

    /**
     * 更新任务信息
     *
     * @param customTask 自定义任务对象，包含任务的详细信息
     * @return 更新操作是否成功
     */
	public boolean updateTask(CustomTaskBO customTask) {
        if (customTask.getTaskType().equals(0)) {
            //创建cron表达式
            String cron = "%s %s %s %s %s %s";
            String day = null;
            String week = null;
            if (StringUtils.isNotEmpty(customTask.getTaskDetail().getWeek())) {
                week = customTask.getTaskDetail().getWeek();
                day = "?";
            } else {
                week = "?";
                day = customTask.getTaskDetail().getDay();
            }
            String cronExpression = String.format(cron, StringUtils.isNotEmpty(customTask.getTaskDetail().getSecond()) ? customTask.getTaskDetail().getSecond() : "*"
                    , StringUtils.isNotEmpty(customTask.getTaskDetail().getMinute()) ? customTask.getTaskDetail().getMinute() : "*"
                    , StringUtils.isNotEmpty(customTask.getTaskDetail().getHour()) ? customTask.getTaskDetail().getHour() : "*"
                    , day
                    , StringUtils.isNotEmpty(customTask.getTaskDetail().getMonth()) ? customTask.getTaskDetail().getMonth() : "*"
                    , week);
            customTask.setScheduledTime(cronExpression);
        }
        updateMoonTask(customTask.getUserName(), customTask);
        BeanCopier copier = BeanCopier.create(CustomTaskBO.class, CustomTaskPo.class, false);
        CustomTaskPo customTaskPo = new CustomTaskPo();
        copier.copy(customTask, customTaskPo, null);
        customTaskPo.setUtime(System.currentTimeMillis());
        HashMap<String, Object> taskDetail = new HashMap<>();
        taskDetail.put("second", customTask.getTaskDetail().getSecond());
        taskDetail.put("minute", customTask.getTaskDetail().getMinute());
        taskDetail.put("hour", customTask.getTaskDetail().getHour());
        taskDetail.put("day", customTask.getTaskDetail().getDay());
        taskDetail.put("month", customTask.getTaskDetail().getMonth());
        taskDetail.put("week", customTask.getTaskDetail().getWeek());
        customTaskPo.setTaskDetail(taskDetail);
        customTaskMapper.update(customTaskPo);
        return true;
    }

    /**
     * 删除指定的任务
     *
     * @param task 要删除的任务对象，包含任务的用户名称和任务ID
     * @return 如果删除成功返回true，否则返回false
     */
	public boolean deleteTask(CustomTaskBO task) {
        deleteMoonTask(task.getUserName(), task.getId());
        return customTaskMapper.deleteById(task.getId()) > 0;
    }

    /**
     * 查询任务列表
     *
     * @param customTask 包含查询条件的CustomTaskBO对象
     * @return 查询到的CustomTaskBO列表，如果没有查询到结果则返回null
     */
	public List<CustomTaskBO> queryTask(CustomTaskBO customTask) {
        QueryWrapper queryWrapper = new QueryWrapper().eq("bot_id", customTask.getBotId());//.eq("user_name", customTask.getUserName());
        List<CustomTaskPo> list = customTaskMapper.selectListByQuery(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<CustomTaskBO> customTaskList = new ArrayList<>();
        list.forEach(customTaskPo -> {
            CustomTaskBO customTaskBo = new CustomTaskBO();
            BeanCopier copier = BeanCopier.create(CustomTaskPo.class, CustomTaskBO.class, false);
            copier.copy(customTaskPo, customTaskBo, null);
            TaskDetail taskDetail = new TaskDetail();
            taskDetail.setSecond(String.valueOf(customTaskPo.getTaskDetail().get("second")));
            taskDetail.setMinute(String.valueOf(customTaskPo.getTaskDetail().get("minute")));
            taskDetail.setHour(String.valueOf(customTaskPo.getTaskDetail().get("hour")));
            taskDetail.setDay(String.valueOf(customTaskPo.getTaskDetail().get("day")));
            taskDetail.setMonth(String.valueOf(customTaskPo.getTaskDetail().get("month")));
            taskDetail.setWeek(String.valueOf(customTaskPo.getTaskDetail().get("week")));
            customTaskBo.setTaskDetail(taskDetail);
            customTaskList.add(customTaskBo);
        });
        return customTaskList;
    }

    /**
     * 执行指定的自定义任务
     *
     * @param customTask 自定义任务对象，包含任务的详细信息
     * @return 任务执行结果的数据
     */
	public String executeTask(CustomTaskBO customTask) {
        log.info("执行任务:{}", customTask);
        CustomTaskPo customTaskPo = customTaskMapper.selectOneById(customTask.getId());
        com.xiaomi.youpin.infra.rpc.Result<String> result = botService.executeBot(null, customTaskPo.getBotId(),customTaskPo.getInput(),customTaskPo.getUserName(),"1");
        return result.getData();
    }

    /**
     * 暂停指定的任务
     *
     * @param customTask 要暂停的任务对象
     * @return 是否成功暂停任务
     */
	public Boolean disableTask(CustomTaskBO customTask) {
        log.info("暂停任务:{}", customTask);
        return enableMoonTask(customTask.getUserName(), customTask.getId());
    }

    private String buildCron() {
        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))
                .withDoM(FieldExpression.questionMark())
                .withMonth(FieldExpression.always())
                .withDoW(FieldExpression.always())
                .withHour(FieldExpression.always())
                .withMinute(FieldExpression.always())
                .instance();
        return cron.asString();
    }

    private Long createMoonTask(String userName, CustomTaskBO taskBO) {
        MoonMoneTpcContext context = new MoonMoneTpcContext();
        context.setTenant("1");
        context.setAccount(userName);
        context.setUserType(0);
        TaskReq taskReq = new TaskReq();
        taskReq.setName(taskBO.getTaskName());
        //todo moon权限用到projectID，暂时写死m78项目
        taskReq.setProjectID(360820L);
        taskReq.setType(TaskTypeEnum.Dubbo.type);
        if (taskBO.getTaskType().equals(0)) {
            taskReq.setScheduleMode(ScheduleModeEnum.Cron.mode);
        } else if (taskBO.getTaskType().equals(1)) {
            taskReq.setScheduleMode(ScheduleModeEnum.FixRate.mode);
        } else {
            taskReq.setScheduleMode(ScheduleModeEnum.SingleShot.mode);
            taskReq.setScheduleParam(convertLongTime(taskBO.getScheduledTime()));
        }
        taskReq.setScheduleParam(taskBO.getScheduledTime()); // 可替换为下面的
        DubboParam dubboParam = new DubboParam();
        dubboParam.setServiceName("run.mone.m78.api.IMRecordProvider");
        dubboParam.setGroup(moonGroup);
        dubboParam.setVersion("1.0");
        dubboParam.setMethodName("executeBot");
        dubboParam.setParameterTypes("[\"java.lang.String\",\"java.lang.Long\",\"java.lang.String\",\"java.lang.String\"]");
        dubboParam.setRetries(0);
        dubboParam.setResponseParseMode(ResultParseEnum.None.id);
        dubboParam.setResponseParseParams("0");
        taskReq.setDubboParam(dubboParam);
        List<String> params = List.of(taskBO.getUserName(), String.valueOf(taskBO.getBotId()), taskBO.getInput(), "1");
        taskReq.setExecParam(gson.toJson(params));
        taskReq.setExecMode(ExecModeEnum.SINGLE.mode);
        taskReq.setAlertConfig("30");
        Long id = moonTaskDubboService.create(context, taskReq).getData();
        Result  result = moonTaskDubboService.enable(context, Arrays.asList(id));
        return id;
    }

    private Boolean updateMoonTask(String userName, CustomTaskBO customTaskBO) {
        MoonMoneTpcContext context = new MoonMoneTpcContext();
        context.setTenant("1");
        context.setAccount(userName);
        context.setUserType(0);
        List<CustomTaskPo> list = customTaskMapper.selectListByIds(Arrays.asList(customTaskBO.getId()));
        TaskReq taskReq = moonTaskDubboService.get(list.get(0).getMoonId()).getData();
        if (customTaskBO.getTaskType().equals(0)) {
            taskReq.setScheduleMode(ScheduleModeEnum.Cron.mode);
        } else if (customTaskBO.getTaskType().equals(1)) {
            taskReq.setScheduleMode(ScheduleModeEnum.FixRate.mode);
        } else {
            taskReq.setScheduleMode(ScheduleModeEnum.SingleShot.mode);
            taskReq.setScheduleParam(convertLongTime(customTaskBO.getScheduledTime()));
        }
        taskReq.setScheduleParam(customTaskBO.getScheduledTime());
        List<String> params = List.of(customTaskBO.getUserName(), String.valueOf(customTaskBO.getBotId()), customTaskBO.getInput(), "1");
        taskReq.setExecParam(gson.toJson(params));
        Result result = moonTaskDubboService.update(context, taskReq);
        return true;
    }

    private Boolean deleteMoonTask(String userName, Long id) {
        MoonMoneTpcContext context = new MoonMoneTpcContext();
        context.setTenant("1");
        context.setAccount(userName);
        context.setUserType(0);
        List<CustomTaskPo> list = customTaskMapper.selectListByIds(Arrays.asList(id));
        Result result = moonTaskDubboService.delete(context, Arrays.asList(list.get(0).getMoonId()));
        return true;
    }

    private Boolean enableMoonTask(String userName, Long id) {
        List<CustomTaskPo> list = customTaskMapper.selectListByIds(Arrays.asList(id));
        Boolean res = true;
        synchronized (list.get(0).getId()) {
            try {
                if (list.get(0).getStatus().equals(1)) {
                    //暂停任务
                    MoonMoneTpcContext context = new MoonMoneTpcContext();
                    context.setTenant("1");
                    context.setAccount(userName);
                    context.setUserType(0);
                    Result result = moonTaskDubboService.disable(context, Arrays.asList(list.get(0).getMoonId()));
                    CustomTaskPo customTaskPO = list.get(0);
                    customTaskPO.setStatus(0);
                    customTaskMapper.update(customTaskPO);
                } else if (list.get(0).getStatus().equals(0)) {
                    //启用任务
                    MoonMoneTpcContext context = new MoonMoneTpcContext();
                    context.setTenant("1");
                    context.setAccount(userName);
                    context.setUserType(0);
                    Result result = moonTaskDubboService.enable(context, Arrays.asList(list.get(0).getMoonId()));
                    CustomTaskPo customTaskPO = list.get(0);
                    customTaskPO.setStatus(1);
                    customTaskMapper.update(customTaskPO);
                }
            } catch (Exception e) {
                log.error("启用任务失败", e);
                res = false;
            }
        }
        return res;
    }

    private String convertLongTime(String dateString) {
        String pattern = "yyyy-MM-dd HH:mm:ss"; // 日期字符串的格式
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date = sdf.parse(dateString);
            // 获取 Date 对象的时间戳（毫秒数）
            return String.valueOf(date.getTime());
        } catch (Exception e) {
            // 解析异常处理
            log.error("日期字符串转时间戳异常", e);
            throw new RuntimeException("日期字符串转时间戳异常:" + dateString);
        }
    }


}
