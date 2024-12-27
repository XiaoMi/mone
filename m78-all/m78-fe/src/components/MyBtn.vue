<template>
  <div class="btn" @click="click" :class="disabled ? 'disabled-class' : ''">
    <div class="left-wrap wrap">
      <i class="iconfont btn-icon" :class="iconClass" :style="{ color: bgColor }"></i>
    </div>
    <div class="right-wrap wrap" :style="{ backgroundColor: bgColor }">
      {{ text }}
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  bgColor: {},
  iconClass: {},
  text: {},
  disabled: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['click'])
const click = () => {
  if (props.disabled) return
  emit('click')
}
</script>

<style lang="scss" scoped>
.btn {
  display: flex;
  border-radius: 5px;
  height: 40px;
  line-height: 40px;
  overflow: hidden;
  cursor: pointer;
}
.right-wrap {
  flex: 1;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}
.left-wrap {
  width: 40px;
  height: 40px;
  line-height: 40px;
  padding-left: 15px;
  background: #e7e7e7;
  position: relative;
}
.left-wrap::after {
  content: '';
  width: 0;
  height: 0;
  border-top: 40px solid #e7e7e7;
  border-right: 27px solid transparent;
  position: absolute;
  right: -27px;
  top: 0;
}
.btn-icon {
  font-size: 25px;
}
.disabled-class {
  opacity: 0.5;
}
</style>
