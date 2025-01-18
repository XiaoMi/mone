package run.mone.m78.service.service.model;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;
import run.mone.ai.z.dto.ZModelDTO;
import run.mone.ai.z.dto.ZModelListReq;
import run.mone.ai.z.dto.ZPageList;
import run.mone.ai.z.enums.ModelTypeEnum;
import run.mone.ai.z.service.ZModelDubboService;
import run.mone.m78.api.bo.model.ModelRes;
import run.mone.m78.service.common.Config;
import run.mone.m78.service.common.GsonUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static run.mone.m78.service.exceptions.ExCodes.STATUS_INTERNAL_ERROR;

/**
 * @author wmin
 * @date 2024/1/24
 */
@Component
@Slf4j
public class ModelService {

    private static final String ADMIN = "admin";
    private static final String USER = "user";

    private static final String ONLINE = "online";

    private static final Predicate<ZModelDTO> onlineFilter = zModelDTO -> zModelDTO.getInfo().containsKey("m78-online");

    private static final Map<String, Integer> vendorsOrder = new HashMap<>();

    static {
        vendorsOrder.put("GPT系列模型", 100);
        vendorsOrder.put("Anthropic Claude", 200);
        vendorsOrder.put("Google Gemini", 250);
        vendorsOrder.put("豆包系列模型", 300);
        vendorsOrder.put("Moonshot月之暗面", 400);
        vendorsOrder.put("通义千问", 500);
        vendorsOrder.put("智谱", 600);
        vendorsOrder.put("deepseek", 700);
    }

    private static final Map<String, String> vendorsImage = new HashMap<>();

    static {
        vendorsImage.put("GPT系列模型", "https://test.com/GPT.png");
        vendorsImage.put("Anthropic Claude", "https://test.com/Claude.png");
        vendorsImage.put("Google Gemini", "https://test.com/gemini.png");
        vendorsImage.put("豆包系列模型", "https://test.com/doubao.png");
        vendorsImage.put("Moonshot月之暗面", "https://test.com/moonshot.png");
        vendorsImage.put("通义千问", "https://test.com/tongyi.png");
        vendorsImage.put("智谱", "https://test.com/zhipu.jpeg");
        vendorsImage.put("deepseek", "https://test.com/deepseek.png");
    }

    @Value("${server.type}")
    private String serverType;

    @DubboReference(check = false, interfaceClass = ZModelDubboService.class, group = "${ref.ai.z.service.group}", version = "${ref.ai.z.service.version}")
    private ZModelDubboService zModelDubboService;

    public Result<List<ModelRes>> getModelList() {
        ZModelListReq modelListReq = new ZModelListReq();
        modelListReq.setZuToken(Config.zToken);

        Result<ZPageList<ZModelDTO>> rst = zModelDubboService.list(modelListReq);
        log.info("model list: {}", GsonUtils.gson.toJson(rst));
        if (rst.getCode() != 0) {
            return Result.fail(STATUS_INTERNAL_ERROR, rst.getMessage());
        }

        List<ZModelDTO> list = rst.getData().getData().stream().filter(i -> i.getType().equals(ModelTypeEnum.AzureOpenAI.id) || i.getType().equals(ModelTypeEnum.OpenAI.id) || i.getType().equals(ModelTypeEnum.AWSClaude.id) || i.getType().equals(ModelTypeEnum.GCPClaude.id)).collect(Collectors.toList());
        list = list.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                ZModelDTO::getCname, // 作为键的函数
                                Function.identity(), // 作为值的函数
                                (existing, replacement) -> existing), // 冲突解决函数，这里选择已有的键
                        map -> new ArrayList<>(map.values()) // 转换为 List
                ));

        List<ModelRes> resultList = list.stream().map(i ->
                ModelRes.builder().name(i.getName()).cname(i.getCname()).info(GsonUtils.gson.toJson(i.getInfo())).build()
        ).collect(Collectors.toList());

        // 根据cname属性对ModelRes列表进行排序
        Collections.sort(resultList, new Comparator<ModelRes>() {
            @Override
            public int compare(ModelRes o1, ModelRes o2) {
                return o1.getCname().compareTo(o2.getCname());
            }
        });

        return Result.success(resultList);
    }

    //获取ZModelDTO的list后，按ZModelDTO里的info里是否含有m78-role这个key筛选，筛选后的list按vendors组成一个map，并且map的value是一个List<ModelRes>
    public Result<Map<String, List<ModelRes>>> getModelListV1(List<Predicate<ZModelDTO>> predicates, Boolean isAdmin) {
        if (predicates == null) {
            predicates = new ArrayList<>();
        }

        ZModelListReq modelListReq = new ZModelListReq();
        modelListReq.setZuToken(Config.zToken);

        Result<ZPageList<ZModelDTO>> rst = zModelDubboService.list(modelListReq);
        log.info("model list: {}", GsonUtils.gson.toJson(rst));
        if (rst.getCode() != 0) {
            return Result.fail(STATUS_INTERNAL_ERROR, rst.getMessage());
        }

        //admin能看到更多的模型
        Predicate<ZModelDTO> predicateM78 = zModelDTO -> zModelDTO.getInfo().containsKey("m78-role") && USER.equals(zModelDTO.getInfo().get("m78-role"));
        if (isAdmin) {
            predicateM78 = zModelDTO -> zModelDTO.getInfo().containsKey("m78-role");
        }
        List<ZModelDTO> tmpFilteredList = rst.getData().getData().stream()
                .filter(predicateM78)
                .collect(Collectors.toList());

        if (isOnline()) {
            predicates.add(onlineFilter);
        }

        if (predicates != null && predicates.size() > 0) {
            for (Predicate<ZModelDTO> predicate : predicates) {
                tmpFilteredList = tmpFilteredList.stream()
                        .filter(predicate)
                        .collect(Collectors.toList());
            }
        }

        List<ZModelDTO> filteredList = new ArrayList<>(tmpFilteredList);
        Map<String, List<ModelRes>> resultMap = filteredList.stream()
                .filter(it -> StringUtils.isNotEmpty(it.getVendors()))
                .collect(Collectors.groupingBy(
                        ZModelDTO::getVendors,
                        Collectors.mapping(i -> ModelRes.builder()
                                        .name(i.getName())
                                        .cname(i.getCname())
                                        .info(GsonUtils.gson.toJson(i.getInfo()))
                                        .vendors(i.getVendors())
                                        .description(i.getDescription())
                                        .imageUrl(vendorsImage.get(i.getVendors()))
                                        .build(),
                                Collectors.toList())
                ));

        Map<String, List<ModelRes>> sortedResultMap = new LinkedHashMap<>();
        vendorsOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(entry -> {
                    if (resultMap.containsKey(entry.getKey())) {
                        sortedResultMap.put(entry.getKey(), resultMap.get(entry.getKey()));
                    }
                });

        return Result.success(sortedResultMap);
    }


    private boolean isOnline() {
        return ONLINE.equals(serverType);
    }


}
