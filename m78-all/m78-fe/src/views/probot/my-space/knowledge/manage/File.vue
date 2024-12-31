<!--
 * @Description: 
 * @Date: 2024-03-20 16:19:04
 * @LastEditTime: 2024-03-26 16:50:26
-->
<template>
  <el-drawer v-model="drawer" title="知识上传" direction="rtl" size="50%" @open="handleOpen">
    <!-- 上传区域 -->
    <div
      id="container"
      ref="container"
      v-show="fileData.fileList.length === 0"
      style="margin-bottom: 20px"
    >
      <el-button type="primary" ref="selectfiles" id="selectfiles" size="mini">上传文件</el-button>
    </div>
    <el-card>
      <el-table :data="fileData.fileList" style="width: 100%">
        <el-table-column prop="name" label="文件名称"></el-table-column>
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
        <el-table-column prop="type" label="文件类型"></el-table-column>
        <el-table-column prop="size" label="文件大小" v-slot="{ row }">
          {{ row.size }}MB
        </el-table-column>

        <el-table-column fixed="right" label="操作" v-slot="{ row }" width="144px">
          <div v-if="row.loadType == 2">
            <div style="padding-bottom: 4px">
              <el-button type="primary" size="mini" @click="methods.FileRead(row)"
                >知识解析</el-button
              >
            </div>
            <div>
              <el-button type="primary" size="mini" @click="methods.customFileRead(row)"
                >自定义知识解析</el-button
              >
            </div>
          </div>
          <el-button v-else type="danger" size="mini" @click="methods.removeFile(row.id)"
            >取消上传</el-button
          >
        </el-table-column>
      </el-table>
    </el-card>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, reactive, computed, defineEmits } from 'vue'
import plupload from 'plupload'
import { embedding, uploadKnowledgeFile } from '@/api/probot-knowledge'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  modelValue: {}
})
const emits = defineEmits(['update:modelValue', 'update'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})

const route = useRoute()
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

async function get_signature() {
  // 可以判断当前expire是否超过了当前时间， 如果超过了当前时间， 就重新取一下，3s 作为缓冲。
  now = Date.parse(new Date()) / 1000
  const { params } = route
  if (expire < now + 3) {
    await uploadKnowledgeFile({
      knowledgeId: params?.knowledgeBaseId
    }).then((res) => {
      var obj = res.data
      host = obj['host']
      policyBase64 = obj['policy']
      accessid = obj['accessid']
      signature = obj['signature']
      expire = parseInt(obj['expire'])
      callbackbody = obj['callback']
      key = obj['dir']
    })
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

async function set_upload_param(up: any, filename: any, ret: any) {
  if (ret == false) {
    ret = await get_signature()
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
        { title: 'Default Files', extensions: 'doc,docx,pdf,txt,md,miapi,athena' }
      ],
      max_file_size: '1024mb', //最大上传的文件
      prevent_duplicates: false //不允许选取重复文件
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
        name: val.name,
        type: val.type,
        size: parseInt((val.origSize / 1024 / 1024) * 100) / 100,
        percentage: 0,
        loadType: 0
      }
    })
    fileData.fileList.push(...objarr)
    methods.FileUplodeOn()
  },
  //上传之前回调
  BeforeUpload(up: any, file: any) {
    console.log('11111')
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
  embeddingFile(params: any, row: any) {
    embedding(params).then((res: any) => {
      if (res?.code === 0) {
        ElMessage({
          message: '文件已经在解析中',
          type: 'success',
          duration: 3000
        })
        fileData.fileList = fileData.fileList.filter((val) => {
          if (val.id == row.id) {
            return false
          } else {
            return true
          }
        })
        emits('update')
        drawer.value = false
      }
    })
  },
  // 知识解析
  FileRead(row: any) {
    this.embeddingFile(
      [
        {
          fileName: row.name,
          knowledgeBaseId: Number(route.params.knowledgeBaseId)
        }
      ],
      row
    )
  },
  //自定义知识解析
  customFileRead(row: any) {
    ElMessageBox.prompt('自定义知识解析分隔符', '自定义知识解析', {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      inputPattern: /\S/,
      inputErrorMessage: '请输入自定义知识解析分隔符'
    }).then(({ value }) => {
      this.embeddingFile(
        [
          {
            fileName: row.name,
            knowledgeBaseId: Number(route.params.knowledgeBaseId),
            separator: value
          }
        ],
        row
      )
    })
  }
}

const uid = ref()
const handleOpen = () => {
  fileData.fileList = []
  if (!uid.value) {
    uid.value = uploader.value.uid
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
  }
}
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
