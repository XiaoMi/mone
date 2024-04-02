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
      <template #suffix v-if="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
      </template>
    </el-input>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="switchShow">{{ t('excle.cancle') }}</el-button>
        <el-button type="primary" @click="confirm">
          {{ t('excle.confirm') }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { t } from '@/locales'
import { ref, watch } from 'vue'
import { updateMeta, getDocDetail } from '@/api/excle.ts'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const props = defineProps({
  uuid: {
    type: String,
    default: ''
  }
})
const inputV = ref('')
const switchShow = () => {
  dialogVisible.value = !dialogVisible.value
}
const confirm = () => {
  const p = {
    newContent: inputV.value || '',
    documentId: props.uuid
  }
  updateMeta(p).then((res) => {
    if (res.code != 0) {
      ElMessage.error(res.message)
      return
    }
    switchShow()
  })
}
const showDialog = () => {
  switchShow()
  loading.value = true
  getDocDetail(props.uuid)
    .then(({ code, data }) => {
      if (code == 0) {
        inputV.value = data.metaContent
      }
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style lang="scss" scoped>
.meta-btn {
  margin-right: 10px;
}
</style>
