<!--
 * @Description: 
 * @Date: 2024-01-24 18:33:54
 * @LastEditTime: 2024-01-24 19:19:32
-->
<template>
  <div>
    <ul>
      <li>
        <h6 class="module-dots">功能介绍</h6>
        <p>
          文档翻译功能允许用户上传包含文本的文档（如 Word、TXT 或 PDF），系统将提取文本并进行翻译。
        </p>
      </li>
      <li>
        <h6 class="module-dots">使用说明</h6>
        <p class="serial-number-title"><span>1. </span>上传文档文件。</p>
        <p class="serial-number-title"><span>2. </span>系统会自动提取文本内容。</p>
        <p class="serial-number-title"><span>3. </span>选择源语言和目标语言。</p>
        <p class="serial-number-title"><span>4. </span>点击“翻译”按钮，等待系统完成翻译。</p>
        <p class="serial-number-title"><span>5. </span>查看翻译结果，并进行必要的编辑或调整。</p>
        <div class="img-container">
          <img src="./translate4-1.png" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Translate4' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped lang="scss"></style>
