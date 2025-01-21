<!--
 * @Description: 
 * @Date: 2024-04-02 10:14:26
 * @LastEditTime: 2024-04-02 15:09:35
-->
<template>
  <el-dialog v-model="dialogFormVisible" title="上架" width="500" @open="open">
    <el-form :model="form" :rules="rules" ref="formRef" class="grounding-container">
      <el-form-item prop="categoryIds" label="分类标签:">
        <el-select
          v-model="form.categoryIds"
          placeholder="请选择分类标签"
          clearable
          multiple
          style="width: 100%"
        >
          <el-option
            v-for="(item, index) in categoryList['2']"
            :key="index + item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="sure"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { ElButton, ElDialog } from 'element-plus'
import { useProbotStore } from '@/stores/probot'
import { submitForm, resetForm } from '@/common/formMethod'
const probotStore = useProbotStore()

const categoryList = computed(() => probotStore.categoryList)

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  }
})
const emits = defineEmits(['update:modelValue', 'onOk'])

const dialogFormVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const formRef = ref()
const form = reactive({
  categoryIds: []
})
const rules = reactive({
  categoryIds: [{ required: true, message: '请至少选择一个标签', trigger: 'change' }]
})

const open = () => {
  resetForm(formRef.value)
  form.categoryIds = probotStore.categoryList['2']
    ?.filter((item) => props.data.pluginCategory?.some((item2) => item2 == item.name))
    ?.map((item) => item.id)
}

const sure = () => {
  submitForm(formRef.value, form).then(() => {
    emits('onOk', {
      id: props.data.id,
      publish: true,
      pluginCategory: form.categoryIds
    })
    dialogFormVisible.value = false
  })
}
</script>

<style scoped lang="scss">
.grounding-container {
  :deep(.oz-select) {
    width: 100%;
  }
  :deep(.oz-tag.oz-tag--info),
  :deep(.oz-tag .oz-tag__close) {
    color: #666;
  }
}
</style>
