package run.mone.agentx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.ReportQueryRequest;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.dto.common.ListResult;
import run.mone.agentx.service.InvokeHistoryService;
import run.mone.hive.bo.CallReportDTO;

import java.util.List;
import java.util.Map;

/**
 * 调用次数上报接口控制器
 * 接收来自各个应用的调用次数上报数据
 * 使用统一的 InvokeHistory 表存储
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {
    
    private final InvokeHistoryService invokeHistoryService;
    
    /**
     * 接收调用次数上报
     * 
     * @param reportDTO 上报数据
     * @return 响应结果
     */
    @PostMapping("/call")
    public Mono<ApiResponse<String>> reportCallCount(@RequestBody CallReportDTO reportDTO) {
        log.debug("Received call count report: appName={}, businessName={}, className={}, methodName={}, inputParams={}", 
                reportDTO.getAppName(), 
                reportDTO.getBusinessName(),
                reportDTO.getClassName(), 
                reportDTO.getMethodName(),
                reportDTO.getInputParams());
        
        return invokeHistoryService.saveReport(reportDTO)
                .map(saved -> ApiResponse.success("Report saved successfully"))
                .onErrorResume(e -> {
                    log.error("Failed to save call count report", e);
                    return Mono.just(ApiResponse.error(500, "Failed to save report: " + e.getMessage()));
                });
    }
    
    /**
     * 批量接收调用次数上报
     * 
     * @param reportList 上报数据列表
     * @return 响应结果
     */
    @PostMapping("/batch")
    public Mono<ApiResponse<String>> batchReportCallCount(@RequestBody List<CallReportDTO> reportList) {
        log.debug("Received batch call count report, size: {}", reportList.size());
        
        return invokeHistoryService.batchSaveReports(reportList)
                .map(count -> ApiResponse.success("Batch report saved successfully, count: " + count))
                .onErrorResume(e -> {
                    log.error("Failed to save batch call count report", e);
                    return Mono.just(ApiResponse.error(500, "Failed to save batch report: " + e.getMessage()));
                });
    }
    
    /**
     * 查询指定应用的调用统计
     * 
     * @param appName 应用名称
     * @return 调用统计列表
     */
    @GetMapping("/app/{appName}")
    public Mono<ApiResponse<List<CallReportDTO>>> getReportsByApp(@PathVariable String appName) {
        return invokeHistoryService.getReportsByApp(appName)
                .collectList()
                .map(ApiResponse::success)
                .onErrorResume(e -> {
                    log.error("Failed to get reports by app: {}", appName, e);
                    return Mono.just(ApiResponse.error(500, "Failed to get reports: " + e.getMessage()));
                });
    }
    
    /**
     * 查询指定业务的调用统计
     * 
     * @param businessName 业务名称
     * @return 调用统计列表
     */
    @GetMapping("/business/{businessName}")
    public Mono<ApiResponse<List<CallReportDTO>>> getReportsByBusiness(@PathVariable String businessName) {
        return invokeHistoryService.getReportsByBusiness(businessName)
                .collectList()
                .map(ApiResponse::success)
                .onErrorResume(e -> {
                    log.error("Failed to get reports by business: {}", businessName, e);
                    return Mono.just(ApiResponse.error(500, "Failed to get reports: " + e.getMessage()));
                });
    }
    
    /**
     * 获取调用统计汇总信息
     * 
     * @return 统计汇总
     */
    @GetMapping("/summary")
    public Mono<ApiResponse<Map<String, Object>>> getSummary() {
        return invokeHistoryService.getSummary()
                .map(ApiResponse::success)
                .onErrorResume(e -> {
                    log.error("Failed to get summary", e);
                    return Mono.just(ApiResponse.error(500, "Failed to get summary: " + e.getMessage()));
                });
    }
    
    /**
     * 分页查询调用上报记录（支持多维度筛选）
     * 
     * 支持的筛选条件：
     * - appName: 应用名称
     * - businessName: 业务名称
     * - className: 类名
     * - methodName: 方法名
     * - type: 类型 (1-agent, 2-mcp, 3-其他)
     * - invokeWay: 调用方式 (1页面, 2接口, 3系统内部, 4调试等)
     * - success: 是否成功
     * - host: 主机名/IP
     * - startTime: 开始时间（时间戳）
     * - endTime: 结束时间（时间戳）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @PostMapping("/list")
    public Mono<ApiResponse<ListResult<CallReportDTO>>> queryReports(@RequestBody ReportQueryRequest request) {
        log.debug("Query reports with conditions: appName={}, businessName={}, className={}, methodName={}, " +
                  "type={}, invokeWay={}, success={}, host={}, startTime={}, endTime={}, page={}, pageSize={}",
                request.getAppName(), request.getBusinessName(), request.getClassName(), request.getMethodName(),
                request.getType(), request.getInvokeWay(), request.getSuccess(), request.getHost(),
                request.getStartTime(), request.getEndTime(), request.getPage(), request.getPageSize());
        
        return invokeHistoryService.queryReportsWithPage(request)
                .map(ApiResponse::success)
                .onErrorResume(e -> {
                    log.error("Failed to query reports", e);
                    return Mono.just(ApiResponse.error(500, "Failed to query reports: " + e.getMessage()));
                });
    }

}

