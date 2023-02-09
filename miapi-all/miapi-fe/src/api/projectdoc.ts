import {Service, IResponse} from '@/common/req'

/**
 * @description 搜索文档
 * @param {string} projectID
 * @param {string} tips
 */
 export const searchDocument = (data): Promise<IResponse> => Service({
	url: '/Document/searchDocument',
	method: 'post',
	data
})


/**
 * @description 获取指定分组文档列表
 * @param {string} projectID
 * @param {string} groupID
 */
 export const getDocumentList = (data): Promise<IResponse> => Service({
	url: '/Document/getDocumentList',
	method: 'get',
	params:data
})

/**
 * @description 获取文档详情
 * @param {string} documentID
 */
 export const getDocumentDetail = (data): Promise<IResponse> => Service({
	url: '/Document/getDocument',
	method: 'get',
	params:data
})

/**
 * @description 获取全部文档
 * @param {string} projectID
 */
 export const getAllDocumentList = (data): Promise<IResponse> => Service({
	url: '/Document/getAllDocumentList',
	method: 'get',
	params:data
})

/**
 * @description 获取分组列表
 * @param {number} projectID
 */
 export const getGroupList = (data): Promise<IResponse> => Service({
	url: '/DocumentGroup/getGroupList',
	method: 'get',
	params: data
})

/**
 * @description 添加文档分组
 * @param {number} groupID
 * @param {string} groupName
 * @param {number} projectID
 * @param {number} parentGroupID
 * @param {boolean} isChild // 是否是子分组，0表示是，1表示不是
 */
 export const addDocGroup = (data): Promise<IResponse> => Service({
	url: '/DocumentGroup/addGroup',
	method: 'post',
	data
})

/**
 * @description 新增文档
 * @param {number} groupID
 * @param {string} contentRaw // 富文本内容
 * @param {string} content // Markdown内容
 * @param {string} title
 * @param {number} projectID
 * @param {number} contentType
 */
 export const addDocument = (data): Promise<IResponse> => Service({
	url: '/Document/addDocument',
	method: 'post',
	data
})

/**
 * @description 删除文档组
 * @param {number} groupID
 * @param {number} projectID
 */
 export const deleteDocGroup = (data): Promise<IResponse> => Service({
	url: '/DocumentGroup/deleteGroup',
	method: 'get',
	params: data
})

/**
 * @description 编辑文档组
 * @param {number} groupID
 * @param {number} projectID
 * @param {string} groupName
 * @param {number} parentGroupID
 * @param {number} isChild ////是否是子分组，0表示是，1表示不是
 */
 export const editDocGroup = (data): Promise<IResponse> => Service({
	url: '/DocumentGroup/editGroup',
	method: 'post',
	data
})

/**
 * @description 删除文档
 * @param {number} projectID
 * @param {string} documentIDs
 */
 export const deleteDocuments = (data): Promise<IResponse> => Service({
	url: '/Document/deleteDocuments',
	method: 'post',
	data
})

/**
 * @description 编辑文档
 * @param {number} projectID
 * @param {number} documentID
 * @param {number} groupID
 * @param {number} contentType // 文档类型
 * @param {string} contentRaw // 富文本内容
 * @param {string} content // Markdown内容
 * @param {string} title // 文档标题
 */
 export const editDocument = (data): Promise<IResponse> => Service({
	url: '/Document/editDocument',
	method: 'post',
	data
})
