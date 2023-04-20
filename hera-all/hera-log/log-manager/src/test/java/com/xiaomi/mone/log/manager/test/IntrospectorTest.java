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
 * @description 内省技术测试
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
