<!--
 * @Description: 
 * @Date: 2024-01-24 18:33:49
 * @LastEditTime: 2024-01-24 19:12:54
-->
<template>
  <div>
    <ul>
      <li>
        <h6 class="module-dots">功能介绍</h6>
        <p>文本翻译功能允许用户输入或粘贴文本，然后通过 AI 翻译服务将其翻译成目标语言</p>
      </li>
      <li>
        <h6 class="module-dots">使用说明</h6>
        <p class="serial-number-title">
          <span>1. </span>在指定的输入框中输入要翻译的文本，或者粘贴待翻译文本。
        </p>
        <p class="serial-number-title">
          <span>2. </span>选择源语言和目标语言，或者让系统自动检测源语言。
        </p>
        <p class="serial-number-title"><span>3. </span>点击“翻译”按钮，等待系统完成翻译。</p>
        <p class="serial-number-title"><span>4. </span>查看翻译结果，并在需要时进行调整或修改。</p>
        <div class="img-container">
          <img src="./translate2-1.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Translate2' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped lang="scss"></style>
