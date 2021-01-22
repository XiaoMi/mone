
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

let config = (function(){
    return function(that,myFullShow) {
        const xAxis = Object.keys(that.countMap).sort(( a,b ) => { 
            return Date.parse(a) - Date.parse(b);
         } )
         const seriesData = xAxis.map( ( it ) => { return that.countMap[it]})
       return {
        title: {
            text: '每日发布次数统计',
            top: '3%',
            left: '2%',
            textStyle:{
                //文字颜色
                color:'black',
                //字体风格,'normal','italic','oblique'
                // fontStyle:'normal',
                // //字体粗细 'normal','bold','bolder','lighter',100 | 200 | 300 | 400...
                // fontWeight:'bold',
                // //字体系列
                // fontFamily:'sans-serif',
                //字体大小
        　　　　 fontSize:16
            }
        },
        color: ['#409EFF'],
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        // toolbox: {
        //     feature: {
        //         saveAsImage: {}
        //     }
        // },
        xAxis: {
            type: 'category',
            boundaryGap: false,
             axisLine: {
                lineStyle: {
                  color: '#aeb4b7fa'
                },
                width: 1
              },
              axisLabel: {
                textStyle: {
                    color: 'black',
                },
                interval:0,  
                rotate:60
              }, 
            data: xAxis
        },
        yAxis: {
            type: 'value',
            splitLine:{
                show: true,
                lineStyle: {
                type:'dashed',
                color: [ '#eee']
            }},//网格线
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            }
        },
        series: [
            {
                name: '发布次数',
                type: 'line',
                stack: '总量',
                symbolSize: 8,
                data: seriesData
            },
        ]
    }
    }
  })()
  
  export default config