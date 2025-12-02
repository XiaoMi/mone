package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.InvokeHistory;
import run.mone.agentx.repository.InvokeHistoryRepository;
import run.mone.hive.bo.CallReportDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvokeHistoryService {
    private final InvokeHistoryRepository invokeHistoryRepository;

    public Mono<InvokeHistory> createInvokeHistory(InvokeHistory invokeHistory) {
        invokeHistory.setInvokeTime(System.currentTimeMillis());
        invokeHistory.setCtime(System.currentTimeMillis());
        invokeHistory.setUtime(System.currentTimeMillis());
        invokeHistory.setState(1);
        return invokeHistoryRepository.save(invokeHistory);
    }

    public Flux<InvokeHistory> findByType(Integer type) {
        return invokeHistoryRepository.findByType(type);
    }

    public Flux<InvokeHistory> findByRelateId(Long relateId) {
        return invokeHistoryRepository.findByRelateId(relateId);
    }

    public Flux<InvokeHistory> findByInvokeUserName(String invokeUserName) {
        return invokeHistoryRepository.findByInvokeUserName(invokeUserName);
    }

    public Mono<InvokeHistory> findById(Long id) {
        return invokeHistoryRepository.findById(id);
    }

    public Mono<InvokeHistory> updateInvokeHistory(InvokeHistory invokeHistory) {
        return invokeHistoryRepository.findById(invokeHistory.getId())
                .flatMap(existingHistory -> {
                    existingHistory.setType(invokeHistory.getType());
                    existingHistory.setRelateId(invokeHistory.getRelateId());
                    existingHistory.setInputs(invokeHistory.getInputs());
                    existingHistory.setOutputs(invokeHistory.getOutputs());
                    existingHistory.setInvokeTime(invokeHistory.getInvokeTime());
                    existingHistory.setInvokeWay(invokeHistory.getInvokeWay());
                    existingHistory.setInvokeUserName(invokeHistory.getInvokeUserName());
                    existingHistory.setUtime(System.currentTimeMillis());
                    return invokeHistoryRepository.save(existingHistory);
                });
    }

    public Mono<Void> deleteInvokeHistory(Long id) {
        return invokeHistoryRepository.findById(id)
                .flatMap(history -> {
                    history.setState(0);
                    history.setUtime(System.currentTimeMillis());
                    return invokeHistoryRepository.save(history);
                })
                .then();
    }

    // ==================== 调用次数上报相关方法 ====================
    
    /**
     * 保存调用上报数据
     * type=2 表示API调用上报
     * 
     * @param reportDTO 上报数据
     * @return 保存后的实体
     */
    public Mono<InvokeHistory> saveReport(CallReportDTO reportDTO) {
        InvokeHistory history = convertFromReportDTO(reportDTO);
        return invokeHistoryRepository.save(history);
    }
    
    /**
     * 批量保存上报数据
     * 
     * @param reportList 上报数据列表
     * @return 保存数量
     */
    public Mono<Long> batchSaveReports(List<CallReportDTO> reportList) {
        List<InvokeHistory> histories = reportList.stream()
                .map(this::convertFromReportDTO)
                .toList();
        
        return invokeHistoryRepository.saveAll(histories)
                .count();
    }
    
    /**
     * 根据应用名称查询上报记录
     * 
     * @param appName 应用名称
     * @return 上报记录列表
     */
    public Flux<CallReportDTO> getReportsByApp(String appName) {
        return invokeHistoryRepository.findByAppName(appName)
                .map(this::convertToReportDTO);
    }
    
    /**
     * 根据业务名称查询上报记录
     * 
     * @param businessName 业务名称
     * @return 上报记录列表
     */
    public Flux<CallReportDTO> getReportsByBusiness(String businessName) {
        return invokeHistoryRepository.findByBusinessName(businessName)
                .map(this::convertToReportDTO);
    }
    
    /**
     * 获取统计汇总信息
     * 
     * @return 统计汇总
     */
    public Mono<Map<String, Object>> getSummary() {
        return invokeHistoryRepository.count()
                .flatMap(totalCount -> 
                    invokeHistoryRepository.findAll()
                            .filter(h -> h.getType() != null && h.getType() == 2) // 只统计调用上报类型
                            .reduce(new HashMap<String, Object>(), (map, history) -> {
                                // 统计各应用调用次数（记录条数）
                                if (history.getAppName() != null) {
                                    String appKey = "app_" + history.getAppName();
                                    long appCount = (long) map.getOrDefault(appKey, 0L);
                                    map.put(appKey, appCount + 1);
                                }
                                
                                // 统计成功/失败次数
                                if (history.getSuccess() != null) {
                                    if (history.getSuccess()) {
                                        long successCount = (long) map.getOrDefault("success_count", 0L);
                                        map.put("success_count", successCount + 1);
                                    } else {
                                        long failCount = (long) map.getOrDefault("fail_count", 0L);
                                        map.put("fail_count", failCount + 1);
                                    }
                                }
                                
                                return map;
                            })
                            .map(map -> {
                                map.put("total_reports", totalCount);
                                return map;
                            })
                );
    }
    
    /**
     * 将CallCountReportDTO转换为InvokeHistory实体
     * 
     * @param dto 上报DTO
     * @return InvokeHistory实体
     */
    private InvokeHistory convertFromReportDTO(CallReportDTO dto) {
        InvokeHistory history = new InvokeHistory();
        history.setType(dto.getType());
        history.setInvokeWay(dto.getInvokeWay());
        history.setAppName(dto.getAppName());
        history.setBusinessName(dto.getBusinessName());
        history.setClassName(dto.getClassName());
        history.setMethodName(dto.getMethodName());
        history.setDescription(dto.getDescription());
        history.setInputs(dto.getInputParams());
        history.setSuccess(dto.getSuccess());
        history.setErrorMessage(dto.getErrorMessage());
        history.setExecutionTime(dto.getExecutionTime());
        history.setHost(dto.getHost());
        history.setInvokeTime(System.currentTimeMillis());
        history.setCtime(System.currentTimeMillis());
        history.setUtime(System.currentTimeMillis());
        history.setState(1);
        return history;
    }
    
    /**
     * 将InvokeHistory实体转换为CallCountReportDTO
     * 
     * @param entity InvokeHistory实体
     * @return CallCountReportDTO
     */
    private CallReportDTO convertToReportDTO(InvokeHistory entity) {
        return CallReportDTO.builder()
                .appName(entity.getAppName())
                .type(entity.getType())
                .invokeWay(entity.getInvokeWay())
                .businessName(entity.getBusinessName())
                .className(entity.getClassName())
                .methodName(entity.getMethodName())
                .description(entity.getDescription())
                .inputParams(entity.getInputs())
                .success(entity.getSuccess())
                .errorMessage(entity.getErrorMessage())
                .executionTime(entity.getExecutionTime())
                .host(entity.getHost())
                .createdAt(entity.getCtime())
                .build();
    }
} 