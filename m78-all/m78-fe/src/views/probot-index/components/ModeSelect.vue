<!--
 * @Description: 
 * @Date: 2024-08-16 15:47:03
 * @LastEditTime: 2024-08-30 10:32:16
-->
<template>
  <div class="mode-select-container">
    <img :src="imgUrl" style="width: 20px; height: 20px" class="select-img" />
    <el-select
      v-model="val"
      :placeholder="props.placeholder"
      :disabled="props.disabled"
      class="mode-select"
    >
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
                <el-icon> <Warning /></el-icon>
              </template>
            </el-popover>
          </div>
        </el-option>
      </el-option-group>
    </el-select>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { chatMode } from '@/api/probot-index'

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
const imgUrl = ref()
const options = ref([])
onMounted(() => {
  chatMode({}).then((res) => {
    options.value = res.data
    if (res.data && Object.keys(res.data).length) {
      const first = res.data[Object.keys(res.data)[0]][0]
      val.value = first.cname
      imgUrl.value = first.imageUrl
    }
  })
})

watch(
  () => [val.value, options.value],
  ([newVal, newOptions]) => {
    const data = Object.keys(newOptions)
    if (newVal && data.length > 0) {
      data.forEach((item, key) => {
        newOptions[item].forEach((v, i) => {
          if (newVal == v.cname) {
            imgUrl.value = v.imageUrl
          }
        })
      })
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped>
.mode-select-container {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
  padding-left: 10px;
  .select-img {
    position: absolute;
    left: 16px;
    z-index: 1;
    cursor: pointer;
  }
}
.mode-select {
  width: 300px;
  :deep(.oz-input__wrapper) {
    padding-left: 36px;
  }
  :deep(.oz-input__suffix) {
    display: none;
  }
}
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
