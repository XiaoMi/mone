<!--
 * @Description: 
 * @Date: 2024-01-18 16:57:29
 * @LastEditTime: 2024-08-30 13:17:29
-->
<template>
  <div class="wrap-background">
    <span class="bg-item bg-outer-item"></span>
    <span class="bg-item bg-outer-item"></span>
    <span class="bg-item bg-outer-item more-item">
      <span class="bg-item bg-inner-item"> <Icon></Icon></span>
      <span class="bg-item bg-inner-item "> <Logo class="logo-txt"></Logo></span>
    </span>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import Icon from './assets/icon.svg'
import Logo from '@/components/common/Logo.vue'

const bindScroll = () => {
  const top = document.querySelector('.page-wrap')?.scrollTop
  const $ele = document.getElementsByClassName('bg-outer-item')
  $ele[0].style.transform = 'rotate(45deg) translate3d(-' + top + 'px, -' + top + 'px, 0px)'

  $ele[1].style.transform = 'rotate(45deg) translate3d(-' + top + 'px, 0px, 0px)'

  $ele[2].style.transform = 'rotate(45deg) translate3d(' + top + 'px, -' + top + 'px, 0px)'
}

onUnmounted(() => {
  document.querySelector('.page-wrap')?.removeEventListener('scroll', bindScroll, false)
})

onMounted(() => {
  document.querySelector('.page-wrap')?.addEventListener('scroll', bindScroll, false)
})
</script>

<style scoped lang="scss">
.logo-txt{
  font-size: 40px!important;
  z-index: 9999;
  position: relative;
  width: 150px;
}
.wrap-background {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: -1;
  overflow: hidden;
  .bg-item {
    position: absolute;
    transform-origin: center center;
    transform: rotate(45deg) translate3d(0px, 0px, 0px);
    box-shadow: (0 0 #0000, 0 0 #0000), (0 0 #0000, 0 0 #0000),
      (0 25px 50px -12px rgba(0, 0, 0, 0.25));
    border-radius: 40px;
  }
  .bg-outer-item {
    color: #fff;
    font-size: 40px;
    &:nth-child(1) {
      width: 473px;
      height: 473px;
      top: -340px;
      left: -80px;
      background-color: rgb(54, 103, 166, 0.1);
      background-image: radial-gradient(
        680px at 5% 20%,
        rgba(54, 103, 166, 0.1),
        rgb(54, 103, 166)
      );
    }
    &:nth-child(2) {
      width: 433px;
      height: 433px;
      top: 224px;
      left: -386px;
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgba(255, 255, 255, 0.1),
        rgb(153, 226, 255)
      );
    }
    &:nth-child(3) {
      width: 1039px;
      height: 859px;
      top: -248px;
      right: -686px;
      background-color: rgb(230, 240, 255);
      background-image: radial-gradient(
        180px at -5% -20%,
        rgba(255, 255, 255, 0.1),
        rgb(168, 202, 255)
      );
    }
  }
  .more-item {
  }
  .bg-inner-item {
    transform: rotate(-90deg);
    &:nth-child(1) {
      width: 500px;
      left: -140px;
      height: 320px;
      bottom: -50px;
      background-color: rgb(54, 103, 166, 0.1);
      background-image: radial-gradient(680px at -5% -20%, #91e5f6, #84d2f6, #59a5d8);
      padding-top: 120px;
      padding-left: 160px;
      svg {
        transform: rotate(-45deg);
        width: 100px;
      }
    }
    &:nth-child(2) {
      width: 350px;
      height: 400px;
      bottom: 60px;
      left: 126px;
      background-color: rgb(223, 245, 249);
      background-image: radial-gradient(380px at -5% -20%, #b7e4c7, #95d5b2, #74c69d);
      padding-top: 160px;
      padding-left: 80px;
     
    }
  }
}
</style>
