<!--
 * @Description: 
 * @Date: 2024-01-11 15:56:12
 * @LastEditTime: 2024-02-20 14:58:27
-->
<template>
  <div class="translate-container">
    <CommontPage
      v-model="rightActive"
      :title="t('translate.rightTitle')"
      :historyBtnTitle="t('translate.historyBtnTitle')"
      @showHistoryCallback="getHistoryTable"
      @showFavoriteCallback="getFavoritesTable"
      :showChatHistory="showChatHistory"
      :uuid="uuidObj.id"
      :noUuidMsg="t('translate.noUuidMsg')"
      @addInput="addInput"
      :inputLoading="inputLoading"
      :inputDisabled="!uuidObj.id"
      @stopReq="stopReq"
    >
      <template #left>
        <div class="translate-left">
          <TranslationStyle v-model="currentStyle"></TranslationStyle>
          <div>
            <TranslateText
              v-show="categoryActive === 1"
              :category="categoryData[0]"
              :categoryActive="categoryActive"
            >
            </TranslateText>
            <TranslateDoc
              v-show="categoryActive === 2"
              :category="categoryData[2]"
              :categoryActive="categoryActive"
            ></TranslateDoc>
            <TranslateImage
              v-show="categoryActive === 3"
              :category="categoryData[1]"
              :categoryActive="categoryActive"
            ></TranslateImage>
            <TranslateWeb
              v-show="categoryActive === 4"
              :category="categoryData[3]"
              :categoryActive="categoryActive"
            ></TranslateWeb>
          </div>
          <div class="translate-left-bottom">
            <Lang v-model="currentLanguage" :origin="origin"></Lang>
            <InputBox
              ref="inputBoxRef"
              v-model="origin"
              :rightActive="rightActive"
              :translationType="categoryActive"
              :currentStyle="currentStyle"
              :currentLanguage="currentLanguage"
              @updateHistory="getHistoryTable"
              @updateFavorite="getFavoritesTable"
            ></InputBox>
          </div>
        </div>
      </template>
      <template #btnsTop>
        <div class="category">
          <h3>{{ t('translate.category') }}</h3>
          <ul>
            <li v-for="(item, index) in categoryData" :key="item.key + index">
              <el-button
                :type="categoryActive === item.key ? 'primary' : ''"
                @click="categoryClick(item.key)"
                ><i :class="['iconfont', item.icon]"></i><span>{{ item.value }}</span></el-button
              >
            </li>
          </ul>
        </div>
      </template>
      <template #chatList>
        <ChatList :data="chatListData" @replaceFn="replaceFn"></ChatList>
      </template>
      <template #historyTable>
        <HistoryTable
          :data="historyData || []"
          @updateData="getHistoryTable"
          @close="closeHistory"
          :title="t('translate.historyTableTitle')"
          :uuid="uuidObj.id"
        />
      </template>
      <template #favoriteTable>
        <HistoryTable
          :data="favoritesData || []"
          @updateData="getFavoritesTable"
          @close="closeFavorites"
          :title="t('translate.favoritesTableTitle')"
          :uuid="uuidObj.id"
        />
      </template>
    </CommontPage>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { t } from '@/locales'
import CommontPage from '@/components/common-page/CommonPage.vue'
import TranslationStyle from './components/TranslationStyle.vue'
import TranslateText from './components/TranslateText.vue'
import TranslateImage from './components/TranslateImage.vue'
import TranslateDoc from './components/TranslateDoc.vue'
import TranslateWeb from './components/TranslateWeb.vue'
import Lang from './components/Lang.vue'
import InputBox from './components/InputBox.vue'
import ChatList from './components/ChatList.vue'
import mitt from '@/utils/bus'
import {
  fetchTranslateHistoryList,
  fetchTranslateChat,
  fetchTranslateFavorites
} from '@/api/translate'
import HistoryTable from './components/HistoryTable.vue'

const origin = ref('')
const inputBoxRef = ref()

const translationStyle = localStorage.getItem('translationStyle')
const currentStyle = ref(translationStyle ? JSON.parse(translationStyle) : [])

