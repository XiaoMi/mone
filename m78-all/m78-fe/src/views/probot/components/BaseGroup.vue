<!--
 * @Description: 
 * @Date: 2024-03-08 16:32:58
 * @LastEditTime: 2024-03-08 16:37:24
-->
<template>
  <div class="base-group">
    <div class="base-group-head">
      <h3 class="title">{{ props.title }}</h3>

      <el-tooltip effect="dark" :content="props.tooltip" placement="top" v-if="props.tooltip">
        <el-link :underline="false" type="info" color="#909399" class="tooltip"
          ><el-icon><QuestionFilled /></el-icon
        ></el-link>
      </el-tooltip>

      <el-link
        v-if="props.btn"
        :underline="false"
        type="primary"
        color="#40a3ff"
        :class="'btn ' + props.btn.size"
        :disabled="props.btn.disabled"
        @click="
          () => {
            props.btn.click()
          }
        "
        ><i :class="'iconfont ' + props.btn.icon"></i><span>{{ props.btn.name }}</span></el-link
      >
    </div>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  tooltip: {
    type: String,
    default: ''
  },
  btn: {
    type: Object,
    default: {}
  }
})
</script>

<style scoped lang="scss">
.base-group-head {
  display: flex;
  align-content: center;
  padding: 0px 0 10px;
  font-size: 14px;
  .title {
    font-size: inherit;
  }
  .tooltip {
    margin-left: 6px;
    &:hover {
      color: #666;
    }
  }

  .btn {
    margin-left: 16px;
    display: flex;
    align-items: center;
    .iconfont {
      font-size: 20px;
      line-height: 20px;
      font-weight: 500;
    }
    span {
      padding-left: 2px;
    }
    &.small {
      .iconfont {
        font-size: 14px;
      }
    }
  }
}
</style>
