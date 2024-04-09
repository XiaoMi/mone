import { post, get } from '@/utils/request'

export function getDataSources<T = any>() {
  return get<T>({
    url: '/v1/datasource/list',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function createDataSource<T = any>(data: {
  host: string
  port: string
  database: string
  user: string
  pwd: string
  jdbcUrl: string
}) {
  return post<T>({
    url: '/v1/datasource/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateDataSource<T = any>(data: {
  host: string
  port: string
  database: string
  user: string
  pwd: string
  jdbcUrl: string
  userName: string
}) {
  return post<T>({
    url: '/v1/datasource/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deleteDataSource<T = any>(data: { id: string }) {
  return post<T>({
    url: '/v1/datasource/delete',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getTables<T = any>(data: { connectionId: string }) {
  return get<T>({
    url: '/v1/datasource/tables',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function getLabels<T = any>(data: { connectionId: string }) {
  return get<T>({
    url: '/v1/datasource/labels',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function executeSql<T = any>(data: {
  connectionId: string
  comment: string
  customKnowledge: string
  upperBound: number
  lowerBound: number
}) {
  return post<T>({
    url: '/v1/datasource/executeSql',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateMeta<T = any>(data: { id: string; customKnowledge: string }) {
  return post<T>({
    url: '/v1/datasource/content',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function queryTableData<T = any>(data: { id: string; customKnowledge: string }) {
  return post<T>({
    url: '/v1/datasource/queryTableData',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function queryTableStructure<T = any>(data: { id: string; customKnowledge: string }) {
  return get<T>({
    url: '/v1/datasource/tableStructure',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function alterTableColumns<T = any>(data: { id: string; customKnowledge: string }) {
  return post<T>({
    url: '/v1/datasource/alterTableColumns',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updateTableDatas<T = any>(data: {
  datasourceId: string
  tableName: string
  operationType: 'DELETE' | 'UPDATE' | 'INSERT'
  id?: string
  updateData?: Record<string, any>
  newData?: Record<string, any>
}) {
  return post<T>({
    url: '/v1/datasource/alterTableDatas',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

interface IConnectionInfo {
  id: number
  host: string
  port: string
  database: string
  user: string
  pwd: string
  jdbcUrl: string
  cluster: string
  kerberos: string
  queue: string
  userName: string
  type: number
  customKnowledge: string
  createTime: string
  updateTime: string
}

/**
 * @description 根据表名和用户名获取表详情，包含连接信息
 */
export function getTableDetail(tableName: string) {
  return get<{
    connectionInfo: IConnectionInfo
    tableName: string
  }>({
    url: `/v1/ai_table/getTableDetail?tableName=${tableName}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
