<template>
  <div class="page-container">
    <div class="left">
      <div class="container-div left-div">
        <slot name="left"></slot>
      </div>
    </div>
    <div class="right">
      <div class="container-div right-div">
        <div class="right-container" v-if="active == 1">
          <div class="top">
            <div class="title-box">
              <p class="title">
                <M78Svg class="m78-svg" />
                <i class="text">{{ props.title || t('excle.aIDocAssistant') }}</i>
              </p>
              <div class="menu">
                <el-button v-if="showDefaultBtns" type="primary" link @click="siwtchShowBtns">
                  <i
                    class="iconfont fold-expand"
                    :class="showBtns ? 'icon-fold' : 'icon-expand'"
                  ></i>
                </el-button>
              </div>
            </div>
            <div class="btn-box" v-if="showBtns">
              <slot name="btnsTop"></slot>
              <MyBtn
                v-if="props.showDefaultBtns"
                bgColor="#67c5ed"
                iconClass="icon-icon-test"
                @click="showHistoryFn"
                :text="props.historyBtnTitle || t('excle.myDocs')"
              />
              <MyBtn
                v-if="props.showDefaultBtns"
                bgColor="#5dc499"
                @click="showFavorite"
                iconClass="icon-tijiansuifang"
                :text="t('excle.myFavorites')"
              />
              <slot name="btnsBtm"></slot>
            </div>
          </div>
          <div class="btm-box">
            <HistoryChatList
              v-if="props.showChatHistory"
              @editHisChatCb="emits('editHisChatCb')"
              @delHisChatCb="emits('delHisChatCb')"
              :list="props.hisChatData"
              @selHis="selHis"
            />
            <div class="chat-list-box">
              <slot name="chatList"></slot>
            </div>
            <CommonInput
              ref="chatInput"
              v-if="showCommonInput"
              v-model="inputText"
              class="input-b"
              @enterFn="inputHandle"
              :loading="inputLoading"
              :placeholder="t('excle.MessageAIDocAssistant')"
              @stopReq="stopReqFn"
              :disabled="props.inputDisabled"
            ></CommonInput>
          </div>
        </div>
        <!--  我的文档 -->
        <slot name="historyTable" v-if="active == 2"></slot>
        <!-- 我的收藏 -->
        <slot name="favoriteTable" v-if="active == 3"></slot>
        <!-- 自定义设置 -->
        <slot name="otherShow" v-if="active == 4"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import M78Svg from '@/svgs/m78.svg'
import { ref, computed } from 'vue'
import { t } from '@/locales'
import CommonInput from '@/components/CommonInput.vue'
import { ElMessage } from 'element-plus'
import HistoryChatList from '@/components/history-chat-list/index.vue'

const props = defineProps({
  uuid: {},
  showDefaultBtns: {
    type: Boolean,
    default: true
  },
  showCommonInput: {
    type: Boolean,
    default: true
  },
  showChatHistory: {},
  title: {
    type: String
  },
  //  如果没有uuid的提示信息
  noUuidMsg: {
    type: String,
    default: '请先上传文档！'
  },
  modelValue: {
    type: Number,
    default: 1
  },
  historyBtnTitle: {
    type: String
  },
  inputLoading: {
    type: Boolean,
    default: false
  },
  inputDisabled: {
    type: Boolean,
    default: false
  },
  hisChatData: {
    type: Array,
    default: () => []
  }
})

const emits = defineEmits([
  'update:modelValue',
  'addInput',
  'stopReq',
  'showHistoryCallback',
  'showFavoriteCallback',
  'editHisChatCb', // 编辑历史记录回调
  'delHisChatCb' // 删除历史记录回调
])
// 1 初始化 2 历史文档 3 我的收藏
const active = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const chatInput = ref()
const showBtns = ref(true)
const siwtchShowBtns = () => {
  showBtns.value = !showBtns.value
}
const inputText = ref('')

const stopReqFn = () => {
  emits('stopReq')
}
const inputHandle = () => {
  if (!inputText.value) return
  if (!props.uuid) {
    ElMessage.warning(props.noUuidMsg)
    return
  }
  emits('addInput', {
    inputV: inputText.value
  })
  inputText.value = ''
}
const tableType = ref('history')
// 展示历史文档
const showHistoryFn = () => {
  active.value = 2
  emits('showHistoryCallback')
}
const showFavorite = () => {
  active.value = 3
  emits('showFavoriteCallback')
}
const selHis = (inputV) => {
  emits('addInput', { inputV })
}
</script>

<style lang="scss" scoped>
.page-container {
  display: flex;
  height: 100%;
}
.left {
  padding: 20px 16px;
  height: 100%;
  flex: 1;
  overflow: hidden;
}
.right {
  flex-basis: 360px;
  min-width: 360px;
  padding: 20px 16px 20px 0;
}

.container-div {
  height: 100%;
  border-radius: 5px;
  background: #fff;
  overflow: hidden;
}
.left-div {
  box-shadow: 0 10px 20px 0 rgba(231, 237, 248, 0.48);
}
.right-div {
  box-shadow: 0 1px 5px 0 rgba(41, 55, 85, 0.36);
}

.page-container {
  .title-box {
    background: #e0f4ff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 8px;
  }
  .m78-svg {
    width: 40px;
  }
  .title {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 15px;
    padding: 8px 0 5px 0;
    .text {
      color: #0f1035;
      font-weight: 500;
    }
  }
  .menu {
    width: 40px;
    height: 40px;
    // background: #365486;
    border-radius: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    cursor: pointer;
  }
  .fold-expand {
    color: #0f1035;
    font-size: 16px;
  }
  .right-container {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .btm-box {
    flex: 1;
    padding: 0 16px 16px 16px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    .input-b {
      width: 100%;
    }
  }
  .btn-box {
    width: 100%;
    padding: 0px 16px;
    :deep(.btn),
    .btn {
      margin-top: 10px;
    }
  }
}
.chat-list-box {
  flex: 1;
  overflow: hidden;
}

.flex-row-reverse {
  .right {
    padding: 20px 0 20px 16px;
  }
}
</style>
