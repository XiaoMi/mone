package com.xiaomi.mone.tpc.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.common.enums.Base;
import com.xiaomi.mone.tpc.common.vo.EnumData;
import org.reflections.Reflections;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnumUtil {

    private static final Map<String, List<EnumData>> mapList = Maps.newHashMap();

    static {
        Reflections reflections = new Reflections("com.xiaomi.mone.tpc.common.enums");
        Set<Class<? extends Base>> baseSet = reflections.getSubTypesOf(Base.class);
        if (!CollectionUtils.isEmpty(baseSet)) {
            for (Class<? extends Base> clsBase : baseSet) {
                if (!Enum.class.isAssignableFrom(clsBase)) {
                    continue;
                }
                Class<? extends Enum> clsEnum = (Class<? extends Enum>)clsBase;
                List<EnumData> list = Lists.newLinkedList();
                mapList.put(clsEnum.getSimpleName(), list);
                Enum[] enums = (clsEnum).getEnumConstants();
                for (int idx = 0; idx < enums.length; idx++) {
                    Base base = (Base)enums[idx];
                    EnumData<Integer, String> data = new EnumData();
                    data.setK(base.getCode());
                    data.setV(base.getDesc());
                    list.add(data);
                }
            }
        }
    }

    public static final Map<String, List<EnumData>> getMapList() {
        return mapList;
    }

    public static void main(String... args) {
        System.err.println(EnumUtil.getMapList());
    }

}
