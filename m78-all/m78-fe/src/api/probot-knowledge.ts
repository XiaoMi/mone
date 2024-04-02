import { post, get } from '@/utils/request'

//知识库列表
export function getKnowledgeList<T = any>(data: {}) {
  return get({
    url: '/v1/knowledge/listKnowledgeBase',
    headers: { 'content-type': 'application/x-www-form-urlencoded' },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 创建
export function create<T = any>(data: { name: string }) {
  return post<T>({
    url: '/v1/knowledge/createKnowledgeBase',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 编辑
export function update<T = any>(data: { name: string }) {
  return post<T>({
    url: '/v1/knowledge/editKnowledgeBase',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 删除
export function deleteApi<T = any>(data: { id: number }) {
  return post<T>({
    url: '/v1/knowledge/deleteKnowledgeBase?id=' + data.id,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 单个知识库详情
export function getSingleKnowledgeBase<T = any>(data: { id: string }) {
  return get({
    url: '/v1/knowledge/getSingleKnowledgeBase',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
// 获取单个知识库下面的知识列表接口
export function getKnowledgeFileMyList<T = any>(data: { knowledgeBaseId?: string }) {
  return get({
    url: '/v1/knowledge/file/myList',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 查询
 */
export function queryKnowledge<T = any>(data: {
  knowledgeId?: number //知识库id数值类型
  queryText?: string //查询文本，字符串
}) {
  return get<T>({
    url: '/v1/knowledge/searchKnowledge',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 文件上传
 */
export function uploadKnowledgeFile<T = any>(data: { knowledgeId?: string }) {
  return get<T>({
    url: '/v1/knowledge/uploadKnowledgeFile',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 知识解析
 */
export function embedding<T = any>(
  data: [
    {
      fileName: string
      knowledgeBaseId: number
    }
  ]
) {
  return post<T>({
    url: '/v1/knowledge/embedding',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

// 删除知识库里的文件
export function deleteKnowledgeFile<T = any>(data: { knowledgeBaseId: string; fileId: string }) {
  return post<T>({
    url: '/v1/knowledge/deleteKnowledgeFile',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 文件详情
 */
export function getBlockList<T = any>(data: { knowledgeId?: number; knowledgeFileId?: number }) {
  return get<T>({
    url: '/v1/knowledge/listKnowledgeFileBlock',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 文件更新
 */
export function updateBlock<T = any>(data: {
  knowledgeId?: number
  knowledgeFileId?: number
  blockContent?: string
  blockId?: number
}) {
  return post<T>({
    url: '/v1/knowledge/addOrUpdateKnowledgeBaseFileBlock',
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 文件删除
 */
export function deleteBlock<T = any>(data: {
  knowledgeId?: string
  knowledgeFileId?: number
  knowledgeFileBlockId?: number
}) {
  return post<T>({
    url: '/v1/knowledge/deleteKnowledgeFileBlock',
    data,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
