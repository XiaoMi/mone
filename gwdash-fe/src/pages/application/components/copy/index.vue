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
    <div class="header">
      <div class="title">副本状态</div>
      <div v-if="showChart">
          <el-form inline size="mini">
            <el-form-item>
              <div class="header-select">选择环境：</div>
            </el-form-item>
            <el-form-item style="font-size: 14px; color: #909399;margin-right:0px;">
              <el-select
                size="mini"
                v-model="envId"
                @change='changeShowAll'>
                <el-option
                  v-for="item in form"
                  :key="item.key"
                  :label="item.name"
                  :value="item.projectId"/>
              </el-select>
            </el-form-item>
          </el-form>
      </div>
    </div>
    <div v-if="showSelect == false" class="no-copy">无</div>
    <div class="content">
      <chart-view v-if='showChart' :show='showChart' :id='id' :envId='envId' ref="myChild"/>
    </div>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import chartView from './components/chart-view'

export default {
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      showChart: false,
      form: [],
      envId : '',
      showSelect : true
    }
  },
  watch: {
    id () {
      this.showChart = false
      this.form = {}
      this.getInitState()
    }
  },
  created () {
    this.getInitState()
  },
  components: {
    chartView
  },
  methods: {
    getInitState () {
      let id = this.id
      let envId = this.env
      if (id === '' || !id) {
        console.log('project id null')
        return
      }
      service({
        url: `/project/env/list?projectId=${id}`
      }).then(res => {
        if ( res.length != 0) {
          this.form = res.map((ele,index)=>{
              let obj = {};
              obj.name = ele.name;
              obj.projectId = ele.id;
              obj.key = index
              return obj
           }); 
           this.envId = this.form[0].projectId
           this.showChart = true
           this.showSelect = true;
        }else{
          this.envId = '';
          this.showSelect = false;
        }
      })
    },
  }
}
</script>

<style lang='scss' scoped>
.header {
  display: flex;
  justify-content: space-between;
  padding: 0px 0px 10px 0px;
  .title {
    color: #333333;
    font-family: PingFang SC;
    font-weight: 500;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
  &-select {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 13px;
    margin-left: 10px;
  }
}
.no-copy {
  color: #333333;
  font-size: 13px;
}
</style>
