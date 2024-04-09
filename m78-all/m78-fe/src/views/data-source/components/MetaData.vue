<template>
  <el-button plain size="small" @click="showDialog" class="meta-btn">{{
    t('excle.metadata')
  }}</el-button>
  <el-dialog v-model="dialogVisible" :title="t('excle.metadata')" width="700">
    <el-input
      v-model="inputV"
      :placeholder="t('excle.metadata')"
      type="textarea"
      :autosize="{ minRows: 3 }"
    >
    </el-input>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="confirm">
          {{ t('excle.confirm') }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { t } from '@/locales'
import { computed, ref, watch } from 'vue'

const emits = defineEmits(['update:modelValue', 'updateMetaValue', 'showDialogEvent'])

const props = defineProps<{
  modelValue: boolean
  metaValue?: string
}>()

const inputV = ref('')

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(dialogVisible) {
    emits('update:modelValue', dialogVisible)
  }
})

watch(
  () => props.metaValue,
  (metaValue, preMetaValue) => {
    if (metaValue && metaValue != preMetaValue) {
      inputV.value = metaValue
    }
  }
)

const confirm = () => {
  emits('updateMetaValue', inputV.value)
}

const showDialog = () => {
  emits('update:modelValue', true)
}
</script>

<style lang="scss" scoped>
.meta-btn {
  margin-right: 10px;
}
</style>
