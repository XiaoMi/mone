<!--
 * @Description:
 * @Date: 2024-01-24 13:32:09
 * @LastEditTime: 2024-01-24 19:04:53
-->
<template>
  <div>
    <b class="title-tip">（要求idea版本不低于2023.1）</b>
    <ul>
      <li>
        <p class="serial-number-title"><span>1.</span> 配置插件仓库地址</p>
        <p>idea->Settings->Plugins->Manage Plugin Repositories</p>
        <p>添加：<a href="https://xxx" target="_blank">https://xxx</a></p>
        <div class="img-container">
          <img src="./code2-1.png" />
        </div>
      </li>
      <li>
        <p class="serial-number-title"><span>2. </span> 安装插件</p>
        <p>市场中直接搜索Athena，目前最新版本为 2024.01.19.1，安装完成后需要Restart</p>
        <div class="img-container">
          <img src="./code2-2.png" />
        </div>
      </li>
      <li>
        <p class="serial-number-title"><span>3. </span> 配置</p>
        <p>idea->Settings->Other Settings->Athena</p>
        <p class="module-dots">
          ultraman_server:
          <a href="https://xxx" target="_blank">https://xxx</a>
        </p>
        <p class="module-dots">
          ai_proxy:
          <a href="https://xxx" target="_blank">https://xxx</a>
        </p>
        <p class="module-dots">
          z_token: 需要到
          <a href="https://xxx/" target="_blank">z平台</a>->个人信息中生成token
          <span class="red-text">//todo</span>
        </p>
        <p class="module-dots">model：推荐gpt35 <span class="red-text">//todo</span></p>
        <p>配置完成，点击apply->ok之后，需要重启一下idea</p>
      </li>
      <li>
        <p class="serial-number-title"><span>4. </span> 快捷键设置</p>
        <div class="img-container">
          <img src="./code2-3.png" />
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

<style scoped lang="scss">
.title-tip {
  font-style: italic;
}
li {
  padding-top: 10px;
}
</style>
