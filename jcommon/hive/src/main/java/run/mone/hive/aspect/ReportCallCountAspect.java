package run.mone.hive.aspect;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import run.mone.hive.annotation.ReportCallCount;
import run.mone.hive.bo.CallReportDTO;
import run.mone.hive.http.CallCountReportClient;

import java.lang.reflect.Method;
import java.net.InetAddress;

/**
 * 调用次数上报切面
 * 拦截带有@ReportCallCount注解的方法，每次调用独立上报
 * 
 * @author goodjava@qq.com
 * @date 2025/12/02
 */
@Slf4j
@Aspect
public class ReportCallCountAspect {
    
    private static final Gson gson = new Gson();
    
    /**
     * 应用名称
     */
    private String appName = "unknown";

    private Integer type = 2;

    private Integer invokeWay = 2;
    
    /**
     * 主机名
     */
    private String host;
    
    public ReportCallCountAspect() {
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            host = "unknown";
            log.warn("Failed to get hostname", e);
        }
    }
    
    /**
     * 设置应用名称
     * 
     * @param appName 应用名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setInvokeWay(Integer invokeWay) {
        this.invokeWay = invokeWay;
    }
    
    /**
     * 环绕通知，拦截带有@ReportCallCount注解的方法
     * 
     * @param joinPoint 切入点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(run.mone.hive.annotation.ReportCallCount)")
    public Object reportCallCount(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ReportCallCount annotation = method.getAnnotation(ReportCallCount.class);
        
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        
        // 获取方法入参
        Object[] args = joinPoint.getArgs();
        String inputParams = serializeParams(args);
        
        long startTime = System.currentTimeMillis();
        boolean success = true;
        String errorMessage = null;
        Object result = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 构建上报数据 - 每次调用独立上报
            CallReportDTO reportDTO = CallReportDTO.builder()
                    .appName(appName)
                    .type(type)
                    .invokeWay(invokeWay)
                    .businessName(annotation.businessName())
                    .className(className)
                    .methodName(methodName)
                    .description(annotation.description())
                    .inputParams(inputParams)
                    .success(success)
                    .errorMessage(errorMessage)
                    .executionTime(executionTime)
                    .createdAt(System.currentTimeMillis())
                    .host(host)
                    .build();
            
            // 上报调用信息
            if (annotation.async()) {
                CallCountReportClient.reportAsync(reportDTO)
                        .exceptionally(ex -> {
                            log.error("Async report failed for {}.{}", className, methodName, ex);
                            return false;
                        });
            } else {
                CallCountReportClient.report(reportDTO);
            }
        }
    }
    
    /**
     * 序列化方法参数为JSON字符串
     * 
     * @param args 方法参数数组
     * @return JSON字符串
     */
    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        try {
            return gson.toJson(args);
        } catch (Exception e) {
            log.warn("Failed to serialize params", e);
            // 如果序列化失败，返回参数类型信息
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(args[i] != null ? args[i].getClass().getSimpleName() : "null");
            }
            sb.append("]");
            return sb.toString();
        }
    }
}

