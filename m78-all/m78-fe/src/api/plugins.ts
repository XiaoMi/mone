import { post, get } from '@/utils/request'
// edit
export function pluginsList<T = any>(data) {
  return post<T>({
    url: '/v1/botplugin/list',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

//首页
export function pluginsList4home<T = any>(data) {
  return post<T>({
    url: '/v1/botplugin/org/list',
    data: {
      ...data,
      status: '0'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function createPlugin<T = any>(data) {
  return post<T>({
    url: '/v1/botplugin/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function deletePlugin<T = any>({ id }) {
  return post<T>({
    url: `/v1/botplugin/delete/${id}`,
    data: {},
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function updatePlugin<T = any>(data) {
  return post<T>({
    url: '/v1/botplugin/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

interface IHttp_header {
  key: string
  value: string
}
interface IBotPluginMeta {
  desc: string
  http_header: IHttp_header
  http_method: string
  input: Array<{
    name: string
    desc: string
  }>
  output: Array<{
    name: string
    desc: string
  }>
}

export interface IPluginItem {
  id: number
  pluginOrgName: string
  pluginOrgDesc: string
  avatarUrl: string
  pluginCnt: number
  botRefCnt: number
  createTime: string
  status: number
  modifyTime: string
  releaseTime: string
  creator: string
  plugins: Array<{
    /** 插件id*/
    id: number
    /** 插件所属空间id*/
    workspaceId: number
    /** 插件名称*/
    name: string
    /** 用户名*/
    userName: string
    /** 插件访问url*/
    apiUrl: string
    /** 插件头像的地址*/
    avatarUrl: string
    /** 插件类型,http/dubbo/grpc(目前只有http，可以不传)*/
    type: string
    /** 关联的featureRouterId*/
    featureRouterId: number
    /** 请求元信息, 形如:{"http_headers": {"content-type": "application/json"}, "http_method": "POST"}*/
    meta: string
    /** 请求元信息, 对象格式*/
    botPluginMeta: IBotPluginMeta
    /** 插件描述*/
    desc: string
  }>
}

/**
 * @description 保存或更新插件
 */
export function saveOrUpdate(data: Partial<IPluginItem>) {
  return post<boolean>({
    url: '/v1/botplugin/org/saveOrUpdate',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 查询插件列表(插件下所有组件一并返回)
 */
export function pluginList(data: {
  /** 插件id*/
  id?: number
  /** 插件所属空间id*/
  workspaceId?: number
  /** 插件名称*/
  name?: string
  /** 类型名称*/
  categoryName?: string
  /** 类型id*/
  categoryId?: number
  /** 用户名*/
  userName?: string
  /** 内置类型名*/
  type?: number
  /** 范围, 全部/我的/收藏 (all/mine/favorite)*/
  scale?: string
  /** 排序, 最受欢迎/最近发布 (favorite/latested)*/
  order?: string
  /** 页大小*/
  pageSize?: number
  /** 页码*/
  pageNum?: number
}) {
  return post<{
    INIT_VALUE: number
    records: Array<IPluginItem>
    pageNumber: number
    pageSize: number
    totalPage: number
    totalRow: number
    optimizeCountQuery: boolean
  }>({
    url: '/v1/botplugin/org/list',
    data: {
      ...data
    },
    headers: {
      workspaceId: data.workspaceId
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 根据id删除插件
 */
export function deleteById(id) {
  return post<boolean>({
    url: `/v1/botplugin/org/deleteById?id=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

interface IM78Category {
  id: number
  name: string
  deleted: number
  createTime: number
}
/**
 * @description 根据id查询组件
 */
export function getBot(id: number) {
  return get<{
    id: object
    orgId: object
    name: string
    apiUrl: string
    avatarUrl: string
    featureRouterId: object
    meta: string
    releaseTime: string
    createTime: string
    modifyTime: string
    status: number
    userName: string
    type: number
    category: string
    categoryId: object
    description: string
    m78Category: IM78Category
    pluginUseTimes: number
  }>({
    url: `/v1/botplugin/get?id=${id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 根据id获取插件
 */
export function getById(data: { id: number }) {
  return post<IPluginItem>({
    url: `/v1/botplugin/org/getById?id=${data.id}`,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 创建组件
 */
export function createComp(data: {
  /** 组件id*/
  id?: number
  /** 组件所属空间id*/
  orgId?: number
  /** 组件名称*/
  name?: string
  /** 用户名，即创建人*/
  userName?: string
  /** 更新人*/
  modifier?: string
  /** 组件启用状态*/
  status?: number
  /** 组件调试状态*/
  debugStatus?: number
  /** 组件创建时间*/
  createTime?: string
  /** 组件访问url*/
  apiUrl?: string
  /** 组件头像的地址*/
  avatarUrl?: string
  /** 组件类型,http/dubbo/grpc(目前只有http，可以不传)*/
  type?: string
  /** 关联的featureRouterId*/
  featureRouterId?: number
  /** 请求元信息, 形如:{"http_headers": {"content-type": "application/json"}, "http_method": "POST"}*/
  meta?: string
  /** 请求元信息, 对象格式*/
  botPluginMeta?: IBotPluginMeta
  /** 组件描述*/
  desc?: string
}) {
  return post<number>({
    url: '/v1/botplugin/create',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 发布或取消发布M78BotPluginOrg
 */
export function publishOrCancel(data) {
  return post<boolean>({
    url: `/v1/botplugin/org/publishOrCancel?id=${data.id}&publish=${data.publish}`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 删除组件
 */
export function deleteComp(id: number) {
  return post<boolean>({
    url: `/v1/botplugin/delete?id=${id}`,
    data: {},
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 测试组件运行
 */
export function testHttpDebug(data) {
  return post<any>({
    url: `/v1/botplugin/http/test?pluginId=${data.pluginId}`,
    data: data.input,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function testDubboDebug(data) {
  return post<any>({
    url: `/v1/botplugin/dubbo/test?pluginId=${data.pluginId}`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 更新组件
 */
export function update(data) {
  return post<boolean>({
    url: '/v1/botplugin/update',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 启用或停用组件
 */
export function compToggle(data: { id: number; enable: boolean }) {
  return post<boolean>({
    url: `/v1/botplugin/toggle?id=${data.id}&enable=${data.enable}`,
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 插件头像生成
 */
export function generateImage(data) {
  return get<{
    url: string
    base64: string
  }>({
    url: '/v1/multiModal/avatar',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

/**
 * @description 插件头像上传
 */
export function uploadPluginImg(data: {
  /** 图片 in base64*/
  base64: string
  /** 插件id*/
  pluginId?: number
}) {
  return post<string>({
    url: '/v1/file/image/avatar/upload',
    data,
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL,
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

export function getResTypes() {
  return get<boolean>({
    url: '/v1/botplugin/resTypes',
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function botpluginOrgImport(data: any) {
  return post<string>({
    url: '/v1/botplugin/org/import',
    data,
    headers: {
      'Content-Type': 'application/json'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}

export function markOfficialByAdmin(data: any) {
  return post<string>({
    url: '/v1/botplugin/org/markOfficialByAdmin',
    data,
    headers: {
      'Content-Type': 'application/json'
    },
    baseURL: import.meta.env.VITE_GLOB_API_NEW_URL
  })
}
