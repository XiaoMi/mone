/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.dblocking;

import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;

import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */


public class Locking {

    private static final int SLEEP = 10;
    private static final long VERSION_INVALID = -1;

    public static long getVersion(Object data) throws NoSuchFieldException, IllegalAccessException {
        if (data == null) {
            return VERSION_INVALID;
        }
        if (data instanceof BaseEntity) {
            return ((BaseEntity) data).getVersion();
        }
        Class dataClass = data.getClass();
        Field field = dataClass.getDeclaredField("version");
        field.setAccessible(true);
        return (long) field.get(data);
    }

    public static void setVersion(Object data, long version) throws NoSuchFieldException, IllegalAccessException {
        if (data == null || version < 0) {
            return;
        }
        if (data instanceof BaseEntity) {
            ((BaseEntity) data).setVersion(version);
            return;
        }
        Class dataClass = data.getClass();
        Field field = dataClass.getDeclaredField("version");
        field.setAccessible(true);
        field.set(data, version);
    }

    /**
     * 数据库乐观锁. write()尝试写一个database data
     * Object data 必须有version变量
     * @param dao  NutDao 参数, 用于读写db data
     * @param data  需要写入数据库的db data.
     * @param dataUpdater 更新db data数据的function
     * @param attempts 尝试次数
     * @return true 如果db write成功
     */
    public static boolean write(NutDao dao, Object data, Function<Object, Object> dataUpdater, int attempts) {
        if (dao == null || data == null || dataUpdater == null || attempts <= 0) {
            return false;
        }
        try {
            long version = getVersion(data);

            for (int i = 0; i < attempts; i++) {
                setVersion(data, version + 1);

                int updateCount = dao.update(data, Cnd.where("version", "=", version));
                if (updateCount > 0) {
                    return true;
                }

                //random sleep
                int sleep = (int) (Math.random() * SLEEP);
                Thread.sleep(sleep);

                //更新data object的数据,然后返回最新的data
                data = dataUpdater.apply(data);
                if (data == null) {
                    return false;
                }
                version = getVersion(data);
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        } catch (Exception e) {
        }
        return false;
    }
}
