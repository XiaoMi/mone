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
    /**
     * 多行应用日志默认索引
     */
    private static final String LOG_APP_MULTIPLE_INDEX = "mione_hera_log_multiple_app_log01";
    /**
     * 单行应用日志默认索引
     */
    private static final String LOG_APP_SINGLE_INDEX = "mione_hera_log_single_app_log01";
    /**
     * nginx应用日志默认索引
     */
    private static final String LOG_APP_NGINX_INDEX = "mione_hera_log_nginx_app_log01";
    /**
     * 其它的应用日志默认索引
     */
    private static final String LOG_APP_OTHER_INDEX = "mione_hera_log_other_app_log01";

    public static final String DRIVER_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \"" + DRIVER_INDEX + "*\"\n" +
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
            "            \"" + DRIVER_INDEX + "\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}\n" +
            "\n";

    public static final String TRACE_SERVICE_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \"" + TRACE_SERVICE_INDEX + "*\"\n" +
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
            "            \"" + TRACE_SERVICE_INDEX + "\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}";

    public static final String TRACE_SPAN_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \"" + TRACE_SPAN_INDEX + "*\"\n" +
            "    ],\n" +
            "        \"settings\":{\n" +
            "  \"index\": {\n" +
            "    \"lifecycle\": {\n" +
            "      \"name\": \"7Del\",\n" +
            "      \"rollover_alias\": \"" + TRACE_SPAN_INDEX + "\"\n" +
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
            "  \"" + TRACE_SPAN_INDEX + "\": {}\n" +
            "}\n" +
            "}";

    public static final String ERROR_SLOW_JSON = "{\n" +
            "    \"index_patterns\":[\n" +
            "        \"" + ERROR_SLOW_INDEX + "*\"\n" +
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
            "            \"" + ERROR_SLOW_INDEX + "\":{\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "}";

    public static final String LOG_APP_MULTIPLE_INDEX_MAPPING = "{\n" +
            "  \"index_patterns\": [\"" + LOG_APP_MULTIPLE_INDEX + "*\"],\n" +
            "  \"settings\": {\n" +
            "    \"index\": {\n" +
            "      \"lifecycle\": {\n" +
            "        \"name\": \"7Del\"\n" +
            "      },\n" +
            "      \"routing\": {\n" +
            "        \"allocation\": {\n" +
            "          \"total_shards_per_node\": \"2\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"number_of_shards\": \"1\",\n" +
            "      \"number_of_replicas\": \"0\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "      \"dynamic\": \"true\",\n" +
            "      \"dynamic_templates\": [{\n" +
            "        \"strings_as_keywords\": {\n" +
            "          \"match_mapping_type\": \"string\",\n" +
            "          \"mapping\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }],\n" +
            "      \"properties\": {\n" +
            "        \"app\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"appName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"className\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"code\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"costTime\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"errorInfo\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"filename\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"group\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"ip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"level\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"line\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"linenumber\": {\n" +
            "          \"type\": \"long\"\n" +
            "        },\n" +
            "        \"logip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"logsource\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"logstore\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"machine\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"message\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"methodName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"mqtag\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"mqtopic\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"noKnow\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"other\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"packageName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"params\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"pid\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"podName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"result\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"search\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"server\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"tail\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"thread\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"threadName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"timestamp\": {\n" +
            "          \"type\": \"date\",\n" +
            "          \"format\": \"epoch_millis\"\n" +
            "        },\n" +
            "        \"traceId\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        }\n" +
            "      }\n" +
            "  },\n" +
            "  \"aliases\": {\n" +
            "    \"" + LOG_APP_MULTIPLE_INDEX + "\": {}\n" +
            "  }\n" +
            "}";

    public static final String LOG_APP_SINGLE_INDEX_MAPPING = "{\n" +
            "  \"index_patterns\": [\"" + LOG_APP_SINGLE_INDEX + "*\"],\n" +
            "  \"settings\": {\n" +
            "    \"index\": {\n" +
            "      \"lifecycle\": {\n" +
            "        \"name\": \"7Del\"\n" +
            "      },\n" +
            "      \"routing\": {\n" +
            "        \"allocation\": {\n" +
            "          \"total_shards_per_node\": \"2\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"number_of_shards\": \"1\",\n" +
            "      \"number_of_replicas\": \"0\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "      \"dynamic\": \"true\",\n" +
            "      \"dynamic_templates\": [{\n" +
            "        \"strings_as_keywords\": {\n" +
            "          \"match_mapping_type\": \"string\",\n" +
            "          \"mapping\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }],\n" +
            "      \"properties\": {\n" +
            "        \"app\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"appName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"className\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"code\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"costTime\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"errorInfo\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"filename\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"group\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"ip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"level\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"line\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"linenumber\": {\n" +
            "          \"type\": \"long\"\n" +
            "        },\n" +
            "        \"logip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"logsource\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"logstore\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"machine\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"message\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"methodName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"mqtag\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"mqtopic\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"noKnow\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"other\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"packageName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"params\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"pid\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"podName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"result\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"search\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"server\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"tail\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"thread\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"threadName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"timestamp\": {\n" +
            "          \"type\": \"date\",\n" +
            "          \"format\": \"epoch_millis\"\n" +
            "        },\n" +
            "        \"traceId\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        }\n" +
            "      }\n" +
            "  },\n" +
            "  \"aliases\": {\n" +
            "    \"" + LOG_APP_SINGLE_INDEX + "\": {}\n" +
            "  }\n" +
            "}";

    public static final String LOG_APP_NGINX_INDEX_MAPPING = "{\n" +
            "  \"index_patterns\": [\"" + LOG_APP_NGINX_INDEX + "*\"],\n" +
            "  \"settings\": {\n" +
            "    \"index\": {\n" +
            "      \"lifecycle\": {\n" +
            "        \"name\": \"7Del\"\n" +
            "      },\n" +
            "      \"routing\": {\n" +
            "        \"allocation\": {\n" +
            "          \"total_shards_per_node\": \"2\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"number_of_shards\": \"1\",\n" +
            "      \"number_of_replicas\": \"0\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "      \"dynamic\": \"true\",\n" +
            "      \"dynamic_templates\": [{\n" +
            "        \"strings_as_keywords\": {\n" +
            "          \"match_mapping_type\": \"string\",\n" +
            "          \"mapping\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }],\n" +
            "      \"properties\": {\n" +
            "        \"app\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"appName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"className\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"code\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"costTime\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"errorInfo\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"filename\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"group\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"ip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"level\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"line\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"linenumber\": {\n" +
            "          \"type\": \"long\"\n" +
            "        },\n" +
            "        \"logip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"logsource\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"logstore\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"machine\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"message\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"methodName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"mqtag\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"mqtopic\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"noKnow\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"other\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"packageName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"params\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"pid\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"podName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"result\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"search\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"server\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"tail\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"thread\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"threadName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"timestamp\": {\n" +
            "          \"type\": \"date\",\n" +
            "          \"format\": \"epoch_millis\"\n" +
            "        },\n" +
            "        \"traceId\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        }\n" +
            "      }\n" +
            "  },\n" +
            "  \"aliases\": {\n" +
            "    \"" + LOG_APP_NGINX_INDEX + "\": {}\n" +
            "  }\n" +
            "}";

    public static final String LOG_APP_OTHER_INDEX_MAPPING = "{\n" +
            "  \"index_patterns\": [\"" + LOG_APP_OTHER_INDEX + "*\"],\n" +
            "  \"settings\": {\n" +
            "    \"index\": {\n" +
            "      \"lifecycle\": {\n" +
            "        \"name\": \"7Del\"\n" +
            "      },\n" +
            "      \"routing\": {\n" +
            "        \"allocation\": {\n" +
            "          \"total_shards_per_node\": \"2\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"number_of_shards\": \"1\",\n" +
            "      \"number_of_replicas\": \"0\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "      \"dynamic\": \"true\",\n" +
            "      \"dynamic_templates\": [{\n" +
            "        \"strings_as_keywords\": {\n" +
            "          \"match_mapping_type\": \"string\",\n" +
            "          \"mapping\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }],\n" +
            "      \"properties\": {\n" +
            "        \"app\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"appName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"className\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"code\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"costTime\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"errorInfo\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"filename\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"group\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"ip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"level\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"line\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"linenumber\": {\n" +
            "          \"type\": \"long\"\n" +
            "        },\n" +
            "        \"logip\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"logsource\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"logstore\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"machine\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"message\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"methodName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"mqtag\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"mqtopic\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"noKnow\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"other\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"packageName\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"params\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"pid\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"podName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"result\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"search\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"server\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"tail\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        },\n" +
            "        \"thread\": {\n" +
            "          \"type\": \"text\"\n" +
            "        },\n" +
            "        \"threadName\": {\n" +
            "          \"type\": \"keyword\"\n" +
            "        },\n" +
            "        \"timestamp\": {\n" +
            "          \"type\": \"date\",\n" +
            "          \"format\": \"epoch_millis\"\n" +
            "        },\n" +
            "        \"traceId\": {\n" +
            "          \"type\": \"keyword\",\n" +
            "          \"ignore_above\": 4096\n" +
            "        }\n" +
            "      }\n" +
            "  },\n" +
            "  \"aliases\": {\n" +
            "    \"" + LOG_APP_OTHER_INDEX + "\": {}\n" +
            "  }\n" +
            "}";

    public static Map<String, String> templates = new HashMap<>();

    static {
        templates.put(DRIVER_INDEX, DRIVER_JSON);
        templates.put(TRACE_SERVICE_INDEX, TRACE_SERVICE_JSON);
        templates.put(TRACE_SPAN_INDEX, TRACE_SPAN_JSON);
        templates.put(ERROR_SLOW_INDEX, ERROR_SLOW_JSON);

        /**
         * 日志初始化每种类型初始化一个索引
         */
        templates.put(LOG_APP_MULTIPLE_INDEX, LOG_APP_MULTIPLE_INDEX_MAPPING);
        templates.put(LOG_APP_SINGLE_INDEX, LOG_APP_SINGLE_INDEX_MAPPING);
        templates.put(LOG_APP_NGINX_INDEX, LOG_APP_NGINX_INDEX_MAPPING);
        templates.put(LOG_APP_OTHER_INDEX, LOG_APP_OTHER_INDEX_MAPPING);
    }

    public static void main(String[] args) {
        // update your es api address
        String esApiAddr = "elasticsearch:9200";

        for (String index : templates.keySet()) {
            System.out.println("curl --location --request PUT 'http://" + esApiAddr + "/_template/" + index + "' \\\n" +
                    "--header 'Content-type: application/json; charset=UTF-8' \\\n" +
                    "--data-raw '" + templates.get(index).replaceAll("\\\n", "").replaceAll("\\\t", "").replaceAll(" ", "") + "'");
            System.out.println("=============================================================");
        }
    }
}
