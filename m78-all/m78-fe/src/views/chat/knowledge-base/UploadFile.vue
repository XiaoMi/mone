<!--
 * @Description:
 * @Date: 2024-01-31 16:53:45
 * @LastEditTime: 2025-01-21 10:34:28
-->
<template>
  <!-- 上传区域 -->
  <div class="pt-[10px]" id="container" ref="container">
    <el-button ref="selectfiles" id="selectfiles">上传新文件</el-button>
    <!-- v-if="fileData.fileList.length" -->
    <!-- <el-button
      id="postfiles"
      ref="postfiles"
      type="primary"
      size="mini"
      @click="methods.FileUplodeOn"
      >开始上传</el-button
    > -->
  </div>
  <el-card v-if="fileData.fileList.length" style="margin-top: 20px">
    <el-table :data="fileData.fileList" style="width: 100%">
      <!-- <el-table-column type="selection" width="55" /> -->
      <el-table-column prop="id" label="文件Id"></el-table-column>
      <el-table-column prop="fileName" label="文件名"></el-table-column>
      <el-table-column label="进度" v-slot="{ row }">
        <el-progress
          :text-inside="true"
          :stroke-width="16"
          :percentage="row.percentage"
        ></el-progress>
      </el-table-column>
      <el-table-column label="上传状态" v-slot="{ row }">
        <el-link
          :type="
            row.loadType == 0
              ? 'info'
              : row.loadType == 1
                ? 'warning'
                : row.loadType == 2
                  ? 'success'
                  : 'danger'
          "
          :underline="false"
          >{{
            row.loadType == 0
              ? '等待上传'
              : row.loadType == 1
                ? '正在上传'
                : row.loadType == 2
                  ? '上传成功'
                  : '上传失败'
          }}</el-link
        >
      </el-table-column>
      <el-table-column prop="fileType" label="文件类型"></el-table-column>
      <el-table-column prop="size" label="文件大小" v-slot="{ row }">
        {{ row.size }}MB
      </el-table-column>

      <el-table-column fixed="right" label="操作" v-slot="{ row }" width="112px">
        <el-button
          type="primary"
          size="small"
          @click="methods.FileRead(row)"
          v-if="row.loadType == 2"
          >知识解析</el-button
        >
        <el-button v-else type="danger" size="small" @click="methods.removeFile(row.id)"
          >取消上传</el-button
        >
      </el-table-column>
      <!-- <el-table-column fixed="right" label="操作" v-slot="{ row }" width="112px">
        <el-button type="danger" size="small" @click="deleteFile(row)">删除</el-button>
      </el-table-column> -->
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, defineEmits, watch } from 'vue'
import plupload from 'plupload'
import { embedding } from '@/api/chat'
import { ElMessage } from 'element-plus'

const emits = defineEmits(['updateFileList'])

const props = defineProps({
  drawer: {
    type: Boolean,
    required: true
  },
  knowledgeId: {
    type: String,
    required: true
  }
})

const container = ref()
var accessid = ''
var host = ''
var policyBase64 = ''
var signature = ''
var callbackbody = ''
var key = ''
var expire = 0
var g_object_name = ''
var now = Date.parse(new Date()) / 1000
var suffix = ''
var body = ''

function send_request() {
  var xmlhttp = null
  if (window.XMLHttpRequest) {
    xmlhttp = new XMLHttpRequest()
  } else if (window.ActiveXObject) {
    // eslint-disable-next-line
    xmlhttp = new ActiveXObject('Microsoft.XMLHTTP')
  }

  if (xmlhttp != null) {
    // const { query } = route
    // serverUrl是 用户获取 '签名和Policy' 等信息的应用服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
    // var serverUrl =
    //   window.location.origin + '/api/ai-plugin/z/oss/policy?knowledgeBaseId=' +props.knowledgeId
    var serverUrl =
      window.location.origin + '/api/z/oss/policy?knowledgeBaseId=' + props.knowledgeId

    xmlhttp.open('GET', serverUrl, false)
    xmlhttp.send(null)
    return xmlhttp.responseText
  } else {
    alert('Your browser does not support XMLHTTP.')
  }
}

function get_signature() {
  // 可以判断当前expire是否超过了当前时间， 如果超过了当前时间， 就重新取一下，3s 作为缓冲。
  now = Date.parse(new Date()) / 1000
  if (expire < now + 3) {
    body = send_request()
    var obj = eval('(' + body + ')')
    host = obj['host']
    policyBase64 = obj['policy']
    accessid = obj['accessid']
    signature = obj['signature']
    expire = parseInt(obj['expire'])
    callbackbody = obj['callback']
    key = obj['dir']
    return true
  }
  return false
}

function get_suffix(filename: any) {
  var pos = filename.lastIndexOf('.')
  suffix = ''
  if (pos != -1) {
    suffix = filename.substring(pos)
  }
  return suffix
}

