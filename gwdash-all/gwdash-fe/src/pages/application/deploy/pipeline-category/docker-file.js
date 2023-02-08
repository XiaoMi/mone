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

const CODE_CHECK_SUBSCRIBE = "code-check-subscribe"
const DOCKER_BUILD_SUBSCRIBE = "docker-build-subscribe"
const DEPLOY_SUBSCRIBE = "deploy-subscribe"

const CODE_CHECK_STAGE = "projectCodeCheckRecord"
const COMPILE_STAGE = "projectCompileRecord"
const DEPLOY_STAGE = "deployInfo"

let dockerFile = () => {
  return [
    {
      id: 1,
      name: "静态扫描",
      desc: "扫描代码",
      key: CODE_CHECK_STAGE,
      subscribe: CODE_CHECK_SUBSCRIBE,
      actions: [
        {
          status: 2,
          name: "重试",
          cmd: "startCodeCheck"
        }
      ],
      stage: {
        active: -1,
        status: -1,
        time: 0,
        processStatus: "wait",
        startAction: "startCodeCheck",
        steps: [
          {
            id: 1,
            name: "开始"
          },
          {
            id: 2,
            name: "检测中"
          },
          {
            id: 3,
            name: "完成"
          }
        ]
      },
      footer: [
        {
          name: "检测日志",
          type: "log",
          data: ""
        }
      ]
    },
    {
      id: 2,
      key: COMPILE_STAGE,
      name: "构建",
      desc: "构建镜像",
      subscribe: DOCKER_BUILD_SUBSCRIBE,
      downloadUrl: "",
      actions: [
        {
          status: 2,
          name: "重试",
          cmd: "startDockerBuild"
        }
      ],
      stage: {
        active: -1,
        status: -1,
        time: 0,
        processStatus: "wait",
        startAction: "startDockerBuild",
        steps: [
          {
            id: 1,
            name: "开始"
          },
          {
            id: 2,
            name: "克隆仓库"
          },
          {
            id: 3,
            name: "构建镜像"
          },
          {
            id: 4,
            name: "上传镜像"
          }
        ]
      },
      footer: [
        {
          name: "构建日志",
          type: "log",
          data: ""
        }
      ]
    },
    {
      id: 3,
      key: DEPLOY_STAGE,
      name: "部署",
      desc: "部署镜像",
      subscribe: DEPLOY_SUBSCRIBE,
      downloadUrl: "",
      actions: [
        {
          name: "部署",
          cmd: "startDeploy"
        }
      ],
      stage: {
        active: -1,
        status: -1,
        time: 0,
        processStatus: "wait",
        startAction: "startDeploy",
        steps: [
          {
            id: 1,
            name: "开始"
          },
          {
            id: 2,
            name: "部署"
          },
          {
            id: 3,
            name: "完成"
          }
        ]
      }
    }
  ]
}

export default dockerFile
