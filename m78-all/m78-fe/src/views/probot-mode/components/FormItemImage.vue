<!--
 * @Description:
 * @Date: 2024-07-22 16:03:27
 * @LastEditTime: 2024-07-26 18:52:54
-->
<template>
  <FormItem :title="title" :prop="prop">
    <template #topRight>
      <el-button type="primary" link @click="moreImg" v-if="data.length > rowNum">更多</el-button>
    </template>
    <div class="image-container" :style="'height: ' + (315 / rowNum + 32) + 'px; overflow: hidden'">
      <div
        v-for="item in imageData"
        :key="item.key"
        :class="['block', value.key === item.key ? 'active' : '']"
        :style="'width:' + 100 / rowNum + '%'"
        @click="imageClick(item)"
      >
        <el-image :src="item.image" fit="contain">
          <template #error>
            <div class="image-slot">
              <el-icon><icon-picture /></el-icon>
            </div>
          </template>
        </el-image>
        <span class="demonstration">{{ item.label }}</span>
      </div>
    </div>
  </FormItem>

  <el-dialog v-model="dialogVisible" :title="title" width="500">
    <div class="image-container">
      <div
        v-for="(item, index) in originData"
        :key="item.key"
        :class="['block', value.key === item.key ? 'active' : '']"
        :style="'width:25%'"
        @click="imageClick(item, index)"
      >
        <el-image style="width: 75px; height: 75px" :src="item.image" fit="contain">
          <template #error>
            <div class="image-slot">
              <el-icon><icon-picture /></el-icon>
            </div>
          </template>
        </el-image>
        <span class="demonstration">{{ item.label }}</span>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogVisible = false"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import FormItem from './FormItem.vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  data: {
    type: Array<{
      key: string
      label: string
      image: string
    }>,
    default: []
  },
  rowNum: {
    type: Number,
    default: 4
  },
  title: {
    type: String,
    default: '图像风格'
  },
  prop: {
    type: String,
    default: ''
  }
})

const emits = defineEmits(['update:modelValue', 'update:data'])

const value = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const originData = ref([...props.data])

const imageData = computed({
  get() {
    return props.data
  },
  set(v) {
    emits('update:data', v)
  }
})

const dialogVisible = ref(false)

const moreImg = () => {
  dialogVisible.value = true
}

const imageClick = (v: any, index?: number) => {
  emits('update:modelValue', v)
  if (index && index >= props.rowNum) {
    imageData.value[0] = imageData.value.splice(index, 1, imageData.value[0])[0]
  }
}
</script>

<style scoped lang="scss">
.image-container {
  width: 100%;
}
.image-container .block {
  text-align: center;
  display: inline-block;
  box-sizing: border-box;
  vertical-align: top;
  cursor: pointer;
  padding-right: 2%;
  .demonstration {
    display: block;
    color: #666;
    font-size: 14px;
    margin-bottom: 6px;
  }
  &.active {
    .demonstration {
      color: #00a9ff;
    }
  }
}
.image-container .el-image {
  padding: 0 5px;
}
.image-container .image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #eee;
  color: #666;
  font-size: 30px;
}
.image-container .image-slot .el-icon {
  font-size: 30px;
}
</style>
