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
      <div class="title">成员</div>
    </div>
    <card :tagsList='owners'  category='owner'  @handleClick='handleClick'>owner</card>
    <card :tagsList='members' category='member' @handleClick='handleClick'>member</card>
    <card :tagsList='testers' category='tester' @handleClick='handleClick'>测试审核人员</card>
    <card :tagsList='groups' category='group'  @handleClick='handleClick'>所属组别</card>

    <el-dialog
      :title="`${dialogTitle}更新`"
      :visible.sync='dialogVisible'
      width='800px'>
      <el-form ref='form' :model='form' :rules='rules' label-width='110px' size='mini'>
        <el-form-item :label='`添加${dialogTitle}`' prop='undefined'>
           <el-select v-model="form.content" multiple filterable placeholder="请选择" style="width: 70%" >
              <el-option v-for="item in selectOptions" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size='mini'>取 消</el-button>
        <el-button type="primary" @click="submitForm('form')" size='mini'>确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import card from './components/card'
export default {
  props: {
    id: {
      type: Number,
      required: true,
    }
  },
  data () {
    return {
      dialogTitle: '',
      dialogVisible: false,
      form: {content:[]},
      rules: {
        owner: [{ required: true, message: '请选择owner', trigger: 'blur' }],
        member: [{ required: true, message: '请选择member', trigger: 'blur' }],
        tester: [{ required: true, message: '请选择测试人员', trigger: 'blur' }],
        group: [{ required: true, message: '请选择组别', trigger: 'blur' }]
      },
      owners: [],
      members: [],
      testers: [],
      groups: [],
      roleType: 0,
      memberOptions: [],
      groupOptions: [],
      ownersIds: [],
      membersIds: [],
      testersIds: [],
      groupsIds: [],
    };
  },
  components: {
    card
  },
  watch: {
    id: function(){
      //项目切换初始化
      this.changeProInit();
      this.getInit();
    }
  },
  
  computed: {
    selectOptions() {
      const category = this.dialogTitle;
      let options = [];
      switch(category) {
        case 'owner':
          this.roleType = 0;
          options = this.memberOptions;
          break;
        case 'member':
          this.roleType = 1;
          options = this.memberOptions;
          break;
        case 'tester':
          this.roleType = 2;
          options = this.memberOptions;
          break;
        case 'group':
          this.roleType = 3;
          options = this.groupOptions;
          break;
      }
      // 回填数据
      this.getBackFillData();
      return options;
    }
  },
  methods: {
    changeProInit() {
      this.ownersIds = [];
      this.membersIds = [];
      this.testersIds = [];
      this.groupsIds = [];
      this.owners = [];
      this.members = [];
      this.testers = [];
      this.groups = [];
      this.roleType = 0;
      this.form = {content:[]}
    },
    getInit() {
      if (this.memberOptions.length>0 && this.groupOptions.length>0) {
        this.getInitMember();
        return
      }
      Promise.all([this.getAllMembers(), this.getAllGroups()]).then(() => {
        this.getInitMember();
      })
    },
    getAllMembers() {
      return service({
        url: '/account/all/list',
        method: 'GET'
      }).then( res => {
        if (!Array.isArray(res)) return;
        this.memberOptions = res.map( item => {
          return {
            name: `${item.name}[${item.userName}]`,
            id: item.id
          }
        })
      })
    },
    getAllGroups() {
      return service({
        url: '/apigroup/listall',
        method: 'GET'
      }).then(res => {
        this.groupOptions = res.groupList || [];
      })
    },
    getInitMember() {
      service({
        url: '/project/members',
        method: 'POST',
        data: { id: this.id }
      }).then( res => {
        let {ownersIds,membersIds,testersIds,groupsIds} = this;
        for (const item of res) {
          switch(item.roleType){
            case 0:
              ownersIds.push(item.accountId);
              break;
            case 1:
              membersIds.push(item.accountId);
              break;
            case 2:
              testersIds.push(item.accountId);
              break;
            default:
              groupsIds.push(item.accountId)
          }
        }
        this.memberOptions.forEach(item => {
          if (ownersIds.includes(item.id)) {
            this.owners.push(item.name);
          } 
          if (membersIds.includes(item.id)) {
            this.members.push(item.name);
          } 
          if (testersIds.includes(item.id)) {
            this.testers.push(item.name);
          }
        })
        
        this.groupOptions.forEach(item => {
          if (groupsIds.includes(item.id)) {
            this.groups.push(item.name)
          }
        })
      })
    },
    handleClick(param) {
      this.dialogTitle = param;
      this.dialogVisible = true
    },
    submitForm(formName) {
      this.dialogVisible = false;
      service({
        url: '/project/addMembers',
        method: 'POST',
        data: {
          projectId: this.id,
          roleType: this.roleType,
          members: this.form.content
        }
      }).then( res => {
        this.$message({
          type: 'success',
          message: '操作成功'
        })
        this.changeProInit();
        this.getInitMember()
      })
    },
    getBackFillData() {
      let content = []
      switch(this.roleType) {
        case 0:
          content = this.ownersIds;
          break;
        case 1:
          content = this.membersIds;
          break;
        case 2:
          content = this.testersIds;
          break;
        case 3:
          content = this.groupsIds;
          break
      }
      this.form = {
        content
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  padding: 0px 0px 10px 0px;
  .title {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
}
</style>