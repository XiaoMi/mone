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
   <div class="search-box">
      <el-input
        placeholder="请输入内容"
        v-model="searchKeyWord"
        @change="searchHandler"
        clearable>
      </el-input>
   </div>
   <div class="result-con">
       <el-radio-group v-model="seletted" v-if="resultList.length>0">
          <p class="checkbox-item"  v-for="item in resultList" :key="item['id']">
          <el-radio :label="item.url">
            <el-tooltip class="item" effect="dark" :content="item.url" placement="right">
                  <span> {{`${item['id']} | ${item.url.split('/').slice(-2).join('/')}`}}</span>
              </el-tooltip>
          </el-radio>
          </p>

      </el-radio-group>
      <p v-else>暂无数据</p>
   </div>

   <div class="btn-group footer">
      <el-button type="primary" class="fr" @click="addToDagHandler">添加到右侧</el-button>
   </div>

  </div>
</template>

<script>
import service from '@/plugin/axios/index'

export default {
  name: 'searchList',
  data () {
    return {
      searchKeyWord: '',
      resultList: [],
      seletted: ''
    }
  },
  computed: {
    selettedDatailList: function () {
      return this.resultList.filter(item => {
        return this.seletted === item.url
      })
    }
  },
  methods: {
    searchHandler (e) {
      this.getList(this.searchKeyWord, this.searchKeyWord, this.searchKeyWord, this.searchKeyWord)
    },
    getList (serviceName = '', pathString = '', urlString = '', name = '') {
      service({
        url: '/apiinfo/list',
        method: 'post',
        data: {
          pageNo: 1,
          pageSize: 20,
          serviceName,
          pathString,
          urlString,
          name,
          showMine: false
        }
      }).then(res => {
        if (res) {
          this.resultList = res.infoList
        }
      })
    },
    addToDagHandler () {
      this.$emit('addToDag', this.selettedDatailList)
      this.seletted = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.result-con{
  margin: 10px;
  height: 300px;
  overflow-y: scroll;
  overflow-x: hidden;
  &>p{
    text-align: center;
    color: #999
  }
}
.result-con::-webkit-scrollbar{
  display: none
}
.checkbox-item{
  margin: 0;
  height: 40px;
  line-height: 40px;
  span:nth-child(1){
      width:32px;
      font-weight: bold;
      text-align: right;
      padding-left: 0;
      padding-right: 10px;
  }
  span{
    padding-left: 10px;
  }
}
.el-checkbox{
  display: block;
}
.fr{
  float: right;
}
</style>
