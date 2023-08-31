{
"dashboard":{
"annotations":{
"list":[
{
"builtIn":1,
"datasource":"-- Grafana --",
"enable":true,
"hide":true,
"iconColor":"rgba(0, 211, 255, 1)",
"name":"Annotations &amp; Alerts",
"type":"dashboard"
}
]
},
"description":"mione",
"editable":true,
"gnetId":null,
"graphTooltip":0,
"id":null,
"iteration":1625575780650,
"links":[
{
"asDropdown":true,
"icon":"external link",
"includeVars":true,
"keepTime":false,
"tags":[

],
"targetBlank":true,
"title":"点击跳转到错误列表",
"tooltip":"",
"type":"link",
"url":"${jaeger_error_list_url}"
}
],
"panels":[
{
"collapsed":false,
"datasource":null,
"gridPos":{
"h":1,
"w":24,
"x":0,
"y":155
},
"id":159,
"panels":[

],
"title":"自定义指标",
"type":"row"
},
{
"collapsed":false,
"datasource":null,
"gridPos":{
"h":1,
"w":24,
"x":0,
"y":0
},
"id":102,
"panels":[

],
"title":"应用健康度",
"type":"row"
},
{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 1
},
"id": 110,
"interval": "30s",
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"exemplar": true,
"expr": "clamp_min(1 - ((sum(sum_over_time(${env}_${serviceName}_dbError_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application)  or clamp_max( absent(notExists{application=\"$application\"}),0) )/ (sum(sum_over_time(${env}_${serviceName}_sqlTotalCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application)or clamp_max( absent(notExists{application=\"$application\"}),1))),0)",
"hide": false,
"interval": "",
"legendFormat": "sql",
"refId": "A"
},
{
"exemplar": true,
"expr": "clamp_min(1 - ((sum(sum_over_time(${env}_${serviceName}_redisError_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),0)  )/ (sum(sum_over_time(${env}_${serviceName}_RedisTotalCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),1) )),0)",
"hide": false,
"interval": "",
"legendFormat": "redis",
"refId": "B"
},
{
"exemplar": true,
"expr": "clamp_min(1 - ((sum(sum_over_time(${env}_${serviceName}_httpError_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s]))  by (application) or clamp_max( absent(notExists{application=\"$application\"}),0) ) / (sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),1) )),0)",
"hide": false,
"interval": "",
"legendFormat": "HTTP",
"refId": "C"
},
{
"exemplar": true,
"expr": "clamp_min(1 - ((sum(sum_over_time(${env}_${serviceName}_dubboConsumerError_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),0)) / (sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),1) )),0)",
"hide": false,
"interval": "",
"legendFormat": "Dubbo调出",
"refId": "D"
},
{
"exemplar": true,
"expr": "clamp_min(1 - ((sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),0) ) / (sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (application) or clamp_max( absent(notExists{application=\"$application\"}),1) )),0)",
"hide": false,
"interval": "",
"legendFormat": "Dubbo调入",
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
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:72",
"decimals": 3,
"format": "percentunit",
"label": null,
"logBase": 1,
"max": "1",
"min": "0",
"show": true
},
{
"$$hashKey": "object:73",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},
{
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {
"custom": {
"align": "center",
"filterable": false,
"displayMode": "color-background"
},
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
},
{
"color": "#EAB839",
"value": 90
}
]
},
"mappings": [],
"color": {
"mode": "continuous-GrYlRd"
}
},
"overrides": [
{
"matcher": {
"id": "byName",
"options": "serverIp"
},
"properties": [
{
"id": "custom.width"
},
{
"id": "custom.displayMode",
"value": "color-text"
},
{
"id": "unit"
},
{
"id": "displayName",
"value": "实例（点击跳转到物理机监控）"
},
{
"id": "links",
"value": [
{
"targetBlank": true,
"title": "跳转到物理机监控",
"url": "${hostUrl}"
}
]
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
"value": "状态"
},
{
"id": "custom.displayMode",
"value": "color-background"
},
{
"id": "color",
"value": {
"fixedColor": "green",
"mode": "thresholds"
}
},
{
"id": "noValue",
"value": "宕机"
},
{
"id": "mappings",
"value": [
{
"from": "",
"id": 1,
"text": "宕机",
"to": "",
"type": 1,
"value": "0"
},
{
"from": "1",
"id": 2,
"text": "存活",
"to": "999999999999999998",
"type": 2,
"value": "1"
}
]
},
{
"id": "custom.displayMode",
"value": "color-background"
},
{
"id": "thresholds",
"value": {
"mode": "absolute",
"steps": [
{
"color": "red",
"value": null
},
{
"color": "green",
"value": 1
}
]
}
}
]
},
{
"matcher": {
"id": "byName",
"options": "Last *"
},
"properties": [
{
"id": "displayName",
"value": "容器启动时间（点击跳转到容器详情)"
},
{
"id": "links",
"value": [
{
"targetBlank": true,
"title": "",
"url": "${containerUrl}"
}
]
},
{
"id": "unit",
"value": "s"
},
{
"id": "custom.displayMode",
"value": "color-text"
},
{
"id": "color",
"value": {
"fixedColor": "blue",
"mode": "fixed"
}
}
]
}
]
},
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 1
},
"id": 148,
"options": {
"showHeader": true,
"sortBy": [
{
"desc": false,
"displayName": "点击ip跳转到容器监控"
}
]
},
"pluginVersion": "7.5.3",
"targets": [
{
"expr": "sum(process_uptime_seconds{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}) by (serverIp,jumpIp)",
"legendFormat": "{{serverIp}}",
"interval": "",
"exemplar": true,
"format": "table",
"hide": false,
"instant": true,
"refId": "A"
},
{
"exemplar": true,
"expr": "sum(container_last_seen{image!=\"\",name=~\"$containerName.*\"}) by (name,ip)",
"format": "table",
"hide": true,
"instant": true,
"interval": "",
"legendFormat": "",
"refId": "B"
}
],
"title": "实例列表 【主机数：$total】",
"transformations": [
{
"id": "filterFieldsByName",
"options": {
"include": {
"names": [
"serverIp",
"Value",
"jumpIp"
]
}
}
},
{
"id": "calculateField",
"options": {
"mode": "reduceRow",
"reduce": {
"reducer": "lastNotNull"
}
}
},
{
"id": "organize",
"options": {
"excludeByName": {
"jumpIp": false
},
"indexByName": {
"serverIp": 0,
"Value": 1,
"Last (not null)": 2,
"jumpIp": 3
},
"renameByName": {
"jumpIp": "宿主机"
}
}
}
],
"type": "table"
},
{
"aliasColors": {},
"dashLength": 10,
"datasource":"${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 24,
"x": 0,
"y": 9
},
"id": 112,
"interval": "30s",
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 400,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (serverIp)",
"interval": "",
"legendFormat": "{{serverIp}}-HTTP",
"refId": "A"
},
{
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (serverIp)",
"hide": false,
"interval": "",
"legendFormat": "{{serverIp}}-Dubbo调出",
"refId": "B"
},
{
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\"}[30s])) by (serverIp)",
"hide": false,
"interval": "",
"legendFormat": "{{serverIp}}-Dubbo调入",
"refId": "C"
}
],
"thresholds": [],
"timeRegions": [],
"title": "调用量变化(30s内总和)",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:175",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:176",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},
{
"collapsed":false,
"datasource":null,
"gridPos":{
"h":1,
"w":24,
"x":0,
"y":17
},
"id":104,
"panels":[

],
"repeat":"application",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"title":"业务指标",
"type":"row"
},
{
"aliasColors": {},
"bars": false,
"dashLength": 10,
"dashes": false,
"datasource":"${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"fillGradient": 0,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 18
},
"hiddenSeries": false,
"id": 116,
"interval": "15s",
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
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
"pluginVersion": "7.5.3",
"pointradius": 2,
"points": false,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"stack": false,
"steppedLine": false,
"targets": [
{
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)",
"instant": false,
"interval": "",
"intervalFactor": 1,
"legendFormat": "total",
"refId": "A"
},
{
"exemplar": true,
"expr": "sum(sum_over_time(${env}_${serviceName}_aopTotalMethodCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (methodName,serverIp)",
"format": "time_series",
"hide": false,
"instant": false,
"interval": "",
"legendFormat": "{{methodName}}-{{serverIp}}",
"refId": "B"
}
],
"thresholds": [],
"timeFrom": null,
"timeRegions": [],
"timeShift": null,
"title": "Http调入 QPS",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"transformations": [],
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:180",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:181",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"scopedVars": {
"application": {
"selected": true,
"text": "${serviceName}",
"value": "${serviceName}"
}
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":66
},
"hiddenSeries":false,
"id":118,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)",
"interval":"",
"legendFormat":"total",
"refId":"A"
},
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (serviceName,serverIp)",
"hide":false,
"interval":"",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Dubbo调入 QPS",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:278",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:279",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 74
},
"id": 168,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "topk(10, sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,serviceName) / sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,serviceName))",
"legendFormat": "{{serverIp}}-{{serviceName}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Dubbo调入 Top10 RT",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:474",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:475",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},

