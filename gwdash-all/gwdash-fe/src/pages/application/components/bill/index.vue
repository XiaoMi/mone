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
      <div class="title">账单统计</div>
      <div @click="handleEdit('更新')" v-if="showChart">
        <!-- <el-link icon="el-icon-edit-outline" :underline="false" style="font-size:18px"></el-link> -->
          <el-form inline size="mini">
            <el-form-item>
              <div class="header-select">统计时间：</div>
            </el-form-item>
            <el-form-item>
              <el-select
                size="mini"
                @change="changeShowAll"
                v-model="showAll">
                <el-option
                  v-for="item in showYear"
                  :key='item.id'
                  :label="item.year"
                  :value="item.year"/>
              </el-select>
            </el-form-item>

            <el-form-item>
              <div class="header-select">选择环境：</div>
            </el-form-item>
            <el-form-item style="font-size: 14px; color: #909399">
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
    <div v-if="showSelect == false" class="no-bill">无账单</div>
    <div class="content">
      <chart-view v-if='showChart' :show='showChart' :id='id' :envId='envId' :year='showAll' ref="myChild"/>
    </div>
    <div class="envName" v-show="envNameShow">
      <span class="circle"></span>
      <span>{{envName}}</span>
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
      showAll: 2020,
      showYear: [
        {
          id: 0,
          year: 2020
        },
        {
          id: 1,
          year: 2021
        },
        {
          id: 2,
          year: 2022
        },
        {
          id: 3,
          year: 2023
        },
        {
          id: 4,
          year: 2024
        },
        {
          id: 5,
          year: 2025
        },
        {
          id: 6,
          year: 2026
        }
      ],
      envId: '',
      envName: '',
      envNameShow: false,
      showSelect: true
    }
  },
  watch: {
    id () {
      this.showChart = false
      //   this.form = {}
      this.getInitConfig()
    }
  },
  created () {
    this.getInitConfig()
  },
  components: {
    chartView
  },
  methods: {
    getInitConfig () {
      let id = this.id
      let envId = this.env
      if (id === '' || !id) {
        console.log('project id null')
        return
      }
      service({
        url: `/project/env/list?projectId=${id}`
      }).then(res => {
        if (res.length !== 0) {
        //   this.showChart = res.status === 0

          this.form = res.map((ele, index) => {
            let obj = {}
            obj.name = ele.name
            obj.projectId = ele.id
            obj.key = index
            return obj
          })
          this.envId = this.form[0].projectId
          this.showChart = true
          this.showSelect = true
          this.changeShowAll()
        } else {
          this.envName = ''
          this.envId = ''
          this.showSelect = false
          this.envNameShow = false
        }
      })
    },
    handleEdit () {
      this.dislogVisible = true
    },
    changeShowAll () {
      let envName = this.form.filter((ele) => {
        return ele.projectId === this.envId
      })
      this.envName = envName[0].name
      this.envNameShow = true
    }
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
.no-bill {
  color: #333333;
  font-size: 13px;
}
.envName {
  border-radius: 17px;
  background: #FFFFFF;
  border: 1px solid #EFF0F4;
  width: 200px;
  height: 34px;
  line-height: 34px;
  font-size: 13px;
  color: #77DAC2;
  margin: 10px auto;
  text-align: center;
  .circle {
    display: inline-block;
    width: 12px;
    height: 12px;
    background: #77DAC2;
    border-radius: 50% 50%;
    margin-right: 10px;
  }
}
</style>
