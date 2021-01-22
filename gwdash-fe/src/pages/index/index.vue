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
    <d2-container class="swipper">
      <div style="width:1051px">
         <div class="home-page-course" style="background: #fff">
            <div class="home-page-course-top">
                <div class="title">Mone教程</div>
                <div class="add" @click="toVideoUpload" v-if="isAdmin">
                  <div class="left"></div>
                  <span>新增教程视频</span>
                </div>
            </div>
            <div class="home-page-course-content">
              <course-list/>
            </div>
         </div>
         <div class="home-page-feature" style="background: #fff">
            <div class="home-page-feature-top">
                <span>常用功能</span>
            </div>
            <div class="home-page-feature-content">
              <div 
               v-if="aside.length === 0"
               class='no-allow'>您暂无权限使用 ~ </div>
              <feature-list :features="features" v-else/>
            </div>
         </div>
      </div>
    </d2-container> 
</template>

<script>
import { mapState } from 'vuex'
import courseList from './components/course-list'
import featureList from './components/feature-list'
const isAdmin = !!(userInfo && userInfo.role == 1)
export default {
  name: 'index',
  data () {
    return {
      isAdmin,
      features: []
    }
  },
  created () {
    this.getFeatureList();
  },
  components: {
    courseList,
    featureList
  },
  methods: {
    getFeatureList () {
       const defaultList = [
         {
           path: '/gateway/apigroup/list',
           desc: '分组管理',
           times: 0
         },
         {
           path: '/application/list',
           desc: '',
           times: 0
         },
         {
           path: '/cat',
           desc: '',
           times: 0
         },
         {
           path: '/wiki/doc/list',
           desc: '文档列表',
           times: 0
         },
       ];
       // 洗数据  
       if (Array.isArray(JSON.parse(localStorage.getItem('features')))) {
        const arr = JSON.parse(localStorage.getItem('features'));
        if(arr[0].title === 'gwdash/apigroup/list' || arr[6].path === '/application/project/list') {
          localStorage.removeItem('features');
        }
       }
       const list = JSON.parse(localStorage.getItem('features')) || defaultList;
       this.features = list.sort((a,b) => b.times-a.times).slice(0,4);
    },
    toVideoUpload () {
      this.$router.push('/course/video');
    }
  },
  computed: {
    ...mapState('d2admin/menu', [
      'aside'
    ])
  }
}
</script>

<style lang="scss" scoped>
.home-page-course {
  width: 1051px;
  &-top {
    width: 1051px;
    height: 60px;
    padding: 15.7px 19px 16.3px 20px;
    box-sizing: border-box;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .title {
      font-family: PingFangSC-Medium;
      font-size: 20px;
      color: #333333;
    }
    .add {
      background: #4578FC;
      border-radius: 2px;
      width: 120px;
      height: 30px;
      display: flex;
      justify-content: center;
      padding-top: 9px;
      box-sizing: border-box;
      cursor: pointer;
      .left {
        width: 12px;
        height: 12px;
        margin-right: 7px;
        background-image: url(https://img.youpin.mi-img.com/middlewareGroup/ff8a134d8d3590da50389d6130f0767d.png?w=36&h=36);
        background-size: contain;
      }
      &:last-child {
        font-family: PingFangSC-Regular;
        font-size: 12px;
        color: #FFFFFF;
      }
    }
  }
  &-content {
    width: 1051px;
    height: 486px;
    border-top: 1px solid #EFF0F4;
    box-sizing: border-box;
  }
}
.home-page-feature {
  width: 1051px;
  height: 210px;
  margin-top: 10px;
  &-top {
    padding: 15px 0px 15px 20px;
    border-bottom: 1px solid #EFF0F4;
    width: 100%;
    height: 50px;
    box-sizing: border-box;
    span {
      font-family: PingFangSC-Medium;
      font-size: 14px;
      color: #333
    }
  }
  &-content {
    width: 1051px;
    height: 160px;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    // padding-left: 11px;
    box-sizing: border-box;
    .no-allow {
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      font-family: PingFangSC-Medium;
      font-size: 16px;
      color: #333
    }
  }
}
</style>

<style lang="scss">
.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .d2-theme-container-main .d2-theme-container-main-body .swipper.container-component .d2-container-full {
  overflow: visible;
}
.d2-layout-header-aside-group .d2-layout-header-aside-content .d2-theme-container .d2-theme-container-main .d2-theme-container-main-body .swipper.container-component .d2-container-full .d2-container-full__body {
  width: 1051px;
  padding: 0px;
  background-color: #EFF0F4;
}
</style>