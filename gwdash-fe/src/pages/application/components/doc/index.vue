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
  <div>
    <div class="header">
      <div class="doc-header">项目文档</div>
    </div>
    <div class="project-doc">
      <div class="project-doc-header">
        <el-form :model="docHeadForm" size="mini">
          <el-form-item label="文档" style="font-weight:regular">
            <el-select v-model="docHeadForm.type" @change="docChange">
              <el-option v-for="item in docHeadOptions" :key="item.id" :label="item.name" :value="item.type"/>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <div class="project-doc-body">
        <el-switch style="margin-bottom: 20px" v-model="edit" active-text="编辑开启"/>
        <el-form :model="docForm" ref="docForm" :rules="docFormRules">
          <el-form-item prop="content">
            <d2-mde v-if="edit" v-model="docForm.content" placeholder="文档主体内容"></d2-mde>
            <div v-else v-html="md2html(docForm.content)" class='document_content'></div>
          </el-form-item>
          <el-form-item v-if="edit" style="text-align:right">
            <el-button size="mini" @click="updateDoc('docForm')">更新文档</el-button>
          </el-form-item>
        </el-form>
        <el-tabs v-if="docHeadForm.type === '1'" value="1">
          <el-tab-pane label="相关issue" name="1">
            <div class="issue-header" style="text-align:right">
              <el-button size="mini" @click="myIssue">我要提issue</el-button>
            </div>
            <div class="issue-content">
              <div class="issue-content-card" v-for="(item, index) in issues" :key="index">
                <div class="card-header">{{item.title}}</div>
                <div class="card-body" v-html="md2html(item.content)"></div>
                <div class="card-footer">
                  <div>
                    <span class="user">{{item.authorName}}</span>
                    <span class="time">{{item.utime}}</span>
                  </div>
                  <div>
                    <router-link class="el-button el-button--text"
                    :to="{ path: `/wiki/issue/${proId}/${item.id}`}">详情</router-link>
                    <el-button v-if="info.uuid === item.authorId" type="danger" size="mini" @click="deleteItem(item.id,'getIssues')">删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="相关评论" name='2'>
            <div class="comment-header" style="text-align:right">
              <el-button size="mini" @click="myComment">我要评论</el-button>
            </div>
            <div class="comment-content">
              <div class="comment-content-card" v-for="(item, index) in comments" :key="index">
                <div class="card-body" v-html="md2html(item.content)"></div>
                <div class="card-footer">
                  <div>
                    <span class="user">{{ item.authorName }}</span>
                    <span class="time">{{ item.utime }}</span>
                  </div>
                  <div>
                    <el-button v-if="info.uuid == item.authorId" type="danger" @click="deleteItem(item.id, 'getComments')" size="mini">删除</el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <common-dialog
      v-if='dialogVisible'
      :dialogTitle='dialogTitle'
      :dialogVisible='dialogVisible'
      @submit='dialogSubmit'
      @close='dialogClose'>
    </common-dialog>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import marked from 'marked'
