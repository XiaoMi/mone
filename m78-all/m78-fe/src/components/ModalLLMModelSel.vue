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
import { computed, ref, onMounted } from 'vue'
import { getModalList } from '@/api/probot-mode.ts'

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
  },
  // 用于给后端接口传的类型
  apiType: {}
})
const val = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const options = ref([])
const getList = () => {
  getModalList({ filter: props.apiType })
    .then((res) => {
      // 如果没有值，默认选中第一个
      const keys = Object.keys(res.data)
      if (!val.value) {
        val.value = res.data[keys[0]][0].cname
      }
      options.value = res.data || []
    })
    .catch((err) => {
      console.log('err', err)
    })
}
onMounted(() => {
  getList()
})
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