{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 74
},
"id": 169,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by(serverIp,serviceName)\n/\nsum(sum_over_time(${env}_${serviceName}_dubboProviderCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (serverIp,serviceName)",
"legendFormat": "{{serverIp}}-{{serviceName}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Dubbo调入 AVG-RT",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:1418",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:1419",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":50
},
"hiddenSeries":false,
"id":150,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{application=\"$application\",functionName=\"$funcName\"}[30s])/30)",
"interval":"",
"legendFormat":"total",
"refId":"A"
},
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_dubboBisTotalCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (serviceName,serverIp)",
"hide":false,
"interval":"",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Dubbo调出 QPS",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1801",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:1802",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":58
},
"hiddenSeries":false,
"id":122,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"topk(10, sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,serviceName) / sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,serviceName))",
"interval":"",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Dubbo调出 Top10 RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:474",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:475",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":26
},
"hiddenSeries":false,
"id":120,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"topk(10, sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,methodName) / sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,methodName))",
"interval":"",
"legendFormat":"{{serverIp}}-{{methodName}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Http调入 Top10 RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:376",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:377",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":50
},
"hiddenSeries":false,
"id":126,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_without_methodName_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,serviceName,serverIp))",
"interval":"",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Dubbo调出 P99-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:762",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:763",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":18
},
"hiddenSeries":false,
"id":124,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_without_methodName_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,serverIp))",
"interval":"",
"legendFormat":"{{serverIp}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Http调入 P99-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:664",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:665",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":58
},
"hiddenSeries":false,
"id":130,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":250,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by(serverIp,serviceName)/sum(sum_over_time(${env}_${serviceName}_dubboConsumerTimeCost_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (serverIp,serviceName)",
"interval":"",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Dubbo调出 AVG-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1418",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:1419",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":26
},
"hiddenSeries":false,
"id":128,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"scopedVars":{
"application":{
"selected":true,
"text":"${serviceName}",
"value":"${serviceName}"
}
},
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (serverIp,methodName) / sum(sum_over_time(${env}_${serviceName}_aopMethodTimeCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (serverIp,methodName) ",
"interval":"",
"legendFormat":"{{serverIp}}-{{methodName}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Http调入 AVG-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1320",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:1321",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"type": "graph",
"title": "Dubbo调入 P99-RT",
"gridPos": {
"x": 12,
"y": 66,
"w": 12,
"h": 8
},
"id": 163,
"options": {
"alertThreshold": true
},
"fieldConfig": {
"defaults": {},
"overrides": []
},
"pluginVersion": "7.5.3",
"renderer": "flot",
"yaxes": [
{
"label": null,
"show": true,
"logBase": 1,
"min": "0",
"max": null,
"format": "ms",
"$$hashKey": "object:121"
},
{
"label": null,
"show": true,
"logBase": 1,
"min": null,
"max": null,
"format": "short",
"$$hashKey": "object:122"
}
],
"xaxis": {
"show": true,
"mode": "time",
"name": null,
"values": [],
"buckets": null
},
"yaxis": {
"align": false,
"alignLevel": null
},
"lines": true,
"fill": 1,
"linewidth": 1,
"dashLength": 10,
"spaceLength": 10,
"pointradius": 2,
"legend": {
"show": true,
"values": true,
"min": false,
"max": true,
"current": true,
"total": false,
"avg": true,
"alignAsTable": true,
"rightSide": true,
"sideWidth": 250
},
"nullPointMode": "null as zero",
"tooltip": {
"value_type": "individual",
"shared": true,
"sort": 2
},
"aliasColors": {},
"seriesOverrides": [],
"thresholds": [],
"timeRegions": [],
"targets": [
{
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_without_methodName_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,serviceName,serverIp))",
"legendFormat":"{{serverIp}}-{{serviceName}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"fillGradient": 0,
"dashes": false,
"hiddenSeries": false,
"points": false,
"bars": false,
"stack": false,
"percentage": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null,
"description": "",
"datasource": "${dataSource}"
},
{
"collapsed":false,
"datasource":"${dataSource}",
"gridPos":{
"h":1,
"w":24,
"x":0,
"y":82
},
"id":106,
"panels":[

],
"title":"中间件",
"type":"row"
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":83
},
"hiddenSeries":false,
"id":132,
"legend":{
"alignAsTable":true,
"avg":false,
"current":true,
"max":true,
"min":true,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (dataSource,sqlMethod) / sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (dataSource,sqlMethod)",
"interval":"",
"legendFormat":"{{dataSource}}-{{sqlMethod}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"DB AVG-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1516",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":"0",
"show":true
},
{
"$$hashKey":"object:1517",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":99
},
"hiddenSeries":false,
"id":134,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (host,port,method) / sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (host,port,method)",
"interval":"",
"legendFormat":"{{host}}:{{port}}-{{method}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Redis AVG-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1614",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:1615",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":99
},
"hiddenSeries":false,
"id":136,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,method,host,port))",
"interval":"",
"legendFormat":"{{host}}:{{port}}-{{method}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Redis P99-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1810",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:1811",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":83
},
"hiddenSeries":false,
"id":138,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,dataSource,sqlMethod))",
"interval":"",
"legendFormat":"{{dataSource}}-{{sqlMethod}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"DB P99-RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1712",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":"0",
"show":true
},
{
"$$hashKey":"object:1713",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":107
},
"hiddenSeries":false,
"id":140,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"topk(10, sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(le,host,port,method) / sum(sum_over_time(${env}_${serviceName}_RedisMethodTimeCost_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(le,host,port,method))",
"interval":"",
"legendFormat":"{{host}}:{{port}}-{{method}} {{key}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Redis Top10 RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:2006",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":0,
"show":true
},
{
"$$hashKey":"object:2007",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":91
},
"hiddenSeries":false,
"id":142,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":false,
"rightSide":true,
"show":true,
"sideWidth":300,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":2,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"topk(10, sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(dataSource,sql) / sum(sum_over_time(${env}_${serviceName}_sqlTotalTimer_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(dataSource,sql))",
"interval":"",
"legendFormat":"{{dataSource}}-{{sql}}",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":" DB Top10 RT",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1908",
"format":"ms",
"label":null,
"logBase":1,
"max":null,
"min":"0",
"show":true
},
{
"$$hashKey":"object:1909",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"breakPoint":"50%",
"cacheTimeout":null,
"combine":{
"label":"Others",
"threshold":0
},
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fontSize":"80%",
"format":"short",
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":91
},
"id":144,
"interval":null,
"legend":{
"header":"",
"percentage":false,
"percentageDecimals":null,
"show":true,
"sideWidth":300,
"sort":"current",
"sortDesc":false,
"values":true,
"sideWidth": 250
},
"legendType":"Under graph",
"links":[

],
"nullPointMode":"null as zero",
"pieType":"pie",
"pluginVersion":"7.5.3",
"strokeWidth":1,
"targets":[
{
"exemplar":true,
"expr":"topk(10,sum(sum_over_time(${env}_${serviceName}_sqlTotalCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[$interval])) by (dataSource,sql))",
"format":"time_series",
"instant":true,
"interval":"",
"legendFormat":"{{dataSource}}-{{sql}}",
"refId":"A"
}
],
"timeFrom":null,
"timeShift":null,
"title":"$interval DB Top10 Query",
"type":"grafana-piechart-panel",
"valueName":"current"
},
{
"aliasColors":{

},
"breakPoint":"50%",
"cacheTimeout":null,
"combine":{
"label":"Others",
"threshold":0
},
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fontSize":"80%",
"format":"short",
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":107
},
"id":146,
"interval":null,
"legend":{
"show":true,
"sideWidth":300,
"values":true
},
"legendType":"Under graph",
"links":[

],
"nullPointMode":"null as zero",
"pieType":"pie",
"pluginVersion":"7.5.3",
"strokeWidth":1,
"targets":[
{
"exemplar":true,
"expr":"topk(10,sum(sum_over_time(${env}_${serviceName}_RedisTotalCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[$interval])) by (host,port,method,key))",
"format":"time_series",
"instant":true,
"interval":"",
"legendFormat":"{{host}}:{{port}}-{{method}} {{key}}",
"refId":"A"
}
],
"timeFrom":null,
"timeShift":null,
"title":"$interval Redis Top10 Query",
"type":"grafana-piechart-panel",
"valueName":"current"
},
{
"collapsed":false,
"datasource": "${dataSource}",
"gridPos":{
"h":1,
"w":24,
"x":0,
"y":115
},
"id":54,
"panels":[

],
"title":"JVM",
"type":"row"
},
{
"cacheTimeout":null,
"colorBackground":false,
"colorValue":true,
"colors":[
"rgba(245, 54, 54, 0.9)",
"#5195ce",
"rgba(50, 172, 45, 0.97)"
],
"datasource":"${dataSource}",
"decimals":1,
"editable":true,
"error":false,
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"format":"s",
"gauge":{
"maxValue":100,
"minValue":0,
"show":false,
"thresholdLabels":false,
"thresholdMarkers":true
},
"gridPos":{
"h":3,
"w":6,
"x":0,
"y":116
},
"height":"",
"id":52,
"interval":null,
"links":[

],
"mappingType":1,
"mappingTypes":[
{
"name":"value to text",
"value":1
},
{
"name":"range to text",
"value":2
}
],
"maxDataPoints":100,
"nullPointMode":"null as zero",
"nullText":null,
"postfix":"",
"postfixFontSize":"50%",
"prefix":"",
"prefixFontSize":"70%",
"rangeMaps":[
{
"from":"null",
"text":"N/A",
"to":"null"
}
],
"sparkline":{
"fillColor":"rgba(31, 118, 189, 0.18)",
"full":false,
"lineColor":"rgb(31, 120, 193)",
"show":false
},
"tableColumn":"",
"targets":[
{
"exemplar":true,
"expr":"process_uptime_seconds{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\"}",
"format":"time_series",
"interval":"",
"intervalFactor":2,
"legendFormat":"",
"metric":"",
"refId":"A",
"step":14400
}
],
"thresholds":"",
"title":"Uptime",
"type":"singlestat",
"valueFontSize":"80%",
"valueMaps":[
{
"op":"=",
"text":"N/A",
"value":"null"
}
],
"valueName":"current"
},
{
"cacheTimeout":null,
"colorBackground":false,
"colorValue":true,
"colors":[
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"datasource":"${dataSource}",
"decimals":1,
"editable":true,
"error":false,
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"format":"percent",
"gauge":{
"maxValue":100,
"minValue":0,
"show":true,
"thresholdLabels":false,
"thresholdMarkers":true
},
"gridPos":{
"h":6,
"w":5,
"x":6,
"y":116
},
"id":58,
"interval":null,
"links":[

],
"mappingType":1,
"mappingTypes":[
{
"name":"value to text",
"value":1
},
{
"name":"range to text",
"value":2
}
],
"maxDataPoints":100,
"nullPointMode":"null as zero",
"nullText":null,
"postfix":"",
"postfixFontSize":"50%",
"prefix":"",
"prefixFontSize":"70%",
"rangeMaps":[
{
"from":"null",
"text":"N/A",
"to":"null"
}
],
"sparkline":{
"fillColor":"rgba(31, 118, 189, 0.18)",
"full":false,
"lineColor":"rgb(31, 120, 193)",
"show":false
},
"tableColumn":"",
"targets":[
{
"exemplar":true,
"expr":"sum(jvm_memory_used_bytes{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\", area=\"heap\"})*100/sum(jvm_memory_max_bytes{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\", area=\"heap\"})",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"",
"refId":"A",
"step":14400
}
],
"thresholds":"70,90",
"title":"Heap Used",
"type":"singlestat",
"valueFontSize":"70%",
"valueMaps":[
{
"op":"=",
"text":"N/A",
"value":"null"
}
],
"valueName":"current"
},
{
"cacheTimeout":null,
"colorBackground":false,
"colorValue":true,
"colors":[
"rgba(50, 172, 45, 0.97)",
"rgba(237, 129, 40, 0.89)",
"rgba(245, 54, 54, 0.9)"
],
"datasource":"${dataSource}",
"decimals":1,
"editable":true,
"error":false,
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"format":"percent",
"gauge":{
"maxValue":100,
"minValue":0,
"show":true,
"thresholdLabels":false,
"thresholdMarkers":true
},
"gridPos":{
"h":6,
"w":5,
"x":11,
"y":116
},
"id":60,
"interval":null,
"links":[

],
"mappingType":2,
"mappingTypes":[
{
"name":"value to text",
"value":1
},
{
"name":"range to text",
"value":2
}
],
"maxDataPoints":100,
"nullPointMode":"connected",
"nullText":null,
"postfix":"",
"postfixFontSize":"50%",
"prefix":"",
"prefixFontSize":"70%",
"rangeMaps":[
{
"from":"null",
"text":"N/A",
"to":"null"
},
{
"from":"-99999999999999999999999999999999",
"text":"N/A",
"to":"0"
}
],
"sparkline":{
"fillColor":"rgba(31, 118, 189, 0.18)",
"full":false,
"lineColor":"rgb(31, 120, 193)",
"show":false
},
"tableColumn":"",
"targets":[
{
"exemplar":true,
"expr":"sum(jvm_memory_used_bytes{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\", area=\"nonheap\"})*100/sum(jvm_memory_max_bytes{application=\"$application\",functionName=\"$funcName\",serverIp=~\"$instance\", area=\"nonheap\"})",
"format":"time_series",
"interval":"",
"intervalFactor":2,
"legendFormat":"",
"refId":"A",
"step":14400
}
],
"thresholds":"70,90",
"title":"Non-Heap Used",
"type":"singlestat",
"valueFontSize":"70%",
"valueMaps":[
{
"op":"=",
"text":"N/A",
"value":"null"
},
{
"op":"=",
"text":"x",
"value":""
}
],
"valueName":"current"
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":6,
"w":8,
"x":16,
"y":116
},
"hiddenSeries":false,
"id":66,
"legend":{
"alignAsTable":false,
"avg":false,
"current":false,
"max":false,
"min":false,
"rightSide":false,
"show":true,
"sideWidth":100,
"total":false,
"values":false,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"process_files_open_files{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Open Files",
"refId":"A"
},
{
"exemplar":true,
"expr":"process_files_max_files{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Max Files",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Process Open Files",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:435",
"format":"locale",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:436",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"cacheTimeout":null,
"colorBackground":false,
"colorValue":true,
"colors":[
"rgba(245, 54, 54, 0.9)",
"#5195ce",
"rgba(50, 172, 45, 0.97)"
],
"datasource":"${dataSource}",
"decimals":null,
"editable":true,
"error":false,
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"format":"dateTimeAsIso",
"gauge":{
"maxValue":100,
"minValue":0,
"show":false,
"thresholdLabels":false,
"thresholdMarkers":true
},
"gridPos":{
"h":3,
"w":6,
"x":0,
"y":119
},
"height":"",
"id":56,
"interval":null,
"links":[

],
"mappingType":1,
"mappingTypes":[
{
"name":"value to text",
"value":1
},
{
"name":"range to text",
"value":2
}
],
"maxDataPoints":100,
"nullPointMode":"connected",
"nullText":null,
"postfix":"",
"postfixFontSize":"50%",
"prefix":"",
"prefixFontSize":"70%",
"rangeMaps":[
{
"from":"null",
"text":"N/A",
"to":"null"
}
],
"sparkline":{
"fillColor":"rgba(31, 118, 189, 0.18)",
"full":false,
"lineColor":"rgb(31, 120, 193)",
"show":false
},
"tableColumn":"",
"targets":[
{
"exemplar":true,
"expr":"process_start_time_seconds{application=\"$application\",functionName=\"$funcName\", serverIp=~\"$instance\"}*1000",
"format":"time_series",
"interval":"",
"intervalFactor":2,
"legendFormat":"",
"metric":"",
"refId":"A",
"step":14400
}
],
"thresholds":"",
"title":"Start time",
"type":"singlestat",
"valueFontSize":"70%",
"valueMaps":[
{
"op":"=",
"text":"N/A",
"value":"null"
}
],
"valueName":"current"
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":7,
"w":12,
"x":0,
"y":122
},
"hiddenSeries":false,
"id":95,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"system_cpu_usage{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-System CPU Usage",
"refId":"A"
},
{
"exemplar":true,
"expr":"process_cpu_usage{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Process CPU Usage",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"CPU Usage",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:607",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:608",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":7,
"w":12,
"x":12,
"y":122
},
"hiddenSeries":false,
"id":96,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"system_load_average_1m{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Load Average [1m]",
"refId":"A"
},
{
"exemplar":true,
"expr":"system_cpu_count{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-CPU Core Size",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Load Average",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:692",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:693",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"decimals":0,
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":129
},
"hiddenSeries":false,
"id":50,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"jvm_classes_loaded_classes{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Classes Loaded",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Classes Loaded",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:777",
"decimals":0,
"format":"locale",
"label":"",
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:778",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":129
},
"hiddenSeries":false,
"id":82,
"legend":{
"avg":false,
"current":false,
"max":false,
"min":false,
"show":true,
"total":false,
"values":false,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"jvm_buffer_memory_used_bytes{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\", id=\"direct\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Used Bytes",
"refId":"A"
},
{
"exemplar":true,
"expr":"jvm_buffer_total_capacity_bytes{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\", id=\"direct\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Capacity Bytes",
"refId":"B"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Direct Buffers",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:947",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:948",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":0,
"y":137
},
"hiddenSeries":false,
"id":68,
"legend":{
"alignAsTable":true,
"avg":true,
"current":true,
"max":true,
"min":true,
"show":true,
"total":false,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"jvm_threads_daemon_threads{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Daemon",
"refId":"A"
},
{
"exemplar":true,
"expr":"jvm_threads_live_threads{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Live",
"refId":"B"
},
{
"exemplar":true,
"expr":"jvm_threads_peak_threads{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-Peak",
"refId":"C"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Threads",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1032",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:1033",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":8,
"w":12,
"x":12,
"y":137
},
"hiddenSeries":false,
"id":78,
"legend":{
"avg":false,
"current":false,
"max":false,
"min":false,
"show":true,
"total":false,
"values":false,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"irate(jvm_gc_memory_allocated_bytes_total{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}[$interval])",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-allocated",
"refId":"A"
},
{
"exemplar":true,
"expr":"irate(jvm_gc_memory_promoted_bytes_total{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}[$interval])",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-promoted",
"refId":"B"
},
{
"exemplar": true,
"expr": "sum(jvm_memory_used_bytes{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\",area=\"nonheap\"}) by (ip)",
"hide": false,
"interval": "",
"legendFormat": "{{ip}}--nonheap-used",
"refId": "C"
},
{
"exemplar": true,
"expr": "sum(jvm_memory_used_bytes{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\",area=\"heap\"}) by (ip)",
"hide": false,
"interval": "",
"legendFormat": "{{ip}}--heap-used",
"refId": "D"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"Memory Allocate/Promote",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1117",
"format":"bytes",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:1118",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":10,
"w":12,
"x":0,
"y":145
},
"hiddenSeries":false,
"id":74,
"legend":{
"alignAsTable":true,
"avg":true,
"current":false,
"hideEmpty":true,
"hideZero":true,
"max":true,
"min":true,
"show":true,
"total":true,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"clamp_min(irate(jvm_gc_pause_seconds_count{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}[1m]),0)",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-{{action}} [{{cause}}]",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"GC Count",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1202",
"format":"locale",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:1203",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},
{
"aliasColors":{

},
"bars":false,
"dashLength":10,
"dashes":false,
"datasource":"${dataSource}",
"fieldConfig":{
"defaults":{

},
"overrides":[

]
},
"fill":1,
"fillGradient":0,
"gridPos":{
"h":10,
"w":12,
"x":12,
"y":145
},
"hiddenSeries":false,
"id":76,
"legend":{
"alignAsTable":true,
"avg":true,
"current":false,
"hideEmpty":true,
"hideZero":true,
"max":true,
"min":true,
"show":true,
"total":true,
"values":true,
"sideWidth": 250
},
"lines":true,
"linewidth":1,
"links":[

],
"nullPointMode":"null as zero",
"options":{
"alertThreshold":true
},
"percentage":false,
"pluginVersion":"7.5.3",
"pointradius":5,
"points":false,
"renderer":"flot",
"seriesOverrides":[

],
"spaceLength":10,
"stack":false,
"steppedLine":false,
"targets":[
{
"exemplar":true,
"expr":"irate(jvm_gc_pause_seconds_sum{serverIp=~\"$instance\", application=\"$application\",functionName=\"$funcName\"}[1m])",
"format":"time_series",
"interval":"",
"intervalFactor":1,
"legendFormat":"{{ip}}-{{action}} [{{cause}}]",
"refId":"A"
}
],
"thresholds":[

],
"timeFrom":null,
"timeRegions":[

],
"timeShift":null,
"title":"GC Stop the World Duration",
"tooltip":{
"shared":true,
"sort":2,
"value_type":"individual"
},
"type":"graph",
"xaxis":{
"buckets":null,
"mode":"time",
"name":null,
"show":true,
"values":[

]
},
"yaxes":[
{
"$$hashKey":"object:1287",
"format":"s",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
},
{
"$$hashKey":"object:1288",
"format":"short",
"label":null,
"logBase":1,
"max":null,
"min":null,
"show":true
}
],
"yaxis":{
"align":false,
"alignLevel":null
}
},

{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 34
},
"id": 171,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_aopClientMethodTimeCount_without_methodName_bucket{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (le,serverIp))",
"legendFormat": "{{serverIp}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调出 P99-RT",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:664",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:665",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},

