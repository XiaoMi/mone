import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

process.env.NODE_ENV = 'development'
const isDev = process.env.NODE_ENV === 'development'
console.log(isDev)

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  base: isDev ? '/' : '/static/',
  server: {
    port: 5175,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        ws: true, // 启用websocket代理
        changeOrigin: true,
      },
    },
  },
})
