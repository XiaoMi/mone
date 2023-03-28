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
"description": "Docker monitoring with Prometheus and cAdvisor",
"editable": true,
"fiscalYearStartMonth": 0,
"gnetId": 193,
"graphTooltip": 1,
"links": [],
"liveNow": false,
"panels": [
{
"colorBackground": false,
"colorValue": false,
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editable": true,
"error": false,
"format": "none",
"gauge": {
"maxValue": 100,
"minValue": 0,
"show": false,
"thresholdLabels": false,
"thresholdMarkers": true
},
"gridPos": {
"h": 3,
"w": 8,
"x": 0,
"y": 0
},
"height": "20",
"id": 7,
"isNew": true,
"links": [],
"mappingType": 1,
"mappingTypes": [
{
"name": "value to text",
"value": 1
},
{
"name": "range to text",
"value": 2
}
],
"maxDataPoints": 100,
"nullPointMode": "connected",
"postfix": "",
"postfixFontSize": "50%",
"prefix": "",
"prefixFontSize": "50%",
"rangeMaps": [
{
"from": "null",
"text": "N/A",
"to": "null"
}
],
"sparkline": {
"fillColor": "rgba(31, 118, 189, 0.18)",
"full": false,
"lineColor": "rgb(31, 120, 193)",
"show": false
},
"tableColumn": "",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "count(container_last_seen{image!=\"\",ip=\"$Node\"})",
"interval": "",
"intervalFactor": 2,
"legendFormat": "",
"metric": "container_last_seen",
"refId": "A",
"step": 240
}
],
"thresholds": "",
"title": "Running containers",
"transparent": true,
"type": "grafana-singlestat-panel",
"valueFontSize": "80%",
"valueMaps": [
{
"op": "=",
"text": "N/A",
"value": "null"
}
],
"valueName": "avg"
},
{
"colorBackground": false,
"colorValue": false,
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editable": true,
"error": false,
"format": "mbytes",
"gauge": {
"maxValue": 100,
"minValue": 0,
"show": false,
"thresholdLabels": false,
"thresholdMarkers": true
},
"gridPos": {
"h": 3,
"w": 8,
"x": 8,
"y": 0
},
"height": "20",
"id": 5,
"isNew": true,
"links": [],
"mappingType": 1,
"mappingTypes": [
{
"name": "value to text",
"value": 1
},
{
"name": "range to text",
"value": 2
}
],
"maxDataPoints": 100,
"nullPointMode": "connected",
"postfix": "",
"postfixFontSize": "50%",
"prefix": "",
"prefixFontSize": "50%",
"rangeMaps": [
{
"from": "null",
"text": "N/A",
"to": "null"
}
],
"sparkline": {
"fillColor": "rgba(31, 118, 189, 0.18)",
"full": false,
"lineColor": "rgb(31, 120, 193)",
"show": false
},
"tableColumn": "",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(container_memory_usage_bytes{image!=\"\",ip=\"$Node\"})/1024/1024",
"interval": "",
"intervalFactor": 2,
"legendFormat": "",
"metric": "container_memory_usage_bytes",
"refId": "A",
"step": 240
}
],
"thresholds": "",
"title": "Total Memory Usage",
"transparent": true,
"type": "grafana-singlestat-panel",
"valueFontSize": "80%",
"valueMaps": [
{
"op": "=",
"text": "N/A",
"value": "null"
}
],
"valueName": "current"
},
{
"colorBackground": false,
"colorValue": false,
"colors": [
"rgba(245, 54, 54, 0.9)",
"rgba(237, 129, 40, 0.89)",
"rgba(50, 172, 45, 0.97)"
],
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editable": true,
"error": false,
"format": "percent",
"gauge": {
"maxValue": 100,
"minValue": 0,
"show": false,
"thresholdLabels": false,
"thresholdMarkers": true
},
"gridPos": {
"h": 3,
"w": 8,
"x": 16,
"y": 0
},
"height": "20",
"id": 6,
"isNew": true,
"links": [],
"mappingType": 1,
"mappingTypes": [
{
"name": "value to text",
"value": 1
},
{
"name": "range to text",
"value": 2
}
],
"maxDataPoints": 100,
"nullPointMode": "connected",
"postfix": "",
"postfixFontSize": "50%",
"prefix": "",
"prefixFontSize": "50%",
"rangeMaps": [
{
"from": "null",
"text": "N/A",
"to": "null"
}
],
"sparkline": {
"fillColor": "rgba(31, 118, 189, 0.18)",
"full": false,
"lineColor": "rgb(31, 120, 193)",
"show": false
},
"tableColumn": "",
"targets": [
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"exemplar": true,
"expr": "sum(rate(container_cpu_user_seconds_total{image!=\"\",ip=\"$Node\"}[5m]) * 100)",
"interval": "",
"intervalFactor": 2,
"legendFormat": "",
"metric": "container_memory_usage_bytes",
"refId": "A",
"step": 240
}
],
"thresholds": "",
"title": "Total CPU Usage",
"transparent": true,
"type": "grafana-singlestat-panel",
"valueFontSize": "80%",
"valueMaps": [
{
"op": "=",
"text": "N/A",
"value": "null"
}
],
"valueName": "current"
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
"decimals": 2,
"editable": true,
"error": false,
"fill": 1,
"fillGradient": 0,
"grid": {},
"gridPos": {
"h": 8,
"w": 24,
"x": 0,
"y": 3
},
"hiddenSeries": false,
"id": 2,
"isNew": true,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 300,
"sort": "avg",
"sortDesc": false,
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
"expr": "sum(rate(container_cpu_user_seconds_total{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}[2m]) * 100) by (pod)",
"hide": false,
"interval": "60",
"intervalFactor": 2,
"legendFormat": "{{pod}}",
"metric": "cpu",
"range": true,
"refId": "A",
"step": 10
}
],
"thresholds": [],
"timeRegions": [],
"title": "CPU Usage",
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
"$$hashKey": "object:184",
"format": "percent",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:185",
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
"decimals": 2,
"editable": true,
"error": false,
"fill": 1,
"fillGradient": 0,
"grid": {},
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 11
},
"hiddenSeries": false,
"id": 1,
"isNew": true,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": false,
"min": false,
"rightSide": true,
"show": true,
"sideWidth": 300,
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
"expr": "sum(container_memory_usage_bytes{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}) by (pod)",
"hide": false,
"interval": "60",
"intervalFactor": 2,
"legendFormat": "{{pod}}",
"metric": "container_memory_usage_bytes",
"range": true,
"refId": "A",
"step": 10
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(container_memory_rss{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}) by (pod)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}} -- rss(去除cache/buffer)",
"range": true,
"refId": "B"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Memory Usage",
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
"$$hashKey": "object:262",
"format": "bytes",
"label": "",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:263",
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
"y": 11
},
"hiddenSeries": false,
"id": 11,
"legend": {
"alignAsTable": true,
"avg": true,
"current": true,
"max": false,
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
"pluginVersion": "9.2.0",
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
"expr": "sum(container_memory_rss{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}) by (pod)/sum(container_spec_memory_limit_bytes{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}) by (pod)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Memory  Usage Ratio",
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
"editable": true,
"error": false,
"fill": 1,
"fillGradient": 0,
"grid": {},
"gridPos": {
"h": 8,
"w": 12,
"x": 0,
"y": 19
},
"hiddenSeries": false,
"id": 3,
"isNew": true,
"legend": {
"avg": false,
"current": false,
"max": false,
"min": false,
"show": true,
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
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_network_receive_bytes_total{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}[5m])) by (pod,interface)",
"hide": false,
"interval": "60",
"intervalFactor": 2,
"legendFormat": "{{pod}} -- {{interface}}",
"metric": "container_network_receive_bytes_total",
"range": true,
"refId": "A",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_network_receive_bytes_total{image!=\"\",ip=\"$Node\",container=\"POD\",interface=\"eth0\",pod=~\"$pod\"}[5m])) by (pod,interface)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}} -- {{interface}}",
"range": true,
"refId": "B"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Network Rx",
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
"$$hashKey": "object:336",
"format": "Bps",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:337",
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
"editable": true,
"error": false,
"fill": 1,
"fillGradient": 0,
"grid": {},
"gridPos": {
"h": 8,
"w": 12,
"x": 12,
"y": 19
},
"hiddenSeries": false,
"id": 4,
"isNew": true,
"legend": {
"avg": false,
"current": false,
"max": false,
"min": false,
"show": true,
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
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_network_transmit_bytes_total{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}[5m])) by (pod,interface)",
"hide": false,
"interval": "60",
"intervalFactor": 2,
"legendFormat": "{{pod}} -- {{interface}}",
"range": true,
"refId": "A",
"step": 20
},
{
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"editorMode": "code",
"exemplar": true,
"expr": "sum(irate(container_network_receive_bytes_total{image!=\"\",ip=\"$Node\",container=\"POD\",interface=\"eth0\",pod=~\"$pod\"}[5m])) by (pod,interface)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}} -- {{interface}}",
"range": true,
"refId": "B"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Network Tx",
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
"$$hashKey": "object:412",
"format": "Bps",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:413",
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
"h": 10,
"w": 24,
"x": 0,
"y": 27
},
"hiddenSeries": false,
"id": 9,
"legend": {
"avg": false,
"current": false,
"max": false,
"min": false,
"show": true,
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
"pluginVersion": "9.2.0",
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
"expr": "sum(container_cpu_load_average_10s{system=\"mione\",image != \"\",ip=\"$Node\",pod=~\"$pod\"} /1000) by (pod)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Load Average",
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
"$$hashKey": "object:1285",
"format": "short",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:1286",
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
"y": 37
},
"hiddenSeries": false,
"id": 13,
"legend": {
"avg": false,
"current": false,
"max": false,
"min": false,
"show": true,
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
"pluginVersion": "9.2.0",
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
"expr": "sum(rate(container_fs_reads_bytes_total{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}[1m])) without (device)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Container File System Read Rate",
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
"$$hashKey": "object:184",
"format": "bytes",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:185",
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
"y": 37
},
"hiddenSeries": false,
"id": 15,
"legend": {
"avg": false,
"current": false,
"max": false,
"min": false,
"show": true,
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
"pluginVersion": "9.2.0",
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
"expr": "sum(rate(container_fs_writes_bytes_total{image!=\"\",ip=\"$Node\",pod=~\"$pod\"}[1m])) without (device)",
"hide": false,
"interval": "60",
"legendFormat": "{{pod}}",
"range": true,
"refId": "A"
}
],
"thresholds": [],
"timeRegions": [],
"title": "Container File System Write Rate",
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
"$$hashKey": "object:1022",
"format": "bytes",
"logBase": 1,
"show": true
},
{
"$$hashKey": "object:1023",
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
"refresh": "5s",
"schemaVersion": 37,
"style": "dark",
"tags": [
"docker"
],
"templating": {
"list": [
{
"current": {
"selected": false,
"text": "10.53.128.150",
"value": "10.53.128.150"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(up{job=~\"mione-china-cadvisor-k8s|mione-china|mione-youpin\"},ip)",
"hide": 0,
"includeAll": false,
"label": "节点",
"multi": false,
"name": "Node",
"options": [],
"query": {
"query": "label_values(up{job=~\"mione-china-cadvisor-k8s|mione-china|mione-youpin\"},ip)",
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
"text": "All",
"value": "$__all"
},
"datasource": {
"type": "prometheus",
"uid": "${prometheusUid}"
},
"definition": "label_values(container_memory_rss{image!=\"\",ip=\"$Node\",container !=\"POD\"},name)",
"hide": 2,
"includeAll": true,
"label": "name",
"multi": true,
"name": "name",
"options": [],
"query": {
"query": "label_values(container_memory_rss{image!=\"\",ip=\"$Node\",container !=\"POD\"},name)",
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
"definition": "label_values(container_memory_rss{image!=\"\",ip=\"$Node\"},pod)",
"hide": 0,
"includeAll": true,
"multi": true,
"name": "pod",
"options": [],
"query": {
"query": "label_values(container_memory_rss{image!=\"\",ip=\"$Node\"},pod)",
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
"from": "now-15m",
"to": "now"
},
"timepicker": {
"refresh_intervals": [
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
"title": "Hera容器监控",
"uid": "hera-docker-monitor",
"version": 15,
"weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera容器监控V1.0"
}