
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
    const seriesData = Object.keys(that.environment).map((it) => {
      let obj = {}
      obj.name = that.envType.filter(item => item.type === it)[0].name
      obj.value = that.environment[it]
      return obj
    })
    return {
      title: {
        text: '各环境发布占比',
        top: '3%',
        left: '2%',
        textStyle: {
          // 文字颜色
          color: 'black',
          // 字体风格,'normal','italic','oblique'
          // fontStyle:'normal',
          // //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
          // fontWeight:'bold',
          // //字体系列
          // fontFamily:'sans-serif',
          // 字体大小
          fontSize: 16
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      color: ['#5bf13f', '#dfe725', '#37a2da', '#e7bcf3', '#BBCF57', '#fbdb5c', '#a9e725'],
      legend: {
        orient: 'vertical',
        left: '15%',
        top: '16%',
        data: (() => {
          if (serverEnv === "intranet") {
            return that.envType.filter(it => !['dev', 'staging'].includes(it.type))
          } else {
            return that.envType
          }
        })()
      },
      series: [
        {
          name: '环境类型',
          type: 'pie',
          radius: ['50%', '70%'],
          center: ['65%', '50%'],
          avoidLabelOverlap: false,
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '30',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: (() => {
            if (serverEnv === "intranet") {
              return seriesData.filter(it => !['日常', 'staging'].includes(it.name))
            } else {
              return seriesData
            }
          })()
        }
      ]
    }
  }
})()

export default config
