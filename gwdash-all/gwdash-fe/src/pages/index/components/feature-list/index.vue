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
  <div class="list">
      <div v-for="(item,index) in newFeatures" :key="index" :class="'list-module list-module-'+item.styleKey" @click='toPath(item.path)'>
        <div class="list-module-eng">{{ item.engIntroduce }}</div>
        <div class="list-module-one">{{ item.title }}</div>
        <div class="list-module-second">{{ item.desc }}</div>
        <i class="quarter"></i>
        <i class="colored-spite"></i>
      </div>
  </div>
</template>

<script>
export default {
  props: {
    features: {
      type: Array,
      required: true
    }
  },
  computed: {
    newFeatures: function () {
      const newList = this.features
      newList.forEach(item => {
        item.styleKey = item.path.split("/")[1]
        if (item.path.includes('/gateway')) {
          item.engIntroduce = 'intelligent Gateway'
          item.title = '智能网关'
        } else if (item.path.includes('/application')) {
          item.engIntroduce = 'Application management'
          item.title = '应用管理'
        } else if (item.path.includes('/sre')) {
          item.engIntroduce = 'Operation center'
          item.title = '运维中心'
        } else if (item.path.includes('/nacos')) {
          item.engIntroduce = 'Configuration center'
          item.title = '配置中心'
        } else if (item.path.includes('/cat')) {
          item.engIntroduce = 'Monitoring center'
          item.title = '监控中心'
        } else {
          item.engIntroduce = 'Wiki management'
          item.title = 'Wiki'
        }
      })
      return newList
    }
  },
  methods: {
    toPath (path) {
      this.$router.push(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.quarter{
  position: absolute;
  width: 100px;
  height: 100px;
  right: 0;
  bottom: 0;
  border-radius: 100% 0 0 0;
  overflow: hidden;
  background: #f6fcf4;
}
.colored-spite{
  position: absolute;
  display: block;
  width:44px;
  height: 44px;
  right: 10px;
  bottom: 20px;
  background-image: url(https://img.youpin.mi-img.com/middlewareGroup/dd8a264ec01b6fc5ec82b501bea845da.png?w=389&h=55);
  background-repeat:  no-repeat;
  background-size: 311px 44px;
}
.list {
  display: flex;
  margin-left: 20px;
  &-module {
    position: relative;
    overflow: hidden;
    cursor: pointer;
     width: 246px;
     height: 120px;
     margin-right: 9px;
     border-radius: 2px;
     padding-top: 22px;
     padding-left: 16px;
     box-sizing: border-box;
     cursor: pointer;
     overflow: hidden;
     border-width: 2px;
     border-style: solid;
     &-gateway{
       border-color: #C4F3C1;
       .colored-spite{
         background-position: -216px 0;
       }
       .quarter{
         background: #f6fcf4;
       }
     }
     &-application{
      border-color: #d6e8ff;
       .colored-spite{
         background-position: -105px 0;
       }
      .quarter{
        background: #f6faff;
      }
     }
     &-wiki{
      border-color: #ffdadf;
       .colored-spite{
         background-position:-269px 0;
       }
      .quarter{
        background: #fff5f8;
      }
     }
     &-nacos{
        border-color: #f3daff;
        .colored-spite{
         background-position:-55px 0;
       }
       .quarter{
         background: #fcf5ff;
       }
     }
     &-sre{
       border-color: #ffe5b0;
        .colored-spite{
         background-position: -158px 0;
       }
       .quarter{
         background: #fffbf1;
       }
     }
     &-cat{
       border-color: #b1f5ea;
       .quarter {
         background: #E8FFFB;
       }
     }
     &-eng {
       height: 17px;
       line-height: 17px;
       opacity: 0.8;
       font-family: PingFangSC-Regular;
       font-size: 12px;
       color: #999999;
       letter-spacing: 0.5px;
     }
     &-one {
       height: 33px;
       line-height: 33px;
       margin-top: 7px;
       font-family: PingFangSC-Medium;
       font-size: 24px;
       color: #333333;
       letter-spacing: 0.5px;
     }
     &-second {
       height: 17px;
       line-height: 17px;
       margin-top: 2px;
       font-family: PingFangSC-Regular;
       font-size: 12px;
       color: #666666;
     }
  }
  &-module::after{
    position: absolute;
    left: -100%;
    top: 0;
    width: 80%;
    height: 100%;
    content: "";
    background: linear-gradient(to right,rgba(255,255,255,0) 0,rgba(255,255,255,.3) 50%,rgba(255,255,255,0) 100%);
    transform: skewX(-45deg);
  }
  &-module:hover:after{
    left: 150%;
    transition: 0.6s ease;
    }
}
</style>
