<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, type UploadProps } from 'element-plus'
import Spreadsheet, { type Options } from 'x-data-spreadsheet'
import * as XLSX from 'xlsx'
import { stox, xtos } from './xlsxspread'

const dialogVisible = ref(false)
const grid = ref<Spreadsheet | null>(null)
const files = ref<
  {
    id: string
    label: string
    value: string
  }[]
>([])
const form = ref({
  id: '',
  prompt: ''
})

const options: Options = {
  mode: 'edit', // edit | read
  showToolbar: false,
  showGrid: true,
  showContextmenu: true,
  view: {
    height: () => document.documentElement.clientHeight - 62,
    width: () => document.documentElement.clientWidth
  },
  row: {
    len: 100,
    height: 25
  },
  col: {
    len: 100,
    width: 100,
    indexWidth: 60,
    minWidth: 60
  },
  style: {
    bgcolor: '#FFF',
    align: 'left',
    valign: 'middle',
    textwrap: false,
    strike: false,
    underline: false,
    color: '#0a0a0a',
    font: {
      name: 'Helvetica',
      size: 10,
      bold: false,
      italic: false
    }
  }
}

// const objTest = [
//   {
//     a: 1,
//     b: 2
//   }
// ]

// const ws = XLSX.utils.json_to_sheet(objTest)
// const wb = XLSX.utils.book_new(ws, 'Sheet1')
// console.log(ws)
// console.log('wb:', stox(wb))

const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles)
  const file = uploadFile.raw
  if (!file) return
  if (file.type == 'text/csv') {
    const reader = new FileReader()
    reader.onload = function fileReadCompleted() {
      // 当读取完成时，内容只在`reader.result`中
      console.log(reader.result)
      const text = reader.result
      const content = stox(XLSX.read(text, { type: 'string' }))
      console.log('content', content)
      grid.value?.loadData(content)
      form.value.id = ''
      form.value.prompt = ''
      dialogVisible.value = false
    }
    reader.onerror = () => {
      dialogVisible.value = false
    }
    reader.readAsText(file)
  } else {
    file
      .arrayBuffer()
      .then((arrayBuffer) => {
        const content = stox(XLSX.read(arrayBuffer))
        form.value.id = ''
        form.value.prompt = ''
        grid.value?.loadData(content)
      })
      .finally(() => {
        dialogVisible.value = false
      })
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'text/csv') {
    ElMessage.error('文件不是csv格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('文件超过2MB!')
    return false
  }
  return true
}

const calculate = async () => {
  if (grid.value == null || !form.value.prompt) {
    return
  }
  const content = XLSX.write(xtos(grid.value.getData()), { type: 'string', bookType: 'csv' })
  console.log(content)
  const blob = new Blob([content], { type: 'text/csv' })
  const file = new File([blob], 'demo.csv')
  const fd = new FormData()
  fd.append('file', file)
  fd.append('prompt', form.value.prompt)
  fetch(`${import.meta.env.VITE_GLOB_API_NEW_URL}/z-proxy/csv/upload`, {
    method: 'POST',
    body: fd
  })
    .then((res) => {
      if (res.ok) {
        console.log('success')
        return res.json()
      } else {
        console.log('error')
      }
    })
    .then((res) => {
      console.log('res is', res)
      files.value.push({
        id: res.data,
        label: form.value.prompt,
        value: res.data
      })
      localStorage.setItem('files', JSON.stringify(files.value))
    })
}

// const fetchFiles = async () => {
//   const response = await fetch("/api/z-proxy/csv/list_reqs");
//   const json = await response.json();
//   console.log(json)
//   // eslint-disable-next-line no-cond-assign, no-constant-condition
//   if (json.code == 0  && Array.isArray(json.data)) {
//     files.value = json.data;
//   } else {
//     files.value = [];
//   }
//   return files.value;
// }

const fetchFile = async (id: string) => {
  const response = await fetch(
    `${import.meta.env.VITE_GLOB_API_NEW_URL}/z-proxy/csv/download?id=${id}`
  )
  const text = await response.text()
  const content = stox(XLSX.read(text, { type: 'string' }))
  console.log(content)
  ;(content || []).forEach((element) => {
    element.rows.len = 100
  })
  grid.value && grid.value.loadData(content)
}

onMounted(async () => {
  grid.value = new Spreadsheet('#x-spreadsheet-demo', options).loadData({}) // load data
  const fileOPtions = JSON.parse(localStorage.getItem('files') || '[]')
  // const files = await fetchFiles();
  files.value = fileOPtions
  if (fileOPtions.length > 0) {
    form.value.id = fileOPtions[0].id
    fetchFile(fileOPtions[0].id)
  }
})
</script>

<template>
  <div class="page">
    <div class="header">
      <div></div>
      <el-form inline size="small">
        <el-form-item label="文件">
          <el-select v-model="form.id" @change="fetchFile">
            <el-option
              v-for="item of files"
              :key="item.id"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分析描述">
          <el-input v-model="form.prompt" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="calculate">开始分析</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="dialogVisible = true">本地导入</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div id="x-spreadsheet-demo"></div>
  </div>
  <el-dialog v-model="dialogVisible" title="导入csv" width="80%">
    <el-upload
      class="upload-demo"
      drag
      action="/api/z-proxy/csv/upload"
      multiple
      :before-upload="beforeUpload"
      :on-change="handleChange"
      :limit="1"
      :auto-upload="false"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">拖拽或<em>点击这里</em>导入</div>
      <template #tip>
        <div class="el-upload__tip"></div>
      </template>
    </el-upload>
  </el-dialog>
</template>

<style lang="scss" scoped>
.header {
  display: flex;
  padding-top: 20px;
  justify-content: space-between;
}
</style>
