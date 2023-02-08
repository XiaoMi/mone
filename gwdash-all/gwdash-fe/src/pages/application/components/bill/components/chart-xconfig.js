
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
          type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: [
        {
          type: 'category',
          data: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
          axisTick: {
            alignWithLabel: true
          },
          name: '月'
        }
      ],
      yAxis: [
        {
          type: 'value',
          name: 'CPU花销（元）',
          nameLocation: 'end'
        }
      ],
      series: [
        {
          name: 'CPU花销',
          type: 'bar',
          barWidth: '60%',
          data: that.showData,
          itemStyle: { // 上方显示数值
            normal: {
              label: {
                show: true, // 开启显示
                position: 'top', // 在上方显示
                textStyle: { // 数值样式
                  color: '#666666',
                  fontSize: 12
                }
              }
            }
          }
        }
      ]
    }
  }
})()

export default config
