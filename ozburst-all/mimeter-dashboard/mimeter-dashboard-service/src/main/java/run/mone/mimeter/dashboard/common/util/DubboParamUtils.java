package run.mone.mimeter.dashboard.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import run.mone.mimeter.dashboard.common.DubboParamItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.xiaomi.mone.http.docs.core.Constants.*;

@Slf4j
public class DubboParamUtils {

    public static Object initWithDefaultValue(List<DubboParamItem> layerItems) {
        try {
            List<Object> reqList = new ArrayList<>();
            if (!layerItems.isEmpty()){
                layerItems.forEach(layerItem -> reqList.add(initWithDefaultValue0(layerItem)));
            }
            return reqList;
        } catch (Exception e) {
            log.warn("DubboParamUtils.initWithDefaultValue, error msg: " + e.getMessage());
            return EMPTY_OBJECT_INSTANCE;
        }
    }

    private static Object initWithDefaultValue(DubboParamItem layerItem) {
        try {
            return initWithDefaultValue0(layerItem);
        } catch (Exception e) {
            log.warn("DubboParamUtils.initWithDefaultValue, error msg: " + e.getMessage());
            return EMPTY_OBJECT_INSTANCE;
        }
    }

    private static Object initWithDefaultValue0(DubboParamItem layerItem) {
        Class<?> classType = layerItem.getItemClass();
        String defaultValue = layerItem.getDefaultValue();
        if (Integer.class.isAssignableFrom(classType) || int.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0 : Integer.valueOf(defaultValue);
        } else if (Byte.class.isAssignableFrom(classType) || byte.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? (byte) 0 : defaultValue;
        } else if (Long.class.isAssignableFrom(classType) || long.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? 0L : Long.valueOf(defaultValue);
        } else if (Double.class.isAssignableFrom(classType) || double.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0D : Double.valueOf(defaultValue);
        } else if (Float.class.isAssignableFrom(classType) || float.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isNumber(defaultValue)) ? 0.0F : Float.valueOf(defaultValue);
        } else if (String.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue)
                    ? (StringUtils.isEmpty(layerItem.getExampleValue()) ? "demoString" : layerItem.getExampleValue())
                    : defaultValue;
        } else if (Character.class.isAssignableFrom(classType) || char.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? 'c' : defaultValue;
        } else if (Short.class.isAssignableFrom(classType) || short.class.isAssignableFrom(classType)) {
            return (StringUtils.isEmpty(defaultValue) || !NumberUtils.isDigits(defaultValue)) ? (short) 0 : Short.valueOf(defaultValue);
        } else if (Boolean.class.isAssignableFrom(classType) || boolean.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? false : Boolean.valueOf(defaultValue);
        } else if (Date.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + Date.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (LocalDate.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDate.class.getName() + "】yyyy-MM-dd" : defaultValue;
        } else if (LocalDateTime.class.isAssignableFrom(classType)) {
            return StringUtils.isEmpty(defaultValue) ? "【" + LocalDateTime.class.getName() + "】yyyy-MM-dd HH:mm:ss" : defaultValue;
        } else if (BigDecimal.class.isAssignableFrom(classType)) {
            return 0;
        } else if (BigInteger.class.isAssignableFrom(classType)) {
            return 0;
        } else if (Enum.class.isAssignableFrom(classType)) {
            Object[] enumConstants = classType.getEnumConstants();
            StringBuilder sb = new StringBuilder(ENUM_VALUES_SEPARATOR);
            try {
                Method getName = classType.getMethod(METHOD_NAME_NAME);
                for (Object obj : enumConstants) {
                    sb.append(getName.invoke(obj)).append(ENUM_VALUES_SEPARATOR);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
            return sb.toString();
        } else if (Map.class.isAssignableFrom(classType)) {
            Map<Object, Object> resMap = new HashMap<>();
            resMap.put(initWithDefaultValue(layerItem.getItemValue().get(0)), initWithDefaultValue(layerItem.getItemValue().get(1)));
            return resMap;
        } else if (classType.isArray() || Collection.class.isAssignableFrom(classType)) {
            List<Object> resList = new ArrayList<>();
            resList.add(initWithDefaultValue(layerItem.getItemValue().get(0)));
            return resList;
        } else {
            if (layerItem.getItemValue() == null) {
                return EMPTY_OBJECT_INSTANCE;
            }

            Map<String, Object> res = new HashMap<>();
            for (DubboParamItem perLayerItem : layerItem.getItemValue()) {
                res.put(perLayerItem.getItemName(), initWithDefaultValue(perLayerItem));
            }
            return res;
        }
    }

}
