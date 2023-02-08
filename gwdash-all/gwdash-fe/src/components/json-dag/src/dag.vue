<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
    <div>
         <div class='flowchart-panel'>

        <div class='middle'>
            <div id="demo-chart">
            </div>
        </div>

      </div>
    </div>
</template>

<script>
import $ from 'jquery'
import './assets/chart.css'
import './assets/demo.css'
import Chart from './assets/libs/chart.js'
let chart
let _createChart
export default {
  name: 'dag',
  data () {
    return {
      current: {},
      xArr: []
    }
  },
  props: {
    data: Array,
    edg: Array,
    edgAutoSave: {
      type: Boolean,
      default: function () {
        return false
      }
    }
  },

  computed: {
    resource: function () {
      return this.formatResource(this.data, this.edg)
    }
  },
  mounted () {
    this.init()
  },

  methods: {
    init () {
      Chart.ready(() => {
        let _this = this
        _createChart = function () {
          return new Chart($('#demo-chart'), {
            onNodeClick (data) {
              // 点击节点时触发
              _this.saveDataByEmit()
              _this.$emit('nodeClick', data.ext)
              _this.current = data
            },
            onNodeDel (data) {
              _this.$emit('nodeDelete', data.ext)
            }
          })
        }

        chart = _createChart()
        const loadInitData = resource => {
          if ($('#demo-chart').length === 0) {
            $('<div id="demo-chart"></div>').appendTo($('.middle'))
            chart = _createChart()
          }

          chart.fromJson(JSON.stringify(this.resource))
        }
        loadInitData(this.resource)
      //  bindEvent();
      })
    },
    saveDataByEmit () {
      let result = this.parseChartJson(chart.toJson())
      this.$emit('saveData', result)
    },
    addNewTask (name, params) {
      const newX = 20
      const newY = 300
      params = params || {}
      params.data = params.data || {}
      params.class = 'node-process'
      params.data.nodeType = 1 // 流程节点类型
      let node = chart.addNode(name, newX, newY, params)
      node.addPort({
        isSource: true
      })
      node.addPort({
        isTarget: true,
        position: 'Top'
      })
    },
    /****
     *  格式化输入  适配这个库要求的节点数据结构
     */
    formatResource (data, edg) {
      if (!data || !edg) return {}
      let result = {}
      result.nodes = data.map(item => {
        let pos = {}
        if (!item._pos) {
          pos = this.getSuitablePos(data, edg, item['index'])
        } else {
          pos = item._pos
        }

        let defaultOptions = {
          name: item['data']['url'],
          nodeId: 'flow-chart-node-' + item['index'],
          nodeType: 1,
          removable: true,
          className: 'node-process',
          procId: '0',
          positionX: pos.x,
          positionY: pos.y,
          ext: { ...item, _pos: { x: pos.x, y: pos.y } }
        }
        return defaultOptions
      })
      result.connections = edg.map(item => {
        return {
          connectionId: `con_${item.from}_${item.to}`,
          pageSourceId: `flow-chart-node-${item.from}`,
          pageTargetId: `flow-chart-node-${item.to}`
        }
      })

      return result
    },
    /****
     * 反序列化 去掉多余的属性
     */

    parseChartJson (jsonObj) {
      var result = {
        data: [],
        edg: []
      }
      // update _pos

      result.data = jsonObj.nodes.map(item => {
        item.ext._pos.x = item.positionX
        item.ext._pos.y = item.positionY
        return item.ext
      })
      result.edg = jsonObj.connections.map(item => {
        return {
          from: +item.pageSourceId.replace('flow-chart-node-', ''),
          to: +item.pageTargetId.replace('flow-chart-node-', '')
        }
      })
      return result
    },
    /****
     * 计算绘制之前每个节点的合适位置
     * 每个节点{
     * positionX:
     * positionY:
     * }
     *
     */
    getSuitablePos (data, edg, nodeId) {
      return {
        x: 0,
        y: 320
      }
    },

    getSerializeObj (str) {
      let obj = {}
      let strArr = decodeURIComponent(str).split('&')
      strArr.forEach(item => {
        var kv = item.split('=')
        obj[kv[0]] = kv[1]
      })
      return obj
    },
    updateCurrent (current) {
      let form = this.getSerializeObj($('#rightForm').serialize())
      let id = +current.nodeId.replace('flow-chart-node-', '')
      let updatedItem = Object.assign({}, form, { id })
      this.$emit('updateData', this.getNewDataByUpdated(updatedItem))
      // 还要更新关系
      let result = this.parseChartJson(chart.toJson())
      this.$emit('updateEdg', result.edg)
    },
    getNewDataByUpdated (newItem) {
      var dataList = this.data.slice()
      let isInArr = false
      for (let i = 0, len = dataList.length; i < len; i++) {
        let item = dataList[i]
        if (item.id === newItem.id) {
          isInArr = true
          dataList[i] = newItem
          break
        }
      }

      if (!isInArr) {
        dataList.push(newItem)
      }
      return dataList
    },
    isDataUpdated (newVal, oldVal) {
      let flag = false
      if (newVal.length !== oldVal.length)flag = true
      return flag
    }
  },
  watch: {
    data: function (newVal, oldVal) {
      if (this.isDataUpdated(newVal, oldVal)) {
        chart.clear()
        $('btn-add').off('click')
        $('#demo-chart').remove()
        this.xArr = []
        this.init()
      }
    },
    edgAutoSave: function (newVal) {
      if (newVal) {
        this.saveDataByEmit()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
#demo-chart{
  overflow: hidden;
}

.btn{
    padding: 4px 16px;
    background: #2196f3;
    color: #fff;
    border: none;
    cursor: pointer;
    outline: 0;
}
#rightForm {
  ul{
    list-style:none;
  }
  label{
    display: inline-block;
    width: 80px;
    text-align: center;
  }
 input{
   padding: 0 5px;

 }
 button{
   display: block;
   margin: 10px auto;
 }
}
</style>
