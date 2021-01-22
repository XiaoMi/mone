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
      <el-form class="header" size="mini" inline>
        <el-form-item>
          <el-select v-model="query.queryKey" placeholder="检索字段" clearable>
            <el-option label='机器名' value='name'></el-option>
            <el-option label='hostname' value='hostname'></el-option>
            <el-option label='ip' value='ip'></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model='query.queryValue' placeholder="检索内容" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.labelKey" placeholder="label key"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="query.labelValue" placeholder="label value"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button  @click="getList" size="mini">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            size="mini"
            @click="showDialog({ userDefinedLabels: [] }, '添加机器')"
          >添加机器</el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            size="mini"
            @click="relationApp"
          >关联应用</el-button>
        </el-form-item>
      </el-form>
    </d2-module>
    <d2-module>
      <div class="table-list">
         <el-dropdown type="primary" size="small" plain @command="handleBatch">
                    <el-button size="mini">
                      批量操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="1">批量删除</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
        <el-table 
        :data="list" 
        style="width:100%;margin-top:5px;" 
        height="550"
        @selection-change="handleSelectionChange"
        >
           <el-table-column
            type="selection"
            width="55">
          </el-table-column>
          <el-table-column prop="id" label="id" width="80"></el-table-column>
          <el-table-column prop="name" label="机器名" width="150"></el-table-column>
          <el-table-column prop="hostname" label="hostname" width="150"></el-table-column>
          <el-table-column prop="ip" label="ip" width="150"></el-table-column>
          <el-table-column prop="group" label="机器分组" width="150"></el-table-column>
          <!--
            <el-table-column  label="机器标签" min-width="150">
                <template slot-scope="scope">
                    <div v-for="(item, index) in scope.row.machineLabels" :key="index">{{ item.key }}={{ item.value }}</div>
                </template>
            </el-table-column>
          -->
          <el-table-column prop="desc" label="描述" width="200" />
          <el-table-column prop="utime" label="更新时间" width="200" />
          <el-table-column label="操作" width="250" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="showDialog(scope.row, '编辑机器')">编辑</el-button>
              <el-button type="text" size="mini" @click="deleteMachine(scope.row.id)">删除</el-button>
              <el-button type='text' size='mini' @click="checkApp(scope.row)">查看应用</el-button>
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
    <el-dialog :title="title" :visible.sync="dialogVisible" width="800px">
      <el-form label-width="110px" ref="ruleForm" :model="form" :rules="rules" size="mini">
        <el-form-item prop="name" label="机器名">
          <el-input v-model="form.name" placeholder="请输入机器名" style="width:50%" />
        </el-form-item>
        <el-form-item prop="group" label="机器分组">
          <el-select v-model="form.group" style="width:50%">
            <el-option
              v-for="item in groupOptions"
              :key="item.key"
              :label="item.value"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item prop="ip" label="ip">
          <el-input
            v-model="form.ip"
            placeholder="请输入机器ip"
            style="width:50%"
            :disabled="title==='编辑机器'"/>
        </el-form-item>
        <el-form-item prop="hostname" label="hostname">
          <el-input v-model="form.hostname" placeholder="请输入hostname" style="width:50%" />
        </el-form-item>
        <el-form-item label="系统级标签" v-if="form.systemLabels">
         <codemirror v-model="form.systemLabels" :options="common_cmOptions"></codemirror>
        </el-form-item>
        <el-form-item label="自定义标签">
          <div style="position: relative">
            <div
              style="display:flex;  width:87%; margin-bottom:8px"
              v-for="(item,index) in form.userDefinedLabels"
              :key="index"
            >
              <el-input value="key：" disabled style="width:80px"></el-input>
              <el-form-item prop="machineKey" style="width:178px;margin-bottom:0px">
                <el-input v-model="item.key"></el-input>
              </el-form-item>
              <el-input value="value：" disabled style="width:80px"></el-input>
              <el-form-item prop="machineValue" style="width:178px;margin-bottom:0px">
                <el-input v-model="item.value"></el-input>
              </el-form-item>
              <span
                class="el-icon-remove-outline del-machineLabel"
                title="删除机器标签"
                @click="delMachineLabel(index)"
                v-if="form.userDefinedLabels.length > 0"
              ></span>
              <span v-else style="width:20px;height:20px;margin-left:7px"></span>
            </div>
            <span
              class="el-icon-circle-plus-outline add-machineLabel"
              title="新增机器标签"
              @click="addMachineLabel"
            ></span>
          </div>
        </el-form-item>
        <el-form-item prop="描述" label="desc">
          <el-input v-model="form.desc" :rows="4" type="textarea" placeholder="请填写相关描述" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleMachine" size="mini">确定</el-button>
      </span>
    </el-dialog>

    <!-- 关联应用 -->
    <el-dialog title="关联应用" :visible.sync='relationAppDialog' width='800px'>
      <el-form label-width='110px' ref='appForm' :model='appForm' :rules='appRules' size='mini'>
        <el-form-item prop='appId' label='项目'>
          <el-select 
            v-model='appForm.appId' 
            style='width:50%' 
            filterable
            remote
            :remote-method='remoteSearch'
            @change='proChange'>
            <el-option 
                v-for='item in projectList'
                :key='item.appId'
                :label='item.label'
                :value='item.appId'>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop='envId' label='环境'>
          <el-select v-model='appForm.envId' style='width:50%' filterable>
            <el-option 
                v-for='item in envList'
                :key='item.envId'
                :label='`${item.envId}: ${item.envName}`'
                :value='item.envId'>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop='machineIds' label='机器'>
          <el-select v-model='appForm.machineIds' style='width:50%' filterable multiple>
            <el-option 
                v-for='item in list'
                :key='item.id'
                :label='item.name'
                :value='item.id'
                class='machineOption'>
               <span>{{ item.hostname }}</span>
               <span>{{ item.ip }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="appFormUpload('appForm')">确 定</el-button>
        <el-button size="mini" @click="relationAppDialog = false">取 消</el-button>
      </span>
    </el-dialog>

    <!-- 查看应用 -->
    <el-dialog  title='查看应用' :visible.sync='checkAppDialog' width='800px'>
      <el-table stripe :data='checkAppList' style='width:100%' class='table-list'>
        <el-table-column prop='id' label='id' width='120'></el-table-column>
        <el-table-column prop='proName' label='项目'></el-table-column>
        <el-table-column prop='envName' label='环境'></el-table-column>
        <el-table-column label='操作'  width='150'>
          <template slot-scope='scope'>
            <el-button type="text" size='mini' @click='unbindApp(scope.row)'>解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot='footer' class="dialog-footer">
         <el-button size="mini" @click="checkAppDialog = false">取 消</el-button>
      </div>
    </el-dialog>
  </d2-container>
</template>
<script>
import service from "@/plugin/axios/index";
import bizutil from "@/common/bizutil";
import qs from "qs";
import envMap from "@/pages/application/env_map";

export default {
  data() {
    return {
      searchList: [],
      selectDisabled: true,
      total: 0,
      page: 1,
      pageSize: 20,
      list: [],
      title: "",
      dialogVisible: false,
      groupOptions: envMap.options,
      form: {
        userDefinedLabels:[],
        systemLabels:""
      },
      rules: {
        name: [
          { required: true, message: "请输入机器名", trigger: "blur" },
          {
            min: 1,
            max: 128,
            message: "长度在 1 到 128 个字符",
            trigger: "blur"
          }
        ],
        group: [{ required: true, message: "请选择", trigger: "blur" }],
        ip: [
          { required: true, message: "请输入ip", trigger: "blur" },
          {
            pattern: /\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}/,
            message: "ip格式不对",
            trigger: "blur"
          }
        ],
        hostname: [
          { required: true, message: "请输入hostname值", trigger: "blur" }
        ],
        desc: [
          {
            min: 0,
            max: 512,
            message: "长度在 1 到 512 个字符",
            trigger: "blur"
          }
        ],
        machineKey: [
          { required: false, message: "请输入key值", trigger: "blur" }
        ],
        machineValue: [
          { required: false, message: "请输入value值", trigger: "blur" }
        ]
      },
      query: {
        queryKey: '',
        queryValue: '',
        labelKey: '',
        labelValue: ''
      },
       common_cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        mode: "text/javascript",
        theme: "base16-dark",
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      systemLabelsKey: [],
      pageDisabled: false,
      relationAppDialog: false,
      checkAppDialog: false,
      projectList: [],
      envList: [],
      appForm: {
        appId: '',
        envId: '',
        machineIds: [],
      },
      appRules: {
        appId: [{ required: true, message: "请选择项目", trigger: "blur" }],
        envId: [{ required: true, message: "请选择环境", trigger: "blur" }],
        machineIds: [{ required: true, message: "请选择机器", trigger: "blur" }],
      },
      checkAppList: [],
      searchWord: '',
      multipleSelection:[]
    }
  },
  created() {
    this.getSystemLabelsKey();
    this.getProList();
  },
  methods: {
    getSystemLabelsKey(){
        service({
          url:"/machine/systemLabels"
        })
        .then(res=>{
          this.systemLabelsKey=res
          this.getList()
        })
    },
    // 批量操作
    handleBatch(cmd) {
      console.log("cmd:", cmd);
      switch (cmd) {
        case "1":
          this.handleBatchDel();
          break;
        default:
          this.$message({
            message: "无效的批量操作",
            type: "warning"
          });
          return;
      }
    },
    handleBatchDel(){
      let ids = this.multipleSelection.map(it=>it.id);
      if(ids.length<=0){
         this.$message.warning("请勾选需要删除的数据");
        return
      }
      this.deleteMachine(ids);

    },
    handleSelectionChange(val) {
        this.multipleSelection = val;
    },
    getList() {
      const page = this.page;
      const pageSize = this.pageSize;
      const query = this.query;
      const queryKey = query.queryKey;
      const queryValue = query.queryValue;
      const labelKey = query.labelKey;
      const labelValue = query.labelValue;
      let url = `/machine/list?page=${page}&pageSize=${pageSize}`;
      if (queryKey) url = `${url}&queryKey=${queryKey}` 
      if (queryValue) url = `${url}&queryValue=${queryValue}` 
      if (labelKey) url = `${url}&labelKey=${labelKey}`;
      if (labelValue) url = `${url}&labelValue=${labelValue}`;
      this.pageDisabled = true;
      service({
        url
      }).then(res => {
        if (!Array.isArray(res.list)) return;
        this.total = res.total;
        this.list = res.list.map(it => {
          const labels = it.labels || {};
          it.utime = bizutil.timeFormat(it.utime);
          it.userDefinedLabels=[],
          it.systemLabels={},
          Object.keys(labels).forEach(key=>{
            let kv = {
              key: key.trim(),
              value: labels[key].trim()
            };
            if(this.systemLabelsKey.indexOf(key)!==-1){
              it.systemLabels[key]=labels[key]
            }else{
              it.userDefinedLabels.push(kv) 
            }
          })
          it.systemLabels=JSON.stringify(it.systemLabels,null,2);
          return it;
        });
        this.pageDisabled = false
      });
      setTimeout(() => {
        this.pageDisabled = false;
      }, 2000);
    },
    showDialog(form, title) {
      this.form = { userDefinedLabels: [], ...form };
      this.title = title;
      this.dialogVisible = true;
    },
    handleMachine() {
      const form = { ...this.form };
      let url = "/machine/add";
      if (form.id != null) url = "/machine/edit";
      this.$refs.ruleForm.validate(valid => {
        if (!valid) {
          return false;
        }
        const labels = {};
        form.userDefinedLabels.forEach(it=>{
          labels[(it.key).trim()]=(it.value).trim()
        })
        // 系统覆盖用户自定义相同key
        let systemLabels={}
        if(form.systemLabels){
          systemLabels = JSON.parse(form.systemLabels);
        }
        
        form.labels = Object.assign({},labels,systemLabels);
        // form.machineLabels = null;
        let sdata=JSON.parse(JSON.stringify(form));
        delete sdata.userDefinedLabels
        delete sdata.systemLabels;
        service({
          url,
          method: "post",
          data: { ...sdata }
        }).then(res => {
          this.$message.success("操作成功");
          this.getList();
          this.dialogVisible = false;
        });
      });
    },
    deleteMachine(ids) {
      let sdata=[]
      if(ids.constructor!==Array){
        sdata=[ids]
      }else{
        sdata=Object.assign([],ids)
      }
      this.$confirm(`此操作将永久删除id为${ids.toString()}机器, 是否继续?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          service({
            url: "/machine/del",
            method: "post",
            data:{ids:sdata}
          }).then(res => {
            this.$message.success("删除成功");
            this.getList();
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除"
          });
        });
    },
    handleCurrentChange(val) {
      this.page = val;
      this.getList();
    },
    // 新增机器标签
    addMachineLabel() {
      this.form.userDefinedLabels.push({});
    },
    // 删除机器标签
    delMachineLabel(index) {
      this.form.userDefinedLabels.splice(index, 1);
    },
    relationApp() {
      this.relationAppDialog = true;
    },
    checkApp(data) {
      this.getMachineApp(data.id);
    },
    remoteSearch(query) {
      this.searchWord = query;
      this.getProList();
    },
    getProList() {
      service({
        url: '/project/list',
        method: 'POST',
        data: { 
          search: this.searchWord,
          showAll: true 
        }
      }).then( res => {
        if(!Array.isArray(res.list)) return;
        this.projectList = res.list.map( item => {
          if (item.gitGroup == null && item.gitName == null && item.gitAddress) {
              const rGitAddress = /^https?:\/\/(?:v9\.)?git\.n\.xiaomi\.com\/([0-9a-zA-Z_-]+)\/([0-9a-zA-Z_-]+)(?:\.git)?$/;
              const match = item.gitAddress.match(rGitAddress);
              if (match && match[1] && match[2]) {
                item.gitGroup = match[1];
                item.gitName = match[2];
              }
          }
          return {
            label: `${item.id}: ${item.gitGroup}/${item.gitName}`,
            appId: item.id
          }
        })
      })
    },
    proChange(id) {
      this.getEnvList(id);
    },
    getEnvList(id) {
      service({
         url: `/project/env/list?projectId=${id}`
      }).then( res => {
         if (!Array.isArray(res)) return;
         this.envList = res.map( item => {
           return {
             envName: item.name,
             envId: item.id
           }
         })
      })
    },
    appFormUpload(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$messaeg({
            message: '请检查参数',
            type: 'warning'
          });
          return false;
        }
        service({
          url: '/machine/application',
          method: 'POST',
          data: {
            ...this.appForm,
            bind: true
          }
        }).then( res => {
          this.appForm = {};
          this.$message({
            message: '添加成功',
            type: 'success'
          })
          this.relationAppDialog = false;
        })
      })
    },
    getMachineApp(id) {
      service({
        url: `/machine/list/app?machineId=${id}`
      }).then( res => {
        this.checkAppList = res.map( item => {
          return {
            id: item.id,
            machineId: item.machineId,
            proName: item.project ? `${item.project.gitGroup}/${item.project.gitName}` : "",
            envName: item.projectEnv ? `${item.projectEnv.name}` : ""
          }
        })
        this.checkAppDialog = true;
      })
    },
    unbindApp(data) {
      this.$confirm("确定解绑该应用吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then( () => {
         service({
            url: '/machine/application',
            method: 'POST',
            data: {
              id: data.id,
              bind: false
            }
          }).then( () => {
            this.$message({
              message: '解绑成功',
              type: 'success'
            })
            this.getMachineApp(data.machineId);
          })
      }).catch(() => {
        this.$message({
          type: "info",
          message: "已取消解绑操作"
        })
      })
    },
    searchTypeChange(val) {
      this.query = {
        keyWord: '',
        labelKey: this.query.labelKey || '',
        labelValue: this.query.labelValue || ''
      }
      const list = this.list;
      this.searchList = list.map( item => {
        if (item[val]) {
          return item[val]
        }
      })  
      this.selectDisabled = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  height: 30px;
}
.add-machineLabel {
  font-size: 22px;
  color: rgb(192, 196, 204);
  cursor: pointer;
}
.del-machineLabel {
  font-size: 22px;
  align-self: center;
  color: rgb(192, 196, 204);
  margin-left: 7px;
  cursor: pointer;
}
.machineOption.el-select-dropdown__item {
  height: 50px;
  border-bottom: 0.5px solid #ccc;
  padding-top: 5px;
  span {
    display: block;
    line-height: 21px;
  }
}
</style>