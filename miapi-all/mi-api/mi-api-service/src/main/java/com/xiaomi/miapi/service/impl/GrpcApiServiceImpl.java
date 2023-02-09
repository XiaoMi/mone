package com.xiaomi.miapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.gson.Gson;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.xiaomi.miapi.common.Pair;
import com.xiaomi.miapi.mapper.ApiGroupMapper;
import com.xiaomi.miapi.mapper.ApiMapper;
import com.xiaomi.miapi.util.GrpcReflectionCall;
import com.xiaomi.miapi.util.RedisUtil;
import com.xiaomi.miapi.util.ServiceInfoCall;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.bo.BatchAddGrpcApiBo;
import com.xiaomi.miapi.bo.GrpcApiInfosBo;
import com.xiaomi.miapi.bo.GrpcApiParam;
import com.xiaomi.miapi.bo.UpdateGrpcApiBo;
import com.xiaomi.miapi.pojo.Api;
import com.xiaomi.miapi.pojo.ApiCache;
import com.xiaomi.miapi.pojo.ProjectOperationLog;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.mapper.ApiCacheMapper;
import com.xiaomi.miapi.service.GrpcApiService;
import com.xiaomi.miapi.common.exception.CommonError;
import io.grpc.ManagedChannel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
public class GrpcApiServiceImpl implements GrpcApiService {
    @Autowired
    RedisUtil redis;

    @Autowired
    ApiMapper apiMapper;

    @Autowired
    RecordService recordService;

    @Autowired
    ApiServiceImpl apiService;

    @Autowired
    ApiGroupMapper apiGroupMapper;

    @Autowired
    ApiCacheMapper apiCacheMapper;

    @Resource(name = "nacosNamingSt")
    private NacosNaming nacosNamingSt;

    private final GrpcReflectionCall grpcReflectionCall = new GrpcReflectionCall();

    private final ServiceInfoCall serviceInfoCall = new ServiceInfoCall();

    public static final Gson gson = new Gson();

    @Override
    public Result<GrpcApiInfosBo> loadGrpcApiInfos(String appName) throws Exception {
        GrpcApiInfosBo grpcApiInfosBo = new GrpcApiInfosBo();
        List<Instance> instanceList = nacosNamingSt.getAllInstances(appName).stream().filter(Instance::isHealthy).collect(Collectors.toList());
        if (instanceList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        Instance tempInstance = instanceList.get(0);
        ManagedChannel channel = grpcReflectionCall.channel(tempInstance.getIp(), tempInstance.getPort());
        String symbol = tempInstance.getMetadata().get("grpc_symbol");
        if (symbol.isEmpty()) {
            return Result.fail(CommonError.ServiceMustGetGrpcConfig);
        }
        grpcApiInfosBo.setGrpcApiInfos(serviceInfoCall.getServicesAndMethods(channel, symbol));
        grpcApiInfosBo.setIp(tempInstance.getIp());
        grpcApiInfosBo.setPort(tempInstance.getPort());
        grpcApiInfosBo.setSymbol(symbol);
        return Result.success(grpcApiInfosBo);
    }

    @Override
    public Result<String> loadGrpcServerAddr(String appName) throws Exception {
        List<Instance> instanceList = nacosNamingSt.getAllInstances(appName).stream().filter(Instance::isHealthy).collect(Collectors.toList());
        if (instanceList.isEmpty()) {
            return Result.fail(CommonError.ServiceMustRun);
        }
        Instance tempInstance = instanceList.get(0);
        String addr = tempInstance.getIp() + ":" + tempInstance.getPort();
        return Result.success(addr);
    }

    @Override
    public Result<Boolean> batchAddGrpcApi(BatchAddGrpcApiBo grpcApiBo) throws Exception {
        List<Pair<String, Integer>> serviceList = grpcApiBo.getServiceMethods().stream().map(grpcServiceMethod -> new Pair<>(grpcServiceMethod.getServiceName(), grpcServiceMethod.getGroupId())).collect(Collectors.toList());
        ManagedChannel channel = grpcReflectionCall.channel(grpcApiBo.getIp(), grpcApiBo.getPort());

        Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap = serviceInfoCall.getFileDescriptorMap(channel, grpcApiBo.getSymbol());

        for (DescriptorProtos.FileDescriptorProto fileProto : fileDescriptorProtoMap.values()) {
            fileProto.getServiceList().forEach(serviceDescriptorProto -> {
                String fullServiceName = fileProto.getPackage() + "." + serviceDescriptorProto.getName();
                Descriptors.FileDescriptor[] dependencies = getDependencies(fileProto, fileDescriptorProtoMap);
                Descriptors.FileDescriptor fileDescriptor = null;
                try {
                    fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileProto, dependencies);
                } catch (Descriptors.DescriptorValidationException ignored) {
                }

                Optional<Pair<String, Integer>> matchedPair = serviceList.stream().filter(pair -> fullServiceName.equals(pair.getLeft())).findFirst();
                Descriptors.FileDescriptor finalFileDescriptor = fileDescriptor;
                matchedPair.ifPresent(stringIntegerPair -> serviceDescriptorProto.getMethodList().forEach(methodDescriptorProto -> this.addGrpcApi(grpcApiBo, fullServiceName, stringIntegerPair.getRight(), triple(finalFileDescriptor, fullServiceName+"."+methodDescriptorProto.getName()))));
            });
        }
        return Result.success(true);
    }

