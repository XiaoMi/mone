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

const cdn = {
  css: [
    // element-ui css
    //  css 文件依赖有字体文件，黄江的接口现在不支持字体文件 后续开发
    // 'https://unpkg.com/element-ui/lib/theme-chalk/index.css'
  ],
  js: [
    // vue must at first!
    'https://xx_replace_xx/architecture/vue.min.js',
    // element-ui js
    'https://xx_replace_xx/architecture/element-ui.min.js',
    // jq  and plugin
    "https://xx_replace_xx/architecture/jQuery.min.js",
    "https://xx_replace_xx/architecture/jsPlumb.min.js"
  ]
}
module.exports = cdn
