<template>
  <div class="flex flex-wrap justify-items-center gap-2">
    <el-tag
      v-for="tag in tags"
      :key="tag"
      closable
      :disable-transitions="false"
      @close="handleClose(tag)"
    >
      {{ tag }}
    </el-tag>
    <el-input
      v-if="inputVisible"
      ref="InputRef"
      v-model="inputValue"
      class="w-20"
      size="small"
      @keyup.enter="handleInputConfirm"
      @blur="handleInputConfirm"
    />
    <el-button
      v-else
      :icon="Plus"
      circle
      type="primary"
      size="small"
      @click="showInput"
    ></el-button>
  </div>
</template>

<script lang="ts" setup>
import { nextTick, ref, computed } from 'vue'
import { ElInput } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const emits = defineEmits(['update:modelValue'])

const props = defineProps<{
  modelValue: string[]
}>()

const tags = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})

const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

const handleClose = (tag: string) => {
  tags.value = tags.value.filter((it) => it === tag)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    tags.value = [...tags.value, inputValue.value]
  }
  inputVisible.value = false
  inputValue.value = ''
}
</script>
