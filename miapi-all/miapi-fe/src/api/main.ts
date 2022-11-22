import { Service, IResponse } from '@/common/req'

/**
 * @description 获取项目列表
 * @returns projectList
 */
export const getProjectList = (): Promise<IResponse> => Service({
  url: '/Project/getProjectList',
  method: 'get'
})

/**
 * @description 修改项目信息
 * @param {number} projectID
 * @param {string} projectName
 * @param {string} projectVersion
 * @param {string} projectUpdateTime
 */
export const modifyProject = (data): Promise<IResponse> => Service({
  url: '/Project/editProject',
  method: 'post',
  data
})

/**
 * @description 删除项目信息
 * @param {number} projectID
 */
export const deleteProject = (data): Promise<IResponse> => Service({
  url: '/Project/deleteProject',
  method: 'post',
  data
})

/**
 * @description 新增项目信息
 * @param {string} projectName
 * @param {string} projectVersion
 * @param {string} projectUpdateTime
 */
export const addProject = (data): Promise<IResponse> => Service({
  url: '/Project/addProject',
  method: 'post',
  data
})

/**
 * @description 收藏项目
 * @param {number} projectID
 */
export const focusProject = (data): Promise<IResponse> => Service({
  url: '/Project/focusProject',
  method: 'post',
  data
})

/**
 * @description 取消收藏项目
 * @param {number} projectID
 */
export const unFocusProject = (data): Promise<IResponse> => Service({
  url: '/Project/unFocusProject',
  method: 'post',
  data
})

/**
 * @description 获取收藏列表
 */
export const getFocusProjects = (): Promise<IResponse> => Service({
  url: '/Project/getFocusProjects',
  method: 'get'
})

/**
 * @description 获取我的项目接口
 */
export const getMyProjects = (): Promise<IResponse> => Service({
  url: '/Project/getMyProjects',
  method: 'get'
})

/**
 * @description 添加项目组 POST
 * @param {string} groupName
 * @param {string} groupDesc
 */
export const addProjectGroup = (data): Promise<IResponse> => Service({
  url: '/Project/addProjectGroup',
  method: 'post',
  data
})

/**
 * @description 编辑项目组信息 POST
 * @param {string} groupName
 * @param {string} groupDesc
 * @param {number} groupId
 */
export const editProjectGroup = (data): Promise<IResponse> => Service({
  url: '/Project/editProjectGroup',
  method: 'post',
  data
})

/**
 * @description 获取单个项目组信息  POST
 * @param {number} projectGroupID
 */
export const getProjectGroupById = (data): Promise<IResponse> => Service({
  url: '/Project/getProjectGroupById',
  method: 'post',
  data
})

/**
 * @description 获取所有项目组信息
 */
export const getAllProjectGroups = (): Promise<IResponse> => Service({
  url: '/Project/getAllProjectGroups',
  method: 'get'
})

/**
 * @description 删除单个项目组  POST
 * @param {number} projectGroupID
 */
export const deleteProjectGroupById = (data): Promise<IResponse> => Service({
  url: '/Project/deleteProjectGroupById',
  method: 'post',
  data
})

/**
 * @description 根据id获取某个项目组下的所有项目 POST
 * @param {number} projectGroupID
 */
export const getProjectListByProjectGroupId = (data): Promise<IResponse> => Service({
  url: '/Project/getProjectListByProjectGroupId',
  method: 'post',
  data
})

/**
 * @description 获取最近浏览的10条Api信息 GET
 */
export const getRecentlyApiList = (): Promise<IResponse> => Service({
  url: '/Api/getRecentlyApiList',
  method: 'get'
})

/**
 * @description 获取最近浏览的10条项目信息 GET
 */
export const getRecentlyProjectList = (): Promise<IResponse> => Service({
  url: '/Project/getRecentlyProjectList',
  method: 'get'
})

/**
 * @description 首页搜索接口 POST
 * @param {string} keyword
 */
export const indexSearch = (data): Promise<IResponse> => Service({
  url: '/Project/indexSearch',
  method: 'post',
  data
})
