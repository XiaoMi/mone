
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
        const seriesData = Object.keys(that.projectType).map( ( it ) => { 
            let obj = {}
            obj.name = it
            obj.value = that.projectType[it]
            return obj
        })
      return  {
        title: {
            text: '项目类型统计',
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
        color:['#409EFF','#fbdb5c','#9ee6b8','#960BE6','#67e0e3'],
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
            orient: 'vertical',
            left: '15%',
            top:'25%',
            data: [
                {
                    name :'spring',
                    icon :'circle'
                } ,
                {
                    name :'docean',
                    icon :'circle'
                }, {
                    name :'filter',
                    icon :'circle'
                } , {
                    name :'plugin',
                    icon :'circle'
                } , {
                    name :'other',
                    icon :'circle'
                } ,],
                
        },
        series: [
            {
                name: '项目类型',
                type: 'pie',
                radius: ['50%', '70%'],
                center:['65%','50%'],
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
                data: seriesData
            }
        ]
    }
    }
  })()
  
  export default config