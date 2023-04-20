package com.xiaomi.mone.log.manager.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessor;
import com.xiaomi.mone.log.manager.service.bind.LogTypeProcessorFactory;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/3/28 20:47
 */
@Slf4j
public class MilogLogTailServiceTest {

    private Gson gson = new Gson();
    private LogTailServiceImpl milogLogtailService;
    private LogTypeProcessorFactory logTypeProcessorFactory;
    private MilogLogTemplateMapper milogLogTemplateMapper;

    @Before
    public void init() {
        Ioc.ins().init("com.xiaomi");
        milogLogtailService = Ioc.ins().getBean(LogTailServiceImpl.class);
        logTypeProcessorFactory = Ioc.ins().getBean(LogTypeProcessorFactory.class);
        milogLogTemplateMapper = Ioc.ins().getBean(MilogLogTemplateMapper.class);
    }


    @Test
    public void testFactory() {
        logTypeProcessorFactory.setMilogLogTemplateMapper(milogLogTemplateMapper);
        LogTypeProcessor logTypeProcessor = logTypeProcessorFactory.getLogTypeProcessor();
        boolean supportedConsume = logTypeProcessor.supportedConsume(LogTypeEnum.APP_LOG_MULTI);
        log.info("supportedConsume:{}", supportedConsume);
    }

    @Test
    public void getList() {
        Ioc.ins().init("com.xiaomi");
        Long tailId = 620L;
        List<String> podList = Lists.newArrayList("127.0.0.1", "127.0.0.1");
//        milogLogtailService.k8sPodIpsSend(tailId, podList, Collections.EMPTY_LIST, 1);
    }

    @Test
    public void test_stream() {
        List<String> list = Lists.newArrayList("1", "2", "3", "4", "10");
        List<String> newList = list.stream().filter(s -> !Objects.equals(s, "3")).collect(Collectors.toList());
        System.out.println(list);
    }

}
