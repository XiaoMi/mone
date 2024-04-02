<template>
  <ul class="setp-wrap">
    <li :class="{ success: props.active > 0, active: props.active == 0 }">
      <span>1</span>
      <span>{{ t('plugin.basicInformation') }}</span>
    </li>
    <li :class="{ success: props.active > 1, active: props.active == 1 }">
      <span>2</span>
      <span>{{ t('plugin.parameterConfiguration') }}</span>
    </li>
    <li :class="{ success: props.active > 2, active: props.active == 2 }">
      <span>3</span>
      <span>{{ t('plugin.debugTitle') }}</span>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import { t } from '@/locales'
const props = defineProps({
  active: {
    type: Number,
    default: 0
  }
})
</script>

<style lang="scss" scoped>
.setp-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  &::before {
    position: absolute;
    content: '';
    height: 2px;
    background-color: #c0c4cc;
    top: 11px;
    left: 50px;
    right: 50px;
  }
  li {
    display: flex;
    flex-flow: column;
    justify-content: center;
    align-items: center;
    position: relative;
    z-index: 1;
    &.success {
      span {
        color: #67c23a;
        border-color: #67c23a !important;
      }
    }
    &.active {
      span {
        color: #303133;
        border-color: #303133 !important;
      }
    }
    span {
      text-align: center;
      color: #c0c4cc;
      font-weight: 700;
      font-size: 14px;
      background-color: #fff;
      width: 80px;
      &:first-child {
        display: inline-block;
        width: 24px;
        height: 24px;
        border: 2px solid #c0c4cc;
        line-height: 20px;
        border-radius: 50%;
        font-size: 12px;
        margin-bottom: 4px;
      }
    }
  }
}
</style>
