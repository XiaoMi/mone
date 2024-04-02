<!-- eslint-disable prettier/prettier -->
<template>
  <div class="doc-container">
    <CommonPage
      v-model="rightActive"
      :showChatHistory="showHistory"
      :uuid="uuidVal"
      @addInput="addInput"
      :inputLoading="inputLoading || excleAnalysising"
      @stopReq="stopReq"
      :inputDisabled="timer ? true : false"
      :hisChatData="hisChatData"
      @editHisChatCb="getHisData"
      @delHisChatCb="getHisData"
    >
      <template #left>
        <div class="left-inner">
          <div class="left-top">
            <div class="file-name">
              <FileNameCom :name="fileName" />
            </div>
            <div class="right-btn">
              <ExcleAction
                @commandFn="excleActionFn"
                v-if="excleModel == 'edit'"
                class="margin-r-10"
              />
              <ChangeExcleMode class="margin-r-10" v-model="excleModel" @change="changeExcleMode" />
              <MetaData v-if="uuidVal" :uuid="uuidVal" />
              <el-button plain size="small" @click="exportFn">{{ t('common.export') }}</el-button>
              <FavoriteBtn
                :favoriteData="{
                  favorite: docDetail?.favorite,
                  id: uuidVal
                }"
                @onSuccess="favoriteSuccess"
              />
              <MoreCom @commandFn="moreAction" />
            </div>
          </div>
          <MyExcle
            ref="myExcle"
            :uuid="uuidVal"
            @uploadSuccess="uploadSuccess"
            @beforeUpload="beforeUpload"
            @uploadFailed="uploadFailed"
          />
          <div v-if="!uuidVal" class="analysis-container">
            <ImportDoc type="drag" @uploadSuccess="uploadSuccess" />
          </div>
          <div class="analysis-container" v-if="excleAnalysising">
            <AnalysisLoading :loadingText="loadingText" />
          </div>
        </div>
      </template>
      <template #btnsTop>
        <ImportDoc
          @uploadSuccess="uploadSuccess"
          type="btn"
          :disabled="excleAnalysising"
          @beforeUpload="beforeUpload"
          @uploadFailed="uploadFailed"
        />
      </template>
      <template #btnsBtm>
        <MyBtn
          bgColor="#ce9de7"
          iconClass="icon-daochu"
          :text="t('excle.exportApi')"
          @click="clickExportApi"
        />
      </template>
      <template #chatList>
        <ChatList v-model="inputsData" />
      </template>
      <template #historyTable>
        <!-- tableType:favoriteTable我的收藏 history 我的文档 -->
        <StarsHistory
          @closeHistory="closeHistory"
          @showItem="clickShowItem"
          dataType="history"
          ref="myDocDom"
          @delItemSuccess="delMyDocSuccess"
        />
      </template>
      <template #favoriteTable>
        <!-- tableType:favoriteTable我的收藏 history 历史文档 -->
        <StarsHistory
          @closeHistory="closeHistory"
          @showItem="clickShowItem"
          dataType="favorite"
          ref="myFavDom"
        />
      </template>
      <template #otherShow>
        <ExportApiTable v-if="activeShow == 'exportApi'" @closeFn="closeHistory" />
      </template>
    </CommonPage>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import MyExcle from './components/MyExcle.vue'
import FavoriteBtn from './components/FavoriteBtn.vue'
import CommonPage from '@/components/common-page/CommonPage.vue'
import MoreCom from './components/MoreCom.vue'
import MyBtn from '@/components/MyBtn.vue'
import { fetchInput, getDocDetail, delDocApi, getDocInputList } from '@/api/excle'
import FileNameCom from './components/FileNameCom.vue'
import { ElMessage } from 'element-plus'
import ChatList from './components/ChatList.vue'
import StarsHistory from './components/StarsHistory.vue'
import { onBeforeUnmount } from 'vue'
import AnalysisLoading from '@/components/AnalysisLoading.vue'
import { t } from '@/locales'
import MetaData from './components/MetaData.vue'
import ImportDoc from './components/ImportDoc.vue'
import ChangeExcleMode from '@/views/doc/components/ChangeExcleMode'
import ExcleAction from './components/ExcleAction'
import ExportApiTable from '@/components/export-http/ExportApiTable'

const myExcle = ref()
const uuidVal = ref('')
const showHistory = ref(false)
const fileName = ref('')
const inputLoading = ref(false)
const docDetail = ref({
  favorite: 0
})
const excelData = ref({
  title: '',
  list: []
})
const metaContent = ref('')
const loadingText = ref('')
const myDocDom = ref() // 我的文档
const myFavDom = ref() // 我的收藏

