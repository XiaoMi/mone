package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.ApiCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiCacheMapper
{
	Integer addApiCache(ApiCache apiCache);

	Integer updateApiCache(ApiCache apiCache);

}
