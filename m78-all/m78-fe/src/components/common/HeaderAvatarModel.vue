<!--
 * @Description:
 * @Date: 2024-01-15 11:36:05
 * @LastEditTime: 2024-08-11 17:41:09
-->
<template>
  <el-form
    ref="formRef"
    :model="form"
    label-position="left"
    class="model-dynamic-from"
    label-width="140px"
  >
    <div class="dynamic-content">
      <el-form-item
        v-for="(item, index) in form.dynamic"
        :key="index"
        :prop="'dynamic.' + index + '.value'"
        :label="item.label"
      >
        <div class="from-item-content">
          <span> :</span>
          <LLMModelSel v-model="item.value" style="width: 100%" />
        </div>
      </el-form-item>
    </div>
    <div class="dynamic-btn">
      <div>
        <el-button text type="primary" @click="fetchModels">{{ t('common.refresh') }}</el-button>
      </div>
      <div>
        <el-button text type="primary" @click="updateModel">{{ t('common.save') }}</el-button>
      </div>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { t } from '@/locales'
import { ElMessage } from 'element-plus'
import { useSettingStore } from '@/stores/settings'
import { updateUserConfig } from '@/api/index'
import LLMModelSel from '@/components/LLMModelSel.vue'

type modelType = 'chatModel' | 'codeModel' | 'documentModel' | 'translateModel'

const settingStore = useSettingStore()
const modelDictionary = {
  chatModel: '聊天使用模型',
  codeModel: '代码使用模型',
  documentModel: '文档使用模型',
  translateModel: '翻译使用模型'
}
const form = ref<{
  dynamic: {
    label: string
    key: string
    value: string
  }[]
}>({
  dynamic: []
})

async function fetchModels() {
  //获取模型数据
  await settingStore.fetchModels()
  await updateForm()
}

async function updateForm() {
  const dynamic = (Object.keys(modelDictionary) as modelType[]).map((item) => {
    return {
      label: modelDictionary[item] || item,
      key: item,
      value: settingStore[item]
    }
  })
  form.value = {
    dynamic
  }
}

function updateModel() {
  var value: Record<string, string> = {}
  form.value.dynamic.forEach((item) => {
    value[item.key] = item.value
  })
  settingStore.updateSetting(value)
  updateUserConfig({
    modelConfig: value
  })
    .then((res) => {
      if (res.code === 0) {
        ElMessage.success(t('common.saveSuccess'))
      }
    })
    .catch(() => {
      ElMessage.success(t('common.saveError'))
    })
}

async function init() {
  await updateForm()
}

init()
</script>

<style scoped lang="scss">
.model-dynamic-from {
  width: 100%;
  display: flex;
  justify-content: space-between;
}
.dynamic-content {
  flex: 1;
}
.dynamic-btn {
  width: 110px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}
.from-item-content {
  width: 100%;
  padding-bottom: 10px;
  display: flex;
  justify-content: space-between;
  span {
    padding-left: 10px;
    padding-right: 10px;
  }
}
</style>
<style lang="scss">
.model-dynamic-from {
  .oz-form-item__label {
    background: #f5f7fa;
    padding-left: 25px;
    border-radius: 5px;
  }
}
</style>