//  excle正在分析中
const excleAnalysising = computed(() => {
  return excleStatus.value == 2
})
const rightActive = ref(1)
const activeShow = ref('exportApi') //当active不是2时候是展示导出接口，还是什么
const timer = ref(null)
// 0：文件上传成功，但尚未完成建表 如果后端返回0则设置为2
// 2：AI解析处理中
// 1：已完成解析
// 3：已完成建表，尚未插入数据
const parsingCode = ref(['0', '2', '3'])
const excleStatus = ref(-1)
const changeExcleStatus = (status, text = '') => {
  excleStatus.value = status
  if (status == 2) {
    // 只有2时候才展示，所以只需要关注2时确认文案
    loadingText.value = text
  }
}
const uploadFileUuid = ref()
const timerFn = () => {
  // 上传文件之后
  timer.value = setTimeout(() => {
    getDocDetail(uploadFileUuid.value)
      .then(({ code, data, status }) => {
        if (code != 0) {
          clearTimerFn()
          return
        }
        // 1：已完成解析
        if (data.status == '1') {
          // 解析完成展示数据
          changeExcleStatus(1)
          uuidVal.value = uploadFileUuid.value
          myExcle.value.initSheet(data.data)
          fileName.value = data.originalFileName
          inputsData.value = []
          metaContent.value = data.metaContent
          clearTimerFn()
          // 2 0 3 解析中
        } else if (parsingCode.value.indexOf(data.status) > -1) {
          changeExcleStatus(2)
          clearTimeout(timer.value)
          timerFn()
        } else {
          // 解析失败
          ElMessage.warning(t('excle.parsingFailed'))
          clearTimerFn()
          changeExcleStatus(data.status)
        }
        if (['2', '0'].indexOf(data.status) < 0) {
          updateMyDoc()
        }
      })
      .catch(() => {
        clearTimerFn()
        changeExcleStatus(data.status)
      })
  }, 2000)
}
const updateMyDoc = () => {
  // 我的文档 需要更新数据
  myDocDom.value?.init()
}
// 导入文件成功
const uploadSuccess = (uploadRes) => {
  ElMessage.success(t('excle.uploadSuccess'))
  resetExcleModel()
  updateMyDoc()
  // 上传成功开始解析
  changeExcleStatus(2)
  docDetail.value = {
    favorite: 0
  }
  showHistory.value = false
  inputsData.value = []
  uploadFileUuid.value = uploadRes.uuid
  timerFn()
}

const inputsData = ref([])
const controller = ref()

const favoriteSuccess = () => {
  docDetail.value.favorite = docDetail?.value.favorite ? 0 : 1
  myFavDom.value?.init()
}
// favorite我的收藏 history 历史文档
const tableType = ref('history')

const exportFn = () => {
  myExcle.value.exportData()
}

const closeHistory = () => {
  rightActive.value = 1
}
//  修改ai聊天记录状态
const changeAiChatStaus = ({ text, indexKey }) => {
  const toChangeIndex = inputsData.value.findIndex((item) => item.indexKey == indexKey)
  if (toChangeIndex < 0) return
  inputsData.value[toChangeIndex] = {
    ...inputsData.value[toChangeIndex],
    text,
    loading: false,
    dateTime: new Date().toLocaleString()
  }
}

const addUserChat = (inputV) => {
  addChatItem({ text: inputV, loading: false, inversion: true })
}

const addInput = ({ inputV, sucCb }) => {
  changeExcleStatus(2)
  inputLoading.value = true
  controller.value = new AbortController()
  addUserChat(inputV)
  const { indexKey } = addChatItem({ text: '', loading: true })
  fetchInput(
    {
      chatContent: inputV,
      documentUuid: uuidVal.value
    },
    controller.value.signal
  )
    .then((res) => {
      const { data, code } = res
      const allObj = {}
      if (res.code != 0 || !data?.data) {
        allObj.text = t('excle.parsingFailed')
      } else {
        myExcle.value.addSheet(data.data)
        allObj.text = t('excle.parsingSuc')
      }
      changeAiChatStaus({ ...allObj, indexKey })
    })
    .catch((err) => {
      const text = err.message == 'canceled' ? t('excle.canceled') : t('excle.parsingFailed')
      changeAiChatStaus({ text, indexKey })
    })
    .finally(() => {
      getHisData()
      if (sucCb) sucCb()
      changeExcleStatus(1)
      inputLoading.value = false
    })
}
// 增加聊天list
const addChatItem = ({ text = '', loading = false, inversion = false }) => {
  const timer = new Date()
  const len = timer.getTime() + '_' + inputsData.value.length
  const item = {
    hideMore: true,
    error: false,
    dateTime: timer.toLocaleString(),
    indexKey: len,
    text,
    loading,
    inversion // 是否是自己
  }
  inputsData.value.push(item)
  return item
}

