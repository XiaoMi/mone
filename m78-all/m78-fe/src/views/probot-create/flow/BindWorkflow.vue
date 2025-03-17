<!--
 * @Description: 
 * @Date: 2024-03-07 11:04:19
 * @LastEditTime: 2024-08-15 17:34:48
-->
<template>
  <BindDialog v-model="dialogVisible" :data="bindData">
    <template #filter><BindWorkflowFilter></BindWorkflowFilter></template>
    <template #list>
      <template v-if="data.length">
        <div v-for="(item, index) in data" :key="index" class="bind-list-item">
          <div class="bind-list-content">
            <BaseInfo :data="item" size="small">
              <div class="more-info">
                <p>创建于{{ dateFormat(item.utime, 'yyyy-mm-dd HH:MM:ss') }}</p>
              </div>
            </BaseInfo>
          </div>
          <div class="btn-container">
            <BaseLink
              :name="item.bind ? '已绑定' : '绑定'"
              @click="bindClick(item)"
              :disabled="item.bind"
            ></BaseLink>
          </div>
        </div>
      </template>
      <el-empty v-else description="不好意思，还没有数据呐~" :image-size="80"></el-empty>
    </template>
  </BindDialog>
</template>

<script setup lang="ts">
import { computed, watch, ref } from 'vue'
import BaseInfo from '@/components/BaseInfo.vue'
import BindDialog from '../components/BindDialog.vue'
import BindWorkflowFilter from './BindWorkflowFilter.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import dateFormat from 'dateformat'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

const workspaceId = computed(() => probotStore.workspaceId)
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  }
})
const emits = defineEmits(['update:modelValue', 'update'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const bindData = ref({
  title: '绑定工作流',
  toCreateTip: '还没有工作流，去新建',
  toCreateRouter: {
    path: '/probot-space',
    query: {
      tab: 'workflow'
    }
  }
})

watch(
  () => workspaceId.value,
  (id) => {
    if (id) {
      bindData.value.toCreateRouter.path = '/probot-space/' + id
    }
  },
  {
    immediate: true,
    deep: true
  }
)

const bindClick = (value: any) => {
  props.data.forEach((item: any) => {
    if (item.id == value.id) {
      item.bind = true
    }
  })
  const selected = props.data.filter((item) => item.bind)
  emits('update', selected)
}
</script>

<style lang="scss" scoped>
.bind-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ddd;
}
</style>
