
/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

let config = (function () {
  return function (that, myFullShow) {
    return {
      // legend icon颜色
      color: ['#FF0000', '#93b7e3'],
      legend: {
        data: [
          {
            name: 'real',
            textStyle: {
              color: '#FF0000'
            }
          },
          {
            name: 'predict',
            textStyle: {
              color: '#93b7e3'
            }
          }
        ]
      },
      // 聚焦后显示数据
      tooltip: {
        trigger: 'axis'
      },
      // 工具盒子
      toolbox: {
        feature: {
          dataView: { show: true, lang: ['数据视图', '关闭', '刷新'] },
          saveAsImage: { show: !myFullShow },
          myFull: {
            show: myFullShow || false,
            title: '全屏展示',
            icon: 'image://xx_replace_xx',
            onclick: () => {
              that.chartDialogVisible = true
            }
          }
        }
      },
      // 缩放条
      dataZoom: [
        {
          type: 'inside',
          show: true,
          // 多x轴中的第一根
          xAxisIndex: [0],
          start: 0,
          end: 100
        },
        {
          type: 'slider',
          xAxisIndex: [0],
          start: 1,
          end: 100,
          handleSize: '80%',
          bottom: 0,
          handleStyle: {
            color: '#ccc',
            shadowBlur: 3,
            shadowColor: 'rgba(0, 0, 0, 0.6)',
            shadowOffsetX: 2,
            shadowOffsetY: 2
          }
        }
      ],
      // 坐标轴距离边框距离
      grid: {
        // left/top/right/bottom
        // x/y/x2/y2
        left: !myFullShow ? 90 : 60
      },
      xAxis: {
        type: 'time',
        name: 'TIME',
        splitLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        name: 'QPM',
        splitLine: {
          show: false
        }
      },
      series: [
        {
          name: 'real',
          type: 'line',
          showSymbol: false,
          data: that.realData
        },
        {
          name: 'predict',
          type: 'line',
          showSymbol: false,
          itemStyle: {
            normal: {
              lineStyle: { color: '#93b7e3' }
            }
          },
          data: that.predictData
        }
      ]
    }
  }
})()

export default config
