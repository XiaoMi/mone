
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

import echarts from 'echarts'
let colors = ['#5793f3', '#d14a61', '#675bba']

let config = (option) => {
  return {
    tooltip: {
      formatter: '{b} : {c}%'
    },
    series: [
      {
        // name: option.forName,
        type: 'gauge',
        min: 0,
        max: option.cpuCount || 100,
        detail: {
          fontSize: '18',
          formatter: '{value}%'
        },
        data: [
          {
            value: option.value,
            name: option.name
          }
        ]
      }
    ]
  }
}
function getFormatTime (timestamp) {
  let d = new Date(timestamp)
  let min = d.getMinutes() > 9 ? d.getMinutes() : '0' + d.getMinutes()
  return `${d.getHours()}:${min}`
}
let usageConfig = (option, isMemory) => {
  if (!option || option.length === 0) return
  let times = option.map(it => getFormatTime(it.ctime))
  let data = []
  if (isMemory) {
    data = option.map(it => (it.memoryUsage * 100).toFixed(2))
  } else {
    data = option.map(it => (it.cpuUsage * 100).toFixed(2))
  }
  let title = isMemory ? 'memoryUsage' : 'cpuUsage'
  let seriesName = isMemory ? 'memory' : 'cpu'
  return {
    tooltip: {
      trigger: 'axis',
      position: function (pt) {
        return [pt[0], '10%']
      },
      formatter: 'time:{b} </br>value: {c}%'
    },
    title: {
      left: 'left',
      text: title
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: times
    },
    yAxis: {
      type: 'value',
      boundaryGap: [0, '100%'],
      axisLabel: {
        formatter: function (value) {
          return value + '%'
        }
      }
    },
    dataZoom: [{
      type: 'inside',
      start: 0,
      end: 100
    }, {
      start: 0,
      end: 10,
      handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
      handleSize: '80%',
      handleStyle: {
        color: '#fff',
        shadowBlur: 3,
        shadowColor: 'rgba(0, 0, 0, 0.6)',
        shadowOffsetX: 2,
        shadowOffsetY: 2
      }
    }],
    series: [
      {
        name: seriesName,
        type: 'line',
        smooth: true,
        symbol: 'none',
        sampling: 'average',
        itemStyle: {
          color: 'rgb(255, 70, 131)'
        },
        // label: {
        //   show: true,
        //   formatter: function (d) {
        //     console.log(d)
        //     return d.data + '%'
        //   }
        // },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
            offset: 0,
            color: 'rgb(255, 158, 68)'
          }, {
            offset: 1,
            color: 'rgb(255, 70, 131)'
          }])
        },
        data: data
      }
    ]
  }
}

export { config, usageConfig }
