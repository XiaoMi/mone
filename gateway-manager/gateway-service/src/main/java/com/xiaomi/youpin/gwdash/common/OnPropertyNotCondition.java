package com.xiaomi.youpin.gwdash.common;

import com.xiaomi.youpin.gwdash.annotation.ConditionalOnPropertyNotEq;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

public class OnPropertyNotCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnPropertyNotEq.class.getName());
        Object name = annotationAttributes.get("name");
        Assert.notNull(name, "解析 ConditionalOnPropertyNot 异常");
        Object value = annotationAttributes.get("value");
        Assert.notNull(value, "解析 ConditionalOnPropertyNot 异常");
        String propertyValue = context.getEnvironment().getProperty((String) name);
        if(propertyValue==null){
            return new ConditionOutcome(true,"");
        }
        propertyValue = propertyValue.trim();
        if("true".equalsIgnoreCase(propertyValue) || "false".equalsIgnoreCase(propertyValue)){
            return new ConditionOutcome(!Objects.equals(Boolean.valueOf(propertyValue),value),"");
        }
        return new ConditionOutcome(true,"");
    }
}
