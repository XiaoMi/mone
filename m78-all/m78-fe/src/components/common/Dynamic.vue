<template>
  <el-form ref="formRef" :model="form" label-position="right" class="dynamic-from">
    <el-form-item
      v-for="(item, index) in form.dynamic"
      :key="index"
      :prop="'dynamic.' + index + '.value'"
      :label="index + 1 + ' .'"
    >
      <div class="from-item-content">
        <el-input v-model="item.value" :placeholder="t('translate.stylePlaceholder')" />
        <div class="from-icon-container">
          <el-button class="from-icon" @click="addDynamic(index)">
            <el-icon><Plus /></el-icon>
          </el-button>
          <el-button class="from-icon" @click="deleteDynamic(index)" v-if="form.dynamic.length > 1">
            <el-icon><Minus /></el-icon>
          </el-button>
        </div>
      </div>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, defineExpose, watch } from 'vue'
import { t } from '@/locales'

const props = defineProps({
  data: {
    type: Array,
    default: []
  }
})

const getArrValue = () => {
  let arr = ref<Array<string>>([])
  form.value.dynamic.forEach((element) => {
    arr.value.push(element.value)
  })
  return arr.value
}
const setArrValue = (data?: []) => {
  form.value.dynamic =
    data?.map((item) => {
      return {
        value: item
      }
    }) || []
}

const form = ref({
  dynamic: [
    {
      value: ''
    }
  ]
})

//添加
const addDynamic = (index: number) => {
  form.value.dynamic.splice(index + 1, 0, {
    value: ''
  })
}
//删除
const deleteDynamic = (index: number) => {
  form.value.dynamic.splice(index, 1)
}
watch(
  () => props.data,
  (val) => {
    setArrValue(val)
  },
  {
    immediate: true,
    deep: true
  }
)

defineExpose({
  reset: () => {
    form.value = {
      dynamic: [
        {
          value: ''
        }
      ]
    }
  },
  getValue: () => {
    return form.value.dynamic
  },
  setValue: (data?: []) => {
    form.value.dynamic = data || []
  },
  setArrValue,
  getArrValue
})
</script>

<style scoped lang="scss">
.dynamic-from {
  padding: 30px 10px 0;
}
.from-item-content {
  display: flex;
  width: 100%;
  .from-icon-container {
    display: flex;
    margin-left: 10px;
  }
  .from-icon {
    border-radius: 50%;
    border: none;
  }
}
</style>
