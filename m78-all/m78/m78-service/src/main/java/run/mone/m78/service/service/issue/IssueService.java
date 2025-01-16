package run.mone.m78.service.service.issue;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78Issue;
import run.mone.m78.service.dao.mapper.M78IssueMapper;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/6/19 11:52
 */
@Service
@Slf4j
public class IssueService extends ServiceImpl<M78IssueMapper, M78Issue> {


    /**
     * 创建一个新的问题
     *
     * @param issue 要创建的问题对象
     * @return 如果保存成功则返回true，否则返回false
     */
	//创建issue(class)
	public boolean createIssue(M78Issue issue) {
		return save(issue);
	}

    /**
     * 更新issue的状态
     *
     * @param issueId issue的ID
     * @param newState 新的状态值
     * @return 如果issue存在并且更新成功，返回true；否则返回false
     */
	//更新issue状态(class)
	public boolean updateIssueStatus(Integer issueId, Integer newState) {
		M78Issue issue = getById(issueId);
		if (issue == null) {
			return false;
		}
		issue.setState(newState);
		return updateById(issue);
	}

    /**
     * 查询状态为1的issue列表
     *
     * @return 状态为1的issue列表
     */
	//查询issue列表,只有状态是1的(class)
	public List<M78Issue> getIssuesWithStateOne() {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("state", 1);
		return list(queryWrapper);
	}

	/**
     * 查询优先级别为High且状态为开放(1)的Issue列表
     *
     * @return 优先级别为High且状态为开放的Issue列表
     */
	//查询优先级别为High的Issue列表,同时状态是1(1代表开放的)(class)
	public List<M78Issue> getHighPriorityOpenIssues() {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("priority", "High").eq("state", 1);
		return list(queryWrapper);
	}


	/**
     * 查询某个用户提交的所有issue
     *
     * @param reporterName 用户名
     * @return 用户提交的所有issue列表
     */
	//查询某个用户提交的所有issue(class)
	public List<M78Issue> getIssuesByReporterName(String reporterName) {
		log.info("Fetching issues reported by: " + reporterName);
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("reporter_name", reporterName);
		return list(queryWrapper);
	}




}
