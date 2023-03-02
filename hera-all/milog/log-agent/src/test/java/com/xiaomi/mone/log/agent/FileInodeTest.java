package com.xiaomi.mone.log.agent;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/16 18:52
 */
@Slf4j
public class FileInodeTest {

    @Test
    public void test() {
        File file = new File("/home/work/log/123/trace.log");
        try {
            BasicFileAttributeView basicview = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
            BasicFileAttributes attr = basicview.readAttributes();
            System.out.println("attr.fileKey():" + attr.fileKey()
                    + " attr.creationTime:" + attr.creationTime()
                    + " attr.lastModifiedTime:" + attr.lastModifiedTime());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void testLength() {
        String str = "/home/work/log/nr-xmstore-xmstore-stockms-930394-66b6fd6b9-6tzq9/xmstore-stockms/trace/trace.log, /home/work/log/mifaas-xmstore-dispatcher-stock-180683-30346-644ff756d9-xd6jq/mifaas/trace/trace.log, /home/work/log/nr-xmstore-xmstore-maindata-960202-c7b7bd45-hrpxm/xmstore-maindata/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-4n6q2/xmstore-stockms/trace/trace.log, /home/work/log/mishop-goods-gms-930358-d74f8fc4d-6mdwg/gms/trace/trace.log, /home/work/log/rikaaa0928-nacostestclient-930731-7dc4455bfb-f9dck/nacostestclient/trace/trace.log, /home/work/log/nr-fortune-fortune-calculation-rebate-calculator-990607-6876l9l/rebate-calculator/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-c46t9/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-performance-990692-69784475fd-24qq9/xmstore-performance/trace/trace.log, /home/work/log/nr-trade-nr-trade-center-930194-7dfd6c6fc9-7xfw9/nr-trade-center/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-vt8pd/xmstore-stockms/trace/trace.log, /home/work/log/youpin-mkact-award-center-990754-c557dcc68-xdfjp/award-center-server/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-68vnk/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-dtkfd/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-n255p/xmstore-stockms/trace/trace.log, /home/work/log/nr-fortune-fortune-calculation-rebate-calculator-990607-5dwcgb4/rebate-calculator/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-sqhg5/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-5779c9c599-fgvsd/xmstore-stockms/trace/trace.log, /home/work/log/nr-trade-nr-trade-center-930194-5d78b65fd5-jbgvs/nr-trade-center/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-4hb4z/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-hn65p/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-mglhs/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-jjh52/xmstore-stockms/trace/trace.log, /home/work/log/nr-trade-nr-trade-center-930194-7dfd6c6fc9-qklkh/nr-trade-center/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-hwwm2/xmstore-stockms/trace/trace.log, /home/work/log/youpin-mkact-entity-prize-930161-6dc664c58-jjdvw/entity-prize-server/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-f9blv/xmstore-stockms/trace/trace.log, /home/work/log/youpin-mkact-wukong-990796-6fd6f5b4bb-mc2sp/wukong-job/trace/trace.log, /home/work/log/nr-back-xmstore-mishow-tv-930546-5565957ccc-xgxns/xmstore-mishow-tv/trace/trace.log, /home/work/log/mishop-goods-gms-960477-678fbbbb49-sxqfz/gms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-2dhfg/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-wsgk7/xmstore-stockms/trace/trace.log, /home/work/log/nr-back-xmstore-replenish-990477-695657dd65-x5p4s/xmstore-replenish/trace/trace.log, /home/work/log/mifaas-zzytest102-240720-60310-8547dc8755-bnv99/mifaas/trace/trace.log, /home/work/log/mit-new-retail-cn-finance-cn-fin-price-match-960547-8b8596mwqzg/cn-fin-price-math/trace/trace.log, /home/work/log/rikaaa0928-nacostestclient-930731-57b6b8cb8b-gnsng/nacostestclient/trace/trace.log, /home/work/log/nr-back-xmstore-procurement-930390-885445b85-vlgnm/xmstore-procurement/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-6rqqm/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-kxk5r/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-gfzz9/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-2n2g5/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-maindata-nearby-930540-54999d668f-hmmd5/maindata-nearby/trace/trace.log, /home/work/log/proretail-proretail-live-960549-b5fff7c78-bjwv2/proretail-live/trace/trace.log, /home/work/log/mishop-goods-gms-930358-d74f8fc4d-r47xl/gms/trace/trace.log, /home/work/log/b2c-svr-youpin-consumable-990805-78668cc8d4-4q6fp/consumable-server/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-77d7bb7c76-l5cwm/xmstore-stockms/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-lqgf7/xmstore-stockms/trace/trace.log, /home/work/log/nr-fortune-fortune-accounting-accounting-biz-930535-5f49b8rnqg2/accounting-biz/trace/trace.log, /home/work/log/nr-fortune-fortune-calculation-rebate-policy-center-960655hqlcp/rebate-policy-center-server/trace/trace.log, /home/work/log/nr-xmstore-xmstore-stockms-930394-fdc669864-mvpxl/xmstore-stockms/trace/trace.log, /home/work/log/nr-back-storems-990482-8667997b65-w95cw/xmstore-storems/trace/trace.log, /home/work/log/nr-back-storems-990482-8667997b65-4bvl6/xmstore-storems/trace/trace.log, /home/work/log/nr-back-xmstore-procurement-930390-797bf69667-g72kn/xmstore-procurement/trace/trace.log, /home/work/log/rikaaa0928-nacostestclient-930391-d8f76f5c9-rx49s/nacostestclient/trace/trace.log, /home/work/log/rikaaa0928-nacostestclient-1050016-5b6db46f78-7wg8r/nacostestclient/trace/trace.log, /home/work/log/nr-xmstore-xmstore-sample-120683-5697f469c6-8gqhr/xmstore-sample/trace/trace.log, /home/work/log/nr-xmstore-xmstore-store-90678-8856d889b-2dq8q/xmstore-store/trace/trace.log";
        log.info("length:{}", str.split(",").length);
    }

    @Test
    public void testStreamForeachReturn() {
        List<String> list = Lists.newArrayList("1", "2", "3", "4", "5");
        Map<String, String> map = new HashMap<>();
        list.stream().forEach(s -> {
            if (Objects.equals("3", s)) {
                return;
            }
            map.put(s, s);
        });
        log.info("result:{}", map);
    }

}
