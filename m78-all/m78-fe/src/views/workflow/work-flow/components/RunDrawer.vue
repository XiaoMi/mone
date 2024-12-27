<template>
  <el-drawer v-model="drawer" title="试运行" direction="rtl" size="50%">
    <el-form ref="formRef" :model="formV" label-position="top" class="run-test">
      <el-form-item
        v-for="(inputItem, index) in formV.inputs"
        :key="inputItem.name"
        :label="`请输入${inputItem.name}`"
        :prop="'inputs.' + index + '.param'"
        :rules="{
          required: inputItem.required,
          message: `请输入${inputItem.name}`,
          trigger: 'blur'
        }"
      >
        <el-input v-model="inputItem.param" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="drawer-footer">
        <el-button
          type="primary"
          :icon="CaretRight"
          @click="submit"
          :loading="loading"
          class="submit-btn"
          >提交</el-button
        >
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { CaretRight } from '@element-plus/icons-vue'
import { testFlow } from '@/api/workflow'
import { useRoute } from 'vue-router'

const props = defineProps({
  modelValue: {},
  nodes: {}
})
const emits = defineEmits(['update:modelValue', 'runStart'])
const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const formV = ref({})
const formRef = ref(null)
const loading = ref(false)
watch(
  () => props.modelValue,
  (val) => {
    if (!val) return
    loading.value = false
    const startNodes = props.nodes.filter((item) => item.nodeType === 'begin')
    formV.value = startNodes[0]
  }
)
const startNodes = computed(() => {
  return props.nodes.filter((item) => item.nodeType === 'begin')
})
const route = useRoute()
const reqFn = () => {
  loading.value = true
  const obj = {}
  formV.value.inputs.forEach((item) => {
    obj[item.name] = item.param
  })
  testFlow({
    flowId: route.params.id,
    inputs: obj
  })
    .then(({ code, data }) => {
      if (code == 0) {
        emits('runStart', data.flowRecordId)
      }
    })
    .catch(() => {
      loading.value = false
    })
}
const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (valid) {
    reqFn()
  } else {
    console.log('error submit!', fields)
  }
}
</script>

<style lang="scss" scoped>
.run-test {
  :deep(.oz-form-item__label) {
    font-weight: 700;
  }
}
.drawer-footer {
  text-align: center;
  .submit-btn {
    background-color: #4d53e8;
    border: none;
    padding: 5px 20px;
  }
  .submit-btn:hover {
    background-color: #2126a7;
  }
}
</style>
