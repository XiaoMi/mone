<template>
  <el-select v-model="val" :placeholder="props.placeholder" :disabled="props.disabled">
    <el-option-group v-for="(group, key) in options" :key="key" :label="key">
      <el-option v-for="item in group" :key="item.cname" :label="item.cname" :value="item.cname">
        <div class="group-container">
          <div class="group-left">
            <img :src="item.imageUrl" style="width: 20px; height: 20px; margin-right: 10px" />
            <span>{{ item.cname }}</span>
          </div>
          <el-popover
            placement="top"
            title=""
            :width="200"
            trigger="hover"
            :content="item.description"
          >
            <template #reference>
              <el-icon><Warning /></el-icon>
            </template>
          </el-popover>
        </div>
      </el-option>
    </el-option-group>
  </el-select>
</template>

<script setup>
import { computed } from 'vue'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

const emits = defineEmits(['update:modelValue'])
const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '类型'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const options = computed(() => probotStore.LLMModelSelObj)
</script>

<style lang="scss" scoped>
.group-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  .group-left {
    display: flex;
    align-items: center;
  }
}
</style>
