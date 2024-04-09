<!--
 * @Description: 
 * @Date: 2024-01-24 18:33:52
 * @LastEditTime: 2024-01-24 19:23:25
-->
<template>
  <div>
    <ul>
      <li>
        <h6 class="module-dots">功能介绍</h6>
        <p>
          网页翻译功能允许用户输入或粘贴网页链接，系统将提取网页内容并进行翻译，以便用户在目标语言中阅读。
        </p>
      </li>
      <li>
        <h6 class="module-dots">使用说明</h6>
        <p class="serial-number-title"><span>1. </span>输入或粘贴要翻译的网页链接。</p>
        <p class="serial-number-title"><span>2. </span> 选择源语言和目标语言。</p>
        <p class="serial-number-title"><span>3. </span>点击“翻译”按钮，等待系统完成翻译。</p>
        <p class="serial-number-title"><span>5. </span>查看翻译结果，可以浏览已翻译的网页。</p>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Translate5' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped lang="scss"></style>
