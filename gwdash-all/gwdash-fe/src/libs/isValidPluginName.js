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
 * 检查上传 plugin 名字
 * name 是后端模板生成，均为 {package_name}_${version}.jar，如 demo-plugin_0.0.2.jar
 * @param {string} name
 *
 */

const isValidPluginName = (name) => {
  const reg = /(.*)\.(jar)$/
  return reg.test(name)
}

export default isValidPluginName
