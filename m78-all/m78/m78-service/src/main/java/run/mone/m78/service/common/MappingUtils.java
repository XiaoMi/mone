package run.mone.m78.service.common;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeanUtils;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/6/24 11:37
 */
public class MappingUtils {

    private static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    /**
     * 将一个对象的属性复制到另一个对象中.
     * @param source 源对象，属性将从此对象复制
     * @param dest 目标对象，属性将复制到此对象
     */
	public static void copyProperties(Object source, Object dest) {
        BeanUtils.copyProperties(source, dest);
    }

    /**
     * 将源对象的属性映射到目标对象的同名属性。
     *
     * @param sourceObject 源对象
     * @param targetClass  目标对象的类
     * @param <S>          源对象类型
     * @param <D>          目标对象类型
     * @return 映射后的目标对象
     */
    public static <S, D> D map(S sourceObject, Class<D> targetClass) {
        if (sourceObject == null) {
            return null;
        }
        BoundMapperFacade<S, D> mapper = (BoundMapperFacade<S, D>) mapperFactory.getMapperFacade(sourceObject.getClass(), targetClass);
        return mapper.map(sourceObject);
    }

    /**
     * 将源对象的属性映射到已存在的目标对象的同名属性。
     *
     * @param sourceObject 源对象
     * @param destinationObject 目标对象
     * @param <S> 源对象类型
     * @param <D> 目标对象类型
     */
    public static <S, D> void map(S sourceObject, D destinationObject) {
        if (sourceObject == null || destinationObject == null) {
            return;
        }
        BoundMapperFacade<S, D> mapper = (BoundMapperFacade<S, D>) mapperFactory.getMapperFacade(sourceObject.getClass(), destinationObject.getClass());
        mapper.map(sourceObject, destinationObject);
    }

}
