import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'

/**
 * @description 封装拷贝
 * @param copyValue 拷贝值
 */
export default function(copyValue) {
	const { toClipboard } = useClipboard()
	return toClipboard(copyValue).then(() => {
		ElMessage.success('已复制到粘贴板')
	}).catch(()=>{
		ElMessage.error('copy failed')
	})
}
