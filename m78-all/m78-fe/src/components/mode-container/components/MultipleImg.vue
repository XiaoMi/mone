<template>
  <div class="imgs-box">
    <el-button v-show="singleUrl && showSingle" class="back-btn" @click="retMultiple"
      >返回</el-button
    >
    <div class="div600" v-if="showSingle">
      <ImgItem :imgUrl="singleUrl" />
    </div>
    <div class="multiple-imgs div600" v-else>
      <div
        v-for="(item, index) in props.result"
        :key="item"
        class="item-img-wrap real-img"
        @click="
          () => {
            showSingleFn(index)
          }
        "
      >
        <div class="item-img">
          <img alt="" class="center-img mult" :src="item" />
        </div>
      </div>
      <div v-for="item in 4 - props.result.length" :key="item" class="item-img-wrap">
        <div class="item-img"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import ImgItem from './ImgItem.vue'

const showSingle = ref(false)
const switchShowSingle = () => {
  showSingle.value = !showSingle.value
}

const retMultiple = () => {
  showSingle.value = false
}
defineExpose({
  retMultiple
})
const singleUrl = ref('')
const showSingleFn = (index) => {
  singleUrl.value = props.result[index]
  switchShowSingle()
}

const props = defineProps({
  result: {}
})
</script>

<style scoped lang="scss">
.item-img-wrap {
  width: 300px;
  height: 300px;
  padding: 10px;
  .item-img {
    width: 100%;
    height: 100%;
    border: dashed 1px #dcdfe6;
  }
  .center-img {
    width: 100%;
    height: 100%;
    object-fit: contain;
    cursor: pointer;
  }
}
.div600 {
  width: 600px;
  height: 600px;
}
.multiple-imgs {
  display: flex;

  flex-wrap: wrap;
}
.imgs-box {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  .back-btn {
    position: absolute;
    top: 0px;
    left: 0px;
  }
}
</style>
