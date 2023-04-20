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
"fieldConfig": {
"defaults": {},
"overrides": [
{
"matcher": {
"id": "byName",
"options": "methodName"
},
"properties": [
{
"id": "displayName",
"value": "接口名"
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
"value": "P99耗时 "
},
{
"id": "unit",
"value": "ms"
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
"value": "QPS"
}
]
},
{
"matcher": {
"id": "byName",
"options": "Value"
},
"properties": [
{
"id": "displayName",
"value": "可用性"
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
"value": "可用性"
},
{
"id": "unit",
"value": "percentunit"
}
]
}
]
},
"fill": 2,
"fillGradient": 7,
"gridPos": {
"h": 9,
"w": 12,
"x": 0,
"y": 0
},
"hiddenSeries": false,
"id": 4,
"interval": "1m",
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 300,
"total": false,
"values": false
},
"lines": true,
"linewidth": 2,
"links": [],
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
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=\"$application\"}[30s])/30) by (methodName)",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"legendFormat": "{{methodName}}",
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调入QPS",
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
"$$hashKey": "object:187",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:188",
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
"fill": 2,
"fillGradient": 7,
"gridPos": {
"h": 9,
"w": 12,
"x": 12,
"y": 0
},
"hiddenSeries": false,
"id": 6,
"legend": {
"alignAsTable": true,
"avg": false,
"current": false,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 300,
"total": false,
"values": false
},
"lines": true,
"linewidth": 2,
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
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_bucket{application=\"$application\"}[30s])) by (le,methodName))",
"interval": "",
"legendFormat": "{{url}}",
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调入 P99-RT",
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
"$$hashKey": "object:365",
"format": "ms",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:366",
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
"current": {
"selected": false,
"text": "150742_diamond_push",
"value": "150742_diamond_push"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(jvm_classes_loaded_classes,application)",
"hide": 0,
"includeAll": false,
"label": "服务名",
"multi": false,
"name": "application",
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
"auto_min": "10s",
"current": {
"selected": false,
"text": "auto",
"value": "$__auto_interval_timeRange"
},
"hide": 0,
"label": "时间间隔",
"name": "timeRange",
"options": [
{
"selected": true,
"text": "auto",
"value": "$__auto_interval_timeRange"
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
"queryValue": "",
"refresh": 2,
"skipUrlSync": false,
"type": "interval"
},
{
"current": {
"selected": false,
"text": "/redis",
"value": "/redis"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(${env}_${serviceName}_aopTotalMethodCount_total{application=\"$application\"},methodName)",
"hide": 2,
"includeAll": false,
"multi": false,
"name": "url",
"options": [],
"query": {
"query": "label_values(${env}_${serviceName}_aopTotalMethodCount_total{application=\"$application\"},methodName)",
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
"current": {
"selected": false,
"text": "1800",
"value": "1800"
},
"hide": 0,
"includeAll": false,
"multi": false,
"name": "query0",
"options": [
{
"selected": false,
"text": "10800",
"value": "10800"
}
],
"query": "10800",
"skipUrlSync": false,
"type": "custom"
}
]
},
"time": {
"from": "now-30m",
"to": "now"
},
"timepicker": {},
"timezone": "",
"title": "Hera-HTTPServer大盘",
"uid": "Hera-HTTPServerMarket",
"version": 63,
"weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera-HTTPServer大盘V1.0"
}