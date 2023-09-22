/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.test;

import com.xiaomi.mone.log.manager.model.pojo.MiLogMachine;
import org.junit.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wtt
 * @version 1.0
 * @description Introspection Technology Test
 * @date 2021/9/22 10:19
 */
public class IntrospectorTest {

    @Test
    public void test1() throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(MiLogMachine.class,Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            System.out.println(propertyDescriptor.getName());
        }
    }
    
    @Test
    public void test2() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        MiLogMachine miLogMachine = new MiLogMachine();
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor("creator", MiLogMachine.class);
        Method writeMethod = propertyDescriptor.getWriteMethod();
        writeMethod.invoke(miLogMachine,"wtt");

        Method readMethod = propertyDescriptor.getReadMethod();
        System.out.println(readMethod.invoke(miLogMachine,null));
    }

    @Test
    public void test3() throws IntrospectionException {
        MiLogMachine miLogMachine = new MiLogMachine();
        PropertyDescriptor pd = new PropertyDescriptor("creator", MiLogMachine.class);
        System.out.println(pd.getPropertyType());
    }
}
