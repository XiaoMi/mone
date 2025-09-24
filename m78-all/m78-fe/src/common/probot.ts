import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteTeam } from '@/api/probot-team'

export const removeTeam = (workspaceId: string | number, callback: () => void) => {
  ElMessageBox.confirm('确定要删除当前空间?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then((action) => {
      if (action === 'confirm') {
        deleteTeam({
          workspaceId
        })
          .then((data) => {
            if (data.data) {
              ElMessage.success('删除成功！')
              callback && callback()
            } else {
              ElMessage.error(data.message || '删除失败')
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    })
    .catch(() => {})
}

export default {}
