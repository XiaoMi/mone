package com.xiaomi.data.push.mock;

import com.google.gson.Gson;
import com.xiaomi.data.push.action.ActionContext;
import com.xiaomi.data.push.action.ActionInfo;
import com.xiaomi.data.push.annotation.Mock;
import com.xiaomi.data.push.annotation.MockType;
import com.xiaomi.data.push.common.TraceId;
import com.xiaomi.data.push.dao.mapper.MockMapper;
import com.xiaomi.data.push.dao.model.MockExample;
import com.xiaomi.data.push.dao.model.MockWithBLOBs;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author goodjava@qq.com
 * <p>
 * mock 处理的,不会调用到下层,而是在这层就被拦截下来
 */
@Aspect
@Configuration
@Order(1)
public class MockAop {

    private static final Logger logger = LoggerFactory.getLogger(MockAop.class);

    @Autowired
    private MockMapper mockMapper;

    @Autowired
    private ActionContext actionContext;


    @Around(value = "@annotation(mock)")
    public Object mock(ProceedingJoinPoint joinPoint, Mock mock) throws Throwable {

        long now = System.currentTimeMillis();


        String packageName = joinPoint.getSignature().getDeclaringTypeName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = packageName + "." + method.getName();

        Class<?> returnType = method.getReturnType();

        Object[] o = joinPoint.getArgs();
        String id = TraceId.getTraceId(o);
        logger.info("mock begin key:{} id:{} name:{} params:{}", mock.key(), id, name, new Gson().toJson(o));
        long startTime = System.currentTimeMillis();


        ActionInfo ai = actionContext.getActionInfos().get(method.toString());
        if (ai == null || !ai.isMock()) {
            return joinPoint.proceed();
        }


        try {
            //使用mock数据
            if (mock.type().equals(MockType.mock)) {
                MockExample example = new MockExample();
                example.createCriteria().andMockKeyEqualTo(mock.key());
                example.setOrderByClause("id desc");
                List<MockWithBLOBs> list = mockMapper.selectByExampleWithBLOBs(example);
                if (list.size() > 0) {
                    String result = list.get(0).getResult();
                    return new Gson().fromJson(result, returnType);
                } else {
                    Object result = joinPoint.proceed();
                    long useTime = System.currentTimeMillis() - startTime;
                    logger.info("mock finish [success] id:{} name:{} result:{} useTime:{}", id, name, new Gson().toJson(result), useTime);
                    return result;
                }
            }
            //记录mock数据
            else if (mock.type().equals(MockType.record)) {
                Object result = joinPoint.proceed();
                long useTime = System.currentTimeMillis() - startTime;
                logger.info("mock finish [success] id:{} name:{} result:{} useTime:{}", id, name, new Gson().toJson(result), useTime);
                String resStr = new Gson().toJson(result);
                MockWithBLOBs mockData = new MockWithBLOBs();
                mockData.setMockKey(mock.key());
                mockData.setResult(resStr);
                mockData.setCreated(now);
                mockData.setUpdated(now);
                mockData.setParams("");
                mockData.setDescription("");
                mockData.setStatus(0);
                mockData.setVersion(0);
                mockMapper.insert(mockData);
                return result;
            }

            return joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.warn("mock finish [failure] id:{} name:{}  error:{}", id, name, throwable.getMessage());
            throw throwable;
        }
    }

}
