import sys
import time
from prometheusUtil import PrometheusUtil

prom_host = "your prometheus host"
client = PrometheusUtil(prom_host, use_https=False)
pql_moment = sys.argv[1]

# 获取当前时间戳（字符串格式）
current_time = str(time.time())

# 执行查询
result = client.query_moment(query=pql_moment, time=current_time)

if result:
    # 使用工具类自带的辅助方法获取数值
    value = client.get_latest_value(result)
    print(f"查询到的最新值: {value}")