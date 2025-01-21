<!--
 * @Description: 
 * @Date: 2024-08-15 21:39:22
 * @LastEditTime: 2024-08-18 15:28:01
-->
<template>
  <div @click="goDetail" class="bot-container">
    <BaseInfo
      :data="{
        describe: item.botInfo.remark,
        name: item.botInfo.name || '----',
        avatarUrl: item.botInfo.avatarUrl || '10'
      }"
    >
    <template #right>
      <div class="right-container"><el-icon><ArrowRight /></el-icon></div>
    </template>
    </BaseInfo>
  </div>
</template>

<script setup lang="ts">
import { ref, watch} from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps({
  data: {
    type: String
  }
})
const item = ref(props.data ? JSON.parse(props.data) : '')

watch(
  () => props.data,
  (newVal = '') => {
    item.value = JSON.parse(newVal)
  }
)

const goDetail=()=>{
  const { href } = router.resolve({
    path: '/probot-visit/'+item.value?.botInfo?.id,
  })
  window.open(href, '_blank')
}
</script>
<style scoped lang="scss">
.bot-container{
  cursor: pointer;
}
.right-container{
  display: flex;
  align-items: center;
  padding:0 10px 0 30px;
  font-size: 18px;
  height: 100%;
  font-weight: bold;
  color: #333
}</style>
