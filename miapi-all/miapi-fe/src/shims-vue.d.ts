
declare module '*.gif' {
  export const gif: any
}

declare module '*.svg' {
  export const svg: any
}
declare module '*.png' {
  export const png: any
}
declare module '*.scss' {
  export const scss: any
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
