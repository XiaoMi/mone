<template>
  <el-tooltip effect="dark" content="重试" placement="top" v-if="isAdmin">
    <el-button link @click="clickFn" class="test-node-btn" :disabled="disabled">
      <i class="iconfont icon-refresh"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'
const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo.admin)

const props = defineProps({
  disabled: {}
})
const emits = defineEmits(['click'])
const clickFn = () => {
  emits('click')
}
</script>
