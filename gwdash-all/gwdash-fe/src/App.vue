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
  <div id="app">
    <keep-alive>
      <router-view/>
    </keep-alive>
    <!-- <el-dialog
      :visible.sync="dialogVisible"
      title="新增功能"
      width="40%"
      :close-on-click-modal="false"
      :before-close="handleClose">
      <div v-if="changeLog.content&&changeLog.content.length>0" v-html="md2html(changeLog.content)"></div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">朕已阅</el-button>
      </span>
    </el-dialog> -->
    <BroadCast/>

  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import marked from 'marked'
import jsMd5 from "js-md5"
import BroadCast from "@/components/broadcast/index"

export default {
  name: 'app',
  data () {
    return {
      dialogVisible: false,
      changeLog: {}
      // broadCastOpen:false
    }
  },
  mounted () {
    // this.reCheckChangeLog();
  },
  methods: {
    // async reCheckChangeLog () {
    //   const db = await this.$store.dispatch('d2admin/db/database')
    //   let changelog_Remote = await this.getNotifacation()

    //   if (!changelog_Remote) {
    //     console.warn('获取ChangeLog 异常，查看接口/open/getNotification')
    //     return
    //   };
    //   let changelog_Local = db.get("changeLog").value()
    //   if (typeof changelog_Local === "undefined" || jsMd5(JSON.stringify(changelog_Local) || "") !== jsMd5(JSON.stringify(changelog_Remote))) {
    //     changelog_Local = changelog_Remote
    //     this.changeLog = changelog_Local
    //     db.set("changeLog", changelog_Local).write()
    //     this.dialogVisible = true
    //   }
    // },
    // getNotifacation () {
    //   return service({
    //     url: '/open/getNotification',
    //     method: 'get',
    //     data: {}
    //   })
    // },
    // md2html (mdStr) {
    //   return marked(mdStr, {
    //     highlight: code => highlight.highlightAuto(code).value,
    //     renderer: new marked.Renderer()
    //   })
    // },
    // handleClose () {
    //   this.dialogVisible = false
    // }
    // toggleIsopenHandler(payload){
    //   this.broadCastOpen=payload;
    // }
  },
  components: {
    BroadCast
  }
}
</script>

<style lang="scss">
@import '~@/assets/style/normalize.scss';
@import '~@/assets/style/public-class.scss';
@import '~@/assets/style/iconfont/iconfont.css';
</style>