{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 34
},
"id": 172,
"interval": "15s",
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "sum(sum_over_time(${env}_${serviceName}_aopClientTotalMethodCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)",
"legendFormat": "total",
"interval": "",
"exemplar": true,
"instant": false,
"intervalFactor": 1,
"refId": "A"
},
{
"expr": "sum(sum_over_time(${env}_${serviceName}_aopClientTotalMethodCount_total{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30) by (methodName,serverIp)",
"legendFormat": "{{methodName}}-{{serverIp}}",
"interval": "",
"exemplar": true,
"format": "time_series",
"hide": false,
"instant": false,
"refId": "B"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调出 QPS",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"transformations": [],
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:180",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:181",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},

{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 42
},
"id": 173,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "sum(sum_over_time(${env}_${serviceName}_aopClientMethodTimeCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (serverIp,methodName) / sum(sum_over_time(${env}_${serviceName}_aopClientMethodTimeCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])) by (serverIp,methodName) ",
"legendFormat": "{{serverIp}}-{{methodName}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调出 AVG-RT",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:1320",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:1321",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
},

{
"aliasColors": {},
"dashLength": 10,
"datasource": "${dataSource}",
"fieldConfig": {
"defaults": {},
"overrides": []
},
"fill": 1,
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 42
},
"id": 174,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": true,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 250,
"total": false,
"values": true
},
"lines": true,
"linewidth": 1,
"nullPointMode": "null as zero",
"options": {
"alertThreshold": true
},
"pluginVersion": "7.5.3",
"pointradius": 2,
"renderer": "flot",
"seriesOverrides": [],
"spaceLength": 10,
"targets": [
{
"expr": "topk(10, sum(sum_over_time(${env}_${serviceName}_aopClientMethodTimeCount_sum{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,methodName) / sum(sum_over_time(${env}_${serviceName}_aopClientMethodTimeCount_count{serverIp=~\"$instance\",application=\"$application\",functionName=\"$funcName\"}[30s])/30)by(serverIp,methodName))",
"legendFormat": "{{serverIp}}-{{methodName}}",
"interval": "",
"exemplar": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Http调出 Top10 RT",
"tooltip": {
"shared": true,
"sort": 2,
"value_type": "individual"
},
"type": "graph",
"xaxis": {
"buckets": null,
"mode": "time",
"name": null,
"show": true,
"values": []
},
"yaxes": [
{
"$$hashKey": "object:376",
"format": "ms",
"label": null,
"logBase": 1,
"max": null,
"min": 0,
"show": true
},
{
"$$hashKey": "object:377",
"format": "short",
"label": null,
"logBase": 1,
"max": null,
"min": null,
"show": true
}
],
"yaxis": {
"align": false,
"alignLevel": null
},
"bars": false,
"dashes": false,
"fillGradient": 0,
"hiddenSeries": false,
"percentage": false,
"points": false,
"stack": false,
"steppedLine": false,
"timeFrom": null,
"timeShift": null
}

],
"refresh":"30s",
"schemaVersion":27,
"style":"dark",
"tags":[
"templated"
],
"templating":{
"list":[
{
"description":null,
"error":null,
"hide":2,
"label":"Application",
"name":"application",
"query":"${title}",
"skipUrlSync":false,
"type":"constant"
},
{
"allValue": null,
"current": {
"selected": true,
"tags": [],
"text": [
"All"
],
"value": [
"$__all"
]
},
"datasource": "${dataSource}",
"definition": "label_values(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\"},serverEnv)",
"description": null,
"error": null,
"hide": 0,
"includeAll": true,
"label": "环境",
"multi": true,
"name": "serverEnv",
"options": [],
"query": {
"query": "label_values(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\"},serverEnv)",
"refId": "StandardVariableQuery"
},
"refresh": 1,
"regex": "",
"skipUrlSync": false,
"sort": 0,
"tagValuesQuery": "",
"tags": [],
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"allValue": null,
"current": {
"selected": true,
"text": [
"All"
],
"value": [
"$__all"
]
},
"datasource": "${dataSource}",
"definition": "label_values(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\",serverEnv=~\"$serverEnv\"},serverIp)",
"description": null,
"error": null,
"hide": 0,
"includeAll": true,
"label": "Instance",
"multi": true,
"name": "instance",
"options": [],
"query": {
"query": "label_values(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\",serverEnv=~\"$serverEnv\"},serverIp)",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "",
"skipUrlSync": false,
"sort": 1,
"tagValuesQuery": "",
"tags": [],
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"allValue": null,
"current": {
"selected": false,
"text": "2",
"value": "2"
},
"datasource":"${dataSource}",
"definition": "query_result(count(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\",serverEnv=~\"$serverEnv\"}))",
"description": null,
"error": null,
"hide": 2,
"includeAll": false,
"label": "主机数",
"multi": false,
"name": "total",
"options": [],
"query": {
"query": "query_result(count(jvm_classes_loaded_classes{application=\"$application\",functionName=\"$funcName\",serverEnv=~\"$serverEnv\"}))",
"refId": "StandardVariableQuery"
},
"refresh": 2,
"regex": "/{} (.*) .*/",
"skipUrlSync": false,
"sort": 0,
"tagValuesQuery": "",
"tags": [],
"tagsQuery": "",
"type": "query",
"useTags": false
},
{
"auto":true,
"auto_count":1,
"auto_min":"10s",
"current":{
"selected":false,
"text":"auto",
"value":"$__auto_interval_interval"
},
"description":null,
"error":null,
"hide":2,
"label":"时间间隔",
"name":"interval",
"options":[
{
"selected":true,
"text":"auto",
"value":"$__auto_interval_interval"
},
{
"selected":false,
"text":"1m",
"value":"1m"
},
{
"selected":false,
"text":"2m",
"value":"2m"
},
{
"selected":false,
"text":"5m",
"value":"5m"
},
{
"selected":false,
"text":"10m",
"value":"10m"
},
{
"selected":false,
"text":"20m",
"value":"20m"
},
{
"selected":false,
"text":"30m",
"value":"30m"
},
{
"selected":false,
"text":"1h",
"value":"1h"
},
{
"selected":false,
"text":"2h",
"value":"2h"
},
{
"selected":false,
"text":"3h",
"value":"3h"
},
{
"selected":false,
"text":"4h",
"value":"4h"
},
{
"selected":false,
"text":"5h",
"value":"5h"
},
{
"selected":false,
"text":"6h",
"value":"6h"
},
{
"selected":false,
"text":"7h",
"value":"7h"
},
{
"selected":false,
"text":"8h",
"value":"8h"
},
{
"selected":false,
"text":"9h",
"value":"9h"
},
{
"selected":false,
"text":"10h",
"value":"10h"
},
{
"selected":false,
"text":"12h",
"value":"12h"
},
{
"selected":false,
"text":"24h",
"value":"24h"
},
{
"selected":false,
"text":"2d",
"value":"2d"
},
{
"selected":false,
"text":"3d",
"value":"3d"
},
{
"selected":false,
"text":"4d",
"value":"4d"
},
{
"selected":false,
"text":"5d",
"value":"5d"
},
{
"selected":false,
"text":"6d",
"value":"6d"
},
{
"selected":false,
"text":"7d",
"value":"7d"
}
],
"query":"1m,2m,5m,10m,20m,30m,1h,2h,3h,4h,5h,6h,7h,8h,9h,10h,12h,24h,2d,3d,4d,5d,6d,7d",
"queryValue":"",
"refresh":2,
"skipUrlSync":false,
"type":"interval"
},
{
"description": null,
"error": null,
"hide": 2,
"label": "容器名",
"name": "containerName",
"query": "${containerName}",
"skipUrlSync": false,
"type": "constant"
},
{
"description":null,
"error":null,
"hide":2,
"label":"funcName",
"name":"funcName",
"query":"${funcName}",
"skipUrlSync":false,
"type":"constant"
}
]
},
"time":{
"from":"now-5m",
"to":"now"
},
"timepicker":{
"refresh_intervals":[
"5s",
"10s",
"30s",
"1m",
"5m",
"15m",
"30m",
"1h",
"2h",
"1d"
],
"time_options":[
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
"timezone":"browser",
"title":"mifaas业务监控-${application}",
"uid":"${uid}",
"version":222
},
"overwrite":false,
"folderId":${folderId},
"folderUid":"${folderUid}",
"message":"mimonitor mifaas 1.1"
}