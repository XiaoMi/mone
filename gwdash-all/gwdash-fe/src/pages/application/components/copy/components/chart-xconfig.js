
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
      color: ['#77DAC2'],
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'line' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '8%',
        bottom: '3%',
        containLabel: true
      },
      // legend icon颜色
      legend: {
        data: [
          {
            name: '机器数量',
            textStyle: {
              color: '#77DAC2'
            }
          }
        ]
      },
      // 工具盒子
      toolbox: {
        feature: {
          dataView: { show: true },
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
      xAxis: {
        type: 'time',
        name: 'TIME',
        splitLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        name: '机器数量',
        splitLine: {
          show: false
        }
      },
      series: [
        {
          name: '机器数量',
          type: 'line',
          showSymbol: false,
          data: that.showData,
          itemStyle: {
            normal: {
              lineStyle: { color: '#77DAC2' }
            }
          }
        }
      ]
    }
  }
})()

export default config
