<!--
 * @Description: 
 * @Date: 2024-01-24 11:31:43
 * @LastEditTime: 2024-01-24 15:02:12
-->
<template>
  <div>
    <p class="title-tip">深度理解代码上下文，为开发者提供强大支持。</p>
    <ul>
      <li>
        <p class="module-dots">生成方法</p>
        <p>
          根据用户注释，智能生成方法实现。其独特之处在于，能自动识别代码生成的依赖范围，如method、class、module，从而保证生成的代码精准、高效
        </p>
        <div class="img-container">
          <img src="./code1-1.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">类分析</p>
        <p>分析当前类包含哪些方法及方法/类功能，帮助用户快速熟悉新项目</p>
        <div class="img-container">
          <img src="./code1-2.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">添加注释</p>
        <p>根据方法体内容自动生成注释，提高代码可读性和可维护性</p>
        <div class="img-container">
          <img src="./code1-3.png" />
        </div>
      </li>
      <li>
        <p class="module-dots">方法重命名</p>
        <p>根据方法体内容自动重命名，真正见名知意</p>
        <div class="img-container">
          <img src="./code1-4.jpeg" />
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
export default {
  name: 'Code1' //给组件命名
}
</script>
<script setup lang="ts"></script>

<style scoped></style>
