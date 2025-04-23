import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

process.env.NODE_ENV = 'development'
const isDev = process.env.NODE_ENV === 'development'
console.log(isDev)

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
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
      "/api/manager/ws/": {
        target: "http://0.0.0.0:8080/ws/",
        ws: true, // 启用websocket代理
        changeOrigin: true,
        rewrite: (path) => {
          return path.replace(/^\/api\/manager\/ws\//, "/");
        },
      },
      "/api/manager/": {
        target: "http://0.0.0.0:8080/api/",
        ws: true, // 启用websocket代理
        changeOrigin: true,
        rewrite: (path) => {
          return path.replace(/^\/api\/manager\//, "/");
        },
      },
    },
  },
})
