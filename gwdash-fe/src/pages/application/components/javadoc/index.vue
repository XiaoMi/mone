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
        <div class="header-left">javadoc</div>
        <div class="header-right"  @click="createJavaDoc(id)">
          <img
              src="https://img.youpin.mi-img.com/middlewareGroup/5b6ff725524442fdba595fe179f105a6.png?w=32&h=32"
              style="height:16px;weight:16px;"
              title="生成javadoc"/>
        </div>
     </div>
     <div class="content">
       <div v-if="Object.keys(javaDocMap).length == 0" class="no-data">暂无数据</div>
       <div v-else class="content-body">
         <div class="content-body-card" v-for="(key,value) in javaDocMap" :key="key">
           <div class="file">{{value}}</div>
           <div class="doc">{{key}}</div>
         </div>
       </div>
     </div>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import qs from 'qs'
import SimpleMDE from 'simplemde'
export default {
  props: {
    id: {
      type: Number,
      required: true
    }
  },
  data () {
    return {
      javaDocMap: {}
    }
  },
  watch: {
    id: function (newId, oldId) {
      if (newId != oldId) {
        this.getDocList(newId)
      }
    }
  },
  created () {
    this.getDocList(this.id)
  },
  methods: {
    getDocList (id) {
      if (!id || id === '') {
        console.log('getDocList id null', id)
        return
      }
      service({
        url: `/javadoc/get?projectId=${id}`
      }).then(res => {
        if (res && res.doc) {
          this.javaDocMap = JSON.parse(res.doc)
        } else {
          this.javaDocMap = {}
        }
      })
    },
    createJavaDoc (id) {
      service({
        url: `/javadoc/create?projectId=${id}`
      }).then(res => {
        if (res && res.code == 0) {
          this.$message({
            type: 'info',
            message: '已经开始生成，稍后来查看'
          })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>

.header {
   display: flex;
   justify-content: space-between;
   padding: 0px 0px 10px 0px;
   .header-left {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
}
.content{
  padding-bottom: 10px;
  // background: #f8f8f8;
  position: relative;
  .content-top {
     position: absolute;
     top: 0;
     left: 0;
     right: 0;
     background-color: #409EFF;
     height: 3px;
     border-radius: 18px;
  }
  .no-data {
    color: rgba(51,51,51,1);
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 13px;
    line-height: 18px;
    letter-spacing: 0px;
    text-align: left;
  }

  .content-body {
    .content-body-card {
      border: 1px solid rgb(233, 230, 230);
      border-radius: 8px;
      padding: 12px 12px 6px 10px;
      margin-top: 12px;
      background: #fff;
      word-break:break-all;
      .file {
        font-size: 15px;
        font-weight: bold;
      }
      .doc {
        margin-top: 10px;
        font-size: 12px;
      }
    }
  }
}
.content-body::-webkit-scrollbar {
  display: none;
}
</style>
