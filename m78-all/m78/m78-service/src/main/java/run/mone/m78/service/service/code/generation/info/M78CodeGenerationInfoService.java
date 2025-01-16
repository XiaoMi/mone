package run.mone.m78.service.service.code.generation.info;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.xiaomi.data.push.client.HttpClientV6;
import com.xiaomi.mone.tpc.api.service.UserOrgFacade;
import com.xiaomi.mone.tpc.common.param.NullParam;
import com.xiaomi.mone.tpc.common.vo.OrgInfoVo;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import run.mone.m78.service.common.GsonUtils;
import run.mone.m78.service.dao.entity.M78CodeGenerationInfo;
import run.mone.m78.service.dao.mapper.M78CodeGenerationInfoMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/6/12 09:05
 */
@Service
@Slf4j
public class M78CodeGenerationInfoService extends ServiceImpl<M78CodeGenerationInfoMapper, M78CodeGenerationInfo> {

    @Value("${code.statistics.sync.url}")
    private String codeStatisticsSyncUrl;

    @NacosValue("${code.statistics.line.limit}")
    private int codeLineLimit;

    @Value("${server.type}")
    private String serverType;

    @DubboReference(check = false, interfaceClass = UserOrgFacade.class, group = "${ref.tpc.service.group}", version = "1.0", timeout = 2000)
//    @DubboReference(interfaceClass = UserOrgFacade.class, group = "staging", version = "1.0", timeout = 2000)
    private UserOrgFacade userOrgFacade;

