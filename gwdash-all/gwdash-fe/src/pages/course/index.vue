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
  <d2-container>
    <d2-module>
      <div class="top" v-if="isAdmin">
        <el-button @click="dialogVisible = true" size="mini">视频上传</el-button>
      </div>
      <div class="contianer">
        <!-- 轮播视频 -->
        <div class="header">
          <div class="title">场景化教程</div>
          <swiper-video :videoList="swiperVideoList"></swiper-video>
        </div>
        <div class="morevideo" @click="handleMoreVideo">{{ buttonContent }}</div>
        <!-- 更多视频 -->
        <div class="content" v-if="moreVideo">
          <el-tabs v-model="activeName">
            <el-tab-pane label="全部" name="all">
              <div class="tab-pane-video">
                <each-video
                  class="each"
                  v-for="item in allVideoList"
                  :key="item.id"
                  :videoContent="[item]"
                  :isAdmin="isAdmin"
                  @doDelVideo="handleDelVideo"
                ></each-video>
              </div>
            </el-tab-pane>
            <el-tab-pane v-for='(it,i) in tagList' :key="i" :label="it.name" :name="value">
              <div class="tab-pane-video">
                <each-video
                  class="each"
                  v-for="item in it.arr"
                  :key="item.id"
                  :videoContent="[item]"
                  :isAdmin="isAdmin"
                  @doDelVideo="handleDelVideo"
                ></each-video>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
        <!-- 视频上传 -->
        <el-dialog title="视频上传" :visible.sync="dialogVisible" width="800px">
          <el-form :model="form" label-width="110px" ref="form" :rules="rules">
            <el-form-item label="视频简述" prop="description">
              <el-input v-model="form.description" placeholder="请输入视频内容描述" style="width:55%"></el-input>
            </el-form-item>
            <el-form-item label="视频类型" prop="tag">
              <el-select
                v-model="form.tag"
                placeholder="请输入视频类型"
                filterable
                default-first-option
                style="width: 55%"
              >
                <el-option
                  v-for="item in tagList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-upload
            class="upload-demo"
            action="xx_replace_xx"
            drag
            multiple
            :headers="header"
            :before-upload="beforeUploadVideo"
            :http-request="uploadFile"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">
              将视频文件拖到此处，或
              <em>点击上传</em>
            </div>
            <div class="el-upload__tip" slot="tip">请保证视频格式正确，且不超过50M</div>
          </el-upload>
        </el-dialog>
      </div>
    </d2-module>
  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"
import axios from "axios"
import qs from "qs"

