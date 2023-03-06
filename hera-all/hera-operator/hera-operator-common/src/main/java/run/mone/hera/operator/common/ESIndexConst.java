package run.mone.hera.operator.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/2/22 12:25 PM
 */
public class ESIndexConst {

    public static final String DRIVER_INDEX = "mione-staging-zgq-driver";

    public static final String TRACE_SERVICE_INDEX = "mione-staging-zgq-jaeger-service";

    public static final String TRACE_SPAN_INDEX = "mione-staging-zgq-jaeger-span";

    public static final String ERROR_SLOW_INDEX = "zgq_common_staging_private_prometheus";

    public static final String DRIVER_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \""+DRIVER_INDEX+"*\"\n" +
            "    ],\n" +
            "        \"settings\":{\n" +
            "            \"index\":{\n" +
            "                \"lifecycle\":{\n" +
            "                    \"name\":\"7Del\"\n" +
            "                },\n" +
            "                \"routing\":{\n" +
            "                    \"allocation\":{\n" +
            "                        \"total_shards_per_node\":\"2\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"number_of_shards\":\"1\",\n" +
            "                \"number_of_replicas\":\"0\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"mappings\":{\n" +
            "                \"_meta\":{\n" +
            "\n" +
            "                },\n" +
            "                \"_source\":{\n" +
            "\n" +
            "                },\n" +
            "                \"properties\":{\n" +
            "                    \"timeStamp\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"password\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"appName\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"ip\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"dataBaseName\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"domainPort\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"type\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"userName\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"timestamp\":{\n" +
            "                        \"type\":\"date\"\n" +
            "                    }\n" +
            "                }\n" +
            "        },\n" +
            "        \"aliases\":{\n" +
            "            \""+DRIVER_INDEX+"\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}\n" +
            "\n";

    public static final String TRACE_SERVICE_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \""+TRACE_SERVICE_INDEX+"*\"\n" +
            "    ],\n" +
            "        \"settings\":{\n" +
            "            \"index\":{\n" +
            "                \"lifecycle\":{\n" +
            "                    \"name\":\"7Del\"\n" +
            "                },\n" +
            "                \"routing\":{\n" +
            "                    \"allocation\":{\n" +
            "                        \"total_shards_per_node\":\"2\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"number_of_shards\":\"1\",\n" +
            "                \"number_of_replicas\":\"0\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"mappings\":{\n" +
            "                \"_meta\":{\n" +
            "\n" +
            "                },\n" +
            "                \"_source\":{\n" +
            "\n" +
            "                },\n" +
            "                \"properties\":{\n" +
            "                    \"operationName\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"dataJson\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"serviceName\":{\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"timestamp\":{\n" +
            "                        \"type\":\"date\"\n" +
            "                    }\n" +
            "                }\n" +
            "        },\n" +
            "        \"aliases\":{\n" +
            "            \""+TRACE_SERVICE_INDEX+"\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}";

