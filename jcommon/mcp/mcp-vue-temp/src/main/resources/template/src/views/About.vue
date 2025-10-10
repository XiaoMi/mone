<template>
  <div class="about">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><InfoFilled /></el-icon>
          <span>关于项目</span>
        </div>
      </template>
      
      <el-descriptions title="项目信息" :column="2" border>
        <el-descriptions-item label="项目名称">My Vue App</el-descriptions-item>
        <el-descriptions-item label="版本">1.0.0</el-descriptions-item>
        <el-descriptions-item label="框架">Vue 3</el-descriptions-item>
        <el-descriptions-item label="语言">TypeScript</el-descriptions-item>
        <el-descriptions-item label="UI 库">Element Plus</el-descriptions-item>
        <el-descriptions-item label="状态管理">Pinia</el-descriptions-item>
        <el-descriptions-item label="路由">Vue Router</el-descriptions-item>
        <el-descriptions-item label="构建工具">Vite</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <h3>功能特性</h3>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="never">
            <h4>开发体验</h4>
            <ul>
              <li>TypeScript 类型安全</li>
              <li>热重载开发</li>
              <li>自动导入组件</li>
              <li>代码提示和补全</li>
            </ul>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="never">
            <h4>技术栈</h4>
            <ul>
              <li>Vue 3 组合式 API</li>
              <li>Element Plus 组件库</li>
              <li>Pinia 状态管理</li>
              <li>Vue Router 路由</li>
            </ul>
          </el-card>
        </el-col>
      </el-row>

      <el-divider />

      <h3>快速开始</h3>
      <el-steps :active="3" finish-status="success">
        <el-step title="安装依赖" description="npm install" />
        <el-step title="启动开发" description="npm run dev" />
        <el-step title="构建项目" description="npm run build" />
      </el-steps>

      <div class="code-block">
        <el-tag type="info" size="large">npm install</el-tag>
        <el-tag type="success" size="large">npm run dev</el-tag>
        <el-tag type="warning" size="large">npm run build</el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 关于页面逻辑
</script>

<style scoped>
.about {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #409eff;
}

h3 {
  color: #303133;
  margin: 20px 0 15px 0;
}

h4 {
  color: #606266;
  margin: 0 0 10px 0;
}

ul {
  margin: 0;
  padding-left: 20px;
}

ul li {
  margin: 5px 0;
  color: #606266;
}

.code-block {
  margin-top: 20px;
  text-align: center;
}

.code-block .el-tag {
  margin: 0 10px;
}
</style>