import swiperVideo from "./components/swiper-video"
import eachVideo from "./components/each-video"
const isAdmin = !!(userInfo && userInfo.role === 1)
export default {
  data () {
    return {
      videoList: [],
      swiperVideoList: [],
      allVideoList: [],
      primaryVideoList: [],
      gatewayVideoList: [],
      applicationVideoList: [],
      sreVideoList: [],
      nacosVideoList: [],
      catVideoList: [],
      fliterVideoList: [],
      otherVideoList: [],
      serviceMeshVideoList: [],
      serverlessVideoList: [],
      activeName: "all",
      dialogVisible: false,
      uploadParam: {},
      moreVideo: false,
      buttonContent: "查看更多视频",
      isAdmin,
      form: {},
      header: {
        "Content-Type": "multipart/form-data"
      },
      rules: {
        description: [
          {
            required: true,
            message: "请输入视频描述【长度在 4 到 12 个字符】",
            trigger: "blur"
          },
          { min: 4, max: 12, message: "长度在 4 到 12 个字符", trigger: "blur" }
        ],
        tag: [{ required: true, message: "请选择", trigger: "blur" }]
      },
      tagList: []
    }
  },
  components: {
    swiperVideo,
    eachVideo
  },
  created () {
    this.getVideoList()
  },
  methods: {
    // 获取视频列表
    getVideoList () {
      service({
        url: '/help/video/list'
      }).then(res => {
        this.videoList = res
        this.swiperVideoList = this.videoList
        this.allVideoList = this.videoList
        this.primaryVideoList = []
        this.gatewayVideoList = []
        this.applicationVideoList = []
        this.sreVideoList = []
        this.nacosVideoList = []
        this.catVideoList = []
        this.fliterVideoList = []
        this.otherVideoList = [];
        this.serviceMeshVideoList = [];
        this.serverlessVideoList = [];
        this.videoList.forEach((item) => {
          switch (item.tag) {
            case "primary":
              this.primaryVideoList.push(item)
              break
            case "gateway":
              this.gatewayVideoList.push(item)
              break
            case "application":
              this.applicationVideoList.push(item)
              break
            case "sre":
              this.sreVideoList.push(item)
              break
            case "nacos":
              this.nacosVideoList.push(item)
              break
            case 'cat':
              this.catVideoList.push(item)
              break
            case "filter":
              this.fliterVideoList.push(item)
              break
            case "serviceMesh":
              this.serviceMeshVideoList.push(item)
              break
            case "serverless":
              this.serverlessVideoList.push(item)
              break
            case "other":
              this.otherVideoList.push(item)
          }
        })
        var _this = this
        _this.tagList = [ 
          { name: "Mone入门", value: "primary", id: 1, arr: this.primaryVideoList },
          { name: "智能网关", value: "gateway", id: 2, arr: this.gatewayVideoList },
          { name: "应用管理", value: "application", id: 3, arr: this.applicationVideoList },
          { name: "运维中心", value: "sre", id: 4, arr: this.sreVideoList },
          { name: "监控中心", value: "cat", id: 6, arr: this.catVideoList },
          { name: "Filter", value: "filter", id: 7, arr: this.fliterVideoList },
          { name: "ServiceMesh", value: "serviceMesh", id: 8, arr: this.serviceMeshVideoList },
          { name: "Serverless", value: "serverless", id: 9, arr: this.serverlessVideoList },
          { name: "其它", value: "other", id: 10, arr: this.otherVideoList }]
      }).catch(() => {
        this.$message({
          type: 'warning',
          message: '请求出错：/help/video/list'
        })
      })
    },

    uploadFile (params) {
      let that = this
      let fileObj = params.file
      let form = new FormData()
      form.append("data", fileObj)
      let uploadUrl = "xx_replace_xx"
      let xhr = new XMLHttpRequest()
      xhr.open("post", uploadUrl, true)
      xhr.send(form)
      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {
            let data = JSON.parse(xhr.responseText) && JSON.parse(xhr.responseText).data;
            data && that.addVideo(data)
          } else {
            console.log("上传接口不可用")
          }
        }
      }
    },
    // 视频上传
    addVideo (param) {
      service({
        url: "/help/video/add",
        method: "POST",
        data: {
          ...this.form,
          url: param
        }
      }).then(res => {
        this.$message.success("视频上传成功")
        this.dialogVisible = false
        this.form = {}
        this.getVideoList()
      })
    },
    beforeUploadVideo (file) {
      const isLimit = file.size / 1024 / 1024 < 50
      const videoTypeArr = [
        "video/mp4",
        "video/ogg",
        "video/flv",
        "video/avi",
        "video/wmv",
        "video/rmvb"
      ]
      if (!isLimit) {
        this.$message.error("上传视频大小不能超过50M哈 ~")
        return
      }
      // file.type仅能判断文件后缀名，若手动更改后缀则无法拦截
      if (videoTypeArr.indexOf(file.type) === -1) {
        this.$message.error("请上传正确的视频格式")
        return
      }
      if (!this.form.description || !this.form.tag) {
        this.$message({
          type: "warning",
          message: "请填写视频简述、选择视频类型"
        })
        return
      }
    },
    // 视频删除
    handleDelVideo (id) {
      this.$confirm("确认删除此视频教程 ?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        service({
          url: `/help/video/del?id=${id}`
        }).then(res => {
          this.$message.success("删除成功")
          this.getVideoList()
        }).catch(() => {
          this.$message({
            type: "warning",
            message: "请求出错: /help/video/del"
          })
        })
      }).catch(() => {
        this.$message({
          type: "info",
          message: "已取消删除"
        })
      })
    },
    handleMoreVideo () {
      this.moreVideo = !this.moreVideo
      this.buttonContent = this.moreVideo ? "收起" : "查看更多视频"
    }
  }
}
</script>

<style lang="scss" scoped>
.top {
  display: flex;
  justify-content: flex-end;
}
.contianer {
  padding: 30px;
  margin: 30px auto;
  .header {
    .title {
      color: #000;
      font-size: 32px;
      font-weight: normal;
      letter-spacing: 0px;
      line-height: 1;
      text-align: center;
      margin-bottom: 12px;
    }
  }
  .morevideo {
    width: 106px;
    margin: 24px auto;
    border: 0.5px solid #409eff;
    padding: 10px;
    cursor: pointer;
    color: #409eff;
    text-align: center;
    font-size: 15px;
  }
}
.upload-demo {
  padding-left: 108px;
  margin-bottom: 70px;
}
.tab-pane-video {
  padding: 10px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  .each {
    margin-left: 12px;
    margin-right: 20px;
    margin-bottom: 30px;
  }
}
.word {
  font-size: 14px;
  text-align: center;
  margin-top: 30px;
}
</style>
<style>
.contianer .el-tabs__item {
  font-size: 14px;
}
</style>
