// 用户相关类型
export interface User {
  id: number
  name: string
  email: string
  avatar?: string
  role: 'admin' | 'user' | 'guest'
  createdAt: string
  updatedAt: string
}

// API 响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  success: boolean
}

// 分页类型
export interface Pagination {
  page: number
  pageSize: number
  total: number
  totalPages: number
}

// 列表响应类型
export interface ListResponse<T> {
  list: T[]
  pagination: Pagination
}

// 路由元信息类型
export interface RouteMeta {
  title?: string
  icon?: string
  requiresAuth?: boolean
  roles?: string[]
}

// 菜单项类型
export interface MenuItem {
  id: string
  title: string
  icon?: string
  path?: string
  children?: MenuItem[]
  meta?: RouteMeta
}

// 表单验证规则类型
export interface FormRule {
  required?: boolean
  message?: string
  trigger?: string | string[]
  validator?: (rule: any, value: any, callback: any) => void
  min?: number
  max?: number
  pattern?: RegExp
}

// 表格列配置类型
export interface TableColumn {
  prop: string
  label: string
  width?: number | string
  minWidth?: number | string
  fixed?: boolean | 'left' | 'right'
  sortable?: boolean
  formatter?: (row: any, column: any, cellValue: any, index: number) => any
  render?: (h: any, params: any) => any
}

// 搜索表单类型
export interface SearchForm {
  keyword?: string
  status?: string | number
  dateRange?: [string, string]
  [key: string]: any
}

// 通用选项类型
export interface Option {
  label: string
  value: string | number
  disabled?: boolean
  children?: Option[]
}

// 文件上传类型
export interface UploadFile {
  name: string
  url: string
  size: number
  type: string
  status: 'ready' | 'uploading' | 'success' | 'error'
  progress?: number
}

// 通知类型
export interface Notification {
  id: string
  title: string
  content: string
  type: 'info' | 'success' | 'warning' | 'error'
  read: boolean
  createdAt: string
}

