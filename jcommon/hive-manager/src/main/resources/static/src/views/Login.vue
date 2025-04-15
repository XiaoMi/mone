<template>
  <div class="login-container">
    <el-card class="login-box">
      <template #header>
        <div class="card-header">
          <h2>欢迎登录</h2>
        </div>
      </template>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin"
        v-if="isLoginForm"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item  class="btn-item">
          <el-button
            type="primary"
            native-type="submit"
            class="login-button"
            size="large"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-position="top"
        @submit.prevent="handleRegister"
        v-else
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="Mail"
            size="large"
          />
        </el-form-item>
        <el-form-item class="btn-item">
          <el-button
            type="primary"
            native-type="submit"
            class="login-button"
            size="large"
            :loading="loading"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <p v-if="isLoginForm" class="register-link"><el-link type="primary" @click="toggleForm">注册账号</el-link></p>
      <p v-else class="register-link"><el-link type="primary" @click="toggleForm">已有账号，登录</el-link></p>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)
const isLoginForm = ref(true)

const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  password: '',
  email: ''
})

const rules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

const registerRules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
})

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true
    const response = await login(loginForm.value)
    if (response.data.code === 200) {
      userStore.setToken(response.data.data.token)
      userStore.setUser({
        id: response.data.data.userId,
        username: response.data.data.username,
      })
      ElMessage.success('登录成功')
      router.push('/agents')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('Login failed:', error)
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true
    const response = await register(registerForm.value)
    if (response.data.code === 200) {
      ElMessage.success('注册成功')
      isLoginForm.value = true
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('Register failed:', error)
  } finally {
    loading.value = false
  }
}

const toggleForm = () => {
  isLoginForm.value = !isLoginForm.value
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, var(--el-color-primary-light-3) 0%, var(--el-color-primary) 100%);
}

.login-box {
  width: 400px;
  border-radius: var(--el-border-radius-base);
  box-shadow: var(--el-box-shadow-light);
  background-color: var(--el-bg-color);
}

.card-header {
  text-align: center;
}

h2 {
  margin: 0;
  color: var(--el-text-color-primary);
  font-size: var(--el-font-size-extra-large);
  font-weight: var(--el-font-weight-primary);
}

.login-button {
  width: 100%;
  margin-top: var(--el-component-size-large);
}

:deep(.el-card__header) {
  padding: var(--el-card-padding);
  border-bottom: 1px solid var(--el-border-color-light);
}

:deep(.el-form-item__label) {
  font-weight: var(--el-font-weight-primary);
  color: var(--el-text-color-regular);
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--el-border-color) inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-border-color-hover) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

.register-link {
  text-align: center;
  margin-top: 8px;
}
.btn-item {
  margin: 0;
}
.btn-item .el-button {
  margin: 12px 0 0 0;
}
</style>
