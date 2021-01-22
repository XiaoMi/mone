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
    <d2-module>
      <div class="fb">
        <div class="fb-header">
          <div :title="`优先级：${priorityTexts[issue.priority - 1]}`" class="priority-text">
            <span v-if="issue.priority === 5" class="urgent level">
              <i class="fa fa-exclamation"></i>
            </span>
            <div v-if="issue.priority === 4" class="serious level"></div>
            <div v-if="issue.priority === 3" class="high level"></div>
            <div v-if="issue.priority === 2" class="middle level"></div>
            <div v-if="issue.priority === 1" class="low level"></div>
          </div>
          <div class="h4">{{issue.title}}</div>
          <div>
            <el-tag class="el-tag" :type="issue.statusType" size="mini">{{issue.statusText}}</el-tag>
            <el-tag class="el-tag" @click="editIssue" size="mini" style="cursor:pointer">编辑</el-tag>
          </div>
        </div>
        <div class="fb-info">
          <span class="prop-key">提问者:</span>
          <span>{{issue.authorName}}</span>
          <span class="prop-key">更新者:</span>
          <span>{{issue.updaterName}}</span>
          <span class="prop-key">指定处理者:</span>
          <span v-for="item in issue.commentUserVos" :key="item.id">{{item.userName}}</span>
        </div>
        <div class="fb-info">
          <span class="prop-key">提问时间:</span>
          <span>{{issue.ctimeFormat}}</span>
          <span class="prop-key">更新时间:</span>
          <span>{{issue.utimeFormat}}</span>
        </div>
        <div class="fb-info" style="margin-bottom: 10px">
          <span class="prop-key">截止时间:</span>
          <span>{{issue.expEndTimeFormat}}</span>
        </div>
        <div class='issue_content' v-html="md2html(issue.content)" />
      </div>
      <el-tabs>
        <el-tab-pane label="解决方案">
          <div class="header">
            <el-select 
              v-model="value" 
              placeholder="请选择" 
              size='mini' 
              @change="handleChange">    
              <el-option
                v-for="item in selectedGroup"
                :key="item"
                :label="item"
                :value="item">
               </el-option>
              </el-select>
            <el-button size="mini" type="primary" @click="mySolution({})">我要贡献方案</el-button>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(answer, index) in showAnswers"
              :key="index"
              :timestamp="`${answer.utimeFormat}`"
              placement="top"
            >
              <div class="comment">
                <div class="comment-body" v-html="md2html(answer.content)" />
                <div class="comment-footer">
                  <div>
                    <span class="comment-user">{{answer.authorName}}</span>
                  </div>
                  <div>
                    <el-button
                      v-if="info.uuid == answer.authorId"
                      type="primary"
                      @click="mySolution(answer)"
                      size="mini"
                    >编辑</el-button>
                    <el-button
                      v-if="info.uuid == answer.authorId"
                      type="danger"
                      @click="deleteItem(answer.id, 'getAnswers')"
                      size="mini"
                    >删除</el-button>
                  </div>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
      <blog-dialog
        title="编辑"
        show-title
        show-member
        show-project
        :form="issueForm"
        :blogVisible.sync="issueDialogVisible"
        @submit="issueSubmit"
      />
      <blog-dialog
        :title="`回答：${issue.title}`"
        :form="form"
        :blogVisible.sync="answerDialogVisible"
        @submit="answerSubmit"
      />
    </d2-module>
  </d2-container>
</template>
<script>
import request from "@/plugin/axios/index";
import marked from "marked";
// import highlight from "highlight.js";
import bizutil from "@/common/bizutil";
import { mapState } from "vuex";
import BlogDialog from "../components/comment/dialog";
import { statusMap } from "../constants/issue_status";

