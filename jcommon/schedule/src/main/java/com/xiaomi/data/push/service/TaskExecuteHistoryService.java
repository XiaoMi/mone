package com.xiaomi.data.push.service;

import com.alibaba.fastjson.JSON;
import com.xiaomi.data.push.common.PageInfo;
import com.xiaomi.data.push.common.TaskHistoryData;
import com.xiaomi.data.push.dao.mapper.TaskExecuteHistoryMapper;
import com.xiaomi.data.push.dao.model.TaskExecuteHistory;
import com.xiaomi.data.push.dao.model.TaskExecuteHistoryExample;
import com.xiaomi.data.push.dto.TaskExecuteContentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskExecuteHistoryService {


    @Autowired
    private TaskExecuteHistoryMapper taskExecuteHistoryMapper;


    public boolean addHistory(TaskExecuteContentDTO taskExecuteContent) {
        if(taskExecuteContent == null){
            return false;
        }
        if(taskExecuteContent.getTaskId() == null || taskExecuteContent.getTaskId() <=0 ){
            return false;
        }
        TaskExecuteHistory executeHistory = new TaskExecuteHistory();
        executeHistory.setTaskId(taskExecuteContent.getTaskId());
        executeHistory.setContent(substringIfTooLong(taskExecuteContent.getContent()));
        executeHistory.setCreatorName(taskExecuteContent.getUserName());
        executeHistory.setTriggerType(taskExecuteContent.getTriggerType());
        taskExecuteHistoryMapper.insertSelective(executeHistory);
        return true;
    }

    /**
     * 数据库该字段的长度是1000
     * @param content
     * @return
     */
    private String substringIfTooLong(String content){
        if(content == null || content.length() < 900){
            return content;
        }
        try {
            TaskHistoryData historyData = JSON.parseObject(content, TaskHistoryData.class);
            historyData.setMessage(historyData.getMessage().substring(0,Math.min(900, historyData.getMessage().length())));
            return JSON.toJSONString(historyData);
        }catch (Exception e){
            log.error("解析保存的json数据失败", e);
        }
        return content.substring(0,900);
    }

    public PageInfo<TaskExecuteHistory> pageHistory(Integer ps, Integer pn, Integer taskId) {
        PageInfo<TaskExecuteHistory> result = new PageInfo<>(ps,pn);
        if(taskId == null || taskId <=0 ){
            return result;
        }
        TaskExecuteHistoryExample example = new TaskExecuteHistoryExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        int total = taskExecuteHistoryMapper.countByExample(example);
        if(total <=0){
            result.setData(new ArrayList<>());
            return result;
        }
        example.setOrderByClause("created_time desc");
        example.setOffset((pn-1)*ps);
        example.setLimit(ps);
        List<TaskExecuteHistory> taskExecuteHistories = taskExecuteHistoryMapper.selectByExample(example);
        result.setTotal(total);
        result.setData(taskExecuteHistories);
        return result;
    }
}
