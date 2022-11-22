package com.xiaomi.mock.dao;

import com.xiaomi.mock.bo.EnableMockBo;
import com.xiaomi.mock.entity.ApiMockData;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/8/9 11:12
 */

@Slf4j
@Service
public class ApiMockDao {
    @Resource
    private NutDao dao;

    private static final Logger logger = LoggerFactory.getLogger(ApiMockDao.class);

    public ApiMockData getApiMockResult(String url,String paramsMd5) {
        try {
            return dao.fetch(ApiMockData.class, Cnd.where("url", "=", url).and("params_md5","=",paramsMd5));
        } catch (Exception e) {
            logger.error("getApiMockResult: " + url, e);
        }
        return null;
    }

    public ApiMockData getApiMockInfo(String url,String paramsMd5) {
        try {
            return dao.fetch(ApiMockData.class, Cnd.where("url", "=", url).and("params_md5","=",paramsMd5));
        } catch (Exception e) {
            logger.error("getApiMockResult: " + url, e);
        }
        return null;
    }

    public ApiMockData getApiMockInfoByProxyUrl(String proxyUrl,String paramsMd5) {
        try {
            return dao.fetch(ApiMockData.class, Cnd.where("mock_proxy_url", "=", proxyUrl).and("params_md5","=",paramsMd5));
        } catch (Exception e) {
            logger.error("getApiMockResult: ", e);
        }
        return null;
    }

    public boolean editApiMockResult(String url, String mockResult,String paramsMd5,Integer mockExpID,Boolean enable,Boolean useMockScript,String mockScript) {
        try {
            ApiMockData apiMockData = dao.fetch(ApiMockData.class, Cnd.where("url", "=", url).and("params_md5","=",paramsMd5));
            if (apiMockData == null){
                return false;
            }
            apiMockData.setApiMockData(mockResult);
            apiMockData.setEnable(enable);
            apiMockData.setMockExpID(mockExpID);
            apiMockData.setUseMockScript(useMockScript);
            apiMockData.setMockScript(mockScript);
            int ok = dao.update(apiMockData);
            if (ok < 0){
                return false;
            }
        } catch (Exception e) {
            logger.error("getApiMockResult: " + url, e);
            return false;
        }
        return true;
    }

    public boolean addApiMockInfo(ApiMockData apiMockData) {
        try {
            dao.insert(apiMockData);
        } catch (Exception e) {
            logger.error("getApiMockResult: " + apiMockData.getUrl(), e);
            return false;
        }
        return true;
    }

    public boolean enableApiMock(EnableMockBo bo) {
        try {
            Chain chain = Chain.make("enable", bo.getEnable());
            dao.update(ApiMockData.class,chain,Cnd.where("mock_expect_id", "=", bo.getMockExpID()));
        } catch (Exception e) {
            logger.error("enableApiMock: " + bo.getMockExpID(), e);
            return false;
        }
        return true;
    }

    public boolean editMockProxy(ApiMockData mockData){
        int ok = dao.update(mockData);
        return ok >= 0;
    }

}
