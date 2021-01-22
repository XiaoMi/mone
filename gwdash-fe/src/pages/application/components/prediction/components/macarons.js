/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

var macarons = {
  "color": [
      "#a5e7f0",
      "#409eff",
      "#93b7e3",
      "#edafda",
      "#333333"
  ],
  "backgroundColor": "rgba(0,0,0,0)",
  "textStyle": {},
  "title": {
      "textStyle": {
          "color": "rgba(64,158,255,0.82)"
      },
      "subtextStyle": {
          "color": "#aaaaaa"
      }
  },
  "line": {
      "itemStyle": {
          "normal": {
              "borderWidth": 1
          }
      },
      "lineStyle": {
          "normal": {
              "width": 2
          }
      },
      "symbolSize": 3,
      "symbol": "emptyCircle",
      "smooth": true
  },
  "radar": {
      "itemStyle": {
          "normal": {
              "borderWidth": 1
          }
      },
      "lineStyle": {
          "normal": {
              "width": 2
          }
      },
      "symbolSize": 3,
      "symbol": "emptyCircle",
      "smooth": true
  },
  "bar": {
      "itemStyle": {
          "normal": {
              "barBorderWidth": 0,
              "barBorderColor": "#ccc"
          },
          "emphasis": {
              "barBorderWidth": 0,
              "barBorderColor": "#ccc"
          }
      }
  },
  "pie": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "scatter": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "boxplot": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "parallel": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "sankey": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "funnel": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "gauge": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          },
          "emphasis": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      }
  },
  "candlestick": {
      "itemStyle": {
          "normal": {
              "color": "#d87a80",
              "color0": "#2ec7c9",
              "borderColor": "#d87a80",
              "borderColor0": "#2ec7c9",
              "borderWidth": 1
          }
      }
  },
  "graph": {
      "itemStyle": {
          "normal": {
              "borderWidth": 0,
              "borderColor": "#ccc"
          }
      },
      "lineStyle": {
          "normal": {
              "width": 1,
              "color": "#aaaaaa"
          }
      },
      "symbolSize": 3,
      "symbol": "emptyCircle",
      "smooth": true,
      "color": [
          "#a5e7f0",
          "#409eff",
          "#93b7e3",
          "#edafda",
          "#333333"
      ],
      "label": {
          "normal": {
              "textStyle": {
                  "color": "#eeeeee"
              }
          }
      }
  },
  "map": {
      "itemStyle": {
          "normal": {
              "areaColor": "#dddddd",
              "borderColor": "#eeeeee",
              "borderWidth": 0.5
          },
          "emphasis": {
              "areaColor": "rgba(254,153,78,1)",
              "borderColor": "#444",
              "borderWidth": 1
          }
      },
      "label": {
          "normal": {
              "textStyle": {
                  "color": "#d87a80"
              }
          },
          "emphasis": {
              "textStyle": {
                  "color": "rgb(100,0,0)"
              }
          }
      }
  },
  "geo": {
      "itemStyle": {
          "normal": {
              "areaColor": "#dddddd",
              "borderColor": "#eeeeee",
              "borderWidth": 0.5
          },
          "emphasis": {
              "areaColor": "rgba(254,153,78,1)",
              "borderColor": "#444",
              "borderWidth": 1
          }
      },
      "label": {
          "normal": {
              "textStyle": {
                  "color": "#d87a80"
              }
          },
          "emphasis": {
              "textStyle": {
                  "color": "rgb(100,0,0)"
              }
          }
      }
  },
  "categoryAxis": {
      "axisLine": {
          "show": true,
          "lineStyle": {
              "color": "#008acd"
          }
      },
      "axisTick": {
          "show": true,
          "lineStyle": {
              "color": "#333"
          }
      },
      "axisLabel": {
          "show": true,
          "textStyle": {
              "color": "#333"
          }
      },
      "splitLine": {
          "show": false,
          "lineStyle": {
              "color": [
                  "#eee"
              ]
          }
      },
      "splitArea": {
          "show": false,
          "areaStyle": {
              "color": [
                  "rgba(250,250,250,0.3)",
                  "rgba(200,200,200,0.3)"
              ]
          }
      }
  },
  "valueAxis": {
      "axisLine": {
          "show": true,
          "lineStyle": {
              "color": "#008acd"
          }
      },
      "axisTick": {
          "show": true,
          "lineStyle": {
              "color": "#333"
          }
      },
      "axisLabel": {
          "show": true,
          "textStyle": {
              "color": "#333"
          }
      },
      "splitLine": {
          "show": false,
          "lineStyle": {
              "color": [
                  "#eee"
              ]
          }
      },
      "splitArea": {
          "show": true,
          "areaStyle": {
              "color": [
                  "rgba(250,250,250,0.3)",
                  "rgba(200,200,200,0.3)"
              ]
          }
      }
  },
  "logAxis": {
      "axisLine": {
          "show": true,
          "lineStyle": {
              "color": "#008acd"
          }
      },
      "axisTick": {
          "show": true,
          "lineStyle": {
              "color": "#333"
          }
      },
      "axisLabel": {
          "show": true,
          "textStyle": {
              "color": "#333"
          }
      },
      "splitLine": {
          "show": false,
          "lineStyle": {
              "color": [
                  "#eee"
              ]
          }
      },
      "splitArea": {
          "show": true,
          "areaStyle": {
              "color": [
                  "rgba(250,250,250,0.3)",
                  "rgba(200,200,200,0.3)"
              ]
          }
      }
  },
  "timeAxis": {
      "axisLine": {
          "show": true,
          "lineStyle": {
              "color": "#008acd"
          }
      },
      "axisTick": {
          "show": true,
          "lineStyle": {
              "color": "#333"
          }
      },
      "axisLabel": {
          "show": true,
          "textStyle": {
              "color": "#333"
          }
      },
      "splitLine": {
          "show": false,
          "lineStyle": {
              "color": [
                  "#eee"
              ]
          }
      },
      "splitArea": {
          "show": false,
          "areaStyle": {
              "color": [
                  "rgba(250,250,250,0.3)",
                  "rgba(200,200,200,0.3)"
              ]
          }
      }
  },
  "toolbox": {
      "iconStyle": {
          "normal": {
              "borderColor": "#2ec7c9"
          },
          "emphasis": {
              "borderColor": "#18a4a6"
          }
      }
  },
  "legend": {
      "textStyle": {
          "color": "#333333"
      }
  },
  "tooltip": {
      "axisPointer": {
          "lineStyle": {
              "color": "#008acd",
              "width": "1"
          },
          "crossStyle": {
              "color": "#008acd",
              "width": "1"
          }
      }
  },
  "timeline": {
      "lineStyle": {
          "color": "#008acd",
          "width": 1
      },
      "itemStyle": {
          "normal": {
              "color": "#008acd",
              "borderWidth": 1
          },
          "emphasis": {
              "color": "#a9334c"
          }
      },
      "controlStyle": {
          "normal": {
              "color": "#008acd",
              "borderColor": "#008acd",
              "borderWidth": 0.5
          },
          "emphasis": {
              "color": "#008acd",
              "borderColor": "#008acd",
              "borderWidth": 0.5
          }
      },
      "checkpointStyle": {
          "color": "#2ec7c9",
          "borderColor": "rgba(46,199,201,0.4)"
      },
      "label": {
          "normal": {
              "textStyle": {
                  "color": "#008acd"
              }
          },
          "emphasis": {
              "textStyle": {
                  "color": "#008acd"
              }
          }
      }
  },
  "visualMap": {
      "color": [
          "#5ab1ef",
          "#e0ffff"
      ]
  },
  "dataZoom": {
      "backgroundColor": "rgba(47,69,84,0)",
      "dataBackgroundColor": "rgba(239,239,255,1)",
      "fillerColor": "rgba(182,162,222,0.2)",
      "handleColor": "#008acd",
      "handleSize": "100%",
      "textStyle": {
          "color": "#333333"
      }
  },
  "markPoint": {
      "label": {
          "normal": {
              "textStyle": {
                  "color": "#eeeeee"
              }
          },
          "emphasis": {
              "textStyle": {
                  "color": "#eeeeee"
              }
          }
      }
  }
}
export default macarons





