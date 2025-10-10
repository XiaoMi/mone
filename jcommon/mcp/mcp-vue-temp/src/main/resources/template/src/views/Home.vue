<template>
  <div class="home">
    <el-card class="welcome-card">
      <template #header>
        <div class="card-header">
          <span>欢迎使用 ${PROJECT_NAME}</span>
        </div>
      </template>
      <div class="welcome-content">
        <el-icon class="welcome-icon" size="64">
          <House />
        </el-icon>
        <h2>这是一个基于 Vue 3 + TypeScript 的${PROJECT_NAME}项目</h2>
        <p>包含了以下技术栈：</p>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="tech-item">
                <el-icon size="32" color="#4fc08d">
                  <Platform />
                </el-icon>
                <h3>Vue 3</h3>
                <p>组合式 API</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="tech-item">
                <el-icon size="32" color="#3178c6">
                  <Document />
                </el-icon>
                <h3>TypeScript</h3>
                <p>类型安全</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="tech-item">
                <el-icon size="32" color="#e6a23c">
                  <ShoppingBag />
                </el-icon>
                <h3>Element Plus</h3>
                <p>UI 组件库</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="tech-item">
                <el-icon size="32" color="#f56c6c">
                  <DataAnalysis />
                </el-icon>
                <h3>Pinia</h3>
                <p>状态管理</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <div class="action-buttons">
          <el-button type="primary" @click="goToCounter">
            <el-icon><Plus /></el-icon>
            查看计数器示例
          </el-button>
          <el-button @click="goToAbout">
            <el-icon><InfoFilled /></el-icon>
            了解更多
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToCounter = () => {
  router.push('/counter')
}

const goToAbout = () => {
  router.push('/about')
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.welcome-content {
  text-align: center;
}

.welcome-icon {
  color: #409eff;
  margin-bottom: 20px;
}

.tech-item {
  text-align: center;
  padding: 20px;
}

.tech-item h3 {
  margin: 10px 0 5px 0;
  color: #303133;
}

.tech-item p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.action-buttons {
  margin-top: 30px;
}

.action-buttons .el-button {
  margin: 0 10px;
}
</style>
