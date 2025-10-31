<template>
  <div class="login-container">
    <div id="particles-js"></div>
    <el-card class="login-box">
      <template #header>
        <div class="card-header">
          <h2>WELCOME TO LOGIN</h2>
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
        <el-form-item label="Username" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="Please enter username"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="Please enter password"
            prefix-icon="Lock"
            size="large"
            show-password
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
            Login
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
        <el-form-item label="Username" prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="Please enter username"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="Please enter password"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="Please enter email"
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
            Register
          </el-button>
        </el-form-item>
      </el-form>
      <p v-if="isLoginForm" class="register-link"><el-link type="primary" @click="toggleForm">Register</el-link></p>
      <p v-else class="register-link"><el-link type="primary" @click="toggleForm">Login</el-link></p>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
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
        internalAccount: response.data.data.internalAccount || ""
      })
      if (!response.data.data.internalAccount) {
        router.push('/bindInner')
        return
      }
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

// 添加粒子效果初始化
onMounted(() => {
  // @ts-ignore
  particlesJS('particles-js', {
    particles: {
      number: {
        value: 80,
        density: {
          enable: true,
          value_area: 800
        }
      },
      color: {
        value: ['#00f0ff', '#b400ff']
      },
      shape: {
        type: 'circle'
      },
      opacity: {
        value: 0.5,
        random: true,
        anim: {
          enable: true,
          speed: 1,
          opacity_min: 0.1,
          sync: false
        }
      },
      size: {
        value: 3,
        random: true,
        anim: {
          enable: true,
          speed: 2,
          size_min: 0.1,
          sync: false
        }
      },
      line_linked: {
        enable: true,
        distance: 150,
        color: '#00f0ff',
        opacity: 0.2,
        width: 1
      },
      move: {
        enable: true,
        speed: 1,
        direction: 'none',
        random: true,
        straight: false,
        out_mode: 'out',
        bounce: false,
        attract: {
          enable: true,
          rotateX: 600,
          rotateY: 1200
        }
      }
    },
    interactivity: {
      detect_on: 'canvas',
      events: {
        onhover: {
          enable: true,
          mode: 'grab'
        },
        onclick: {
          enable: true,
          mode: 'push'
        },
        resize: true
      },
      modes: {
        grab: {
          distance: 140,
          line_linked: {
            opacity: 0.5
          }
        },
        push: {
          particles_nb: 4
        }
      }
    },
    retina_detect: true
  })
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #0a0a1a;
  padding: 20px;
  box-sizing: border-box;
  font-family: 'Orbitron', sans-serif;
  position: relative;
  overflow: hidden;

  flex-grow: 1;
}

.login-box {
  width: 100%;
  max-width: 500px;
  background: rgba(10, 10, 26, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 240, 255, 0.3);
  border-radius: 15px;
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.2),
              0 0 40px rgba(180, 0, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.login-box:hover {
  box-shadow: 0 0 30px rgba(0, 240, 255, 0.3),
              0 0 50px rgba(180, 0, 255, 0.2);
}

.card-header {
  text-align: center;
  margin-bottom: 20px;
  animation: glow 2s infinite alternate;
}

h2 {
  font-size: 2rem;
  color: #00f0ff;
  text-shadow: 0 0 10px #00f0ff, 0 0 20px #b400ff;
}

:deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.5) !important;
  border: 1px solid rgba(0, 240, 255, 0.3) !important;
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  border-color: #b400ff !important;
  box-shadow: 0 0 10px rgba(180, 0, 255, 0.5) !important;
}

:deep(.el-input__inner) {
  color: #00f0ff !important;
  font-family: 'Orbitron', sans-serif;
}

:deep(.el-form-item__label) {
  color: rgba(0, 240, 255, 0.8) !important;
  font-family: 'Orbitron', sans-serif;
}

.login-button {
  background: linear-gradient(90deg, #00f0ff, #b400ff) !important;
  border: none !important;
  color: #0a0a1a !important;
  font-family: 'Orbitron', sans-serif;
  font-weight: bold;
  border-radius: 8px !important;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  width: 100%;
}

.login-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    to bottom right,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.3) 50%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: rotate(30deg);
  transition: all 0.5s;
  opacity: 0;
  animation: shine 1.5s infinite;
}

.login-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(180, 0, 255, 0.4);
  animation: pulse-border 1.5s infinite;
}

.login-button:active {
  transform: translateY(0);
}

@keyframes shine {
  0% {
    transform: rotate(30deg) translate(-30%, -30%);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: rotate(30deg) translate(30%, 30%);
    opacity: 0;
  }
}

@keyframes pulse-border {
  0% {
    box-shadow: 0 5px 15px rgba(180, 0, 255, 0.4);
  }
  50% {
    box-shadow: 0 5px 25px rgba(0, 240, 255, 0.6);
  }
  100% {
    box-shadow: 0 5px 15px rgba(180, 0, 255, 0.4);
  }
}

.register-link {
  text-align: center;
  margin-top: 20px;
}

.register-link :deep(.el-link) {
  color: #00f0ff !important;
  font-family: 'Orbitron', sans-serif;
}

@keyframes glow {
  0% {
    text-shadow: 0 0 10px #00f0ff, 0 0 20px #b400ff;
  }
  100% {
    text-shadow: 0 0 15px #00f0ff, 0 0 30px #b400ff;
  }
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(0, 240, 255, 0.1) 0%, transparent 70%);
  animation: pulse 4s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.8;
  }
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
}

#particles-js {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
}
.btn-item:deep(.el-button) {
  margin: 0 auto;
}

.light #particles-js {
  display: none;
}

.dark #particles-js {
  display: none;
}

.light .login-container {
  background-color: #f5f7fa;
}
.light .login-box {
  background: rgba(255,255,255,0.8);
  border: 1px solid #e0e0e0;
}
.light h2 {
  color: #409eff;
  text-shadow: 0 0 10px #409eff, 0 0 20px #a0cfff;
}
.light :deep(.el-input__wrapper) {
  background: #fff !important;
  border: 1px solid #dcdfe6 !important;
}
.light .login-button {
  background: linear-gradient(90deg, #409eff, #67c23a) !important;
  color: #fff !important;
}

.dark .login-container {
  background-color: #0a0a1a;
}
.dark .login-box {
  background: rgba(10, 10, 26, 0.7);
  border: 1px solid rgba(0, 240, 255, 0.3);
}
.dark h2 {
  color: #00f0ff;
  text-shadow: 0 0 10px #00f0ff, 0 0 20px #b400ff;
}
.dark :deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.5) !important;
  border: 1px solid rgba(0, 240, 255, 0.3) !important;
}
.dark .login-button {
  background: linear-gradient(90deg, #00f0ff, #b400ff) !important;
  color: #0a0a1a !important;
}
</style>
