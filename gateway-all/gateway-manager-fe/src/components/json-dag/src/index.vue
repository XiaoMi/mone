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
  <el-row>
  <el-col :span="5" class="grid">
      <div class="tab-bar">
        搜索API
      </div>
      <searchList @addToDag="addToDagHandler"></searchList>
 </el-col>
  <el-col :span="13" class="grid">
     <div class="tab-bar">
        编辑API关系
      </div>
    <div class="grid-content bg-purple-light">
      <dag
      :data="valueJson['taskList']"
      :edg="valueJson['dependList']"
      :edgAutoSave="edgAutoSave"
      @nodeClick="nodeClickHandler"
      @nodeDelete="nodeDeleteHandler"
      @saveData="saveDAGDataHandler"
      ></dag>
    </div>
</el-col>
  <el-col :span="6" class="grid">
  <div class="tab-bar">
      配置API
    </div>
    <div class="grid-content bg-purple">
      <nodeInfo v-model="editData.data" v-if="editData.id!==-1"></nodeInfo>
    </div>
 </el-col>
</el-row>
 <el-row>
    <el-button type="primary" @click="saveAsJsonHandler" class="save-btn">保存编辑</el-button>
 </el-row>
  </div>
</template>

<script>
import searchList from './searchList.vue'
import dag from './dag.vue'
import nodeInfo from './nodeInfo.vue'
import Vue from 'vue'

export default {
  name: 'jsonDag',
  data () {
    return {
      edgAutoSave: false,
      editData: {
        id: -1,
        paramExtract: '',
        result: false
      },
      valueJson: {}
    }
  },
  props: {
    value: {
      type: String,
      default: function () {
        return ''
      }
    }
  },
  computed: {
    taskListLen: function () {
      if (!this.valueJson || !this.valueJson.taskList) return 0
      return this.valueJson.taskList.length
    }
  },

  created () {
    this.valueJson = this.transformJson(this.value)
  },
  components: {
    searchList,
    dag,
    nodeInfo
  },
  methods: {
    addToDagHandler (list) {
      let newItems = this.createNodeData(list)

      // 首先获取dag图里面的数据
      this.edgAutoSave = true
      setTimeout(() => {
        if (newItems.length > 0) {
          this.valueJson.taskList = this.valueJson.taskList.concat(newItems)
        //  console.log(this.valueJson.taskList.map(item=>item.index));
        }
        this.edgAutoSave = false
      }, 0)
    },
    nodeClickHandler (data) {
      this.editData = data
    },
    nodeDeleteHandler (item) {
      // this.editData = {}
      /****
       * 每个节点的顺序和边的依赖都来源于index 参数
       * 所以在删除了某一个节点后需要对每个节点的index 进行一个更新
       * 同样的 边也需要更新
       */
      let { data, index } = item
      let newTaskList = []

      this.valueJson.taskList.forEach(taskItem => {
        if (taskItem['index'] < index) {
          newTaskList.push(taskItem)
        } else if (taskItem['index'] > index) {
          newTaskList.push(Object.assign({}, { ...taskItem }, { index: taskItem['index'] - 1 }))
        }
      })
      let newDependList = []
      this.valueJson.dependList.forEach(dependItem => {
        let newItem = Object.assign({}, dependItem)
        if (dependItem.from > index) {
          newItem.from = dependItem.from - 1
        }
        if (dependItem.to > index) {
          newItem.to = dependItem.to - 1
        }
        if (dependItem.from !== index && dependItem.to !== index) {
          newDependList.push(newItem)
        }
      })
      this.valueJson.taskList = newTaskList
      this.valueJson.dependList = newDependList
    },
    saveDAGDataHandler (payload) {
      this.valueJson.taskList = payload.data
      this.valueJson.dependList = payload.edg
    },
    createNodeData (list) {
      let defaultNodeObj = {
        index: 0,
        taskId: 0,
        dependList: [],
        status: 0,
        data: {
          id: 0,
          url: '',
          paramExtract: '{}',
          paramMap: {},
          httpMethod: '',
          result: false
        }
      }
      // 过滤掉和valueJSON重复ID的数据
      list = list.filter(item => {
        let { id } = item
        for (let task of this.valueJson.taskList) {
          if (id === task['data']['id']) {
            return false
          }
        }
        return true
      })

      let index = this.taskListLen
      let result = list.map(item => {
        let { id, url, httpMethod } = item
        let newData = Object.assign({}, defaultNodeObj.data, {
          id,
          url,
          httpMethod
        })
        return Object.assign({}, defaultNodeObj, {
          index: index++,
          data: newData
        })
      })

      return result
    },
    /****
     * 转为字符串转为json 保留paramExtract参数为字符串形式
     */
    transformJson (json) {
      let taskList = []
      let dependList = []
      let originJson = {}
      try {
        originJson = JSON.parse(json)
        taskList = originJson['taskList'] || []
        dependList = originJson['dependList'] || []
      } catch (error) {}
      let transformedTaskList = taskList.map(item => {
        item['data']['paramExtract'] = JSON.stringify(
          item['data']['paramExtract'],
          null,
          4
        )
        return item
      })

      return { taskList: transformedTaskList, dependList }
    },
    saveAsJsonHandler () {
      this.edgAutoSave = true

      setTimeout(() => {
        this.edgAutoSave = false
        let newJSON = this.transformParamExtractToObj(this.valueJson)
        this.$emit('jsonDagUpdate', JSON.stringify(newJSON, null, 2))
      }, 0)
    },
    updateDataHandler (payload) {
      this.valueJson.taskList = payload
    },
    updateEdgHandler (payload) {
      this.valueJson.dependList = payload
    },
    updateTaskList (item) {
      let { data } = item
      var newTaskList = this.valueJson.taskList.map(task => {
        if (data.id === task['data']['id']) {
          task['data'] = data
        }
        return task
      })
      this.valueJson.taskList = newTaskList
    },
    transformParamExtractToObj (valueJSON) {
      let { taskList, dependList } = valueJSON
      let clonedJSON = JSON.parse(JSON.stringify(valueJSON))

      let newTaskList = clonedJSON.taskList.map(item => {
        let newItem = Object.assign({}, item)
        try {
          newItem['data']['paramExtract'] = JSON.parse(
            newItem['data']['paramExtract']
          )
        } catch (error) {
          this.$message.error('paramExtract JSON有误')
          newItem['data']['paramExtract'] = {}
        }
        return newItem
      })
      return { taskList: newTaskList, dependList }
    }
  },
  watch: {
    value: function (newVal) {
      // this.valueJson = newVal
    },
    editData: {
      handler: function (newVal) {
        this.updateTaskList(newVal)
      },
      deep: true
    }
  }
}
</script>

<style lang="scss">
.save-btn {
  float: right;
  margin-top: 15px;
}
.grid{
  border: 1px solid #f6f6f6;
  border-radius: 5px;
 .tab-bar{
    color: #409EFF;
    border-bottom: 1px solid #f6f6f6;
    height: 30px;
    line-height: 30px;
    text-align: center;

 }
}

</style>
