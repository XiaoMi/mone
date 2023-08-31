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
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"description": "",
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 7,
"w": 12,
"x": 0,
"y": 0
},
"hiddenSeries": false,
"id": 2,
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 220,
"total": false,
"values": false
},
"lines": true,
"linewidth": 1,
"links": [],
"nullPointMode": "connected",
"options": {
"alertThreshold": true
},
"percentage": false,
"pluginVersion": "9.2.0-pre",
"pointradius": 2,
"points": false,
"renderer": "flot",
"repeatDirection": "h",
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
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=~\"$serverName\"}[30s])/30)by(application)",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Http QPS",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=~\"$serverName\"}[30s])/30)by(application)",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调入 QPS",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=~\"$serverName\"}[30s])/30) by(application)",
"hide": true,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调出 QPS",
"refId": "C"
}
],
"thresholds": [],
"timeRegions": [],
"title": "QPS",
"tooltip": {
"shared": true,
"sort": 0,
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
"$$hashKey": "object:105",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:106",
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
"h": 7,
"w": 12,
"x": 12,
"y": 0
},
"hiddenSeries": false,
"id": 10,
"interval": "30s",
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 220,
"total": false,
"values": false
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
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=~\"$serverName\"}[30s])) by(application)",
"interval": "",
"legendFormat": "{{application}}--HTTP",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=~\"$serverName\"}[30s])) by (application)",
"hide": true,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调出",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=~\"$serverName\"}[30s])) by (application)",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调入",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "",
"hide": false,
"interval": "",
"legendFormat": "",
"refId": "D"
}
],
"thresholds": [],
"timeRegions": [],
"title": "调用量趋势图",
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
"$$hashKey": "object:353",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:354",
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
"x": 0,
"y": 7
},
"hiddenSeries": false,
"id": 4,
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 220,
"total": false,
"values": false
},
"lines": true,
"linewidth": 1,
"nullPointMode": "connected",
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
"exemplar": true,
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_bucket{application=~\"$serverName\"}[30s])) by (le,application))",
"interval": "",
"legendFormat": "{{application}}--Http 99%耗时",
"refId": "A",
"target": ""
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_bucket{application=~\"$serverName\"}[30s])) by (le,application))",
"hide": true,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调出 99%耗时",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=~\"$serverName\"}[30s])) by (le,application))",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调入 99%耗时",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_bucket{application=~\"$serverName\"}[30s])) by (le,application))",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Redis 99%耗时",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_bucket{application=~\"$serverName\"}[30s])) by (le,application))",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--DB 99%耗时",
"refId": "E"
}
],
"thresholds": [],
"timeRegions": [],
"title": "99%请求耗时",
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
"$$hashKey": "object:237",
"format": "ms",
"logBase": 1,
"min": "0",
"show": true
},
{
"$$hashKey": "object:238",
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
"y": 7
},
"hiddenSeries": false,
"id": 8,
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 220,
"total": false,
"values": false
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
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_sqlSuccessCount_total{application=~\"$serverName\"}[30s])) by (application) / sum(sum_over_time(${env}_${serviceName}_sqlTotalCount_total{application=~\"$serverName\"}[30s])) by (application)",
"interval": "",
"legendFormat": "{{application}}--DB",
"refId": "A"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_RedisSuccessCount_total{application=~\"$serverName\"}[30s])) by (application) / sum(sum_over_time(${env}_${serviceName}_RedisTotalCount_total{application=~\"$serverName\"}[30s])) by (application)",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Redis",
"refId": "B"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_aopSuccessMethodCount_total{application=~\"$serverName\"}[30s])) by (application)\n/ sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=~\"$serverName\"}[30s])) by (application)\n",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--HTTP",
"refId": "C"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboBisSuccessCount_total{application=~\"$serverName\"}[30s])) by (application) / sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=~\"$serverName\"}[30s])) by (application)",
"hide": true,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调出",
"refId": "D"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "clamp_max(sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledSuccessCount_total{application=~\"$serverName\"}[30s])) by (application)  / sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=~\"$serverName\"}[30s])) by (application),1)",
"hide": false,
"interval": "",
"legendFormat": "{{application}}--Dubbo 调入",
"refId": "E"
}
],
"thresholds": [],
"timeRegions": [],
"title": "可用性",
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
"$$hashKey": "object:77",
"format": "percentunit",
"logBase": 1,
"max": "1",
"min": "0",
"show": true
},
{
"$$hashKey": "object:78",
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
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
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
"options": "Value #C"
},
"properties": [
{
"id": "displayName",
"value": "cpu平均使用率"
},
{
"id": "unit",
"value": "percentunit"
},
{
"id": "color",
"value": {
"mode": "thresholds"
}
},
{
"id": "custom.displayMode",
"value": "lcd-gauge"
},
{
"id": "thresholds",
"value": {
"mode": "percentage",
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
{
"id": "custom.align",
"value": "center"
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
"value": "平均load1"
},
{
"id": "custom.displayMode",
"value": "color-text"
},
{
"id": "color",
"value": {
"mode": "thresholds"
}
},
{
"id": "thresholds",
"value": {
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
{
"id": "custom.align",
"value": "center"
},
{
"id": "custom.width",
"value": 150
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #B"
},
"properties": [
{
"id": "displayName",
"value": "JVM Heap Used"
},
{
"id": "unit",
"value": "percent"
},
{
"id": "custom.displayMode",
"value": "lcd-gauge"
},
{
"id": "color",
"value": {
"mode": "thresholds"
}
},
{
"id": "thresholds",
"value": {
"mode": "percentage",
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
{
"id": "custom.align",
"value": "center"
}
]
},
{
"matcher": {
"id": "byName",
"options": "application"
},
"properties": [
{
"id": "displayName",
"value": "应用名【点击应用名查看详细】"
},
{
"id": "custom.displayMode",
"value": "color-background"
},
{
"id": "custom.align",
"value": "center"
},
{
"id": "links",
"value": [
{
"targetBlank": true,
"title": "",
"url": "${serviceMarketUrl}"
}
]
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value #A"
},
"properties": [
{
"id": "displayName",
"value": "实例数"
},
{
"id": "custom.width",
"value": 80
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
"options": "Value #E"
},
"properties": [
{
"id": "displayName",
"value": "http异常数"
},
{
"id": "custom.displayMode",
"value": "gradient-gauge"
},
{
"id": "custom.align",
"value": "center"
},
{
"id": "noValue",
"value": "0"
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
"value": "dubbo 调出 异常数"
},
{
"id": "custom.displayMode",
"value": "gradient-gauge"
},
{
"id": "custom.align",
"value": "center"
},
{
"id": "noValue",
"value": "0"
},
{
"id": "color",
"value": {
"mode": "continuous-GrYlRd"
}
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
"value": "dubbo 调入 异常数"
},
{
"id": "custom.displayMode",
"value": "gradient-gauge"
},
{
"id": "noValue",
"value": "0"
},
{
"id": "color",
"value": {
"mode": "continuous-GrYlRd"
}
}
]
}
]
},
"gridPos": {
"h": 10,
"w": 24,
"x": 0,
"y": 15
},
"id": 6,
"options": {
"footer": {
"fields": "",
"reducer": [
"sum"
],
"show": false
},
"showHeader": true,
"sortBy": [
{
"desc": false,
"displayName": "dubbo provider异常数"
}
]
},
"pluginVersion": "9.2.0-pre",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "count(jvm_classes_loaded_classes{application=~\"$serverName\"})by(application)",
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
"exemplar": true,
"expr": "avg(system_cpu_usage{application=~\"$serverName\"}) by(application)",
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
"exemplar": true,
"expr": "avg(system_load_average_1m{application=~\"$serverName\"})by(application)",
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
"exemplar": true,
"expr": "avg(jvm_memory_used_bytes{application=~\"$serverName\", area=\"heap\"})by(application)*100  /avg(jvm_memory_max_bytes{application=~\"$serverName\", area=\"heap\"}) by(application)",
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
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_httpError_total{application=~\"$serverName\"}[$interval])) by(application)",
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
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboConsumerError_total{application=~\"$serverName\"}[$interval])) by (application)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "G"
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=~\"$serverName\"}[$interval])) by (application)",
"format": "table",
"hide": false,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "H"
}
],
"title": "应用列表[$interval]",
"transformations": [
{
"id": "filterFieldsByName",
"options": {
"include": {
"names": [
"application",
"Value #C",
"Value #D",
"Value #B",
"Value #A",
"Value #E",
"Value #G",
"Value #H"
]
}
}
},
{
"id": "labelsToFields",
"options": {}
},
{
"id": "merge",
"options": {}
},
{
"id": "organize",
"options": {
"excludeByName": {},
"indexByName": {
"Value #A": 1,
"Value #B": 4,
"Value #C": 2,
"Value #D": 3,
"Value #E": 5,
"Value #G": 6,
"application": 0
},
"renameByName": {}
}
}
],
"type": "table"
}
],
"schemaVersion": 37,
"style": "dark",
"tags": [],
"templating": {
"list": [
{
"current": {
"selected": true,
"text": [
"667_zzytest",
"753_nr_order",
"10000309_nr_ipms_srv_cn_test",
"90766_nr_commission",
"90925_cn_crm_order_core",
"618_gps",
"60649_xmstore_goods_center",
"60697_proretail_general",
"90983_xmstore_mishow",
"90946_xmstore_ops",
"841_user_profile",
"91010_xmstore_assets_center",
"60681_mistore_bff",
"60647_nr_qrcode_sys"
],
"value": [
"667_zzytest",
"753_nr_order",
"10000309_nr_ipms_srv_cn_test",
"90766_nr_commission",
"90925_cn_crm_order_core",
"618_gps",
"60649_xmstore_goods_center",
"60697_proretail_general",
"90983_xmstore_mishow",
"90946_xmstore_ops",
"841_user_profile",
"91010_xmstore_assets_center",
"60681_mistore_bff",
"60647_nr_qrcode_sys"
]
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(jvm_classes_loaded_classes,application)",
"hide": 0,
"includeAll": false,
"multi": true,
"name": "serverName",
"options": [],
"query": {
"query": "label_values(jvm_classes_loaded_classes,application)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 0,
"tagValuesQuery": "",
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"auto": true,
"auto_count": 1,
"auto_min": "1s",
"current": {
"selected": false,
"text": "auto",
"value": "$__auto_interval_interval"
},
"hide": 2,
"label": "时间间隔",
"name": "interval",
"options": [
{
"selected": true,
"text": "auto",
"value": "$__auto_interval_interval"
},
{
"selected": false,
"text": "5m",
"value": "5m"
}
],
"query": "5m",
"refresh": 2,
"skipUrlSync": false,
"type": "interval"
}
]
},
"time": {
"from": "now-5m",
"to": "now"
},
"timepicker": {},
"timezone": "",
"title": "Hera-服务大盘",
"uid": "hera-serviceMarket",
"version": 1,
"weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera-服务大盘V1.0"
}