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
    <d2-module margin-bottom :padding-bottom="false">
      <el-form inline size="mini" :model="qForm">
        <el-form-item label="关键字" class='margin-right'>
          <el-input v-model="qForm.title" placeholder="输入关键字查询" @input="titleInput" />
        </el-form-item>
        <el-form-item label="创建者" class='margin-right'>
          <el-select
            v-model="qForm.authorId"
            clearable
            filterable
            default-first-option
            placeholder="指定创建者"
            @change="createChange"
          >
            <el-option
              v-for="item in mOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理者" class='margin-right'>
          <el-select
            v-model="qForm.userIds"
            multiple
            value-key="userId"
            clearable
            filterable
            placeholder="指定处理者"
            default-first-option
            @change="handleChange"
          >
            <el-option
              v-for="item in mOptions"
              :key="item.label"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" class='margin-right'>
          <el-select clearable multiple v-model="qForm.status" @change="stateChange">
            <el-option
              v-for="item in issueOptions"
              :key="item.label"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围" class='margin-right'>
          <el-date-picker
            v-model="qForm.dateRange"
            type="daterange"
            value-format="timestamp"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="dateChange"
          ></el-date-picker>
        </el-form-item>
        <el-form-item style='margin-left: 18px'>
          <el-button size='mini' @click="query">查询</el-button>
        </el-form-item>
      </el-form>
    </d2-module>
    <d2-module>
      <div class="table-list">
        <el-table :data="tableList" style="width: 100%">
          <el-table-column prop="id" label="id" width="100">
            <template slot-scope="scope">
              <div style="display:flex; align-items:center" :title="`优先级：${priorityTexts[scope.row.priority - 1]}`">
                <div class="priority-text">
                  <span v-if="scope.row.priority === 5" class="urgent level">
                    <i class="fa fa-exclamation"></i>
                  </span>
                  <div v-if="scope.row.priority === 4" class="serious level"></div>
                  <div v-if="scope.row.priority === 3" class="high level"></div>
                  <div v-if="scope.row.priority === 2" class="middle level"></div>
                  <div v-if="scope.row.priority === 1" class="low level"></div>
                </div>
                <div>{{ scope.row.id }}</div>
              </div>
            </template>
          </el-table-column>
           <el-table-column
            prop="title"
            label="title"
            width="160"
            header-align="center"
            align="left"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column prop="authorName" label="创建者" width="120"></el-table-column>
          <el-table-column label="指定处理者" width="120">
            <template slot-scope="scope">
              <div :class="{'deal-names': scope.row.commentUserVos.length > 4}">
                <el-tag
                   size="mini"
                   type="info"
                   style="width: 90px"
                   v-for="item in scope.row.commentUserVos" :key="item.id">{{item.userName}}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template slot-scope="scope">
              <el-tag size="mini" :type="scope.row.statusType">{{scope.row.statusText}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="ctimeFormat" label="创建时间" width="160"></el-table-column>
          <el-table-column prop="utimeFormat" label="更新时间" width="160"></el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template slot-scope="scope">
              <router-link
                v-if="scope.row.type == 3"
                class="el-button el-button--text"
                :to="{ path: `/wiki/issue/${scope.row.projectId}/${scope.row.id}`}"
              >详情</router-link>
              <el-button
                v-if="info.uuid == scope.row.authorId"
                type="danger"
                class="el-button--orange"
                @click="deleteItem(scope.row.id, 'query')"
                size="mini"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <d2-pagination
          marginTop
          :currentPage='page'
          :pageSize='pageSize'
          :total='total'
          :pageDisabled='pageDisabled'
          @doCurrentChange='handleCurrentChange'>
        </d2-pagination>
      </div>
    </d2-module>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index"
import bizutil from "@/common/bizutil"
import { mapState } from "vuex"
import { options as issueOptions, statusMap } from "../constants/issue_status"

export default {
  data () {
    const end = new Date()
    const start = new Date()
    end.setTime(end.getTime() + 1000 * 3600 * 24)
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 30 * 12)
    const feedbacksObj = JSON.parse(localStorage.getItem("feedbacksObj")) || {}
    return {
      total: 10,
      page: 1,
      pageSize: 10,
      issueOptions,
      mOptions: [],
      qForm: {
        title: feedbacksObj.title || "",
        authorId: feedbacksObj.authorId || "",
        userIds: feedbacksObj.userIds || [userInfo.id],
        status: feedbacksObj.status || [],
        dateRange: feedbacksObj.dateRange || [start.getTime(), end.getTime()]
      },
      tableList: [],
      priorityTexts: ["低", "中", "高", "严重", "紧急"],
      pageDisabled: false
    }
  },
  created () {
    this.query()
    this.getMembers()
  },
  computed: {
    ...mapState("d2admin/user", ["info"])
  },
  methods: {
    query () {
      this.pageDisabled = true
      const qForm = this.qForm
      const dateRange = qForm.dateRange && qForm.dateRange.length ? qForm.dateRange : [0, 0]
      request({
        url: "/comment/fuzzy/query",
        method: "post",
        data: {
          ...qForm,
          btime: dateRange[0],
          etime: dateRange[1],
          page: this.page,
          pageSize: this.pageSize,
          type: 3
        }
      }).then(data => {
        if (!Array.isArray(data && data.list)) return
        this.total = data.total
        this.tableList = data.list.map(item => {
          return {
            ...item,
            commentUserVos: JSON.parse(item.commentUserVos || "[]"),
            statusType: (statusMap[item.status] || {}).type,
            statusText: (statusMap[item.status] || {}).text,
            ctimeFormat: bizutil.timeFormat(item.ctime),
            utimeFormat: bizutil.timeFormat(item.utime),
            priority: item.priority === 0 ? 1 : item.priority
          }
        })
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },
    deleteItem (id, funName) {
      this.$confirm("此操作将永久删除该文件, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: "/comment/del",
            method: "post",
            data: {
              id
            }
          }).then(res => {
            if (res === true) {
              this.$message.success("删除成功")
              this[funName]()
            }
          })
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          })
        })
    },
    getMembers () {
      request({
        url: "/account/all/list",
        method: "get"
      }).then(accounts => {
        if (!Array.isArray(accounts)) return
        this.mOptions = accounts.map(item => {
          return {
            label: `${item.name}[${item.userName}]`,
            value: item.id
          }
        })
      })
    },
    handleCurrentChange (val) {
      this.page = val
      this.query()
    },
    titleInput (title) {
      this.qForm.title = title
      localStorage.setItem("feedbacksObj", JSON.stringify(this.qForm))
    },
    createChange (authorId) {
      this.qForm.authorId = authorId
      localStorage.setItem("feedbacksObj", JSON.stringify(this.qForm))
    },
    handleChange (userIds) {
      this.qForm.userIds = userIds
      localStorage.setItem("feedbacksObj", JSON.stringify(this.qForm))
    },
    stateChange (status) {
      this.qForm.status = status
      localStorage.setItem("feedbacksObj", JSON.stringify(this.qForm))
    },
    dateChange (dateRange) {
      this.qForm.dateRange = dateRange
      localStorage.setItem("feedbacksObj", JSON.stringify(this.qForm))
    }
  }
}
</script>
<style lang="scss" scoped>
.margin-right {
  margin-right: 20px;
}
.deal-names {
  height: 92px;
  overflow-y:scroll;
}
.deal-names::-webkit-scrollbar {
  display: none;
}
.priority-text {
  display: inline-block;
  margin-right: 8px;
  width: 17px;
  .urgent {
    font-size: 17px;
    color: #ff1919;
    font-style: oblique;
  }
  .serious {
    background-image: url(https://assets.codehub.cn/static-enterprise/media/priority-high.b634c63f.svg);
  }
  .high {
    background-image: url(https://assets.codehub.cn/static-enterprise/media/priority-medium.f5cd3125.svg);
  }
  .middle {
    background-image: url(https://assets.codehub.cn/static-enterprise/media/priority-low.384a36b7.svg);
  }
  .low {
    background-image: url(https://assets.codehub.cn/static-enterprise/media/priority-none.79eebfa3.svg);
  }
  .level {
    width: 17px;
    height: 17px;
    background-size: cover;
  }
}
</style>
<style>
.priority-text .fa {
  font-size: 17px;
  color: #ff1919;
  font-style: oblique;
  position: relative;
  bottom: -1px;
}
</style>