    public static final String TRACE_SPAN_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \""+TRACE_SPAN_INDEX+"*\"\n" +
            "    ],\n" +
            "        \"settings\":{\n" +
            "  \"index\": {\n" +
            "    \"lifecycle\": {\n" +
            "      \"name\": \"7Del\",\n" +
            "      \"rollover_alias\": \""+TRACE_SPAN_INDEX+"\"\n" +
            "    },\n" +
            "    \"routing\": {\n" +
            "      \"allocation\": {\n" +
            "        \"total_shards_per_node\": \"6\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"number_of_shards\": \"1\",\n" +
            "    \"number_of_replicas\": \"0\"\n" +
            "  }\n" +
            "},\n" +
            "        \"mappings\":{\n" +
            "    \"_meta\": {},\n" +
            "    \"_source\": {},\n" +
            "    \"properties\": {\n" +
            "      \"traceID\": {\n" +
            "        \"ignore_above\": 256,\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"process\": {\n" +
            "        \"type\": \"object\",\n" +
            "        \"properties\": {\n" +
            "          \"tag\": {\n" +
            "            \"type\": \"object\"\n" +
            "          },\n" +
            "          \"serviceName\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"tags\": {\n" +
            "            \"type\": \"nested\",\n" +
            "            \"properties\": {\n" +
            "              \"tagType\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"type\": {\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"value\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"key\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"references\": {\n" +
            "        \"type\": \"nested\",\n" +
            "        \"properties\": {\n" +
            "          \"spanID\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"traceID\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"refType\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"startTimeMillis\": {\n" +
            "        \"format\": \"epoch_millis\",\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"flags\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"operationName\": {\n" +
            "        \"ignore_above\": 256,\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"parentSpanID\": {\n" +
            "        \"ignore_above\": 256,\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"tags\": {\n" +
            "        \"type\": \"nested\",\n" +
            "        \"properties\": {\n" +
            "          \"val\\r\\nue\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"v\\r\\nalue\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"tagType\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"\\r\\nvalue\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"type\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"value\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"key\": {\n" +
            "            \"ignore_above\": 256,\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"key\\r\\n\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"duration\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"spanID\": {\n" +
            "        \"ignore_above\": 256,\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"@timestamp\": {\n" +
            "        \"format\": \"epoch_millis||strict_date_optional_time\",\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"startTime\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"tag\": {\n" +
            "        \"type\": \"object\"\n" +
            "      },\n" +
            "      \"logs\": {\n" +
            "        \"type\": \"nested\",\n" +
            "        \"properties\": {\n" +
            "          \"fields\": {\n" +
            "            \"type\": \"nested\",\n" +
            "            \"properties\": {\n" +
            "              \"v\\r\\nalue\": {\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"tagType\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"type\": {\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"value\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              },\n" +
            "              \"key\": {\n" +
            "                \"ignore_above\": 256,\n" +
            "                \"type\": \"keyword\"\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          \"timestamp\": {\n" +
            "            \"type\": \"long\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"timestamp\": {\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "},\n" +
            "        \"aliases\":{\n" +
            "  \""+TRACE_SPAN_INDEX+"\": {}\n" +
            "}\n" +
            "}";

    public static final String ERROR_SLOW_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \""+ERROR_SLOW_INDEX+"*\"\n" +
            "    ],\n" +
            "        \"settings\":{\n" +
            "            \"index\":{\n" +
            "                \"lifecycle\":{\n" +
            "                    \"name\":\"1Warm_7Del\"\n" +
            "                },\n" +
            "                \"routing\":{\n" +
            "                    \"allocation\":{\n" +
            "                        \"total_shards_per_node\":\"2\"\n" +
            "                    }\n" +
            "                },\n" +
            "                \"number_of_shards\":\"1\",\n" +
            "                \"number_of_replicas\":\"0\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"mappings\":{\n" +
            "                \"_meta\":{\n" +
            "\n" +
            "                },\n" +
            "                \"_source\":{\n" +
            "\n" +
            "                },\n" +
            "                \"properties\":{\n" +
            "                    \"traceId\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"functionName\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"errorType\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"moduleName\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"errorCode\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"env\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"serviceName\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"type\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"url\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"duration\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"serverEnv\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"functionId\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"domain\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"host\":{\n" +
            "                        \"type\":\"ip\"\n" +
            "                    },\n" +
            "                    \"_class\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"dataSource\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"group\":{\n" +
            "                        \"ignore_above\":4096,\n" +
            "                        \"type\":\"keyword\"\n" +
            "                    },\n" +
            "                    \"timestamp\":{\n" +
            "                        \"format\":\"epoch_millis\",\n" +
            "                        \"type\":\"date\"\n" +
            "                    }\n" +
            "                }\n" +
            "        },\n" +
            "        \"aliases\":{\n" +
            "            \""+ERROR_SLOW_INDEX+"\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}";

    public static Map<String,String> templates = new HashMap<>();

    static {
        templates.put(DRIVER_INDEX, DRIVER_JSON);
        templates.put(TRACE_SERVICE_INDEX, TRACE_SERVICE_JSON);
        templates.put(TRACE_SPAN_INDEX, TRACE_SPAN_JSON);
        templates.put(ERROR_SLOW_INDEX, ERROR_SLOW_JSON);
    }

    public static void main(String[] args) {
        System.out.println(DRIVER_JSON);
    }
}
