package com.xiaomi.mone.log.manager.esParse;

import com.xiaomi.mone.log.api.enums.EsOperatorEnum;
import com.xiaomi.mone.log.manager.service.statement.StatementMatchParseFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.junit.Test;

import java.util.List;

import static com.xiaomi.mone.log.manager.common.utils.ManagerUtil.getKeyColonPrefix;

public class StatementMatchParseFactoryTest {

    @Test
    public void testGetStatementMatchParse() {
        String message = "level : INFO ";
        String keys = "timestamp:1,level:1,traceId:1,threadName:1,className:1,line:1,methodName:1,appName:2,message:1,tail:3,uri:2,remote:2,delay:2,code:2,parent:2,gwid:2,app:2,path:2,linenumber:3,logsource:3,logip:3";
        String columnTypes = "date,keyword,keyword,text,text,keyword,keyword,text,keyword,keyword,text,ip,text,text,text,text,text,text,long,text,keyword";
        List<String> keyList = getKeyColonPrefix(keys);
        BoolQueryBuilder matchParseQuery = StatementMatchParseFactory.getStatementMatchParseQueryBuilder(message, keyList);
        System.out.println(matchParseQuery);
    }

    @Test
    public void analyseTransformOsTest() {
        String message = " not \"PrometheusFilter and test\" or message:\"异常\"";
        StatementMatchParseFactory.analyseTransformOs(message);
    }

    @Test
    public void analyseTransformOsTestMultiple() {
        String message = "\"9645ff1e024c2479c5cd7bbf7e402ce6\" and \"京东到家className=JingDongPromotionProxy\" not \"b1933ce60cb54854ad1c405ea3523bdb\"";
        StatementMatchParseFactory.analyseTransformOs(message);
    }

    @Test
    public void splitBySeparatorTest() {
//        String message = "\"PrometheusFilter and test\" and message:\"异常\"";
//        String message = "\"PrometheusFilter\" and message:\"异常\"";
//        String message = "\"PrometheusFilter and\" and message:\"异常\"";
//        String message = "'\"异常\"  and code";
//        String message = "'\\\"异常'  and code";
//        String message = "'异常'  and code";
        String message = "\"test\"  and code or \"fff\"";
        String separator = EsOperatorEnum.AND_OPERATOR.getCode();
        List<String> splitBySeparators = StatementMatchParseFactory.splitBySeparator(message, separator);
        splitBySeparators.stream().forEach(System.out::println);
    }

}
