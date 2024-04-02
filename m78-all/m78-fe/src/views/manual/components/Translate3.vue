<!--
 * @Description: 
 * @Date: 2024-01-24 18:33:56
 * @LastEditTime: 2024-01-24 19:18:00
-->
<template>
  <div>
    <ul>
      <li>
        <h6 class="module-dots">功能介绍</h6>
        <p>
          图片翻译功能允许用户上传包含文本的图片，然后系统通过 OCR
          技术提取文本，并将其翻译成目标语言。
        </p>
      </li>
      <li>
        <h6 class="module-dots">使用说明</h6>
        <p class="serial-number-title"><span>1. </span>选择要上传的包含文本的图片文件。</p>
        <p class="serial-number-title"><span>2. </span>系统会自动进行 OCR 文本提取。</p>
        <p class="serial-number-title"><span>3. </span>选择源语言和目标语言。</p>
        <p class="serial-number-title"><span>4. </span>点击“翻译”按钮，等待系统完成翻译。</p>
        <p class="serial-number-title"><span>5. </span>查看翻译结果，并进行必要的编辑或调整。</p>
        <div class="img-container">
          <img src="./translate3-1.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Translate3' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped lang="scss"></style>
