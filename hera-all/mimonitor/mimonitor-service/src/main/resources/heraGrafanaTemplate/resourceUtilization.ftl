{
"dashboard":{
"annotations": {
"list": [
{
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
"editable": true,
"fiscalYearStartMonth": 0,
"graphTooltip": 0,
"id": null,
"links": [],
"liveNow": false,
"panels": [
{
"aliasColors": {
"192.168.200.241:9100_Total": "dark-red",
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
"y": 0
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
"pluginVersion": "9.2.0-pre",
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
"exemplar": true,
"expr": "count(node_cpu_seconds_total{mode='system',system=\"mione\"})",
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
"exemplar": true,
"expr": "sum(node_load5{system=\"mione\"})",
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
"exemplar": true,
"expr": "avg(1 - avg(rate(node_cpu_seconds_total{mode=\"idle\",system=\"mione\"}[$interval])) by (instance)) * 100",
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
"title": "mione整体总负载与整体平均CPU使用率",
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
"192.168.200.241:9100_总内存": "dark-red",
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
"y": 0
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
"pluginVersion": "9.2.0-pre",
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
"exemplar": true,
"expr": "sum(node_memory_MemTotal_bytes{system=\"mione\"})",
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
"exemplar": true,
"expr": "sum(node_memory_MemTotal_bytes{system=\"mione\"} - node_memory_MemAvailable_bytes{system=\"mione\"})",
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
"exemplar": true,
"expr": "(sum(node_memory_MemTotal_bytes{system=\"mione\"} - node_memory_MemAvailable_bytes{system=\"mione\"}) / sum(node_memory_MemTotal_bytes{system=\"mione\"}))*100",
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
"title": "mione整体总内存与整体平均内存使用率",
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
"y": 0
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
"pluginVersion": "9.2.0-pre",
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
"exemplar": true,
"expr": "sum(avg(node_filesystem_size_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance))",
"format": "time_series",
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
"exemplar": true,
"expr": "sum(avg(node_filesystem_size_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance))",
"format": "time_series",
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
"exemplar": true,
"expr": "(sum(avg(node_filesystem_size_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance))) *100/(sum(avg(node_filesystem_avail_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance))+(sum(avg(node_filesystem_size_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance)) - sum(avg(node_filesystem_free_bytes{system=\"mione\",fstype=~\"xfs|ext.*\"})by(device,instance))))",
"format": "time_series",
"instant": false,
"interval": "30m",
"intervalFactor": 1,
"legendFormat": "总平均使用率",
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "mione整体总磁盘与整体平均磁盘使用率",
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
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fieldConfig": {
"defaults": {
"color": {
"mode": "thresholds"
},
"custom": {
"align": "auto",
"displayMode": "auto",
"filterable": false,
"inspect": false
},
"mappings": [],
"thresholds": {
"mode": "absolute",
"steps": [
{
"color": "green",
"value": null
},
{
"color": "red",
"value": 80
}
]
}
},
"overrides": [
{
"matcher": {
"id": "byName",
"options": "Value #B"
},
"properties": [
{
"id": "displayName",
"value": "容器分配 CPU Core"
},
{
"id": "unit",
"value": "short"
},
{
"id": "custom.align",
"value": "center"
},
{
"id": "thresholds",
"value": {
"mode": "absolute",
"steps": [
{
"color": "#37872D",
"value": null
},
{
"color": "#FA6400",
"value": 80
},
{
"color": "#C4162A",
"value": 90
}
]
}
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #C"
},
"properties": [
{
"id": "displayName",
"value": "容器CPU使用率"
},
{
"id": "unit",
"value": "percent"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.displayMode",
"value": "color-background"
},
{
"id": "custom.align"
},
{
"id": "thresholds",
"value": {
"mode": "absolute",
"steps": [
{
"color": "#37872D",
"value": null
},
{
"color": "#FA6400",
"value": 80
},
{
"color": "#C4162A",
"value": 90
}
]
}
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #D"
},
"properties": [
{
"id": "displayName",
"value": "容器Mem使用率"
},
{
"id": "unit",
"value": "percentunit"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.displayMode",
"value": "color-background"
},
{
"id": "custom.align"
},
{
"id": "thresholds",
"value": {
"mode": "percentage",
"steps": [
{
"color": "#37872D",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 80
},
{
"color": "#C4162A",
"value": 90
}
]
}
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #E"
},
"properties": [
{
"id": "displayName",
"value": "容器Mem 使用量"
},
{
"id": "unit",
"value": "bytes"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align",
"value": "center"
},
{
"id": "thresholds",
"value": {
"mode": "absolute",
"steps": [
{
"color": "#37872D",
"value": null
},
{
"color": "rgba(237, 129, 40, 0.89)",
"value": 80
},
{
"color": "#C4162A",
"value": 90
}
]
}
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #F"
},
"properties": [
{
"id": "displayName",
"value": "容器Mem 分配总量"
},
{
"id": "unit",
"value": "bytes"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align",
"value": "center"
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #G"
},
"properties": [
{
"id": "displayName",
"value": "I/O Tx"
},
{
"id": "unit",
"value": "Bps"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align"
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #H"
},
"properties": [
{
"id": "displayName",
"value": "I/O Rx"
},
{
"id": "unit",
"value": "Bps"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align"
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #I"
},
"properties": [
{
"id": "displayName",
"value": "Net Tx"
},
{
"id": "unit",
"value": "Bps"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align"
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #J"
},
"properties": [
{
"id": "displayName",
"value": "Net Rx"
},
{
"id": "unit",
"value": "Bps"
},
{
"id": "decimals",
"value": 2
},
{
"id": "custom.align"
}
]
},
{
"matcher": {
"id": "byName",
"options": "ip"
},
"properties": [
{
"id": "custom.align",
"value": "center"
},
{
"id": "displayName",
"value": "ip (点击跳转到容器监控)"
}
]
},
{
"matcher": {
"id": "byName",
"options": "name"
},
"properties": [
{
"id": "custom.align",
"value": "center"
},
{
"id": "displayName",
"value": "容器名"
}
]
}
]
},
"gridPos": {
"h": 7,
"w": 24,
"x": 0,
"y": 7
},
"id": 2,
"options": {
"footer": {
"fields": "",
"reducer": [
"sum"
],
"show": false
},
"showHeader": true
},
"pluginVersion": "9.2.0-pre",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(container_last_seen{application=\"$application\",ip=~\"$Node\",image!=\"\"}) by (ip)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "container_spec_cpu_quota{system=\"mione\" ,ip=~\"$Node\",application=\"$application\"} / container_spec_cpu_period{system=\"mione\" ,ip=~\"$Node\",application=\"$application\"}",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_cpu_usage_seconds_total{image!=\"\",ip=~\"$Node\",application=\"$application\"}[1m])) without (cpu) * 100",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "container_memory_rss{image!=\"\",ip=~\"$Node\",application=\"$application\"} / container_spec_memory_limit_bytes{image!=\"\",ip=~\"$Node\",application=\"$application\"}",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(container_memory_usage_bytes{application=\"$application\",ip=~\"$Node\",image!=\"\"}) by (ip) - sum(container_memory_cache{application=\"$application\",ip=~\"$Node\",image!=\"\"}) by (ip)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "E"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(container_spec_memory_limit_bytes{ip=~\"$Node\",application=\"$application\"}) by (instance)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "F"
}
],
"title": "Node list",
"transformations": [
{
"id": "merge",
"options": {}
},
{
"id": "merge",
"options": {
"reducers": []
}
},
{
"id": "filterFieldsByName",
"options": {
"include": {
"names": [
"ip",
"Value #B",
"Value #C",
"Value #D",
"Value #E",
"Value #F",
"name"
]
}
}
}
],
"type": "table"
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
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 14
},
"hiddenSeries": false,
"id": 199,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0-pre",
"pointradius": 2,
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
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_cpu_usage_seconds_total{image!=\"\",ip=~\"$Node\",application=\"$application\"}[1m])) without (cpu) * 100",
"hide": false,
"interval": "",
"legendFormat": "{{ip}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Container CPU Usage Ratio",
"tooltip": {
"shared": true,
"sort": 0,
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
"$$hashKey": "object:80",
"format": "percent",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:81",
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
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 14
},
"hiddenSeries": false,
"id": 11,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 300,
"sort": "avg",
"sortDesc": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0-pre",
"pointradius": 2,
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
"editorMode": "code",
"exemplar": true,
"expr": "container_memory_rss{image!=\"\",ip=~\"$Node\",application=\"$application\"}/container_spec_memory_limit_bytes{image!=\"\",ip=~\"$Node\",application=\"$application\"}",
"interval": "",
"legendFormat": "{{ip}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Container Memory  Usage Ratio",
"tooltip": {
"shared": true,
"sort": 0,
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
"$$hashKey": "object:182",
"decimals": 3,
"format": "percentunit",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:183",
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
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 24,
"x": 0,
"y": 22
},
"hiddenSeries": false,
"id": 200,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0-pre",
"pointradius": 2,
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
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_cpu_usage_seconds_total{image!=\"\",ip=~\"$Node\",application=\"$application\"}[1m])) without (cpu) * 100 / (container_spec_cpu_quota{system=\"mione\" ,ip=~\"$Node\",application=\"$application\"} / container_spec_cpu_period{system=\"mione\" ,ip=~\"$Node\",application=\"$application\"})",
"hide": false,
"interval": "",
"legendFormat": "{{ip}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Container CPU Usage Ratio （100%最高）",
"tooltip": {
"shared": true,
"sort": 0,
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
"$$hashKey": "object:80",
"format": "percent",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:81",
"format": "short",
"logBase": 1,
"show": true
}
],
"yaxis": {
"align": false
}
}
],
"schemaVersion": 37,
"style": "dark",
"tags": [],
"templating": {
"list": [
{
"hide": 2,
"name": "origin_prometheus",
"query": "Prometheus-mione-test",
"skipUrlSync": false,
"type": "constant"
},
{
"auto": true,
"auto_count": 30,
"auto_min": "10s",
"current": {
"selected": false,
"text": "auto",
"value": "$__auto_interval_interval"
},
"hide": 2,
"name": "interval",
"options": [
{
"selected": true,
"text": "auto",
"value": "$__auto_interval_interval"
},
{
"selected": false,
"text": "1m",
"value": "1m"
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
},
{
"selected": false,
"text": "1h",
"value": "1h"
},
{
"selected": false,
"text": "6h",
"value": "6h"
},
{
"selected": false,
"text": "12h",
"value": "12h"
},
{
"selected": false,
"text": "1d",
"value": "1d"
},
{
"selected": false,
"text": "7d",
"value": "7d"
},
{
"selected": false,
"text": "14d",
"value": "14d"
},
{
"selected": false,
"text": "30d",
"value": "30d"
}
],
"query": "1m,10m,30m,1h,6h,12h,1d,7d,14d,30d",
"refresh": 2,
"skipUrlSync": false,
"type": "interval"
},
{
"current": {
"selected": true,
"text": "300923_home_mishop",
"value": "300923_home_mishop"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(container_memory_rss{image!=\"\",system=\"mione\"},application)",
"hide": 0,
"includeAll": false,
"label": "name",
"multi": false,
"name": "application",
"options": [],
"query": {
"query": "label_values(container_memory_rss{image!=\"\",system=\"mione\"},application)",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "",
"skipUrlSync": false,
"sort": 0,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"current": {
"selected": true,
"text": [
"All"
],
"value": [
"$__all"
]
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(container_last_seen{application=~\"$application\"},ip)",
"hide": 0,
"includeAll": true,
"label": "节点",
"multi": true,
"name": "Node",
"options": [],
"query": {
"query": "label_values(container_last_seen{application=~\"$application\"},ip)",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "",
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
"from": "now-6h",
"to": "now"
},
"timepicker": {},
"timezone": "",
"title": "Hera-k8s资源使用率大盘",
"uid": "hera-resource-utilization",
"version": 8,
"weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera-k8s资源使用率大盘V1.0"
}