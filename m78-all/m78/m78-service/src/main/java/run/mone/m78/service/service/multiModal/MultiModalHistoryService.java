package run.mone.m78.service.service.multiModal;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.stereotype.Service;
import run.mone.m78.api.bo.ListResult;
import run.mone.m78.api.bo.multiModal.M78MultiModalHistoryInfo;
import run.mone.m78.api.bo.multiModal.WanxTaskNotifyReq;
import run.mone.m78.api.bo.multiModal.image.HistoryQryReq;
import run.mone.m78.service.dao.entity.M78MultiModalHistoryPo;
import run.mone.m78.service.dao.mapper.M78MultiModalHistoryMapper;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static run.mone.m78.service.exceptions.ExCodes.*;

/**
 * @author wmin
 * @date 2024/7/25
 */
@Service
@Slf4j
public class MultiModalHistoryService extends ServiceImpl<M78MultiModalHistoryMapper, M78MultiModalHistoryPo> {

	@Resource
	private M78MultiModalHistoryMapper historyMapper;

	@Resource
	private Redis redis;

	private static final ScheduledExecutorService STATUS_SCAN_POOL_EXECUTOR =
			new ScheduledThreadPoolExecutor(
					10,
					new BasicThreadFactory.Builder().namingPattern("multiModal-status-scan-pool-%d").build(),
					new ThreadPoolExecutor.DiscardPolicy());

	private final ConcurrentHashMap<Long, Future<?>> statusScanFutureMap = new ConcurrentHashMap<>();

	private long delayMinutes = 15;

	/**
     * 插入一条新的M78MultiModalHistory记录，并根据需要提交延迟定时任务检查运行状态。
     *
     * @param po M78MultiModalHistoryPo对象，包含要插入的记录信息
     * @param runStatusScanRequired 是否需要提交延迟定时任务检查运行状态
     * @return 包含插入记录ID的Result对象，如果插入失败则返回错误信息
     */
	//insert，入参为M78MultiModalHistoryInfo，出参为Result<Integer>
	public Result<Integer> insert(M78MultiModalHistoryPo po, boolean runStatusScanRequired) {
        po.setCtime(System.currentTimeMillis());
        po.setUtime(System.currentTimeMillis());
		boolean isSaved = save(po);
		if (isSaved) {
			//提交延迟定时任务，15min后执行，检查该记录的runStatus如果仍为运行中，则置为失败，message置为'超时'
			if (runStatusScanRequired){
				Future<?> future = STATUS_SCAN_POOL_EXECUTOR.schedule(() -> {
					M78MultiModalHistoryPo historyPo = historyMapper.selectOneById(po.getId());
					if (historyPo != null && historyPo.getRunStatus() == 0) {
						M78MultiModalHistoryPo poToUpdate = new M78MultiModalHistoryPo();
						poToUpdate.setId(po.getId());
						poToUpdate.setUtime(System.currentTimeMillis());
						poToUpdate.setRstMessage("Timeout Failure");
						poToUpdate.setRunStatus(2);
						log.info("STATUS_SCAN done.id:{}, rst:{}", po.getId(), historyMapper.insertOrUpdateSelective(poToUpdate));
					}
				}, delayMinutes, TimeUnit.MINUTES);
				statusScanFutureMap.put(po.getId(), future);
			}
			return Result.success(po.getId().intValue());
		} else {
            return Result.fail(STATUS_INTERNAL_ERROR, "Failed to insert new history in the database");
		}
	}

	/**
     * 更新M78MultiModalHistoryPo对象
     *
     * @param po 要更新的M78MultiModalHistoryPo对象
     * @return 更新结果，成功返回Result.success(true)，失败返回相应的错误信息
     */
	//update, 入参为入参为M78MultiModalHistoryInfo， 出参为Result<Boolean>
	public Result<Boolean> update(M78MultiModalHistoryPo po) {
	    if (po == null || po.getId() == null) {
	        return Result.fail(STATUS_BAD_REQUEST, "Invalid input parameters");
	    }

		M78MultiModalHistoryPo existingPo = getPoById(po.getId());
	    if (existingPo == null) {
	        return Result.fail(STATUS_NOT_FOUND, "No history found with the given id");
	    }

		po.setUtime(System.currentTimeMillis());
	    boolean isUpdated = updateById(po);

	    if (isUpdated) {
	        return Result.success(true);
	    } else {
	        return Result.fail(STATUS_INTERNAL_ERROR, "Failed to update history in the database");
	    }
	}