// import highlight from 'highlight.js'
import { mapState } from 'vuex'
import { pjPre } from './components/constants/type_info'
import commonDialog from './components/dialog'
import bizutil from '@/common/bizutil'
export default {
  props: {
    id: {
      type: Number,
      required: true,
    }
  },

  data () {
    return {
      docsPre: pjPre.project,
      docHeadForm: { type: "1" },
      docHeadOptions: [
        { name: '项目文档', type: "1" },
        { name: 'todo文档', type: "6" },
        { name: 'change log文档', type: "7" },
      ],
      edit: false,
      docForm: {
        content: '',
      },
      docFormRules: {
        content: [
          { required: true, message: "请填写内容", trigger: 'blur' },
          { min: 6, message: "文档主题内容不少于6个字符", trigger: 'blur' }
        ]
      },
      issues: [],
      comments: [],
      proId: "",
      contentHeight: 0,
      flag:'1',
      dialogTitle: '',
      dialogVisible: false
    }
  },
  components: {
    commonDialog
  },
  created () {
    this.getDocsDetail(this.id,"1");
    this.getComments(this.id,2);
    this.getIssues(this.id,3);
  },
  watch: {
    id: function(){
      this.docForm.content = "";
      this.issues = [];
      this.comments = [];
      this.docHeadForm.type = '1';
      this.dialogVisible = false;
      this.getDocsDetail(this.id,"1");
      this.getComments(this.id,2);
      this.getIssues(this.id,3);
    }
  },
  computed: {
    ...mapState('d2admin/user', ['info'])
  },
  methods: {
    getDocsDetail (id,type) {
      if(['local','dev'].includes(serverEnv)) return
      this.proId = `${this.docsPre}`+ id;
      service({
        url: '/comment/query',
        method: 'POST',
        data: {
          projectId: this.proId,
          type
        }
      }).then(res => {
        if (!Array.isArray(res)) return;
        if (type === '1' || type === '6' || type === '7') {
          this.docForm = res[0] || {};
          return
        }
        if (type === 2) {
          this.comments = res.map(item => {
            return {
              ...item,
              ctime: bizutil.timeFormat(item.ctime)
            }
          })
        }
        if (type === 3) {
          this.issues = res.map(item => {
            return {
              ...item,
              ctime: bizutil.timeFormat(item.ctime)
            }
          })
        }
      })
    },
    getComments (id,type) {
      this.getDocsDetail(id,type);
    },
    getIssues (id,type) {
      this.getDocsDetail(id,type)
    },
    docChange (type) {
       this.docForm.content = "";
       this.getDocsDetail(this.id,type)
    },
    md2html (mdStr) {
      return marked(mdStr, {
        // highlight: code => highlight.highlightAuto(code).value,
        renderer: new marked.Renderer()
      })
    },
    updateDoc (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        let url = this.docForm.id ? '/comment/modify' : '/comment/create';
        service({
          url,
          method: 'POST',
          data: {
            ...this.docForm,
            type: this.docHeadForm.type,
            projectId: `${this.docsPre}`+ this.id
          }
        }).then ( res => {
          if (res) {
            this.$message.success('文档更新成功');
            this.getDocsDetail(this.id,this.docHeadForm.type);
            this.edit = false
          }
        })
      })
    },
    myIssue () {
      this.dialogTitle = 'Issue'
      this.dialogVisible = true
    },
    myComment () {
      this.dialogTitle = '评论'
      this.dialogVisible = true;
    },
    dialogSubmit(param) {
      const targetParam = {
        ...param,
        projectId: `${this.docsPre}` + this.id,
        parentId: 0
      }
      if (this.dialogTitle === 'Issue') {
        targetParam.status = 1;
        targetParam.type = 3;
        targetParam.commentUserVos = JSON.stringify(targetParam.commentUserVos)
      } else {
        targetParam.type = 2
      }
      service({
        url: '/comment/create',
        method: 'POST',
        data: {
          ...targetParam
        }
      }).then(res => {
        if (res) {
          this.$message.success('操作成功');
          this.dialogVisible = false
        }
      })
    },
    dialogClose() {
      this.dialogVisible = false;
    },
    deleteItem (id, funName) {
      this.$confirm('此操作将永久删除，是否继续?','提示',{
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: '/comment/del',
          method: 'POST',
          data: { id }
        }).then(res => {
          if (res === true) {
            this.$message.success('删除成功');
            if (funName === 'getIssues') {
              this.getIssues(this.id, 3);
            } else {
              this.getComments(this.id, 2);
            }
          }
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  padding: 0px 0px 10px 0px;
  .doc-header {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
}
.project-doc {
  position: relative;
  padding: 6px 0px 6px 0px;
  /deep/ .project-doc-header {
     margin-bottom: 20px;
     background: #FFF;
     border-bottom: 1px solid rgba(239,240,244,1);
    .el-form-item__label {
      color: rgba(51,51,51,1);
      font-family: PingFang SC;
      font-weight: regular;
      font-size: 13px;
     }
  }
  .project-doc-body {
     background: #FFF;
     margin-bottom: 20px;
  }
}
.issue-content,
.comment-content {
   margin-top: 8px;
   width: 709px;
}
.issue-content::-webkit-scrollbar,
.comment-content::-webkit-scrollbar{
  display: none;
}
.issue-content-card,
.comment-content-card {
   border: 1px solid rgb(233, 230, 230);
   border-radius: 8px;
   padding: 12px 12px 6px 10px;
   margin-top: 12px;
   overflow-x: auto;
   overflow-y: hidden;
}
.card-header {
  font-size: 15px;
  color: #2b2b2b;
}
.card-body {
  font-size: 14px;
  color: #2b2b2b;
}
.card-footer {
  height: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .user {
    font-size: 15px;
    margin-right: 10px;
    color: #409EFF;
  }
  .time {
    color: #999;
    font-size: 12px;
  }
}
  /deep/ .el-tabs__nav-wrap::after {
    height: 1px;
    background-color:rgba(239,240,244,1);
  }
  /deep/ .el-switch {
    width: 100px;
    .el-switch__core {
      position: absolute;
      right: 0;
    }
    .el-switch__label {
      position: absolute;
      left: 0;
      margin-left: 0px;
      span {
        font-size: 13px;
      }
    }
  }
  /deep/ .el-tabs__item {
    font-size: 13px;;
  }
</style>
<style lang='scss'>
.project-doc-header .el-form-item__label {
  font-size: 14px;
  color: #909399;
  font-weight: bold;
  margin-bottom: 0px;
}
.card-body,
.document_content {
  img {
    max-width: 100%;
  }
}
</style>