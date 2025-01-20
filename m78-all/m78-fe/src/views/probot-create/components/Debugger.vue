<template>
  <el-drawer v-model="drawer" @close="close" title="调试" direction="rtl" size="50%" @open="open">
    <DebuggerPage v-if="drawer" :data="data" type="debug" :topicId="topicId" :showImg="showImg"></DebuggerPage>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed,ref } from 'vue'
import DebuggerPage from '@/components/probot/DebuggerPage.vue'
import { getTopicId } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()
const props = defineProps<{
  modelValue: boolean
  data: Object
}>()

const emits = defineEmits(['update:modelValue'])

const drawer = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})
const LLMModelSelList = computed(() => probotStore.LLMModelSelList)
const topicId=ref('')
const showImg=ref(false)

const close = () => {
  // probotStore.setCreatedRobtId('')
}
const open=()=>{
  showImg.value=false
  const {info}=LLMModelSelList.value.find((item)=>item.cname===props.data.botSetting.aiModel)
  if(info){
    try {
      const infoObj=JSON.parse(info)
      if(infoObj&& infoObj['ability.vision']){
        showImg.value=true
      }
    } catch (error) {
      console.log('error',error)
    }
    
  }
  
  getTopicId({"botId":props.data?.botId,"topicType":'1',"createIfNotExist":'true'}).then((res)=>{
    topicId.value=res?.data[0]?.id
  })
}
</script>
