{
"dashboard":{
"annotations": {
"list": [
{
"$$hashKey": "object:2875",
"builtIn": 1,
"datasource": {
"type": "datasource",
"uid": "grafana"
},
"enable": true,
"hide": true,
"iconColor": "rgba(0, 211, 255, 1)",
"name": "Annotations & Alerts",
"target": {
"limit": 100,
"matchAny": false,
"tags": [],
"type": "dashboard"
},
"type": "dashboard"
}
]
},
"description": "Hera物理机监控",
"editable": true,
"fiscalYearStartMonth": 0,
"gnetId": 8919,
"graphTooltip": 0,
"links": [
{
"$$hashKey": "object:2300",
"icon": "bolt",
"tags": [],
"targetBlank": true,
"title": "Update",
"tooltip": "更新当前仪表板",
"type": "link",
"url": "https://grafana.com/dashboards/8919"
},
{
"$$hashKey": "object:2301",
"icon": "question",
"tags": [],
"targetBlank": true,
"title": "GitHub",
"tooltip": "查看更多仪表板",
"type": "link",
"url": "https://github.com/starsliao"
},
{
"$$hashKey": "object:2302",
"asDropdown": true,
"icon": "external link",
"tags": [],
"targetBlank": true,
"title": "",
"type": "dashboards"
}
],
"liveNow": false,
"panels": [
{
"collapsed": false,
"datasource": {
"type": "prometheus",
"uid": "vthUK7inz"
},
"gridPos": {
"h": 1,
"w": 24,
"x": 0,
"y": 0
},
"id": 187,
"panels": [],
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "vthUK7inz"
},
"refId": "A"
}
],
"title": "资源总览（关联JOB项）当前选中主机：$show_hostname，实例：$node",
"type": "row"
},
{
"columns": [],
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "分区使用率、磁盘读取、磁盘写入、下载带宽、上传带宽，如果有多个网卡或者多个分区，是采集的使用率最高的网卡或者分区的数值。\n\n连接数：CurrEstab - 当前状态为 ESTABLISHED 或 CLOSE-WAIT 的 TCP 连接数。",
"fontSize": "80%",
"gridPos": {
"h": 7,
"w": 24,
"x": 0,
"y": 1
},
"id": 185,
"showHeader": true,
"sort": {
"col": 30,
"desc": true
},
"styles": [
{
"$$hashKey": "object:1808",
"alias": "主机名",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 1,
"link": false,
"linkTooltip": "",
"linkUrl": "",
"mappingType": 1,
"pattern": "nodename",
"thresholds": [],
"type": "string",
"unit": "bytes"
},
{
"$$hashKey": "object:1809",
"alias": "IP（链接到明细）",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"link": false,
"linkTargetBlank": false,
"mappingType": 1,
"pattern": "instance",
"thresholds": [],
"type": "number",
"unit": "short"
},
{
"$$hashKey": "object:1810",
"alias": "内存",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"link": false,
"mappingType": 1,
"pattern": "Value #B",
"thresholds": [],
"type": "number",
"unit": "bytes"
},
{
"$$hashKey": "object:1811",
"alias": "CPU核",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"mappingType": 1,
"pattern": "Value #C",
"thresholds": [],
"type": "number",
"unit": "short"
},
{
"$$hashKey": "object:1812",
"alias": " 运行时间",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #D",
"thresholds": [],
"type": "number",
"unit": "s"
},
{
"$$hashKey": "object:1813",
"alias": "分区使用率*",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #E",
"thresholds": [
"70",
"85"
],
"type": "number",
"unit": "percent"
},
{
"$$hashKey": "object:1814",
"alias": "CPU使用率",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #F",
"thresholds": [
"70",
"85"
],
"type": "number",
"unit": "percent"
},
{
"$$hashKey": "object:1815",
"alias": "内存使用率",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #G",
"thresholds": [
"70",
"85"
],
"type": "number",
"unit": "percent"
},
{
"$$hashKey": "object:1816",
"alias": "磁盘读取*",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #H",
"thresholds": [
"10485760",
"20485760"
],
"type": "number",
"unit": "Bps"
},
{
"$$hashKey": "object:1817",
"alias": "磁盘写入*",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #I",
"thresholds": [
"10485760",
"20485760"
],
"type": "number",
"unit": "Bps"
},
{
"$$hashKey": "object:1818",
"alias": "下载带宽*",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #J",
"thresholds": [
"30485760",
"104857600"
],
"type": "number",
"unit": "bps"
},
{
"$$hashKey": "object:1819",
"alias": "上传带宽*",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #K",
"thresholds": [
"30485760",
"104857600"
],
"type": "number",
"unit": "bps"
},
{
"$$hashKey": "object:1820",
"alias": "5m负载",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #L",
"thresholds": [],
"type": "number",
"unit": "short"
},
{
"$$hashKey": "object:1821",
"alias": "连接数",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "Value #M",
"thresholds": [
"1000",
"1500"
],
"type": "string",
"unit": "short"
},
{
"$$hashKey": "object:1822",
"alias": "TCP_tw",
"align": "center",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"mappingType": 1,
"pattern": "Value #N",
"thresholds": [
"5000",
"20000"
],
"type": "number",
"unit": "short"
},
{
"$$hashKey": "object:1823",
"alias": "",
"align": "right",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"decimals": 2,
"pattern": "/.*/",
"thresholds": [],
"type": "hidden",
"unit": "short"
}
],
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_uname_info{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - 0",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "主机名",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(time() - node_boot_time_seconds{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"})by(instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "运行时间",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - 0",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "总内存",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "count(node_cpu_seconds_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",mode='system'}) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "总核数",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_load5{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "5分钟负载",
"refId": "L"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "(1 - avg(rate(node_cpu_seconds_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",mode=\"idle\"}[$interval])) by (instance)) * 100",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "CPU使用率",
"refId": "F"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "(1 - (node_memory_MemAvailable_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} / (node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"})))* 100",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "内存使用率",
"refId": "G"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "max((node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"ext.?|xfs\"}-node_filesystem_free_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"ext.?|xfs\"}) *100/(node_filesystem_avail_bytes {origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"ext.?|xfs\"}+(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"ext.?|xfs\"}-node_filesystem_free_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"ext.?|xfs\"})))by(instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "分区使用率",
"refId": "E"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "max(rate(node_disk_read_bytes_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}[$interval])) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "最大读取",
"refId": "H"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "max(rate(node_disk_written_bytes_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}[$interval])) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "最大写入",
"refId": "I"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_netstat_Tcp_CurrEstab{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - 0",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "连接数",
"refId": "M"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_sockstat_TCP_tw{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - 0",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "TIME_WAIT",
"refId": "N"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "max(rate(node_network_receive_bytes_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}[$interval])*8) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "下载带宽",
"refId": "J"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "max(rate(node_network_transmit_bytes_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}[$interval])*8) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "上传带宽",
"refId": "K"
}
],
"title": "服务器资源总览表【JOB：$job，主机总数：$total】",
"transform": "table",
"type": "table-old"
},
{
"aliasColors": {
"localhost:9100_Total": "dark-red",
"Idle - Waiting for something to happen": "#052B51",
"guest": "#9AC48A",
"idle": "#052B51",
"iowait": "#EAB839",
"irq": "#BF1B00",
"nice": "#C15C17",
"sdb_每秒I/O操作%": "#d683ce",
"softirq": "#E24D42",
"steal": "#FCE2DE",
"system": "#508642",
"user": "#5195CE",
"磁盘花费在I/O操作占比": "#ba43a9"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 7,
"w": 8,
"x": 0,
"y": 8
},
"hiddenSeries": false,
"id": 191,
"legend": {
"alignAsTable": false,
"avg": false,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": false,
"min": false,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"maxPerRow": 6,
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:1325",
"alias": "总平均使用率",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
},
{
"$$hashKey": "object:1326",
"alias": "总核数",
"color": "#C4162A"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "count(node_cpu_seconds_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\", mode='system'})",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总核数",
"refId": "B",
"step": 240
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(node_load5{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"})",
"format": "time_series",
"hide": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总5分钟负载",
"refId": "A",
"step": 240
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "avg(1 - avg(rate(node_cpu_seconds_total{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",mode=\"idle\"}[$interval])) by (instance)) * 100",
"format": "time_series",
"hide": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总平均使用率",
"refId": "F",
"step": 240
}
],
"thresholds": [],
"timeRegions": [],
"title": "$job：整体总负载与整体平均CPU使用率",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:8791",
"format": "short",
"label": "总负载",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:8792",
"decimals": 0,
"format": "percent",
"label": "平均使用率",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"localhost:9100_总内存": "dark-red",
"内存_Avaliable": "#6ED0E0",
"内存_Cached": "#EF843C",
"内存_Free": "#629E51",
"内存_Total": "#6d1f62",
"内存_Used": "#eab839",
"可用": "#9ac48a",
"总内存": "#bf1b00"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 1,
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 7,
"w": 8,
"x": 8,
"y": 8
},
"height": "300",
"hiddenSeries": false,
"id": 195,
"legend": {
"alignAsTable": false,
"avg": false,
"current": true,
"max": false,
"min": false,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": false,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:1423",
"alias": "总内存",
"color": "#C4162A",
"fill": 0
},
{
"$$hashKey": "object:1424",
"alias": "总平均使用率",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"})",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总内存",
"refId": "A",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - node_memory_MemAvailable_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"})",
"format": "time_series",
"hide": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总已用",
"refId": "B",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "(sum(node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"} - node_memory_MemAvailable_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}) / sum(node_memory_MemTotal_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}))*100",
"format": "time_series",
"hide": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总平均使用率",
"refId": "H"
}
],
"thresholds": [],
"timeRegions": [],
"title": "$job：整体总内存与整体平均内存使用率",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:8938",
"format": "bytes",
"label": "总内存量",
"logBase": 1,
"min": "0",
"show": true
},
{
"$$hashKey": "object:8939",
"format": "percent",
"label": "平均使用率",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 1,
"description": "",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 7,
"w": 8,
"x": 16,
"y": 8
},
"hiddenSeries": false,
"id": 197,
"legend": {
"alignAsTable": false,
"avg": false,
"current": true,
"hideEmpty": false,
"hideZero": false,
"max": false,
"min": false,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:1523",
"alias": "总平均使用率",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
},
{
"$$hashKey": "object:1524",
"alias": "总磁盘量",
"color": "#C4162A"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(avg(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance))",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总磁盘量",
"refId": "E"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "sum(avg(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance))",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总使用量",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "(sum(avg(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance))) *100/(sum(avg(node_filesystem_avail_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance))+(sum(avg(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\",fstype=~\"xfs|ext.*\"})by(device,instance))))",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总平均使用率",
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "$job：整体总磁盘与整体平均磁盘使用率",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:8990",
"decimals": 1,
"format": "bytes",
"label": "总磁盘量",
"logBase": 1,
"min": "0",
"show": true
},
{
"$$hashKey": "object:8991",
"format": "percent",
"label": "平均使用率",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"collapsed": false,
"datasource": {
"type": "prometheus",
"uid": "vthUK7inz"
},
"gridPos": {
"h": 1,
"w": 24,
"x": 0,
"y": 15
},
"id": 189,
"panels": [],
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "vthUK7inz"
},
"refId": "A"
}
],
"title": "资源明细：【$show_hostname】",
"type": "row"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"decimals": 0,
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(245, 54, 54, 0.9)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 1
},
{
"color": "rgba(50, 172, 45, 0.97)",
"value": 3
}
]
},
"unit": "s"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 0,
"y": 16
},
"hideTimeOverride": true,
"id": 15,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "none",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"lastNotNull"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "auto"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(time() - node_boot_time_seconds{ip=~\"$node\"})",
"format": "time_series",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A",
"step": 40
}
],
"title": "运行时间",
"type": "stat"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"fieldConfig": {
"defaults": {
"color": {
"mode": "thresholds"
},
"decimals": 1,
"mappings": [
{
"options": {
"0": {
"text": "N/A"
}
},
"type": "value"
}
],
"max": 100,
"min": 0.1,
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "green",
"value": null
},
{
"color": "#EAB839",
"value": 70
},
{
"color": "red",
"value": 90
}
]
},
"unit": "percent"
},
"overrides": []
},
"gridPos": {
"h": 6,
"w": 3,
"x": 2,
"y": 16
},
"id": 177,
"options": {
"displayMode": "lcd",
"minVizHeight": 10,
"minVizWidth": 0,
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"last"
],
"fields": "",
"values": false
},
"showUnfilled": true,
"text": {}
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "100 - (avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"idle\"}[$interval])) * 100)",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "总CPU使用率",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"iowait\"}[$interval])) * 100",
"hide": true,
"instant": true,
"interval": "",
"legendFormat": "IOwait使用率",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(1 - (node_memory_MemAvailable_bytes{ip=~\"$node\"} / (node_memory_MemTotal_bytes{ip=~\"$node\"})))* 100",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "内存使用率",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"$maxmount\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"$maxmount\"})*100 /(node_filesystem_avail_bytes {ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"$maxmount\"}+(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"$maxmount\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"$maxmount\"}))",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "最大分区({{mountpoint}})使用率",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(1 - ((node_memory_SwapFree_bytes{ip=~\"$node\"} + 1)/ (node_memory_SwapTotal_bytes{ip=~\"$node\"} + 1))) * 100",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "交换分区使用率",
"refId": "F"
}
],
"transformations": [],
"type": "bargauge"
},
{
"columns": [],
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "本看板中的：磁盘总量、使用量、可用量、使用率保持和df命令的Size、Used、Avail、Use% 列的值一致，并且Use%的值会四舍五入保留一位小数，会更加准确。\n\n注：df中Use%算法为：(size - free) * 100 / (avail + (size - free))，结果是整除则为该值，非整除则为该值+1，结果的单位是%。\n参考df命令源码：",
"fontSize": "80%",
"gridPos": {
"h": 6,
"w": 10,
"x": 5,
"y": 16
},
"id": 181,
"links": [
{
"targetBlank": true,
"title": "https://github.com/coreutils/coreutils/blob/master/src/df.c",
"url": "https://github.com/coreutils/coreutils/blob/master/src/df.c"
}
],
"scroll": true,
"showHeader": true,
"sort": {
"col": 6,
"desc": false
},
"styles": [
{
"$$hashKey": "object:307",
"alias": "分区",
"align": "auto",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"mappingType": 1,
"pattern": "mountpoint",
"thresholds": [
""
],
"type": "string",
"unit": "bytes"
},
{
"$$hashKey": "object:308",
"alias": "可用空间",
"align": "auto",
"colorMode": "value",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 1,
"mappingType": 1,
"pattern": "Value #A",
"thresholds": [
"10000000000",
"20000000000"
],
"type": "number",
"unit": "bytes"
},
{
"$$hashKey": "object:309",
"alias": "使用率",
"align": "auto",
"colorMode": "cell",
"colors": [
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 1,
"mappingType": 1,
"pattern": "Value #B",
"thresholds": [
"70",
"85"
],
"type": "number",
"unit": "percent"
},
{
"$$hashKey": "object:310",
"alias": "总空间",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 0,
"link": false,
"mappingType": 1,
"pattern": "Value #C",
"thresholds": [],
"type": "number",
"unit": "bytes"
},
{
"$$hashKey": "object:311",
"alias": "文件系统",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"link": false,
"mappingType": 1,
"pattern": "fstype",
"thresholds": [],
"type": "string",
"unit": "short"
},
{
"$$hashKey": "object:312",
"alias": "设备名",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"dateFormat": "YYYY-MM-DD HH:mm:ss",
"decimals": 2,
"link": false,
"mappingType": 1,
"pattern": "device",
"preserveFormat": false,
"sanitize": false,
"thresholds": [],
"type": "string",
"unit": "short"
},
{
"$$hashKey": "object:313",
"alias": "",
"align": "auto",
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"decimals": 2,
"pattern": "/.*/",
"preserveFormat": true,
"sanitize": false,
"thresholds": [],
"type": "hidden",
"unit": "short"
}
],
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-0",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "总量",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_filesystem_avail_bytes {ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-0",
"format": "table",
"hide": false,
"instant": true,
"interval": "10s",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}) *100/(node_filesystem_avail_bytes {ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}+(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}))",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "B"
}
],
"title": "【$show_hostname】：各分区可用空间(EXT.*/XFS)",
"transform": "table",
"type": "table-old"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"decimals": 2,
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"max": 100,
"min": 0,
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(50, 172, 45, 0.97)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 20
},
{
"color": "#d44a3a",
"value": 50
}
]
},
"unit": "percent"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 15,
"y": 16
},
"id": 20,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "area",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"last"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "auto"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"iowait\"}[$interval])) * 100",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A",
"step": 20
}
],
"title": "CPU iowait",
"type": "stat"
},
{
"aliasColors": {
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_cni0_in": "light-red",
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_cni0_in下载": "green",
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_cni0_out上传": "yellow",
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_eth0_in下载": "purple",
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_eth0_out": "purple",
"cn-shenzhen.i-wz9cq1dcb6zwc39ehw59_eth0_out上传": "blue"
},
"bars": true,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editable": true,
"error": false,
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"grid": {},
"gridPos": {
"h": 6,
"w": 7,
"x": 17,
"y": 16
},
"hiddenSeries": false,
"id": 183,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": false,
"show": false,
"sort": "current",
"sortDesc": true,
"total": true,
"values": true
},
"lines": false,
"linewidth": 2,
"links": [],
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 1,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2015",
"alias": "/.*_out上传$/",
"transform": "negative-Y"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "increase(node_network_receive_bytes_total{ip=~\"$node\",device=~\"$device\"}[60m])",
"hide": false,
"interval": "60m",
"intervalFactor": 1,
"legendFormat": "{{device}}_in下载",
"metric": "",
"refId": "A",
"step": 600,
"target": ""
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "increase(node_network_transmit_bytes_total{ip=~\"$node\",device=~\"$device\"}[60m])",
"hide": false,
"interval": "60m",
"intervalFactor": 1,
"legendFormat": "{{device}}_out上传",
"refId": "B",
"step": 600
}
],
"thresholds": [],
"timeRegions": [],
"title": "每小时流量$device",
"tooltip": {
"msResolution": false,
"shared": true,
"sort": 0,
"value_type": "cumulative"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2022",
"format": "bytes",
"label": "上传（-）/下载（+）",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2023",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(245, 54, 54, 0.9)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 1
},
{
"color": "rgba(50, 172, 45, 0.97)",
"value": 2
}
]
},
"unit": "short"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 0,
"y": 18
},
"id": 14,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "none",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"lastNotNull"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "value"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "count(node_cpu_seconds_total{ip=~\"$node\", mode='system'})",
"format": "time_series",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A",
"step": 20
}
],
"title": "CPU 核数",
"type": "stat"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(245, 54, 54, 0.9)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 100000
},
{
"color": "rgba(50, 172, 45, 0.97)",
"value": 1000000
}
]
},
"unit": "short"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 15,
"y": 18
},
"id": 179,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "none",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"lastNotNull"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "auto"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(node_filesystem_files_free{ip=~\"$node\",mountpoint=\"$maxmount\",fstype=~\"ext.?|xfs\"})",
"format": "time_series",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A",
"step": 20
}
],
"title": "剩余节点数:$maxmount ",
"type": "stat"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"decimals": 0,
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(245, 54, 54, 0.9)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 2
},
{
"color": "rgba(50, 172, 45, 0.97)",
"value": 3
}
]
},
"unit": "bytes"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 0,
"y": 20
},
"id": 75,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "none",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"lastNotNull"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "auto"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(node_memory_MemTotal_bytes{ip=~\"$node\"})",
"format": "time_series",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{instance}}",
"refId": "A",
"step": 20
}
],
"title": "总内存",
"type": "stat"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"mappings": [
{
"options": {
"match": "null",
"result": {
"text": "N/A"
}
},
"type": "special"
}
],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "rgba(245, 54, 54, 0.9)",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 1024
},
{
"color": "rgba(50, 172, 45, 0.97)",
"value": 10000
}
]
},
"unit": "locale"
},
"overrides": []
},
"gridPos": {
"h": 2,
"w": 2,
"x": 15,
"y": 20
},
"id": 178,
"links": [],
"maxDataPoints": 100,
"options": {
"colorMode": "value",
"graphMode": "none",
"justifyMode": "auto",
"orientation": "horizontal",
"reduceOptions": {
"calcs": [
"lastNotNull"
],
"fields": "",
"values": false
},
"text": {},
"textMode": "auto"
},
"pluginVersion": "9.2.0",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(node_filefd_maximum{ip=~\"$node\"})",
"format": "time_series",
"hide": false,
"instant": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "A",
"step": 20
}
],
"title": "总文件描述符",
"type": "stat"
},
{
"aliasColors": {
"localhost:9100_Total": "dark-red",
"Idle - Waiting for something to happen": "#052B51",
"guest": "#9AC48A",
"idle": "#052B51",
"iowait": "#EAB839",
"irq": "#BF1B00",
"nice": "#C15C17",
"sdb_每秒I/O操作%": "#d683ce",
"softirq": "#E24D42",
"steal": "#FCE2DE",
"system": "#508642",
"user": "#5195CE",
"磁盘花费在I/O操作占比": "#ba43a9"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"description": "",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 8,
"x": 0,
"y": 22
},
"hiddenSeries": false,
"id": 7,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"maxPerRow": 6,
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2103",
"alias": "/.*总使用率/",
"color": "#C4162A",
"fill": 0
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"system\"}[$interval])) by (instance) *100",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "系统使用率",
"refId": "A",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"user\"}[$interval])) by (instance) *100",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "用户使用率",
"refId": "B",
"step": 240
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"iowait\"}[$interval])) by (instance) *100",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "磁盘IO使用率",
"refId": "D",
"step": 240
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(1 - avg(rate(node_cpu_seconds_total{ip=~\"$node\",mode=\"idle\"}[$interval])) by (instance))*100",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "总使用率",
"refId": "F",
"step": 240
}
],
"thresholds": [],
"timeRegions": [],
"title": "CPU使用率",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:11294",
"decimals": 0,
"format": "percent",
"label": "",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:11295",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"localhost:9100_总内存": "dark-red",
"使用率": "yellow",
"内存_Avaliable": "#6ED0E0",
"内存_Cached": "#EF843C",
"内存_Free": "#629E51",
"内存_Total": "#6d1f62",
"内存_Used": "#eab839",
"可用": "#9ac48a",
"总内存": "#bf1b00"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 8,
"x": 8,
"y": 22
},
"height": "300",
"hiddenSeries": false,
"id": 156,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2191",
"alias": "总内存",
"color": "#C4162A",
"fill": 0
},
{
"$$hashKey": "object:2192",
"alias": "使用率",
"color": "rgb(0, 209, 255)",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_MemTotal_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "总内存",
"refId": "A",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_MemTotal_bytes{ip=~\"$node\"} - node_memory_MemAvailable_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "已用",
"refId": "B",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_MemAvailable_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "可用",
"refId": "F",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_Buffers_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "内存_Buffers",
"refId": "D",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_MemFree_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "内存_Free",
"refId": "C",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_Cached_bytes{ip=~\"$node\"}",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "内存_Cached",
"refId": "E",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_memory_MemTotal_bytes{ip=~\"$node\"} - (node_memory_Cached_bytes{ip=~\"$node\"} + node_memory_Buffers_bytes{ip=~\"$node\"} + node_memory_MemFree_bytes{ip=~\"$node\"})",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "",
"refId": "G"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(1 - (node_memory_MemAvailable_bytes{ip=~\"$node\"} / (node_memory_MemTotal_bytes{ip=~\"$node\"})))* 100",
"format": "time_series",
"hide": false,
"interval": "30m",
"intervalFactor": 10,
"legendFormat": "使用率",
"refId": "H"
}
],
"thresholds": [],
"timeRegions": [],
"title": "内存信息",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2215",
"format": "bytes",
"logBase": 1,
"min": "0",
"show": true
},
{
"$$hashKey": "object:2216",
"format": "percent",
"label": "内存使用率",
"logBase": 1,
"max": "100",
"min": "0",
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"localhost:9100_em1_in下载": "super-light-green",
"localhost:9100_em1_out上传": "dark-blue"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 8,
"x": 16,
"y": 22
},
"height": "300",
"hiddenSeries": false,
"id": 157,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 2,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2300",
"alias": "/.*_out上传$/",
"transform": "negative-Y"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_network_receive_bytes_total{ip=~'$node',device=~\"$device\"}[$interval])*8",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_in下载",
"refId": "A",
"step": 4
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_network_transmit_bytes_total{ip=~'$node',device=~\"$device\"}[$interval])*8",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_out上传",
"refId": "B",
"step": 4
}
],
"thresholds": [],
"timeRegions": [],
"title": "每秒网络带宽使用$device",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2307",
"format": "bps",
"label": "上传（-）/下载（+）",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2308",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"15分钟": "#6ED0E0",
"1分钟": "#BF1B00",
"5分钟": "#CCA300"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"editable": true,
"error": false,
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 1,
"grid": {},
"gridPos": {
"h": 8,
"w": 8,
"x": 0,
"y": 30
},
"height": "300",
"hiddenSeries": false,
"id": 13,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"maxPerRow": 6,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2388",
"alias": "/.*总核数/",
"color": "#C4162A"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_load1{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "1分钟负载",
"metric": "",
"refId": "A",
"step": 20,
"target": ""
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_load5{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "5分钟负载",
"refId": "B",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": "node_load15{instance=~\"$node\"}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "15分钟负载",
"refId": "C",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"expr": " sum(count(node_cpu_seconds_total{instance=~\"$node\", mode='system'}) by (cpu,instance)) by(instance)",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "CPU总核数",
"refId": "D",
"step": 20
}
],
"thresholds": [],
"timeRegions": [],
"title": "系统平均负载",
"tooltip": {
"msResolution": false,
"shared": true,
"sort": 2,
"value_type": "cumulative"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2395",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2396",
"format": "short",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"vda_write": "#6ED0E0"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"description": "Read bytes 每个磁盘分区每秒读取的比特数\nWritten bytes 每个磁盘分区每秒写入的比特数",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 1,
"gridPos": {
"h": 8,
"w": 8,
"x": 8,
"y": 30
},
"height": "300",
"hiddenSeries": false,
"id": 168,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2480",
"alias": "/.*_读取$/",
"transform": "negative-Y"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_read_bytes_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_读取",
"refId": "A",
"step": 10
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_written_bytes_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_写入",
"refId": "B",
"step": 10
}
],
"thresholds": [],
"timeRegions": [],
"title": "每秒磁盘读写容量",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2487",
"format": "Bps",
"label": "读取（-）/写入（+）",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2488",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"alert": {
"alertRuleTags": {},
"conditions": [
{
"evaluator": {
"params": [
1
],
"type": "gt"
},
"operator": {
"type": "and"
},
"query": {
"params": [
"C",
"10s",
"now"
]
},
"reducer": {
"params": [],
"type": "avg"
},
"type": "query"
}
],
"executionErrorState": "alerting",
"for": "10s",
"frequency": "10sG",
"handler": 1,
"message": "/home磁盘使用率超过80%",
"name": "{{mountpoint}}磁盘使用率超过80%报警",
"noDataState": "alerting",
"notifications": [
{
"uid": "BZpzO977z"
}
]
},
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 1,
"description": "",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 8,
"x": 16,
"y": 30
},
"hiddenSeries": false,
"id": 174,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"rightSide": false,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2567",
"alias": "/Inodes.*/",
"yaxis": 2
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_filesystem_size_bytes",
"hide": true,
"interval": "",
"legendFormat": "{{ip}}",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"/home\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint =\"/home\"}) *100/(node_filesystem_avail_bytes {ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint =\"/home\"}+(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint=\"/home\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint =\"/home\"}))",
"hide": true,
"interval": "",
"legendFormat": "{{mountpoint}}",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}) *100/(node_filesystem_avail_bytes {ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}+(node_filesystem_size_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}-node_filesystem_free_bytes{ip=~'$node',fstype=~\"ext.*|xfs\",mountpoint !~\".*pod.*\"}))",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{mountpoint}}",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_filesystem_files_free{ip=~'$node',fstype=~\"ext.?|xfs\"} / node_filesystem_files{ip=~'$node',fstype=~\"ext.?|xfs\"}",
"hide": true,
"interval": "",
"legendFormat": "Inodes：{{instance}}：{{mountpoint}}",
"refId": "B"
}
],
"thresholds": [
{
"colorMode": "critical",
"fill": true,
"line": true,
"op": "gt",
"value": 1,
"visible": true
}
],
"timeRegions": [],
"title": "磁盘使用率",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2574",
"format": "percent",
"label": "",
"logBase": 1,
"max": "100",
"min": "0",
"show": true
},
{
"$$hashKey": "object:2575",
"decimals": 2,
"format": "percentunit",
"logBase": 1,
"max": "1",
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"vda_write": "#6ED0E0"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"description": "Reads completed: 每个磁盘分区每秒读完成次数\n\nWrites completed: 每个磁盘分区每秒写完成次数\n\nIO now 每个磁盘分区每秒正在处理的输入/输出请求数",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 9,
"w": 8,
"x": 0,
"y": 38
},
"height": "300",
"hiddenSeries": false,
"id": 161,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2654",
"alias": "/.*_读取$/",
"transform": "negative-Y"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_reads_completed_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_读取",
"refId": "A",
"step": 10
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_writes_completed_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_写入",
"refId": "B",
"step": 10
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_disk_io_now{ip=~\"$node\"}",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}",
"refId": "C"
}
],
"thresholds": [],
"timeRegions": [],
"title": "磁盘读写速率（IOPS）",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2661",
"format": "iops",
"label": "读取（-）/写入（+）I/O ops/sec",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2662",
"format": "short",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"Idle - Waiting for something to happen": "#052B51",
"guest": "#9AC48A",
"idle": "#052B51",
"iowait": "#EAB839",
"irq": "#BF1B00",
"nice": "#C15C17",
"sdb_每秒I/O操作%": "#d683ce",
"softirq": "#E24D42",
"steal": "#FCE2DE",
"system": "#508642",
"user": "#5195CE",
"磁盘花费在I/O操作占比": "#ba43a9"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "每一秒钟的自然时间内，花费在I/O上的耗时。（wall-clock time）\n\nnode_disk_io_time_seconds_total：\n磁盘花费在输入/输出操作上的秒数。该值为累加值。（Milliseconds Spent Doing I/Os）\n\nrate(node_disk_io_time_seconds_total[1m])：\n计算每秒的速率：(last值-last前一个值)/时间戳差值，即：1秒钟内磁盘花费在I/O操作的时间占比。",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 9,
"w": 8,
"x": 8,
"y": 38
},
"hiddenSeries": false,
"id": 175,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": false,
"rightSide": false,
"show": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"links": [],
"maxPerRow": 6,
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_io_time_seconds_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_每秒I/O操作%",
"refId": "C"
}
],
"thresholds": [],
"timeRegions": [],
"title": "每1秒内I/O操作耗时占比",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2746",
"format": "percentunit",
"label": "",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2747",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"vda": "#6ED0E0"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"description": "Read time seconds 每个磁盘分区读操作花费的秒数\n\nWrite time seconds 每个磁盘分区写操作花费的秒数\n\nIO time seconds 每个磁盘分区输入/输出操作花费的秒数\n\nIO time weighted seconds每个磁盘分区输入/输出操作花费的加权秒数",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 1,
"fillGradient": 1,
"gridPos": {
"h": 9,
"w": 8,
"x": 16,
"y": 38
},
"height": "300",
"hiddenSeries": false,
"id": 160,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": true,
"show": true,
"sort": "current",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2827",
"alias": "/,*_读取$/",
"transform": "negative-Y"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_read_time_seconds_total{ip=~\"$node\"}[$interval]) / rate(node_disk_reads_completed_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_读取",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_write_time_seconds_total{ip=~\"$node\"}[$interval]) / rate(node_disk_writes_completed_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_写入",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_io_time_seconds_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}",
"refId": "A",
"step": 10
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_disk_io_time_weighted_seconds_total{ip=~\"$node\"}[$interval])",
"format": "time_series",
"hide": true,
"interval": "",
"intervalFactor": 1,
"legendFormat": "{{device}}_加权",
"refId": "D"
}
],
"thresholds": [],
"timeRegions": [],
"title": "每次IO读写的耗时（参考：小于100ms）(beta)",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2834",
"format": "s",
"label": "读取（-）/写入（+）",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2835",
"format": "short",
"logBase": 1,
"show": false
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"localhost:9100_TCP_alloc": "semi-dark-blue",
"TCP": "#6ED0E0",
"TCP_alloc": "blue"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"decimals": 2,
"description": "Sockets_used - 已使用的所有协议套接字总量\n\nCurrEstab - 当前状态为 ESTABLISHED 或 CLOSE-WAIT 的 TCP 连接数\n\nTCP_alloc - 已分配（已建立、已申请到sk_buff）的TCP套接字数量\n\nTCP_tw - 等待关闭的TCP连接数\n\nUDP_inuse - 正在使用的 UDP 套接字数量\n\nRetransSegs - TCP 重传报文数\n\nOutSegs - TCP 发送的报文数\n\nInSegs - TCP 接收的报文数",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 16,
"x": 0,
"y": 47
},
"height": "300",
"hiddenSeries": false,
"id": 158,
"interval": "",
"legend": {
"alignAsTable": true,
"avg": false,
"current": true,
"hideEmpty": true,
"hideZero": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sort": "max",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 5,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:2914",
"alias": "/.*Sockets_used/",
"color": "#E02F44",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_netstat_Tcp_CurrEstab{ip=~'$node'}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "CurrEstab",
"refId": "A",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_sockstat_TCP_tw{ip=~'$node'}",
"format": "time_series",
"hide": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "TCP_tw",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_sockstat_sockets_used{ip=~'$node'}",
"hide": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "{{ip}} Sockets_used",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_sockstat_UDP_inuse{ip=~'$node'}",
"hide": false,
"interval": "",
"legendFormat": "UDP_inuse",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_sockstat_TCP_alloc{ip=~'$node'}",
"hide": false,
"interval": "",
"legendFormat": "{{ip}}-TCP_alloc",
"refId": "E"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_Tcp_PassiveOpens{ip=~'$node'}[$interval])",
"hide": true,
"interval": "",
"legendFormat": "{{instance}}_Tcp_PassiveOpens",
"refId": "G"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_Tcp_ActiveOpens{ip=~'$node'}[$interval])",
"hide": true,
"interval": "",
"legendFormat": "{{instance}}_Tcp_ActiveOpens",
"refId": "F"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_Tcp_InSegs{ip=~'$node'}[$interval])",
"hide": false,
"interval": "",
"legendFormat": "Tcp_InSegs",
"refId": "H"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_Tcp_OutSegs{ip=~'$node'}[$interval])",
"interval": "",
"legendFormat": "Tcp_OutSegs",
"refId": "I"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_Tcp_RetransSegs{ip=~'$node'}[$interval])",
"hide": false,
"interval": "",
"legendFormat": "Tcp_RetransSegs",
"refId": "J"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_netstat_TcpExt_ListenDrops{ip=~'$node'}[$interval])",
"hide": true,
"interval": "",
"legendFormat": "",
"refId": "K"
}
],
"thresholds": [],
"timeRegions": [],
"title": "网络Socket连接信息",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"transformations": [],
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:2929",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:2930",
"format": "short",
"label": "已使用的所有协议套接字总量",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
},
{
"aliasColors": {
"filefd_localhost:9100": "super-light-green",
"switches_localhost:9100": "semi-dark-red",
"使用的文件描述符_localhost:9100": "red",
"每秒上下文切换次数_localhost:9100": "yellow",
"每秒上下文切换次数_localhost:9100": "yellow"
},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"links": []
},
"overrides": []
},
"fill": 0,
"fillGradient": 1,
"gridPos": {
"h": 8,
"w": 8,
"x": 16,
"y": 47
},
"hiddenSeries": false,
"hideTimeOverride": false,
"id": 16,
"legend": {
"alignAsTable": false,
"avg": false,
"current": true,
"max": false,
"min": false,
"rightSide": false,
"show": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 2,
"links": [],
"nullPointMode": "null",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0",
"pointradius": 1,
"points": false,
"renderer": "flot",
"seriesOverrides": [
{
"$$hashKey": "object:3015",
"alias": "/每秒上下文切换次数.*/",
"color": "#FADE2A",
"lines": false,
"pointradius": 1,
"points": true,
"yaxis": 2
},
{
"$$hashKey": "object:3016",
"alias": "/使用的文件描述符.*/",
"color": "#F2495C"
}
],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "node_filefd_allocated{ip=~\"$node\"}",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"intervalFactor": 5,
"legendFormat": "{{ip}} 使用的文件描述符",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "rate(node_context_switches_total{ip=~\"$node\"}[$interval])",
"hide": false,
"interval": "",
"intervalFactor": 5,
"legendFormat": "每秒上下文切换次数",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "  (node_filefd_allocated{ip=~\"$node\"}/node_filefd_maximum{ip=~\"$node\"}) *100",
"format": "time_series",
"hide": true,
"instant": false,
"interval": "",
"intervalFactor": 5,
"legendFormat": "使用的文件描述符占比_{{instance}}",
"refId": "C"
}
],
"thresholds": [],
"timeRegions": [],
"title": "打开的文件描述符(左 )/每秒上下文切换次数(右)",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"mode": "time",
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:3037",
"format": "short",
"label": "使用的文件描述符",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:3038",
"format": "short",
"label": "context_switches",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
}
],
"refresh": "",
"schemaVersion": 37,
"style": "dark",
"tags": [
"Prometheus",
"node_exporter"
],
"templating": {
"list": [
{
"allValue": "",
"current": {
"isNone": true,
"selected": false,
"text": "None",
"value": ""
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(origin_prometheus)",
"hide": 0,
"includeAll": false,
"label": "数据源",
"multi": false,
"name": "origin_prometheus",
"options": [],
"query": {
"query": "label_values(origin_prometheus)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"current": {
"selected": false,
"text": "All",
"value": "$__all"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\"}, job)",
"hide": 0,
"includeAll": true,
"label": "JOB",
"multi": true,
"name": "job",
"options": [],
"query": {
"query": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\"}, job)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"current": {
"selected": false,
"text": "All",
"value": "$__all"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}, nodename)",
"hide": 0,
"includeAll": true,
"label": "主机名",
"multi": false,
"name": "hostname",
"options": [],
"query": {
"query": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}, nodename)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"allFormat": "glob",
"current": {
"selected": false,
"text": "localhost",
"value": "localhost"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\",nodename=~\"$hostname\",job=~\"$job\"},ip)",
"hide": 0,
"includeAll": false,
"label": "Instance",
"multi": true,
"multiFormat": "regex values",
"name": "node",
"options": [],
"query": {
"query": "label_values(node_uname_info{origin_prometheus=~\"$origin_prometheus\",nodename=~\"$hostname\",job=~\"$job\"},ip)",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"allFormat": "glob",
"current": {
"selected": false,
"text": "All",
"value": "$__all"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(node_network_info{origin_prometheus=~\"$origin_prometheus\",device!~'tap.*|veth.*|br.*|docker.*|virbr.*|lo.*|cni.*'},device)",
"hide": 0,
"includeAll": true,
"label": "网卡",
"multi": true,
"multiFormat": "regex values",
"name": "device",
"options": [],
"query": {
"query": "label_values(node_network_info{origin_prometheus=~\"$origin_prometheus\",device!~'tap.*|veth.*|br.*|docker.*|virbr.*|lo.*|cni.*'},device)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 1,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"auto": false,
"auto_count": 100,
"auto_min": "10s",
"current": {
"selected": false,
"text": "2m",
"value": "2m"
},
"hide": 0,
"label": "时间间隔",
"name": "interval",
"options": [
{
"selected": false,
"text": "30s",
"value": "30s"
},
{
"selected": false,
"text": "1m",
"value": "1m"
},
{
"selected": true,
"text": "2m",
"value": "2m"
},
{
"selected": false,
"text": "3m",
"value": "3m"
},
{
"selected": false,
"text": "5m",
"value": "5m"
},
{
"selected": false,
"text": "10m",
"value": "10m"
},
{
"selected": false,
"text": "30m",
"value": "30m"
}
],
"query": "30s,1m,2m,3m,5m,10m,30m",
"queryValue": "",
"refresh": 2,
"skipUrlSync": false,
"type": "interval"
},
{
"current": {
"isNone": true,
"selected": false,
"text": "None",
"value": ""
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "query_result(topk(1,sort_desc (max(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",instance=~'$node',fstype=~\"ext.?|xfs\",mountpoint!~\".*pods.*\"}) by (mountpoint))))",
"hide": 2,
"includeAll": false,
"label": "最大挂载目录",
"multi": false,
"name": "maxmount",
"options": [],
"query": {
"query": "query_result(topk(1,sort_desc (max(node_filesystem_size_bytes{origin_prometheus=~\"$origin_prometheus\",instance=~'$node',fstype=~\"ext.?|xfs\",mountpoint!~\".*pods.*\"}) by (mountpoint))))",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "/.*\\\"(.*)\\\".*/",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"current": {
"isNone": true,
"selected": false,
"text": "None",
"value": ""
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(node_uname_info{job=~\"$job\",instance=~\"$node\"}, nodename)",
"hide": 2,
"includeAll": false,
"label": "展示使用的主机名",
"multi": false,
"name": "show_hostname",
"options": [],
"query": {
"query": "label_values(node_uname_info{job=~\"$job\",instance=~\"$node\"}, nodename)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 5,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"current": {
"selected": false,
"text": "3",
"value": "3"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "query_result(count(node_uname_info{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}))",
"hide": 2,
"includeAll": false,
"label": "主机数",
"multi": false,
"name": "total",
"options": [],
"query": {
"query": "query_result(count(node_uname_info{origin_prometheus=~\"$origin_prometheus\",job=~\"$job\"}))",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "/{} (.*) .*/",
"skipUrlSync": false,
"sort": 0,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
}
]
},
"time": {
"from": "now-12h",
"to": "now"
},
"timepicker": {
"hidden": false,
"now": true,
"refresh_intervals": [
"15s",
"30s",
"1m",
"5m",
"15m",
"30m"
],
"time_options": [
"5m",
"15m",
"1h",
"6h",
"12h",
"24h",
"2d",
"7d",
"30d"
]
},
"timezone": "browser",
"title": "Hera物理机监控",
"uid": "hera-node-monitor",
"version": 1,
"weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera物理机监控V1.0"
}