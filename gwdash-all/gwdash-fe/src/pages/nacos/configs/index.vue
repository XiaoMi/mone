<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <d2-container>
    <d2-module margin-bottom>
      <el-row>
        <el-col :span="5" class="grid">
          <el-input v-model="dataId" placeholder="dataId 如 tesla" size="mini" />
        </el-col>
        <el-col :span="5" class="grid" style="margin-left: 10px;">
          <el-input v-model="group" placeholder="group 如 DEFAULT_GROUP" size="mini" />
        </el-col>
        <el-col :span="5" class="grid" style="margin-left: 10px;">
          <el-input v-model="namespaceId" placeholder="namespaceId" size="mini" />
        </el-col>
        <el-col :span="1" style='margin-left:5px'>
          <el-popover trigger="hover" placement="left-end">
            <div style="text-align:center">
              <img width="500" src="xx_replace_xx" />
            </div>
            <i style="margin-top: 5px; margin-left: 3px;" slot="reference" class="el-icon-question"></i>
          </el-popover>
        </el-col>
        <el-col :span="1" class="grid" style="margin-left: 240px">
          <el-button size="mini" @click="search">查询</el-button>
        </el-col>
      </el-row>
    </d2-module>

    <d2-module>
      <template v-if="configsStr.length != 0">
        <el-row style="margin-top: 10px;">
          <el-col :span="12" class="grid">
            <el-input type="textarea" autosize placeholder v-model="configsStr"></el-input>
          </el-col>
        </el-row>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-col>
            <el-button @click="update" type="danger" size="mini" class="grid">
              修改
            </el-button>
          </el-col>
        </el-row>
      </template>
    </d2-module>

  </d2-container>
</template>

<script>
import service from "@/plugin/axios/index"

const STATUS_SEARCH = "SEARCH"
const STATUS_EDIT = "EDIT"

export default {
  data () {
    return {
      dataId: "",
      group: "DEFAULT_GROUP",
      namespaceId: "Public",
      configsStr: "",
      status: STATUS_SEARCH
    }
  },
  watch: {
    configsStr: function (newVal) {
      if (newVal.length === 0) {
        this.configsStr = " "
      }

      if (newVal.length >= 2) {
        this.configsStr = this.configsStr
      }
    }
  },
  methods: {
    search () {
      this.getList(this.dataId, this.group, this.namespaceId)
    },
    update () {
      service({
        url: "/nacos/configs/update",
        method: "POST",
        data: {
          dataId: this.dataId,
          group: this.group,
          namespaceId: this.namespaceId,
          content: this.configsStr
        }
      }).then(res => {
        if (res) {
          this.$message({
            message: "修改成功",
            type: "success"
          })
        } else {
          this.$message({
            message: "修改失败",
            type: "error"
          })
        }
      })
    },
    getList (dataId, group, namespaceId) {
      service({
        url: "/nacos/configs/list",
        method: "POST",
        data: {
          dataId,
          group,
          namespaceId
        }
      }).then(res => {
        if (res) {
          this.configsStr = JSON.parse(res).pageItems[0].content
        } else {
          this.configsStr = ""
        }
      })
    }
  }
}
</script>
