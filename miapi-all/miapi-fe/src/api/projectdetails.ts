import {Service, IResponse} from '@/common/req'

/**
 * @description 获取项目详情
 * @param {number} projectID
 */
 export const getProjectDetail = (data): Promise<IResponse> => Service({
	url: '/Project/getProject',
	method: 'post',
	data
})