    // 用于将统计数据同步到线上数据库的线程池
    private ThreadPoolExecutor dataSyncPool = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    // 创建一个缓存，存储员工所在组织，设置过期时间为3天
    private static final Cache<String, String> USER_ORG = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.DAYS)
            .build();

    /**
     * 上传生成的代码信息到数据库
     *
     * @param codeInfo 代码生成信息对象
     * @return 是否成功保存到数据库
     */
    //上传生成code信息到数据库(class)
    public boolean uploadCodeInfo(M78CodeGenerationInfo codeInfo) {
        // 强制校验单次提交的代码行数是否超过上限
        checkCodeLineLimit(codeInfo);
        // 补全周、日信息
        codeInfo.setDay(getCurrentDateFormatted(codeInfo.getCtime()));
        codeInfo.setWeekOfYear(getCurrentWeekOfYear(codeInfo.getCtime()));
        if("staging".equals(serverType)) {
            // 强制设置type为1，为了区分Athena、mi copilot等等
            codeInfo.setType(1);
        }
        // 补全部门信息
        setTier(codeInfo);
        boolean save = save(codeInfo);
        // 数据双写到线上
        syncDataToOnlineDatabase(codeInfo);
        return save;

    }

    /**
     * 强制校验单次提交的代码行数是否超过上限，如果超过上限，则将代码行数设置为上限值
     */
    private void checkCodeLineLimit(M78CodeGenerationInfo codeInfo) {
        if (codeInfo.getCodeLinesCount() > codeLineLimit) {
            codeInfo.setCodeLinesCount(codeLineLimit);
        }
    }

    /**
     * 使用dataSyncPool将数据同步到线上数据库
     *
     * @param codeInfo 包含要同步的数据的M78CodeGenerationInfo对象
     */
    // 使用dataSyncPool将数据同步到线上数据库
    public void syncDataToOnlineDatabase(M78CodeGenerationInfo codeInfo) {
        // 如果url为空，则不同步
        if (StringUtils.isEmpty(codeStatisticsSyncUrl)) {
            return;
        }
        dataSyncPool.execute(() -> {
            try {

                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");

                HttpClientV6.post(codeStatisticsSyncUrl, GsonUtils.gson.toJson(codeInfo), header, 1000);
            } catch (Exception e) {
                log.error("Error occurred while syncing data to online database", e);
            }
        });
    }

    /**
     * 根据用户名设置M78CodeGenerationInfo对象的层级信息
     *
     * @param info 包含用户名和层级信息的M78CodeGenerationInfo对象
     */
    public void setTier(M78CodeGenerationInfo info) {
        String orgNameByUserName = getOrgNameByUserName(info.getUsername());
        if (StringUtils.isNotEmpty(orgNameByUserName)) {
            String[] split = orgNameByUserName.split("/");
            for (int i = 1; i < split.length; i++) {
                switch (i) {
                    case 1:
                        info.setTier1(split[1]);
                        break;
                    case 2:
                        info.setTier2(split[2]);
                        break;
                    case 3:
                        info.setTier3(split[3]);
                        break;
                    case 4:
                        info.setTier4(split[4]);
                        break;
                    case 5:
                        info.setTier5(split[5]);
                        break;


                }
            }
        }
    }

    /**
     * 根据时间戳获取当前日期，格式为yyyy.MM.dd
     *
     * @param timestamp 时间戳，单位为毫秒
     * @return 格式化后的日期字符串，格式为yyyy.MM.dd
     */
    // 按照时间戳获取当前日期格式2024.07.11
    public String getCurrentDateFormatted(long timestamp) {
        LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    /**
     * 根据时间戳获取当前周在一年中的周数
     *
     * @param timestamp 时间戳，单位为毫秒
     * @return 当前周在一年中的周数
     */
    // 按照时间戳获取当前周，一年中的第几周
    public String getCurrentWeekOfYear(long timestamp) {
        LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return String.valueOf(date.get(WeekFields.of(Locale.getDefault()).weekOfYear()));
    }

    /**
     * 根据ID和用户名删除M78CodeGenerationInfo记录
     *
     * @param id       记录的ID
     * @param username 用户名
     * @return 如果删除成功返回true，否则返回false
     */
    //按id删除M78CodeGenerationInfo和username(class)
    public boolean deleteByIdAndUsername(Long id, String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id).eq("username", username);
        return remove(queryWrapper);
    }

    /**
     * 根据用户名查询用户最后三条生成的M78CodeGenerationInfo记录
     *
     * @param username 用户名
     * @return 用户最后三条生成的M78CodeGenerationInfo记录列表
     */
    //按用户名,查询他最后三条生成的M78CodeGenerationInfo(class)
    public List<M78CodeGenerationInfo> getLastThreeCodeGenerationsByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username)
                .orderBy("utime").limit(3);
        return list(queryWrapper);
    }

    /**
     * 获取最新的三条M78CodeGenerationInfo记录
     *
     * @return 包含最新三条M78CodeGenerationInfo记录的列表
     */
    //获取最新的三条M78CodeGenerationInfo(class)
    public List<M78CodeGenerationInfo> getLatestThreeCodeGenerations() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderBy("utime", true).limit(3);
        return list(queryWrapper);
    }

    /**
     * 删除某个用户下的所有M78CodeGenerationInfo记录
     *
     * @param username 用户名
     * @return 删除操作是否成功
     */
    //删除某个用户下的所有M78CodeGenerationInfo(class)
    public boolean deleteAllByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return remove(queryWrapper);
    }

    /**
     * 按用户名统计用户最近一个月上传的代码数量
     *
     * @param userName 用户名
     * @return 最近一个月上传的代码数量
     */
    //按用户名(userName)统计用户最近一个月上传的代码数量(class)
    public long countUserCodeUploadsLastMonth(String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", userName)
                .ge("utime", LocalDate.now().minusMonths(1));
        return count(queryWrapper);
    }

    /**
     * 获取某个用户生成的总代码行数
     *
     * @param userName 用户名
     * @return 用户生成的总代码行数，如果没有记录则返回0
     */
    //获取某个用户生成的总代码行数(class)
    public long getTotalCodeLinesByUser(String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", userName);
        queryWrapper.select("SUM(code_lines_count) as total_code_lines");
        return Optional.ofNullable(getObj(queryWrapper))
                .map(obj -> ((BigDecimal) obj).longValue())
                .orElse(0L);
    }

    /**
     * 按用户分组，查询每个用户的生成代码的行数，支持分页
     *
     * @param currentPage 当前页码
     * @param pageSize    每页显示的记录数
     * @return 分页后的用户代码行数信息
     */
    //按用户分组,查询每个用户的生成代码的行数,支持分页(class)
    public Page<M78CodeGenerationInfo> getUserCodeLinesGroupedByUser(int currentPage, int pageSize) {
        Page<M78CodeGenerationInfo> p = new Page<>(currentPage, pageSize);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("username", "SUM(code_lines_count) as codeLinesCount");
        queryWrapper.groupBy("username");
        queryWrapper.orderBy("codeLinesCount", false);
        return page(p, queryWrapper);
    }

    /**
     * 根据用户名查询用户今天生成的代码行数
     *
     * @param userName 用户名
     * @return 用户今天生成的代码行数，如果没有记录则返回0
     */
    //根据用户查询用户今天生成的代码行数(class)
    public long getTodayCodeLinesByUser(String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", userName)
                .ge("utime", LocalDate.now().atStartOfDay());
        queryWrapper.select("SUM(code_lines_count) as today_code_lines");
        return Optional.ofNullable(getObj(queryWrapper))
                .map(obj -> ((BigDecimal) obj).longValue())
                .orElse(0L);
    }

    /**
     * 根据用户名获取组织名称
     *
     * @param userName 用户名
     * @return 组织名称，如果未找到则返回null
     */
    public String getOrgNameByUserName(String userName) {
        String orgLocal = USER_ORG.getIfPresent(userName);
        if (StringUtils.isEmpty(orgLocal)) {
            NullParam nullParam = new NullParam();
            nullParam.setAccount(userName);
            nullParam.setUserType(0);
            Result<OrgInfoVo> orgByAccount = userOrgFacade.getOrgByAccount(nullParam);
            if (orgByAccount != null && 0 == orgByAccount.getCode()) {
                OrgInfoVo data = orgByAccount.getData();
                if (data != null) {
                    String namePath = data.getNamePath();
                    USER_ORG.put(userName, namePath);
                    return namePath;
                }
            }
        } else {
            return orgLocal;
        }
        return null;
    }

    /**
     * 按照key删除Cache中的指定值，如果key为空，则删除所有值
     *
     * @param key 要删除的缓存项的键，如果为空则删除所有缓存项
     */
    // 按照key删除Cache中的指定值，如果key为空，则删除所有值
    public void removeCacheByKey(String key) {
        if (StringUtils.isEmpty(key)) {
            USER_ORG.invalidateAll();
        } else {
            USER_ORG.invalidate(key);
        }
    }


}
