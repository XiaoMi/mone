<template>
  <el-dialog
    v-model="dialogVisible"
    :title="t(`excle.${props.itemId ? 'editExportHttp' : 'createExportHttp'}`)"
    width="700"
  >
    <el-form ref="ruleFormRef" :model="ruleForm" label-position="left">
      <el-form-item
        :label="t('excle.exportHttpName')"
        prop="name"
        :rules="{
          required: true,
          message: t('excle.enterText'),
          trigger: 'blur'
        }"
      >
        <el-input v-model="ruleForm.name" style="width: 100%" />
      </el-form-item>
      <div class="conditons-div">
        <p class="conditons-title">{{ t('excle.conditions') }}</p>
        <div class="list">
          <div
            v-for="(condition, index) in ruleForm.routerMeta"
            :key="index"
            class="condition-item"
          >
            <el-form-item
              label="Key"
              :prop="'routerMeta.' + index + '.key'"
              :rules="{
                required: true,
                message: '请输入Key',
                trigger: 'blur'
              }"
            >
              <el-input v-model="condition.key" />
            </el-form-item>
            <el-form-item
              label="Value"
              :prop="'routerMeta.' + index + '.value'"
              :rules="{
                required: true,
                message: '请输入Value',
                trigger: 'blur'
              }"
            >
              <el-input v-model="condition.value" />
            </el-form-item>
            <div class="btns">
              <el-icon class="btn-icon" @click="removeCondition(index)"><CircleClose /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ t('excle.cancle') }}</el-button>
        <el-button type="primary" @click="addCondition">{{ t('excle.addConditions') }} </el-button>
        <el-button type="primary" @click="confirmEdit">{{ t('excle.confirm') }}</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { t } from '@/locales'
import { getHttpApi, editHttpApi } from '@/api/excle'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  itemId: {}
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
const ruleForm = ref({
  name: '',
  routerMeta: [
    {
      key: '',
      value: ''
    }
  ]
})
const ruleFormRef = ref()
const exporHttpSubmit = () => {
  const { name, routerMeta, labelId } = ruleForm.value
  const obj = {}
  routerMeta.forEach((item) => {
    obj[item.key] = item.value
  })
  const params = {
    name,
    reqData: obj,
    id: props.itemId,
    labelId
  }
  editHttpApi(params).then(({ code, message }) => {
    if (code != 0) {
      ElMessage.error(message)
      return
    }
    emits('update')
    dialogVisible.value = false
    ElMessage.success(t('common.editSuccess'))
  })
}
const confirmEdit = () => {
  ruleFormRef.value.validate((valid) => {
    if (valid) {
      exporHttpSubmit()
    } else {
      return false
    }
  })
}
const removeCondition = (index) => {
  ruleForm.value.routerMeta.splice(index, 1)
}
const addCondition = () => {
  ruleForm.value.routerMeta.push({
    key: '',
    value: ''
  })
}
watch(dialogVisible, (val) => {
  if (val) {
    if (props.itemId) {
      const params = { id: props.itemId }
      getHttpApi(params).then((res) => {
        if (res.code != 0) {
          ElMessage.error(res.message)
          return
        }
        const { name, routerMeta } = res.data
        let routerMetaArr = []
        for (let key in routerMeta) {
          routerMetaArr.push({
            key: key,
            value: routerMeta[key]
          })
        }
        ruleForm.value = { ...ruleForm.value, ...res.data, name, routerMeta: routerMetaArr || [] }
      })
    }
  }
})
</script>

<style scoped lang="scss">
.tag-item-box {
  display: inline-block;
}
.tag-item {
  cursor: pointer;
  margin: 0 7px 7px 0;
  color: #606060;
  border: solid 1px #80d4ff;
  background: #fff;
  white-space: pre-wrap;
  height: auto;
  line-height: 14px;
  padding: 3px 0 3px 3px;
  border-radius: 2px;
  display: flex;
  justify-content: space-between;
}
.more-icon {
  color: #909399;
  border-radius: 9px;
  outline: none;
  font-size: 12px;
  margin: 1px 3px 0 3px;
  color: #00a9ff;
}
.chat-his-list-box {
  margin-top: 10px;
  border: solid 1px #606060;
  border-radius: 5px;
}

.empty-box {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding: 20px 0;
}
.conditons-title {
  padding: 0 10px 10px 5px;
}
.condition-item {
  display: flex;
  justify-content: space-between;
  :deep(.oz-form-item) {
    flex: 1;
  }
}
.conditons-div {
  display: flex;
  align-items: baseline;
  .list {
    flex: 1;
  }
}
.btn-icon {
  font-size: 20px;
}
.list-box {
  padding: 0 10px;
  max-height: 150px;
  overflow: auto;
}
.title {
  height: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  .title-text,
  .expand-icon {
    font-size: 14px;
  }
}

.expand-btn {
  cursor: pointer;
  padding: 5px;
}
.btns {
  padding: 7px 0 0 7px;
}
</style>