const categoryActive = ref(1)
const categoryData = computed(() => {
  return [
    {
      icon: 'icon-class-text',
      value: t('translate.text'),
      key: 1
    },
    {
      icon: 'icon-class-img',
      value: t('translate.img'),
      key: 3,
      accept: 'image/*'
    },
    {
      icon: 'icon-class-doc',
      value: t('translate.doc'),
      key: 2,
      accept:
        'application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/pdf,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,text/plain'
    },
    {
      icon: 'icon-class-web',
      value: t('translate.web'),
      key: 4
    }
  ]
})
// 类别点击
const categoryClick = (key: number) => {
  categoryActive.value = key
}

const currentLanguage = ref({ fromLanguage: '', toLanguage: '' })

// 控制主页面1历史2收藏3
const rightActive = ref(1)
// 显示历史聊天记录
const showChatHistory = ref(false)
const uuidObj = ref({
  id: ''
})
const inputLoading = ref(false)
const chatListData = ref([])
// 增加聊天list
const addChatItem = ({ text = '', loading = false, inversion = false, textType = '' }) => {
  const timer = new Date()
  const len = timer.getTime() + '_' + chatListData.value.length
  const item = {
    hideMore: true,
    error: false,
    dateTime: timer.toLocaleString(),
    indexKey: len,
    text,
    loading,
    textType,
    inversion // 是否是自己
  }
  chatListData.value.push(item)
  return item
}
const addUserChat = (text) => {
  addChatItem({ text, inversion: true })
}
const addAiChat = () => {
  const item = addChatItem({ text: '', loading: true, textType: 'translate' })
  return item
}
//  修改ai聊天记录状态
const changeAiChatStaus = ({ text, indexKey, data, textType = 'translate' }) => {
  const toChangeIndex = chatListData.value.findIndex((item) => item.indexKey == indexKey)
  if (toChangeIndex < 0) return
  chatListData.value[toChangeIndex] = {
    ...chatListData.value[toChangeIndex],
    text,
    loading: false,
    dateTime: new Date().toLocaleString(),
    translateData: data,
    textType
  }
}

// 输入框输入
const addInput = (data) => {
  addUserChat(data.inputV)
  const { indexKey } = addAiChat()
  inputLoading.value = true
  controller.value = new AbortController()
  fetchTranslateChat(
    {
      requirements: currentStyle.value,
      fromLanguage: currentLanguage.value.fromLanguage,
      toLanguage: currentLanguage.value.toLanguage,
      fromText: origin.value,
      toText: inputBoxRef.value.getResultValue(),
      chatContent: data.inputV
    },
    controller.value.signal
  )
    .then((res) => {
      const obj =
        res?.data && res.code == 0
          ? { data: res.data, text: t('excle.parsingSuc') }
          : { text: t('excle.parsingFailed'), textType: '' }
      changeAiChatStaus({ ...obj, indexKey })
    })
    .catch((err) => {
      const obj =
        err.message == 'canceled'
          ? { text: t('excle.canceled') }
          : { text: t('excle.parsingFailed') }
      changeAiChatStaus({ ...obj, indexKey, textType: '' })
    })
    .finally(() => {
      inputLoading.value = false
    })
}
// 当前uuid
mitt.on('translationUuid', (id: string) => {
  uuidObj.value.id = id
})

const historyData = ref([])
const favoritesData = ref([])

// 获取历史数据
const getHistoryTable = () => {
  fetchTranslateHistoryList({
    type: categoryActive.value
  }).then((res) => {
    historyData.value = res.data
  })
}
const closeHistory = () => {
  rightActive.value = 1
}
// 收藏
const getFavoritesTable = () => {
  fetchTranslateFavorites({
    type: categoryActive.value
  }).then((res) => {
    favoritesData.value = res.data
  })
}
const closeFavorites = () => {
  rightActive.value = 1
}
const replaceFn = (text) => {
  inputBoxRef.value.changeResult(text)
}
const controller = ref()
const stopReq = () => {
  controller.value?.abort()
}
</script>

<style lang="scss" scoped>
.translate-container {
  height: 100%;
  .translate-left {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    overflow: auto;
    &-bottom {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: auto;
    }
  }
  .category {
    padding-top: 10px;
    h3 {
      font-size: 16px;
      line-height: 30px;
      padding-bottom: 10px;
    }
    ul {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
    }
    li {
      width: 46%;
      padding-bottom: 20px;

      .oz-button {
        width: 100%;
      }
      span {
        padding-left: 10px;
      }
    }
  }
}
</style>
