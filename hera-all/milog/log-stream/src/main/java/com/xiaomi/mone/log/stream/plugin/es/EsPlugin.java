package com.xiaomi.mone.log.stream.plugin.es;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.model.EsInfo;
import com.xiaomi.mone.log.stream.compensate.MqMessageDTO;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.es.EsProcessorConf;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Service
public class EsPlugin {
    private static EsConfig esConfig;

    private static ConcurrentHashMap<String, EsService> esServiceMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, EsProcessor> esProcessorMap = new ConcurrentHashMap<>();

    public static boolean InitEsConfig() {
        EsConfig config = new EsConfig();
        Config ins = Config.ins();
        try {
            config.setBulkActions(Integer.parseInt(ins.get("es.bulk_actions", "100")));
            config.setByteSize(Long.parseLong(ins.get("es.byte_size", "5")));
            config.setConcurrentRequest(Integer.parseInt(ins.get("es.concurrent_request", "10")));
            config.setFlushInterval(Integer.parseInt(ins.get("es.flush_interval", "")));
            config.setRetryNumber(Integer.parseInt(ins.get("es.retry_num", "3")));
            config.setRetryInterval(Integer.parseInt(ins.get("es.retry_interval", "3")));
            log.info("[EsPlugin.getEsProcessor] init es config:{}", config);
        } catch (Exception e) {
            log.error("[EsPlugin.InitEsConfig] init es config err:", e);
            return false;
        }
        EsPlugin.esConfig = config;
        return true;
    }

    public static EsProcessor getEsProcessor(EsInfo esInfo, Consumer<MqMessageDTO> onFailedConsumer) {
        return getEsProcessor(esInfo, EsPlugin.esConfig, onFailedConsumer);
    }

    public static synchronized EsProcessor getEsProcessor(EsInfo esInfo, EsConfig config, Consumer<MqMessageDTO> onFailedConsumer) {
        EsProcessor esProcessor = esProcessorMap.get(cacheKey(esInfo));
        if (esProcessor == null) {
            EsService esService = esServiceMap.get(cacheKey(esInfo));
            if (esService == null) {
                if (StringUtils.isNotBlank(esInfo.getUser()) && StringUtils.isNotBlank(esInfo.getPwd())) {
                    esService = new EsService(esInfo.getAddr(), esInfo.getUser(), esInfo.getPwd());
                } else if (StringUtils.isNotBlank(esInfo.getToken())) {
                    esService = new EsService(esInfo.getAddr(), esInfo.getToken(), esInfo.getCatalog(), esInfo.getDatabase());
                } else {
                    esService = new EsService(esInfo.getAddr(), esInfo.getUser(), esInfo.getPwd());
                }
                esServiceMap.put(cacheKey(esInfo), esService);
            }
            esProcessor = esService.getEsProcessor(new EsProcessorConf(config.getBulkActions(), config.getByteSize(), config.getConcurrentRequest(), config.getFlushInterval(),
                    config.getRetryNumber(), config.getRetryInterval(), new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {
//                            log.info("before send to es,desc:{}", request.getDescription());
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                    log.debug("success send to es,desc:{}", request.getDescription());
                    AtomicInteger count = new AtomicInteger();
                    response.spliterator().forEachRemaining(x -> {
                        if (x.isFailed()) {
                            BulkItemResponse.Failure failure = x.getFailure();
                            String msg = String.format(
                                    "Index:[%s], type:[%s], id:[%s], itemId:[%s], opt:[%s], version:[%s], errMsg:%s"
                                    , x.getIndex()
                                    , x.getType()
                                    , x.getId()
                                    , x.getItemId()
                                    , x.getOpType().getLowercase()
                                    , x.getVersion()
                                    , failure.getCause().getMessage()
                            );
                            log.error("Bulk executionId:[{}] has error messages:\t{}", executionId, msg);
                            count.incrementAndGet();
                        }
                    });
                    log.debug("Finished handling bulk commit executionId:[{}] for {} requests with {} errors", executionId, request.numberOfActions(), count.intValue());
                }

                @Override
                public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                    log.error(String.format("fail send %s message to es,desc:%s,es addr:%s", request.numberOfActions(), request.getDescription(), esInfo.getAddr()), new Exception(failure));
                    Class clazz = failure.getClass();
                    log.error("Bulk [{}] finished with [{}] requests of error:{}, {}, {}:-[{}]", executionId
                            , request.numberOfActions()
                            , clazz.getName()
                            , clazz.getSimpleName()
                            , clazz.getTypeName()
                            , clazz.getCanonicalName()
                            , failure.getMessage());
                    MqMessageDTO MqMessageDTO = new MqMessageDTO();
                    MqMessageDTO.setEsInfo(esInfo);
                    List<MqMessageDTO.CompensateMqDTO> compensateMqDTOS = Lists.newArrayList();
                    String cacheKey = cacheKey(esInfo);
                    Gson gson = new Gson();
                    request.requests().stream().filter(x -> x instanceof IndexRequest)
                            .forEach(x -> {
                                Map source = ((IndexRequest) x).sourceAsMap();
                                log.error("Failure to handle index:[{}], type:[{}],id:[{}] data:[{}]", x.index(), x.type(), x.id(), gson.toJson(source));
                                EsProcessor esProcessor = esProcessorMap.get(cacheKey);
                                esProcessor.bulkInsert(x.index(), source);
                                MqMessageDTO.CompensateMqDTO compensateMqDTO = new MqMessageDTO.CompensateMqDTO();
                                compensateMqDTO.setMsg(gson.toJson(source));
                                compensateMqDTO.setEsIndex(x.index());
                                compensateMqDTOS.add(compensateMqDTO);
                            });
                    MqMessageDTO.setCompensateMqDTOS(compensateMqDTOS);
                    //消息发送到mq中消费
                    //mqMessageDispose.product(MqMessageDTO);
                    onFailedConsumer.accept(MqMessageDTO);
                }
            }));
            esProcessorMap.put(cacheKey(esInfo), esProcessor);
            return esProcessor;
        }
        return esProcessor;
    }

    private static String cacheKey(EsInfo esInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(esInfo.getId()).append(",");
        stringBuilder.append(esInfo.getAddr());
        if (StringUtils.isNotBlank(esInfo.getUser())) {
            stringBuilder.append(",").append(esInfo.getUser());
        }
        if (StringUtils.isNotBlank(esInfo.getPwd())) {
            stringBuilder.append(",").append(esInfo.getPwd());
        }
        if (StringUtils.isNotBlank(esInfo.getToken())) {
            stringBuilder.append(",").append(esInfo.getToken());
        }
        if (StringUtils.isNotBlank(esInfo.getCatalog())) {
            stringBuilder.append(",").append(esInfo.getCatalog());
        }
        if (StringUtils.isNotBlank(esInfo.getDatabase())) {
            stringBuilder.append(",").append(esInfo.getDatabase());
        }
        return stringBuilder.toString();

    }
}
