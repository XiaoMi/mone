import type { FormInstance } from 'element-plus'

//提交
export const submitForm = async (formEl: FormInstance | undefined, form: any) => {
  if (!formEl) return
  const checkEl = await formEl.validate((valid, fields) => {
    if (valid) {
      return true
    } else {
      console.log('error submit!', fields)
      return false
    }
  })
  if (checkEl) {
    return Promise.resolve(form || undefined)
  }
  return Promise.reject(form || undefined)
}

//重置
export const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
