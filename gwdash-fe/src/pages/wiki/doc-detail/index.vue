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
      <el-switch style="margin-bottom: 20px" v-model="edit" active-text="编辑开启" />
      <el-form
        class="commont-form"
        :disabled="!edit"
        :rules="rules"
        ref="ruleForm"
        size="mini"
        :model="doc"
      >
        <el-form-item prop="title">
          <el-input v-model="doc.title" placeholder="文档标题,检索字段" />
        </el-form-item>
        <el-form-item prop="content">
          <d2-mde v-if="edit" v-model="doc.content" placeholder="文档主体内容" />
          <div v-else v-html="md2html(doc.content)" />
        </el-form-item>
        <el-form-item v-if="edit" style="text-align:right;">
          <el-button type="primary" @click="updateDoc('ruleForm')">更新文档</el-button>
        </el-form-item>
      </el-form>
      <el-tabs v-if="type == 1">
        <el-tab-pane label="相关issue">
          <div class="box-header">
            <el-button size="mini" type="primary" @click="myIssue">我要提issue</el-button>
          </div>
          <div class="box-content">
            <div class="comment" v-for="(item, index) in issues" :key="index">
              <div class="comment-header">
                <div>{{item.title}}</div>
              </div>
              <div class="comment-body" v-html="md2html(item.content)" />
              <div class="comment-footer">
                <div>
                  <span class="comment-user">{{item.authorName}}</span>
                  <span class="comment-time">{{timeFormat(item.utime)}}</span>
                </div>
                <div>
                  <router-link
                    class="el-button el-button--text"
                    :to="{ path: `/wiki/issue/${projectId}/${item.id}`}"
                  >详情</router-link>
                  <el-button
                    v-if="info.uuid == item.authorId"
                    type="danger"
                    @click="deleteItem(item.id, 'getIssues')"
                    size="mini"
                  >删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="相关评论">
          <div class="box-header">
            <el-button size="mini" type="primary" @click="myComment">我要评论</el-button>
          </div>
          <div class="box-content">
            <div class="comment" v-for="(item, index) in comments" :key="index">
              <div class="comment-body" v-html="md2html(item.content)" />
              <div class="comment-footer">
                <div>
                  <span class="comment-user">{{item.authorName}}</span>
                  <span class="comment-time">{{timeFormat(item.utime)}}</span>
                </div>
                <div>
                  <el-button
                    v-if="info.uuid == item.authorId"
                    type="danger"
                    @click="deleteItem(item.id, 'getComments')"
                    size="mini"
                  >删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <blog-dialog
        title="评论"
        :form="commentForm"
        :blogVisible.sync="commentDialogVisible"
        @submit="commentSubmit"
      />
      <blog-dialog
        title="提issue"
        show-title
        show-member
        show-exp-time
        :form="issuesForm"
        :blogVisible.sync="issueDialogVisible"
        @submit="issueSubmit"
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

export default {
  components: {
    BlogDialog
  },
  data() {
    const projectId = `${this.$route.params.projectId}`;
    const type = `${this.$route.params.type}`;
    return {
      edit: false,
      doc: {
        title: "",
        content: "",
        type,
        projectId
      },
      rules: {
        title: [
          { required: true, message: "文档标题必填", trigger: "blur" },
          { min: 3, message: "文档标题不少于3个字符", trigger: "blur" }
        ],
        content: [
          { required: true, message: "必填字段", trigger: "blur" },
          { min: 6, message: "文档主体内容不少于6个字符", trigger: "blur" }
        ]
      },
      issuesForm: {},
      commentForm: {},
      parentId: "0",
      comments: [],
      issues: [],
      projectId,
      type,
      commentDialogVisible: false,
      issueDialogVisible: false
    };
  },
  computed: {
    ...mapState("d2admin/user", ["info"])
  },
  created() {
    this.getDocsDetail();
    this.getComments();
    this.getIssues();
  },
  methods: {
    getDocsDetail() {
      if(['local','dev'].includes(serverEnv)) return
      request({
        url: "/comment/query",
        method: "post",
        data: {
          projectId: this.projectId,
          type: this.type
        }
      }).then(data => {
        if (!Array.isArray(data)) return;
        this.doc = data[0] || this.doc;
      });
    },
    getComments() {
      if(['local','dev'].includes(serverEnv)) return
      request({
        url: "/comment/query",
        method: "post",
        data: {
          projectId: this.projectId,
          type: 2
        }
      }).then(data => {
        if (!Array.isArray(data)) return;
        this.comments = data;
      });
    },
    getIssues() {
      if(['local','dev'].includes(serverEnv)) return
      request({
        url: "/comment/query",
        method: "post",
        data: {
          projectId: this.projectId,
          type: 3
        }
      }).then(data => {
        if (!Array.isArray(data)) return;
        this.issues = data;
      });
    },
    updateDoc(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          return false;
        }
        const doc = this.doc;
        let url = "/comment/modify";
        if (!doc.id) {
          url = "/comment/create";
        }
        request({
          url,
          method: "post",
          data: { ...doc }
        }).then(res => {
          if (res === true) {
            this.$message.success("更新操作成功");
          }
        });
      });
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
    timeFormat(time) {
      return bizutil.timeFormat(time);
    },
    md2html(mdStr) {
      return marked(mdStr, {
        // highlight: code => highlight.highlightAuto(code).value,
        renderer: new marked.Renderer()
      });
    },
    myComment() {
      // 初始化提问表单
      this.commentForm = {
        title: "",
        content: "",
        projectId: this.projectId,
        parentId: 0,
        type: 2
      };
      this.commentDialogVisible = true;
    },
    myIssue() {
      // 初始化提问表单
      this.issuesForm = {
        title: "",
        commentUserVos: "",
        content: "",
        projectId: this.projectId,
        parentId: 0,
        type: 3,
        status: 1
      };
      this.issueDialogVisible = true;
    },
    commentSubmit() {
      this.getComments();
    },
    issueSubmit() {
      this.getIssues();
    }
  }
};
</script>
<style lang="scss" scoped>
.box-header {
  display: flex;
  justify-content: flex-end;
}
.box-content {
  &-card {
    margin-top: 15px;

    &-header {
      display: flex;
      justify-content: space-between;
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
    justify-items: center;
    line-height: 40px;
  }
}
</style>
