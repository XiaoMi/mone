import useClipboard from 'vue-clipboard3'
import { ElMessage } from 'element-plus'

/**
 * @description 封装拷贝
 * @param copyValue 拷贝值
 */
export default function(copyValue) {
	const { toClipboard } = useClipboard()
	return toClipboard(copyValue).then(() => {
		ElMessage.success('copy successfully')
	}).catch(()=>{
		ElMessage.error('copy failed')
	})
}