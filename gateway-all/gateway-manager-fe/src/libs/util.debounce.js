/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * @description 存储 cookie 值
 * @param {String} name cookie name
 * @param {String} value cookie value
 * @param {Object} setting cookie setting
 */
// 增加前缘触发功能
var debounce = (fn, wait, immediate = false) => {
  let timer = 0
  let startTimeStamp = 0
  let context, args

  let run = (timerInterval) => {
    timer = setTimeout(() => {
      let now = (new Date()).getTime()
      let interval = now - startTimeStamp
      if (interval < timerInterval) { // the timer start time has been reset，so the interval is less than timerInterval
        startTimeStamp = now
        run(wait - interval) // reset timer for left time
      } else {
        if (!immediate) {
          fn.apply(context, args)
        }
        clearTimeout(timer)
        timer = null
      }
    }, timerInterval)
  }

  return function () {
    context = this
    args = arguments
    let now = (new Date()).getTime()
    startTimeStamp = now // set timer start time

    if (!timer) {
      if (immediate) {
        fn.apply(context, args)
      }
      run(wait) // last timer alreay executed, set a new timer
    }
  }
}
export default debounce
