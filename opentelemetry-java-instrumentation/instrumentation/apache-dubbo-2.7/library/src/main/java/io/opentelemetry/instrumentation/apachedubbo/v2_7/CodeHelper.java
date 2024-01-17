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

package io.opentelemetry.instrumentation.apachedubbo.v2_7;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"SystemOut","CatchAndPrintStackTrace"})
public class CodeHelper {

    private static CodeHelper ins;

    //Result结构有变更时，记得更新此处
    private static final Set<String> MONE_RESULT_KEY = new HashSet<>(Arrays.asList(new String[]{"code", "message", "data", "traceId", "attachments", "class"}));

    private static final List<String> RESULT_FILED_NAME = Arrays.asList("code", "message", "data");

    private static class LazyHolder {
        private static final CodeHelper ins = new CodeHelper();
    }

    public static CodeHelper ins() {
        return LazyHolder.ins;
    }



    public CheckCodeResult checkCode(Object result) {
        CheckCodeResult ccr = new CheckCodeResult();
        ccr.setSuccess(true);
        if (null == result) {
            return ccr;
        }
        if ("run.mone.common.Result".equals(result.getClass().getName())) {
            handleRpcResult(result, ccr);
        } else if (result instanceof Map) {
            Map m = (Map) result;
            Set<String> keyset = m.keySet();
            // mone result
            if (MONE_RESULT_KEY.containsAll(keyset)) {
                String code = String.valueOf(m.get("code"));
                wrapperCcr(ccr, code, String.valueOf(m.get("message")), m.get("data"));
            }
        }
        return ccr;
    }

    private static void handleRpcResult(Object result, CheckCodeResult ccr) {
        try {
            // Use reflection instead of type casting to obtain the value of the Result attribute.
            // This is to address the issue of the Muzzle check error in the business code where the Result dependency was not imported.
            Class<?> resultClass = result.getClass();
            if (checkFieldExist(resultClass.getDeclaredFields())) {
                String code = getFieldStringValue(result, "code");
                String message = getFieldStringValue(result, "message");
                Object data = getFieldObjectValue(result, "data");
                wrapperCcr(ccr, code, message, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFieldStringValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return String.valueOf(field.get(obj));
    }

    private static Object getFieldObjectValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static boolean checkFieldExist(Field[] fields) {
        if (fields == null) {
            return false;
        }
        Set<String> fieldNamesSet =
                Arrays.stream(fields).map(Field::getName).collect(Collectors.toSet());
        return fieldNamesSet.containsAll(RESULT_FILED_NAME);
    }

    private static void wrapperCcr(CheckCodeResult ccr, String code, String message, Object data) {
        if (code.startsWith("5")) {
            ccr.setCode(code);
            ccr.setSuccess(false);
            ccr.setMessage(message);
            ccr.setData(null == data ? "null" : data);
        }
    }
}
