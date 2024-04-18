<!--
 * @Description: 
 * @Date: 2024-01-11 17:19:34
 * @LastEditTime: 2024-03-05 10:31:35
-->
<template>
  <div class="lang">
    <div class="lang-left">
      <LangMenu
        :menuData="leftMenuData"
        v-model="leftMenuActive"
        @selected="selectedMenu"
      ></LangMenu>
      <LangSelect :current="leftData" :allLang="allLang" @selected="selectedLeft"></LangSelect>
    </div>
    <el-button class="lang-center" @click="exChange"
      ><i class="iconfont icon-exchange"></i
    ></el-button>
    <div class="lang-right">
      <LangMenu :menuData="rightMenuData" v-model="rightMenuActive"></LangMenu>
      <LangSelect :current="rightData" :allLang="allLang" @selected="selectedRight"></LangSelect>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import { useAppStore } from '@/stores'
import mitt from '@/utils/bus'
import { t } from '@/locales'
import langEn from './langEn'
import langZh from './langZh'
import LangMenu from './LangMenu.vue'
import LangSelect from './LangSelect.vue'
import { fetchTranslateType } from '@/api/translate'

const props = defineProps({
  modelValue: {
    type: Object,
    default: {}
  },
  origin: {}
})
const emits = defineEmits(['update:modelValue'])

const selectedLang = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const appStore = useAppStore()
const detectLanguage = computed(() => t('translate.detectLanguage')) //检测语言文本
const detection = ref('') //检测出的值
const detectionData = ref() //检测整体数据
const leftMenuActive = ref(0)
const rightMenuActive = ref(0)
let leftMenuData = ref()
let rightMenuData = ref()
const langIndex = {
  left: '1',
  right: '0'
}
const allLang = ref()
let leftData = ref()
let rightData = ref()

const setData = () => {
  const l = leftData.value?.length
  const r = rightData.value?.length
  leftMenuData.value = l ? [...detectionData.value, ...leftData.value] : [...detectionData.value]
  rightMenuData.value = r ? [...rightData.value] : []
  selectedLang.value = {
    fromLanguage: l
      ? leftMenuActive.value === 0
        ? detection.value
        : leftMenuData.value[leftMenuActive.value].value
      : '',
    toLanguage: r ? rightData.value[0].value : ''
  }
}
const selectedMenu = (index: number) => {
  mitt.emit('translateType', props.origin)
  mitt.emit('translateLang', {
    fromLanguage: index === 0 ? detection.value : leftMenuData.value[index].value
  })
}
const exChange = () => {
  let temp = leftData.value
  leftData.value = [...rightData.value]
  rightData.value = [...temp]
  setData()
}
const selectedLeft = (data: string) => {
  leftData.value = [
    {
      value: data
    }
  ]
  setData()
}
const selectedRight = (data: string) => {
  rightData.value = [
    {
      value: data
    }
  ]
  setData()
}

onMounted(() => {
  mitt.on('translateLang', (data) => {
    leftData.value = [
      {
        value: data?.fromLanguage || allLang.value[langIndex.left]
      }
    ]
    rightData.value = [
      {
        value: data?.toLanguage || allLang.value[langIndex.right]
      }
    ]
    setData()
  })
  mitt.on('translateType', async (value?: string) => {
    let str = detectLanguage.value
    detection.value == ''
    if (value && leftMenuActive.value === 0) {
      await fetchTranslateType({ content: value }).then(({ data }) => {
        if (data?.type) {
          str = detectLanguage.value + ' - ' + data.type
          detection.value = data.type
        }
      })
    }
    detectionData.value = [
      {
        value: str
      }
    ]
    setData()
  })
})
watch(
  () => appStore.language,
  (val) => {
    nextTick(() => {
      mitt.emit('translateType', '')
      if (val === 'en-US') {
        allLang.value = langEn
      } else if (val === 'zh-CN') {
        allLang.value = langZh
      }
      mitt.emit('translateLang')
    })
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style scoped lang="scss">
.lang {
  display: flex;
  align-items: center;
  &-left {
    flex: 1;
    display: flex;
    align-items: center;
  }

  &-center {
    border: none;
    border-radius: 50%;
    margin: 0 10px;
  }

  &-right {
    flex: 1;
    display: flex;
    align-items: center;
  }
}
</style>
