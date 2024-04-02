<template>
  <div class="workflow-header flex-center">
    <div class="left flex-center">
      <el-icon class="ret-btn" @click="backFn"><ArrowLeft /></el-icon>
      <img :src="HeaderImg" alt="" class="img-header" />
      <div>
        <h5 class="name">
          {{ flowInfo?.name }}
          <el-button link :icon="Edit" @click="editClick"></el-button>
        </h5>
        <p class="desc">{{ flowInfo?.desc }}</p>
      </div>
    </div>
    <div class="right">
      <slot name="rBtns"></slot>
    </div>
    <CreateFlow v-model="showCreate" :preInfo="flowInfo" type="edit" @createSuc="editSuc" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import HeaderImg from '../imgs/workflow.png'
import CreateFlow from '@/views/workflow-list/CreateFlow.vue'
import { Edit } from '@element-plus/icons-vue'

const props = defineProps({
  flowInfo: {}
})
const emits = defineEmits(['editNameSuc'])
const router = useRouter()
const showCreate = ref(false)
const backFn = () => {
  router.back()
}
const editClick = () => {
  showCreate.value = true
}
const editSuc = () => {
  emits('editNameSuc')
}
</script>

<style lang="scss" scoped>
.workflow-header {
  background: #f7f7fa;
  border-bottom: 1px solid#1d1c2314;
  justify-content: space-between;
  padding: 16px 24px;
  // :deep(.oz-button:hover) {
  //   background-color: #f2f2f2;
  // }
  // :deep(.oz-button--primary:hover) {
  //   background-color: #2126a7;
  // }
}
.flex-center {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ret-btn {
  font-size: 20px;
  margin-left: 4px;
  margin-right: 20px;
}
.img-header {
  height: 32px;
  width: 32px;
  border-radius: 8px;
  margin-right: 12px;
}
.name {
  color: #1d1c23;
  font-size: 14px;
  font-weight: 600;
  line-height: 24px;
}
.desc {
  display: inline-block;
  background-color: #f0f0f5;
  border-radius: 4px;
  color: #1d1c2399;
  padding: 0 6px;
}
.edit-btn {
  font-size: 12px;
  vertical-align: middle;
  margin-left: 3px;
}
</style>