function set_upload_param(up: any, filename: any, ret: any) {
  if (ret == false) {
    ret = get_signature()
  }
  g_object_name = key
  if (filename != '') {
    suffix = get_suffix(filename)
    g_object_name += '${filename}'
  }
  up.setOption({
    url: host,
    multipart_params: {
      key: g_object_name,
      policy: policyBase64,
      OSSAccessKeyId: accessid,
      success_action_status: '200', //让服务端返回200,不然，默认会返回204
      callback: callbackbody,
      signature: signature
    }
  })

  up.start()
}

const fileData = reactive({
  fileList: [] as Array<{
    id: number
    loadType: number
    name: string
    type: string
    size: number
    percentage: number
  }>,
  fileOptions: {
    runtimes: 'html5,flash,silverlight,html4',
    browse_button: 'selectfiles',
    //multi_selection: false,
    container: container.value,
    flash_swf_url: 'lib/plupload-2.1.2/js/Moxie.swf',
    silverlight_xap_url: 'lib/plupload-2.1.2/js/Moxie.xap',
    url: 'http://oss.aliyuncs.com',
    filters: {
      mime_types: [
        //文件格式 doc、docx、pdf、txt
        { title: 'Default Files', extensions: 'doc,docx,pdf,txt,md,miapi' }
      ],
      max_file_size: '1024mb', //最大上传的文件
      prevent_duplicates: true //不允许选取重复文件
    }
  }
})
const uploader = computed(() => {
  return new plupload.Uploader(fileData.fileOptions)
})

const methods = {
  //绑定进队列
  FilesAdded(uploader: any, files: any) {
    let objarr = files.map((val: any) => {
      return {
        id: val.id,
        fileName: val.name,
        type: val.type,
        size: parseInt((val.origSize / 1024 / 1024) * 100) / 100,
        percentage: 0,
        loadType: 0
      }
    })
    fileData.fileList = [...fileData.fileList, ...objarr]
    methods.FileUplodeOn()
  },
  //上传之前回调
  BeforeUpload(up: any, file: any) {
    set_upload_param(up, file.name, true)
    fileData.fileList = fileData.fileList.map((val) => {
      if (val.id == file.id) {
        val.loadType = 1
      }
      return val
    })
  },
  //上传进度回调
  UploadProgress(uploader: any, file: any) {
    fileData.fileList = fileData.fileList.map((val) => {
      if (val.id == file.id) {
        val.percentage = file.percent
      }
      return val
    })
  },
  // 上传成功回调
  FileUploaded(uploader: any, file: any, responseObject: any) {
    fileData.fileList = fileData.fileList.map((val) => {
      if (val.id == file.id) {
        if (responseObject.status == 200) {
          val.loadType = 2
        } else {
          val.loadType = 3
        }
      }
      return val
    })
  },
  //取消上传回调
  removeFile(id: number) {
    uploader.value.removeFile(id)
    fileData.fileList = fileData.fileList.filter((val) => {
      if (val.id == id) {
        return false
      } else {
        return true
      }
    })
  },
  //开始上传
  FileUplodeOn() {
    set_upload_param(uploader.value, '', false)
  },
  // 知识解析
  FileRead(row: any) {
    embedding([{ fileName: row.fileName, knowledgeBaseId: props.knowledgeId }]).then((res: any) => {
      console.log('res', res)
      if (res?.code === 0 && Array.isArray(res?.data)) {
        ElMessage({
          message: '解析成功',
          type: 'success',
          duration: 3000
        })
        emits(
          'updateFileList',
          res.data.map((it: { id: string; fileName: string; fileType: string }) => {
            return {
              id: it.id,
              fileName: it.fileName,
              fileType: it.fileType
            }
          })
        )
        fileData.fileList = fileData.fileList.filter((val) => {
          if (val.id == row.id) {
            return false
          } else {
            return true
          }
        })
      } else {
        ElMessage.error('失败')
      }
    })
  }
}

onMounted(() => {
  //实例化一个plupload上传对象
  uploader.value.init()
  //绑定进队列
  uploader.value.bind('FilesAdded', methods.FilesAdded)
  //绑定进度
  uploader.value.bind('UploadProgress', methods.UploadProgress)
  //上传之前
  uploader.value.bind('BeforeUpload', methods.BeforeUpload)
  //上传成功监听
  uploader.value.bind('FileUploaded', methods.FileUploaded)
})

// //监听
// watch(
//   () => props.drawer,
//   () => {
//     fileData.fileList = []
//   },
//   {
//     immediate: true,
//     deep: true
//   }
// )
</script>

<style scoped lang="scss">
.card-container {
  margin-top: 20px;
}
.file-upload-head {
  padding-left: 0px;
}
.file-card {
  height: calc(100% - 32px);
  display: flex;
  flex-direction: column;
}
</style>
