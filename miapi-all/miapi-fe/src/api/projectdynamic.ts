import {Service, IResponse} from '@/common/req'

/**
 * @description 获取项目日志列表
 * @param {number} projectID
 * @param {number} page
 * @param {number} pageSize
 */
 export const getProjectLogList = (data): Promise<IResponse> => Service({
	url: '/Project/getProjectLogList',
	method: 'post',
	data
})