    @Transactional
    private void addGrpcApi(BatchAddGrpcApiBo grpcApiBo, String fullServiceName, Integer groupId, Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> triple) {
        //唯一关联
        String grpcServicePath = StringUtils.join(new String[]{fullServiceName, triple.getRight().toProto().getName()}, '.');
        Api oldApi = apiMapper.getApiInfoByUrlAndProject(grpcServicePath, 0,grpcApiBo.getProjectID());
        if (Objects.nonNull(oldApi)) {
            Result.fail(CommonError.APIAlreadyExist);
            return;
        }
        Api api = new Api();
        api.setApiEnv(grpcApiBo.getEnv());
        api = apiService.checkAndFillApiInfo(api);
        api.setProjectID(grpcApiBo.getProjectID());
        api.setGroupID(groupId);
        api.setApiRequestType(0);
        api.setApiStatus(0);
        api.setStarred(0);
        api.setApiRequestParamType(0);
        api.setApiResponseParamType(0);
        api.setApiName(triple.getRight().toProto().getName());
        api.setApiProtocol(Consts.GRPC_API_TYPE);
        api.setApiURI(grpcServicePath);
        api.setUpdateUsername(grpcApiBo.getUpdateUserName());
        api.setDubboApiId(0);
        api.setGatewayApiId(0);
        api.setApiNoteType(0);
        api.setApiDesc("");
        api.setApiRemark("");
        int result = apiMapper.addApi(api);
        if (result < 0) {
            Result.fail(CommonError.UnknownError);
            return;
        }
        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("baseInfo", api);
        cache.put("requestInfo", parseGrpcReqParam(triple));
        cache.put("resultInfo", parseGrpcRspParam(triple));
        cache.put("appName",grpcApiBo.getAppName());
        cache.put("errorCodes", StringUtils.EMPTY_STRING);

        ApiCache apiCache = new ApiCache();
        apiCache.setApiID(api.getApiID());
        apiCache.setApiJson(gson.toJson(cache));
        apiCache.setGroupID(api.getGroupID());
        apiCache.setProjectID(api.getProjectID());
        apiCache.setStarred(api.getStarred());
        apiCache.setUpdateUsername(api.getUpdateUsername());
        if (apiCacheMapper.addApiCache(apiCache) < 1) {
            Result.fail(CommonError.UnknownError);
            return;
        }
        String updateMsg = "add grpc api";
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);
        recordService.doRecord(api, null, "添加grpc接口", "添加grpc接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_ADD);
        Result.success(true);
    }

