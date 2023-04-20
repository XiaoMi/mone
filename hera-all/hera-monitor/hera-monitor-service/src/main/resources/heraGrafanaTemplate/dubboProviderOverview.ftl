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
              "options": "总调用数"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "总体平均可用率"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "unit",
                "value": "percentunit"
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "总体QPS"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "错误数"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "总体平均P95"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
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
              "options": "总体平均P99"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "unit",
                "value": "ms"
              }
            ]
          }
        ]
      },
      "gridPos": {
        "h": 4,
        "w": 24,
        "x": 0,
        "y": 0
      },
      "id": 5,
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
          "exemplar": true,
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange]))",
          "format": "table",
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
          "expr": "clamp_max(sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledSuccessCount_total{application=\"$application\"}[$timeRange])) / \nsum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])),1)",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[${query0}s])/${query0})",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange]))",
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
          "expr": "histogram_quantile(0.95,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le))",
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
          "expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le))",
          "format": "table",
          "hide": false,
          "instant": true,
          "interval": "",
          "legendFormat": "",
          "refId": "F"
        }
      ],
      "title": "总览",
      "transformations": [
        {
          "id": "filterFieldsByName",
          "options": {
            "include": {
              "names": [
                "Value #A",
                "Value #B",
                "Value #C",
                "Value #D",
                "Value #E",
                "Value #F"
              ]
            }
          }
        },
        {
          "id": "concatenate",
          "options": {}
        },
        {
          "id": "organize",
          "options": {
            "excludeByName": {},
            "indexByName": {},
            "renameByName": {
              "Value #A": "总调用数",
              "Value #B": "总体平均可用率",
              "Value #C": "总体QPS",
              "Value #D": "错误数",
              "Value #E": "总体平均P95",
              "Value #F": "总体平均P99"
            }
          }
        }
      ],
      "type": "table"
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
              "options": "服务名"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "custom.width",
                "value": 400
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "方法名"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "custom.width",
                "value": 300
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "P99耗时"
            },
            "properties": [
              {
                "id": "custom.width",
                "value": 200
              },
              {
                "id": "unit",
                "value": "ms"
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
              },
              {
                "id": "color"
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
                      "value": 1000
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
              "options": "QPS"
            },
            "properties": [
              {
                "id": "custom.width",
                "value": 150
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
              "options": "可用性"
            },
            "properties": [
              {
                "id": "custom.displayMode",
                "value": "color-background-solid"
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
                      "color": "red",
                      "value": null
                    },
                    {
                      "color": "semi-dark-green",
                      "value": 0.8
                    }
                  ]
                }
              },
              {
                "id": "unit",
                "value": "percentunit"
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
              "options": "错误数"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "displayName",
                "value": "$timeRange 错误数"
              },
              {
                "id": "noValue",
                "value": "0"
              },
              {
                "id": "color",
                "value": {
                  "fixedColor": "semi-dark-orange",
                  "mode": "thresholds"
                }
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
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
                      "color": "semi-dark-orange",
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
              "options": "P95耗时"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "color"
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
              },
              {
                "id": "unit",
                "value": "ms"
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
                      "value": 1000
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
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "displayName",
                "value": "$timeRange 总数"
              }
            ]
          }
        ]
      },
      "gridPos": {
        "h": 11,
        "w": 24,
        "x": 0,
        "y": 4
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
          "expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le,serviceName))",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[${query0}s])/${query0}) by (serviceName)",
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
          "expr": "(1 - ((sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (serviceName)) / (sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (serviceName))) )or 0*(sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (serviceName))+1\n",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (serviceName) or 0*absent(sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (serviceName))",
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
          "expr": "histogram_quantile(0.95,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le,serviceName))",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (serviceName)",
          "format": "table",
          "hide": false,
          "instant": true,
          "interval": "",
          "legendFormat": "",
          "refId": "F"
        }
      ],
      "title": "Dubbo Provider 总览（service级）",
      "transformations": [
        {
          "id": "merge",
          "options": {}
        },
        {
          "id": "filterFieldsByName",
          "options": {
            "include": {
              "names": [
                "methodName",
                "serviceName",
                "Value #A",
                "Value #B",
                "Value #C",
                "Value #D",
                "Value #E",
                "Value #F"
              ]
            }
          }
        },
        {
          "id": "organize",
          "options": {
            "excludeByName": {},
            "indexByName": {
              "Value #A": 6,
              "Value #B": 2,
              "Value #C": 7,
              "Value #D": 4,
              "Value #E": 5,
              "Value #F": 3,
              "methodName": 1,
              "serviceName": 0
            },
            "renameByName": {
              "Value #A": "P99耗时",
              "Value #B": "QPS",
              "Value #C": "可用性",
              "Value #D": "错误数",
              "Value #E": "P95耗时",
              "methodName": "方法名",
              "serviceName": "服务名"
            }
          }
        }
      ],
      "type": "table"
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
              "options": "服务名"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "custom.width",
                "value": 400
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "方法名"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "custom.width",
                "value": 300
              }
            ]
          },
          {
            "matcher": {
              "id": "byName",
              "options": "P99耗时"
            },
            "properties": [
              {
                "id": "custom.width",
                "value": 200
              },
              {
                "id": "unit",
                "value": "ms"
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
              },
              {
                "id": "color"
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
                      "value": 1000
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
              "options": "QPS"
            },
            "properties": [
              {
                "id": "custom.width",
                "value": 150
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
              "options": "可用性"
            },
            "properties": [
              {
                "id": "custom.displayMode",
                "value": "color-background-solid"
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
                      "color": "red",
                      "value": null
                    },
                    {
                      "color": "semi-dark-green",
                      "value": 0.8
                    }
                  ]
                }
              },
              {
                "id": "unit",
                "value": "percentunit"
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
              "options": "错误数"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "displayName",
                "value": "$timeRange 错误数"
              },
              {
                "id": "noValue",
                "value": "0"
              },
              {
                "id": "color",
                "value": {
                  "fixedColor": "semi-dark-orange",
                  "mode": "thresholds"
                }
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
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
                      "color": "semi-dark-orange",
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
              "options": "P95耗时"
            },
            "properties": [
              {
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "color"
              },
              {
                "id": "custom.displayMode",
                "value": "color-text"
              },
              {
                "id": "unit",
                "value": "ms"
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
                      "value": 1000
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
                "id": "custom.align",
                "value": "center"
              },
              {
                "id": "displayName",
                "value": "$timeRange 总数"
              }
            ]
          }
        ]
      },
      "gridPos": {
        "h": 11,
        "w": 24,
        "x": 0,
        "y": 15
      },
      "id": 3,
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
          "exemplar": true,
          "expr": "histogram_quantile(0.99,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le,serviceName,methodName))",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[${query0}s])/${query0}) by (methodName,serviceName)",
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
          "expr": "(1 - ((sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (methodName)) / (sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (methodName))) )or 0*(sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (methodName))+1",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (methodName) or 0*absent(sum(sum_over_time(${env}_${serviceName}_dubboProviderError_total{application=\"$application\"}[$timeRange])) by (methodName))",
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
          "expr": "histogram_quantile(0.95,sum(sum_over_time(${env}_${serviceName}_dubboProviderCount_bucket{application=\"$application\"}[$timeRange])) by (le,serviceName,methodName))",
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
          "expr": "sum(sum_over_time(${env}_${serviceName}_dubboMethodCalledCount_total{application=\"$application\"}[$timeRange])) by (methodName,serviceName)",
          "format": "table",
          "hide": false,
          "instant": true,
          "interval": "",
          "legendFormat": "",
          "refId": "F"
        }
      ],
      "title": "Dubbo Provider 总览（method级）",
      "transformations": [
        {
          "id": "merge",
          "options": {}
        },
        {
          "id": "filterFieldsByName",
          "options": {
            "include": {
              "names": [
                "methodName",
                "serviceName",
                "Value #A",
                "Value #B",
                "Value #C",
                "Value #D",
                "Value #E",
                "Value #F"
              ]
            }
          }
        },
        {
          "id": "organize",
          "options": {
            "excludeByName": {},
            "indexByName": {
              "Value #A": 6,
              "Value #B": 2,
              "Value #C": 7,
              "Value #D": 4,
              "Value #E": 5,
              "Value #F": 3,
              "methodName": 1,
              "serviceName": 0
            },
            "renameByName": {
              "Value #A": "P99耗时",
              "Value #B": "QPS",
              "Value #C": "可用性",
              "Value #D": "错误数",
              "Value #E": "P95耗时",
              "methodName": "方法名",
              "serviceName": "服务名"
            }
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
          "text": "30m",
          "value": "30m"
        },
        "hide": 0,
        "name": "timeRange",
        "options": [
          {
            "selected": false,
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
            "selected": true,
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
        "queryValue": "",
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
  "title": "Hera-DubboProvider总览",
  "uid": "hera-dubboprovider-overview",
  "version": 38,
  "weekStart": ""
},
"overwrite":false,
"folderUid":"Hera",
"message":"Hera-DubboProvider总览V1.0"
}