const stopReq = () => {
  controller.value?.abort()
}

const clearTimerFn = () => {
  clearTimeout(timer.value)
  timer.value = null
}
const clickShowItem = (item) => {
  showItem(item.id)
}
// 点击历史文档 或者 我的收藏 或上传完文件
const showItem = (id) => {
  changeExcleStatus(2)
  clearTimerFn()
  uuidVal.value = id
  showHistory.value = true
  getDocDetail(id)
    .then(({ data, code }) => {
      if (code != 0) return
      resetExcleModel()
      const resData = data?.data || []
      myExcle.value.initSheet(resData)
      fileName.value = data.originalFileName
      docDetail.value = data
      metaContent.value = data.metaContent
      inputsData.value = []
    })
    .finally(() => {
      changeExcleStatus(1)
    })
}
const beforeUpload = () => {
  changeExcleStatus(2, t('excle.uplodingDocTips'))
}
const uploadFailed = () => {
  changeExcleStatus(503)
}
const delMyDocSuccess = (id) => {
  if (uuidVal.value == id) {
    myExcle.value.initSheet([])
    uuidVal.value = null
  }
}

const deleteDoc = () => {
  changeExcleStatus(2, t('excle.deletingTip'))
  delDocApi(uuidVal.value)
    .then((res) => {
      uuidVal.value = null
      if (res.code != 0) return
      showHistory.value = false
      inputsData.value = []
      ElMessage.success(t('common.deleteSuccess'))
      // 如果展示的是我的文档则需要更新
      updateMyDoc()
    })
    .catch(() => {
      ElMessage.error(t('common.deleteFailed'))
    })
    .finally(() => {
      changeExcleStatus(1)
    })
}

const addRowFn = () => {
  myExcle.value.appendRow()
}
const addColFn = (keyName) => {
  myExcle.value.appendColFn(keyName)
}
const excleActionFn = (command, keyName) => {
  switch (command) {
    case 'addRow':
      addRowFn()
      break
    case 'addCol':
      addColFn(keyName)
      break
    default:
      break
  }
}
const moreAction = (command, keyName) => {
  switch (command) {
    case 'delete':
      deleteDoc()
      break
    case 'refresh':
      refreshExcle()
      break
    default:
      break
  }
}

const refreshExcle = () => {
  showItem(uuidVal.value)
}

const excleModel = ref('read')
const resetExcleModel = () => {
  // 重置
  excleModel.value = 'read'
}
const changeExcleMode = (val) => {
  myExcle.value.changeMode(val)
}
const hisChatData = ref([])
//  获取历史聊天记录
const getHisData = () => {
  if (!showHistory.value) return
  getDocInputList(uuidVal.value).then((res) => {
    if (res.code != 0) return
    hisChatData.value = res?.data?.chatInfoList || []
  })
}

const clickExportApi = () => {
  activeShow.value = 'exportApi'
  rightActive.value = 4
}

watch(
  () => showHistory.value,
  (val) => {
    if (val) {
      getHisData()
    }
  }
)

onBeforeUnmount(() => {
  if (timer.value) {
    clearTimerFn()
  }
})
</script>

<style scoped lang="scss">
.doc-container {
  height: 100%;
}

.x-spreadsheet-demo {
  width: 100%;
}
.l-top {
  text-align: right;
}
.btm {
  position: fixed;
  bottom: 0;
  display: flex;
  width: 100%;
  padding: 10px 20px;
}

.left-inner {
  height: 100%;
  padding: 16px;
  display: flex;
  flex-direction: column;
  position: relative;
  .analysis-container {
    z-index: 13;
    position: absolute;
    width: calc(100% - 32px);
    height: calc(100% - 32px);
  }
}
.left-top {
  height: 38px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
}
.right-btn {
  display: flex;
  justify-content: center;
  align-items: center;
}
.icon-file-exe {
  font-size: 23px;
  margin: 3px 3px 0 0;
}
.margin-r-10 {
  margin-right: 10px;
}
.file-name {
  flex: 1;
  overflow: hidden;
}
</style>
