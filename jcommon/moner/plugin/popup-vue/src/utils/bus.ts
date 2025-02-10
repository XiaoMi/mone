/*
 * @Description:
 * @Date: 2024-01-12 15:36:08
 * @LastEditTime: 2024-01-12 17:12:07
 */

import mitt, { type Emitter } from 'mitt'

// 定义类型别名，因全局使用并且需要自定义事件名称，所以使用索引签名定义内容
type Events = {
  [propName: string]: any
}
// 提供泛型参数让 emitter 能自动推断参数类型
const mittBus: Emitter<Events> = mitt<Events>()
export default mittBus