    @Override
    @Transactional
    public Result<Boolean> updateGrpcApi(UpdateGrpcApiBo updateGrpcApiBo) {
        Api api = apiMapper.getApiInfoByUrlAndProject(updateGrpcApiBo.getApiPath(), 0,updateGrpcApiBo.getProjectId());
        if (Objects.isNull(api)) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        api.setApiUpdateTime(new Timestamp(System.currentTimeMillis()));
        api.setUpdateUsername(updateGrpcApiBo.getUpdateUserName());
        api.setApiDesc(updateGrpcApiBo.getApiDesc());
        api.setApiRemark(updateGrpcApiBo.getApiRemark());

        int result = apiMapper.updateApi(api);
        if (result < 0) {
            return Result.fail(CommonError.UnknownError);
        }
        Map<String, Object> cache = new HashMap<String, Object>();
        cache.put("baseInfo", api);
        cache.put("requestInfo", updateGrpcApiBo.getRequestParam());
        cache.put("resultInfo", updateGrpcApiBo.getResponseParam());
        cache.put("appName",updateGrpcApiBo.getAppName());
        cache.put("errorCodes", updateGrpcApiBo.getApiErrorCodes());

        ApiCache apiCache = new ApiCache();
        apiCache.setApiID(api.getApiID());
        apiCache.setApiJson(gson.toJson(cache));
        apiCache.setGroupID(api.getGroupID());
        apiCache.setProjectID(api.getProjectID());
        apiCache.setStarred(api.getStarred());
        apiCache.setUpdateUsername(api.getUpdateUsername());
        if (apiCacheMapper.updateApiCache(apiCache) < 1) {
            return Result.fail(CommonError.UnknownError);
        }

        String updateMsg = "update grpc api";
        if (StringUtils.isNotEmpty(updateGrpcApiBo.getUpdateMsg())) {
            updateMsg = updateGrpcApiBo.getUpdateMsg();
        }
        apiService.recordApiHistory(api, gson.toJson(cache), updateMsg);

        recordService.doRecord(api, null, "更新 grpc 接口", "更新 grpc 接口" + api.getApiName(), ProjectOperationLog.OP_TYPE_UPDATE);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> getGrpcApiDetail(String username, int projectID, int apiID) {
        Map<String, Object> map = new HashMap<>();
        Api api = apiMapper.getApiInfo(projectID, apiID);
        if (null == api) {
            return Result.fail(CommonError.APIDoNotExist);
        }
        if (StringUtils.isEmpty(api.getApiEnv())) {
            map.put("apiEnv", "staging");
        } else {
            map.put("apiEnv", api.getApiEnv());
        }
        String fullServiceName = extraPrefix(api.getApiURI());
        String methodName = extraSuffix(api.getApiURI());
        String serviceName = extraSuffix(fullServiceName);

        map.put("updateUsername", api.getUpdateUsername());
        map.put("projectID", api.getProjectID());
        map.put("groupID", api.getGroupID());
        map.put("apiNoteType", api.getApiNoteType());
        map.put("apiRemark", api.getApiRemark());
        map.put("apiDesc", api.getApiDesc());
        map.put("apiStatus", api.getApiStatus());
        map.put("fullApiPath", api.getApiURI());
        map.put("apiName", api.getApiName());
        map.put("methodName",methodName);
        map.put("serviceName", serviceName);

        String groupName = apiGroupMapper.getGroupByID(api.getGroupID()).getGroupName();
        map.put("groupName", groupName);

        Map<String, Object> result = apiMapper.getApi(projectID, apiID);
        Map<String, Object> apiJson = JSONObject.parseObject(result.get("apiJson").toString());
        if (apiJson != null && !apiJson.isEmpty()){
            map.put("requestInfo", apiJson.get("requestInfo"));
            map.put("resultInfo", apiJson.get("resultInfo"));
            map.put("appName",apiJson.get("appName"));
            map.put("errorCodes", apiJson.get("errorCodes"));
        }
        redis.recordRecently10Apis(username, apiID);
        return Result.success(map);
    }

    private GrpcApiParam parseGrpcReqParam(Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> triple) {
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(triple.getRight().getInputType());

        GrpcApiParam grpcApiParam = new GrpcApiParam();
        if (Objects.nonNull(messageBuilder.getDescriptorForType()) && !messageBuilder.getDescriptorForType().getFullName().isEmpty()) {
            grpcApiParam.setParamKey("arg_0");
            grpcApiParam.setParamType(messageBuilder.getDescriptorForType().getFullName());
            List<GrpcApiParam> childList = new ArrayList<>();
            grpcApiParam.setChildList(childList);
            if (!messageBuilder.getDescriptorForType().getFields().isEmpty()) {
                messageBuilder.getDescriptorForType().getFields().forEach(fieldDescriptor -> {
                    GrpcApiParam childParam = new GrpcApiParam();
                    recursionConstructParam(fieldDescriptor, childParam);
                    childList.add(childParam);
                });
            }
        }
        return grpcApiParam;
    }

    private GrpcApiParam parseGrpcRspParam(Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> triple) {
        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder(triple.getRight().getOutputType());
        GrpcApiParam grpcApiParam = new GrpcApiParam();
        if (Objects.nonNull(messageBuilder.getDescriptorForType()) && !messageBuilder.getDescriptorForType().getFullName().isEmpty()) {
            grpcApiParam.setParamKey("output_0");
            grpcApiParam.setParamType(messageBuilder.getDescriptorForType().getFullName());
            List<GrpcApiParam> childList = new ArrayList<>();
            grpcApiParam.setChildList(childList);
            if (!messageBuilder.getDescriptorForType().getFields().isEmpty()) {
                messageBuilder.getDescriptorForType().getFields().forEach(fieldDescriptor -> {
                    GrpcApiParam childParam = new GrpcApiParam();
                    recursionConstructParam(fieldDescriptor, childParam);
                    childList.add(childParam);
                });
            }
        }
        return grpcApiParam;
    }

    private void recursionConstructParam(Descriptors.FieldDescriptor field, GrpcApiParam param) {
        if (Objects.nonNull(field.getJsonName()) && !field.getJsonName().isEmpty()) {
            param.setParamKey(field.getJsonName());
        } else {
            param.setParamKey(field.getFullName());
        }
        //obj
        if ("MESSAGE".equals(field.getType().name()) && !field.isRepeated()) {
            param.setParamType(field.getMessageType().getFullName());
            List<GrpcApiParam> subParamList = new ArrayList<>();
            if (!field.getMessageType().getFields().isEmpty()) {
                field.getMessageType().getFields().forEach(sField -> {
                    GrpcApiParam subParam = new GrpcApiParam();
                    recursionConstructParam(sField, subParam);
                    subParamList.add(subParam);
                });
                param.setChildList(subParamList);
            }
        } else if (field.isRepeated()) {
            //list
            if ("MESSAGE".equals(field.getType().name())) {
                //obj list
                param.setParamType("[]" + field.getFullName());

                List<GrpcApiParam> subParamList = new ArrayList<>();
                if (!field.getMessageType().getFields().isEmpty()) {
                    field.getMessageType().getFields().forEach(sField -> {
                        GrpcApiParam subParam = new GrpcApiParam();
                        recursionConstructParam(sField, subParam);
                        subParamList.add(subParam);
                    });
                    param.setChildList(subParamList);
                }

                GrpcApiParam subParam = new GrpcApiParam();
                subParam.setParamKey("item");
                subParam.setParamType(field.getType().name());
                subParam.setChildList(subParamList);
                param.setChildList(Collections.singletonList(subParam));
            } else {
                //basic type
                param.setParamType("[]" + field.getType().name());
            }
            GrpcApiParam subParam = new GrpcApiParam();
            subParam.setParamKey("item");
            subParam.setParamType(field.getType().name());
            param.setChildList(Collections.singletonList(subParam));
        } else {
            param.setParamType(field.getType().name());
            param.setParamNotNull(field.isRequired());
            if (field.hasDefaultValue()) {
                param.setParamValue(StringUtils.nullSafeToString(field.getDefaultValue()));
            }
        }
    }

    private Triple<Descriptors.FileDescriptor, Descriptors.ServiceDescriptor, Descriptors.MethodDescriptor> triple(Descriptors.FileDescriptor fileDescriptor, String methodFullName) {
        String fullServiceName = extraPrefix(methodFullName);
        String methodName = extraSuffix(methodFullName);
        String serviceName = extraSuffix(fullServiceName);
        Descriptors.ServiceDescriptor serviceDescriptor = fileDescriptor.getFile().findServiceByName(serviceName);
        Descriptors.MethodDescriptor methodDescriptor = serviceDescriptor.findMethodByName(methodName);
        return Triple.of(fileDescriptor, serviceDescriptor, methodDescriptor);
    }

    private static DescriptorProtos.FileDescriptorProto findServiceFileDescriptorProto(String packageName,
                                                                                       String serviceName,
                                                                                       Map<String, DescriptorProtos.FileDescriptorProto> fileDescriptorProtoMap) {
        for (DescriptorProtos.FileDescriptorProto proto : fileDescriptorProtoMap.values()) {
            if (proto.getPackage().equals(packageName)) {
                boolean exist = proto.getServiceList()
                        .stream()
                        .anyMatch(s -> serviceName.equals(s.getName()));
                if (exist) {
                    return proto;
                }
            }
        }

        throw new IllegalArgumentException("服务不存在");
    }

    private static Descriptors.FileDescriptor[] getDependencies(DescriptorProtos.FileDescriptorProto proto,
                                                                Map<String, DescriptorProtos.FileDescriptorProto> finalDescriptorProtoMap) {
        return proto.getDependencyList()
                .stream()
                .map(finalDescriptorProtoMap::get)
                .map(f -> toFileDescriptor(f, getDependencies(f, finalDescriptorProtoMap)))
                .toArray(Descriptors.FileDescriptor[]::new);
    }

    @SneakyThrows
    private static Descriptors.FileDescriptor toFileDescriptor(DescriptorProtos.FileDescriptorProto fileDescriptorProto,
                                                               Descriptors.FileDescriptor[] dependencies) {
        return Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, dependencies);
    }

    private String extraPrefix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(0, index);
    }

    private String extraSuffix(String content) {
        int index = content.lastIndexOf(".");
        return content.substring(index + 1);
    }
}
