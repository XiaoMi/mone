
<template>
    <BindLayout
      class="flow-workflow"
      title="知识库绑定"
      tooltip="允许绑定私有知识库"
      :btn="{
        name: '绑定',
        icon: 'icon-plus1',
        click: addPlug,
        size: 'small',
        disabled: props.disabled
      }"
      :data="infoData"
      empty="给你的Probot绑定知识库吧"
    >
      <template v-slot:right="{ item, index }">
        <div class="btn-container">
          <BaseLink name="卡片样式" icon="icon-danlanqiapianyangshi"></BaseLink>
          <BaseLink name="移除" icon="icon-yichu" @click="removeItem(item)"></BaseLink>
        </div>
      </template>
    </BindLayout>
    <BindKnowlege v-if="bindWorkflowVisible" v-model="bindWorkflowVisible"></BindKnowlege>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import BindKnowlege from './BindKnowlege.vue'
import { useProbotStore } from '@/stores/probot'
import BaseLink from '@/components/probot/BaseLink.vue'
import BindLayout from '../components/BindLayout.vue'

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
const probotStore = useProbotStore()

const infoData = computed(() => probotStore.bindKnowlege)

const bindWorkflowVisible = ref(false)

const addPlug = () => {
  bindWorkflowVisible.value = true
}

const removeItem = (item: any) => {
  probotStore.setBindKnowlege(infoData.value.filter((it) => it.id != item.id))
}
</script>

<style scoped lang="scss">
.btn-container {
  display: flex;
  justify-content: space-between;
  width: 200px;
  float: right;
}
</style>