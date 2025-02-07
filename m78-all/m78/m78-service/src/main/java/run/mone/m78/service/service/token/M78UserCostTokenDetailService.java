package run.mone.m78.service.service.token;

import com.google.gson.Gson;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.client.HttpClientV6;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.service.dao.entity.M78UserCostTokenDetail;
import run.mone.m78.service.dao.mapper.M78UserCostTokenDetailMapper;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 服务层实现。
 *
 * @author hoho
 * @since 2024-09-20
 */
@Service
@Slf4j
public class M78UserCostTokenDetailService extends ServiceImpl<M78UserCostTokenDetailMapper, M78UserCostTokenDetail> {

    @Resource
    private M78UserTokenSumService m78UserTokenSumService;

    private static Gson gson = new Gson();


    @Value("${embedding.server}")
    private String embeddingServer;

    /**
     * 异步添加用户消耗token记录
     * <p>
     * 该方法使用虚拟线程执行器异步处理以下任务：
     * 1. 计算输入和输出的token数量
     * 2. 设置消耗的token数量和当前日期时间
     * 3. 保存用户消耗token详情记录
     * 4. 更新用户的token总和
     * <p>
     * 如果在处理过程中发生异常，将记录错误日志
     *
     * @param entity M78UserCostTokenDetail对象，包含用户消耗token的详细信息
     */
    public void addCostTokenRecord(M78UserCostTokenDetail entity) {
        Executors.newVirtualThreadPerTaskExecutor().submit(() -> {
            try {
                long tokenNum = getTokenNum(entity.getInput() + entity.getOutput());
                entity.setUser(processUsername(entity.getUser()));
                entity.setCostToken(tokenNum);
                entity.setDate(LocalDateTime.now());
                save(entity);
                m78UserTokenSumService.updateTokenSum(entity.getUser(), entity.getCostToken());
            } catch (Exception e) {
                log.error("addCostTokenRecord error:", e);
            }
        });

    }

    /**
     * 统计每个人在指定时间区间内的token使用量
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 每个用户在指定时间区间内的token使用量映射
     */
    //统计每个人区间时间内的token使用量
    public Map<String, Long> calculateTokenUsageByUserInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.between("date", startDate, endDate);

        List<M78UserCostTokenDetail> records = list(queryWrapper);

        return records.stream()
                .collect(Collectors.groupingBy(
                        M78UserCostTokenDetail::getUser,
                        Collectors.summingLong(M78UserCostTokenDetail::getCostToken)
                ));
    }

    //


    private Long getTokenNum(String input) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("content-type", "application/json");
            Map<String, String> post = new HashMap<>();
            post.put("data", input);
            String body = HttpClientV6.post(String.format("%s/token_num", embeddingServer), gson.toJson(post), headers, 100000);
            log.info("getTokenNum {}", body);
            return Long.parseLong(body);
        } catch (Exception e) {
            log.error("getTokenNum {}", input, e);
            return 0L;
        }
    }

    /**
     * 处理用户名，根据特定格式返回处理后的字符串
     *
     * @param username 要处理的用户名
     * @return 如果用户名包含下划线，则返回第一个下划线之前的部分；否则返回原字符串
     */
    //处理username，如果username格式是xxxx_xx_xx，取第一个_之前的字符串。如果不是返回原字符串
    public String processUsername(String username) {
        if (username == null || username.isEmpty()) {
            return username;
        }

        int firstUnderscoreIndex = username.indexOf('_');
        if (firstUnderscoreIndex != -1) {
            return username.substring(0, firstUnderscoreIndex);
        }

        return username;
    }


}
