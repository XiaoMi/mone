import { Service, IResponse } from '@/common/req'

/**
 * @description 上传图片
 * @param {file} file
 */
export const uploadImg = (data): Promise<IResponse> => Service({
  url: '/OpenApi/uploadImage',
  method: 'post',
  data,
  headers: { 'Content-Type': 'multipart/form-data' }
})
