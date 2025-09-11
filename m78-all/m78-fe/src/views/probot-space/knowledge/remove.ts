/*
 * @Description:
 * @Date: 2024-03-20 16:33:45
 * @LastEditTime: 2024-03-23 15:51:57
 */
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/locales'
import { deleteApi } from '@/api/probot-knowledge'

export default (row: any, callback: () => void) => {
  ElMessageBox.confirm(t('common.confirmDel'), 'Warning', {
    type: 'warning',
    confirmButtonText: t('common.yes'),
    cancelButtonText: t('common.no'),
    title: t('common.delete')
  }).then(async () => {
    deleteApi({
      id: row.id
    }).then((res) => {
      if (res.code === 0) {
        ElMessage.success('删除成功')
        callback && callback()
      } else {
        ElMessage.error(res.message)
      }
    })
  })
}
