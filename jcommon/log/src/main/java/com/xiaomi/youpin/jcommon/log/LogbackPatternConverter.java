/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.xiaomi.youpin.jcommon.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

import java.util.Map;

public class LogbackPatternConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return traceId(iLoggingEvent);
    }

    public static String traceId(ILoggingEvent iLoggingEvent) {
        try {
            Map<String, String> mdcMap = iLoggingEvent.getMDCPropertyMap();
            String traceId = mdcMap.get("trace_id");
            if (null != mdcMap && null != traceId) {
                return traceId;
            }

            traceId = MDC.get("trace_id");
            if (null != traceId) {
                return traceId;
            }

            traceId = MDC.get("tid");
            if (null != traceId) {
                return traceId;
            }

            return null;
        } catch (Exception e) {
            return "";
        }
    }
}
