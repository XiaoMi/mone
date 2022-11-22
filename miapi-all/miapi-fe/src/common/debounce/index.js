/**
 * @description 防抖
 * @param fn 目标函数&方法
 * @param t 时间ms
 * @param immediately 是否立即执行
 */
export default function(fn, t, immediately) {
  let timeout = null;
  return function(val) {
    let that = this;
    if (timeout) {
      clearTimeout(timeout);
    } else if(immediately) {
      fn.call(that, val)
    }
    timeout = setTimeout(() => {
      fn.call(that, val);
    }, t);
  };
}