	/**
     * 根据id查询M78MultiModalHistoryInfo，查询条件是未删除
     *
     * @param id 要查询的M78MultiModalHistoryInfo的id
     * @return 包含查询结果的Result对象，如果未找到则返回失败信息
     */
	//根据id查询M78MultiModalHistoryInfo，查询条件是未删除
	public Result<M78MultiModalHistoryInfo> getById(Long id) {
		M78MultiModalHistoryPo po = getPoById(id);
		if (po == null) {
			return Result.fail(STATUS_NOT_FOUND, "No history found with the given id");
		}
		M78MultiModalHistoryInfo info = convertToInfo(po);
		return Result.success(info);
	}

	private M78MultiModalHistoryPo getPoById(Long id){
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("id", id).eq("deleted", 0);
		return getOne(queryWrapper);
	}

	/**
     * 根据任务ID查询M78MultiModalHistoryInfo
     *
     * @param taskId 任务ID
     * @return 包含M78MultiModalHistoryInfo的Result对象，如果未找到则返回失败的Result对象
     */
	//根据task id查询M78MultiModalHistoryInfo
	public Result<M78MultiModalHistoryInfo> getByTaskId(String taskId) {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("task_id", taskId).eq("deleted", 0);
		M78MultiModalHistoryPo po = getOne(queryWrapper);
		if (po == null) {
			return Result.fail(STATUS_NOT_FOUND, "No history found with the given task id");
		}
		M78MultiModalHistoryInfo info = convertToInfo(po);
		return Result.success(info);
	}

	/**
     * 根据给定的historyIds获取M78MultiModalHistoryInfo列表
     *
     * @param historyIds 历史记录的ID列表
     * @return 包含M78MultiModalHistoryInfo对象的结果列表，如果historyIds为空或没有找到对应的记录，则返回相应的失败或空列表结果
     */
	//根据historyIds获取M78MultiModalHistoryInfo列表，返回结果是List<M78MultiModalHistoryInfo>
	public Result<List<M78MultiModalHistoryInfo>> getHistoryListByIds(List<Long> historyIds) {
	    if (CollectionUtils.isEmpty(historyIds)) {
	        return Result.fail(STATUS_BAD_REQUEST, "History IDs cannot be empty");
	    }

	    List<M78MultiModalHistoryPo> poList = listByIds(historyIds);
	    if (CollectionUtils.isEmpty(poList)) {
	        return Result.success(new ArrayList<>());
	    }

	    List<M78MultiModalHistoryInfo> infoList = poList.stream()
	            .filter(po -> po.getDeleted() == 0)
	            .map(this::convertToInfo)
	            .collect(Collectors.toList());

	    return Result.success(infoList);
	}

	/**
     * 分页查询历史记录列表
     *
     * @param req 查询请求参数，包含分页信息和过滤条件
     * @return 包含历史记录信息的分页结果
     */
	//分页查询list
	public Result<ListResult<M78MultiModalHistoryInfo>> getHistoryList(HistoryQryReq req) {
		ListResult<M78MultiModalHistoryInfo> listResult = new ListResult<>();
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("deleted", 0);

		if (req.getUserName() != null) {
			queryWrapper.eq("user_name", req.getUserName());
		}
		if (req.getRunStatus() != null) {
			queryWrapper.eq("run_status", req.getRunStatus());
		}
		if (req.getType() != null) {
			queryWrapper.eq("type", req.getType());
		}
		Page<M78MultiModalHistoryPo> poPage = super.page(Page.of(req.getPageNum(), req.getPageSize()), queryWrapper.orderBy("ctime", false));
		if (CollectionUtils.isNotEmpty(poPage.getRecords())) {
			List<M78MultiModalHistoryInfo> infoList = poPage.getRecords().stream()
					.map(this::convertToInfo)
					.collect(Collectors.toList());
			listResult.setList(infoList);
		}
		listResult.setTotalPage(poPage.getTotalPage());
		listResult.setPage(poPage.getPageNumber());
		listResult.setPageSize(poPage.getPageSize());
		return Result.success(listResult);
	}

