<!--
 * @Description: 
 * @Date: 2024-10-10 16:59:25
 * @LastEditTime: 2024-10-21 15:52:41
-->
<template>
  <div class="form-item-container">
    <div class="top">
      <div class="top-left" v-if="title">
        <h3 class="title"><span v-if="require" class="require-txt">*</span>{{ title }}</h3>
        <el-popover placement="top" :width="200" trigger="hover" :content="tip" v-if="tip">
          <template #reference>
            <el-icon color="#666"><Warning /></el-icon>
          </template>
        </el-popover>
      </div>
      <div class="top-right" v-if="slotTopRight">
        <slot name="topRight"></slot>
      </div>
    </div>
    <p class="explain" v-if="explain">{{ explain }}</p>
    <el-form-item :label="label" :prop="prop" style="margin-bottom: 12px">
      <slot></slot>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { useSlots } from 'vue'

const slotTopRight = !!useSlots().topRight

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  tip: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: ''
  },
  prop: {
    type: String,
    default: ''
  },
  explain: {
    type: String,
    default: ''
  },
  require: {}
})
</script>

<style scoped lang="scss">
.form-item-container {
}
.top {
  display: flex;
  align-items: center;
  width: 100%;
}
.top-left {
  padding-bottom: 4px;
  flex: 1;
  display: flex;
  align-items: center;
  .title {
    font-size: 14px;
    color: #383743;
    line-height: 20px;
    padding-right: 6px;
    font-weight: normal;
  }
}
.top-right {
  padding-bottom: 4px;
}
.explain {
  font-size: 12px;
  color: #666;
  line-height: 24px;
}
.require-txt {
  color: #f56c6c;
  margin-right: 4px;
}
</style>
