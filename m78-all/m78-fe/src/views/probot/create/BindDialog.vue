<!--
 * @Description:
 * @Date: 2024-03-06 19:26:26
 * @LastEditTime: 2024-03-19 20:46:43
-->
<template>
  <el-dialog v-model="dialogVisible" width="1000">
    <template #header="{ close, titleId, titleClass }">
      <div class="dialog-header">
        <h4 :id="titleId" :class="titleClass">{{ props.data.title }}</h4>
        <BaseLink :name="props.data.toCreateTip" @click="toCreateBind"></BaseLink>
      </div>
    </template>
    <div class="bind-dialog-content">
      <slot name="filter" v-if="isHeader"></slot>
      <div class="list-container"><slot name="list"></slot></div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElDialog } from 'element-plus'
import { useRouter } from 'vue-router'
import BaseLink from '../components/BaseLink.vue'

const router = useRouter()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  },
  isHeader: {
    type: Boolean,
    default: true
  }
})
const emits = defineEmits(['update:modelValue', 'toCreateBind'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const toCreateBind = () => {
  const toCreateRouter = props.data.toCreateRouter
  if (toCreateRouter) {
    if (
      toCreateRouter.startsWith &&
      (toCreateRouter.startsWith('http://') ||
        toCreateRouter.startsWith('https://') ||
        toCreateRouter.startsWith('//'))
    ) {
      window.location.href = toCreateRouter
    } else {
      router.push(props.data.toCreateRouter)
    }
  }
}
</script>

<style lang="scss">
.bind-dialog-content {
  .bind-list-content {
    flex: 1;
    padding: 20px 10px 10px;
    border-left: 3px solid #40a3ff;
    display: flex;
    align-items: center;
    p {
      color: #666;
      width: 300px;
      text-align: left;
    }
  }
  .more-info {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 500px;
    p {
      padding-right: 50px;
      color: rgb(107, 114, 128);
      line-height: 30px;
      width: auto;
    }
  }
}
</style>
<style scoped lang="scss">
.dialog-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  h4 {
    padding-right: 10px;
  }
}
.bind-dialog-content {
  .list-container {
    margin-top: 10px;
    border-top: 1px solid #ddd;
  }
}
</style>