	/**
     * 根据给定的ID列表删除记录，即将deleted字段置为1，并返回成功删除的ID列表
     *
     * @param ids 要删除的记录的ID列表
     * @return 包含成功删除的ID列表的结果对象，如果输入参数无效或未找到记录，则返回相应的失败信息
     */
	//根据ids删除，即将deleted置为1，返回成功删除的ids
	public Result<List<Long>> deleteByIds(List<Long> ids) {
	    if (CollectionUtils.isEmpty(ids)) {
	        return Result.fail(STATUS_BAD_REQUEST, "Invalid input parameters");
	    }

	    List<M78MultiModalHistoryPo> existingPos = listByIds(ids);
	    if (CollectionUtils.isEmpty(existingPos)) {
	        return Result.fail(STATUS_NOT_FOUND, "No history found with the given ids");
	    }

	    List<Long> deletedIds = new ArrayList<>();
	    for (M78MultiModalHistoryPo po : existingPos) {
	        if (po.getDeleted() == 0) {
	            po.setDeleted(1);
	            po.setUtime(System.currentTimeMillis());
	            boolean isUpdated = updateById(po);
	            if (isUpdated) {
	                deletedIds.add(po.getId());
	            }
	        }
	    }

	    if (deletedIds.isEmpty()) {
	        return Result.fail(STATUS_INTERNAL_ERROR, "Failed to delete history in the database");
	    } else {
	        return Result.success(deletedIds);
	    }
	}


	/**
     * 根据任务ID更新记录，首先查询记录是否存在
     *
     * @param notifyReq 包含任务ID和更新信息的请求对象
     * @return 更新结果的封装对象，包含布尔值，表示更新是否成功
     */
	//根据task id更新，先查询是否存在
	public Result<Boolean> notifyByTaskId(WanxTaskNotifyReq notifyReq) {
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.eq("task_id", notifyReq.getTaskId()).eq("deleted", 0);
		M78MultiModalHistoryPo existingPo = getOne(queryWrapper);

		if (existingPo == null) {
			return Result.fail(STATUS_NOT_FOUND, "No history found with the given task id");
		}

		M78MultiModalHistoryPo poToUpdate = new M78MultiModalHistoryPo();
		poToUpdate.setId(existingPo.getId());
		poToUpdate.setUtime(System.currentTimeMillis());
		poToUpdate.setRstMessage(notifyReq.getMessage());
		poToUpdate.setRunStatus(notifyReq.getTaskStatus());
		poToUpdate.setMultiModalResourceOutput(notifyReq.getResultUrl());
		int count = historyMapper.insertOrUpdateSelective(poToUpdate);
		cancelStatusScanTask(existingPo.getId());
		return Result.success(count > 0 ? true : false);
	}

	/**
     * 取消状态扫描任务
     *
     * @param id 任务的唯一标识符
     */
	public void cancelStatusScanTask(Long id) {
		try {
			log.info("cancelStatusScanTask id:{}", id);
			Future<?> future = statusScanFutureMap.get(id);
			if (future != null) {
				future.cancel(true); // 参数false表示不中断正在执行的任务
				statusScanFutureMap.remove(id);
			}
		} catch (Exception e){
			log.error("cancelStatusScanTask error id:{},", id, e);
		}
	}

	//todo 停服时将未完成的扫描任务id集合存到redis中，启动后继续scan


	//M78MultiModalHistoryPo转为M78MultiModalHistoryInfo
	private M78MultiModalHistoryInfo convertToInfo(M78MultiModalHistoryPo po) {
		if (po == null) {
			return null;
		}
		return M78MultiModalHistoryInfo.builder()
				.id(po.getId())
				.workSpaceId(po.getWorkSpaceId())
				.type(po.getType())
				.aiModel(po.getAiModel())
				.deleted(po.getDeleted())
				.runStatus(po.getRunStatus())
				.userName(po.getUserName())
				.ctime(po.getCtime())
				.utime(po.getUtime())
				.multiModalResourceOutput(po.getMultiModalResourceOutput())
				.setting(po.getSetting())
				.rstMessage(po.getRstMessage())
				.build();
	}


    //M78MultiModalHistoryInfo转为M78MultiModalHistoryPo
	private M78MultiModalHistoryPo convertToPo(M78MultiModalHistoryInfo info) {
		if (info == null) {
			return null;
		}
		return M78MultiModalHistoryPo.builder()
				.id(info.getId())
				.workSpaceId(info.getWorkSpaceId())
				.type(info.getType())
				.aiModel(info.getAiModel())
				.deleted(info.getDeleted())
				.runStatus(info.getRunStatus())
				.userName(info.getUserName())
				.ctime(info.getCtime())
				.utime(info.getUtime())
				.multiModalResourceOutput(info.getMultiModalResourceOutput())
				.setting(info.getSetting())
				.build();
	}


}