export default {
  components: {
    BlogDialog
  },
  data() {
    return {
      edit: false,
      projectId: this.$route.params.projectId,
      id: this.$route.params.issueId,
      issue: {
        title: "",
        content: ""
      },
      issueForm: {},
      form: {},
      answers: [],
      showAnswers: [],
      answerDialogVisible: false,
      issueDialogVisible: false,
      selectedGroup: [ 'all' ],
      value: 'all',
      priorityTexts: ["低", "中", "高", "严重", "紧急"],
    };
  },
  created() {
    this.getIssueDetail();
    this.getAnswers();
  },
  computed: {
    ...mapState("d2admin/user", ["info"])
  },
  methods: {
    getIssueDetail() {
      request({
        url: "/comment/fetch",
        method: "post",
        data: {
          id: this.id
        }
      }).then(res => {
        res.commentUserVos = JSON.parse(res.commentUserVos || "[]");
        res.statusText = (statusMap[res.status] || {}).text;
        res.statusType = (statusMap[res.status] || {}).type;
        res.ctimeFormat = bizutil.timeFormat(res.ctime);
        res.utimeFormat = bizutil.timeFormat(res.utime);
        res.expEndTimeFormat = res.expEndTime
          ? bizutil.timeFormat(res.expEndTime)
          : "";
        res.priority = res.priority === 0 ? 1 : res.priority;
        this.issue = res
        this.selectedGroup = this.selectedGroup.concat( res.commentUserVos.map( it =>  it.userName ))
      })
    },
    getAnswers() {
      if(['local','dev'].includes(serverEnv)) return
      request({
        url: "/comment/query",
        method: "post",
        data: {
          projectId: this.projectId,
          parentId: this.id,
          type: 4
        }
      }).then(data => {
        if (!Array.isArray(data)) return;
        this.answers = data.map(item => {
          return {
            ...item,
            utimeFormat: bizutil.timeFormat(item.utime),
            ctimeFormat: bizutil.timeFormat(item.ctime)
          };
        });
        this.showAnswers = this.answers
      });
    },
    handleChange(){
      this.value == 'all' ? 
      this.showAnswers= this.answers : 
      this.showAnswers=this.answers.filter((it) => {
        return it.authorName == this.value
      })
    },
    editIssue() {
      this.issueForm = {
        projectId: this.projectId,
        parentId: 0,
        type: 3,
        ...this.issue
      };
      this.issueDialogVisible = true;
    },
    deleteItem(id, funName) {
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
              this.$message.success("删除成功");
              this[funName]();
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    mySolution(item) {
      this.form = {
        projectId: this.projectId,
        parentId: this.id,
        type: 4,
        ...item
      };
      this.answerDialogVisible = true;
    },
    md2html(mdStr) {
      return marked(mdStr, {
        // highlight: code => highlight.highlightAuto(code).value,
        renderer: new marked.Renderer()
      });
    },
    issueSubmit() {
      this.getIssueDetail();
    },
    answerSubmit() {
      this.getAnswers();
    }
  }
};
</script>
<style lang="scss" scoped>
.header {
  // text-align: right;
  display: flex;
  justify-content: space-between;;
  margin-bottom: 10px;
}
.h4 {
  font-size: 16px;
  font-weight: bold;
}
.fb {
  color: #2b2b2b;

  &-header {
    display: flex;
    align-items: flex-end;

    .el-tag {
      margin-left: 6px;
    }
  }

  &-info {
    margin-top: 15px;
    font-size: 13px;

    span {
      margin-right: 10px;
    }
  }
}
.comment {
  margin-top: 10px;
  border: 1px dashed #ccc;
  padding: 15px;
  &-header {
    font-size: 15px;
    color: #2b2b2b;
  }
  &-user {
    font-size: 15px;
    margin-right: 10px;
    color: #007ed7;
  }
  &-time {
    color: #999;
    font-size: 12px;
  }
  &-body {
    font-size: 14px;
    color: #2b2b2b;
  }
  &-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    line-height: 40px;
  }
}

.prop-key {
  font-size: 14px;
  color: #909399;
  font-weight: bold;
}

.priority-text {
  display: inline-block;
  margin-right: 8px;
  position: relative;
  bottom: 1px;
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
<style lang='scss'>
.priority-text .fa {
  font-size: 17px;
  color: #ff1919;
  font-style: oblique;
  position: relative;
  bottom: -2px;
}
.issue_content,
.comment-body {
  img {
    max-width: 100%;
  }
}
</style>