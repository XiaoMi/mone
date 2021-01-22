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
   <d2-module margin-bottom>资源列表</d2-module>
   <d2-module>
      <el-form ref="searchForm" class="search-form" :inline="true" size="mini" :model="searchForm" label-width="80px">
        <el-form-item label="Owner">
          <el-input size="mini" @change="handleSearch" style="width:140px;" v-model.trim="searchForm.owner"></el-input>
        </el-form-item>
        <el-form-item label="IP"  v-model.trim="searchForm.ip">
          <el-input size="mini" @change="handleSearch" style="width:140px;fontSize:12px" v-model.trim="searchForm.ip"></el-input>
        </el-form-item>
      </el-form>
      <el-table stripe class='table-list' :data="list">
        <el-table-column fixed  prop="id" label="id" width="100"></el-table-column>
        <el-table-column prop="ip"  width="150" label="ip地址"></el-table-column>
        <el-table-column prop="cpu" label="cpu"></el-table-column>
        <el-table-column prop="remainCpu" label="空闲CPU"></el-table-column>
        <el-table-column prop="showMem" label="内存"></el-table-column>
        <el-table-column prop="showRemainMem" label="空闲内存"></el-table-column>
        <el-table-column prop="showLoadAverage" label="负荷"></el-table-column>
        <el-table-column prop="ports" label="端口">
          <template slot-scope="scope">
            <el-tag class="port-tag" size="mini" v-for="(port,index) in scope.row.ports" :key='index'>{{port}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="owners" label="所有者">
          <template slot-scope="scope">
            <el-tag class="port-tag" size="mini" v-for="(owner,index) in scope.row.owners" :key='index'>{{owner}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="rorder" label="order"></el-table-column>
        <el-table-column prop="apps" label="应用" width='180'>
          <template slot-scope="scope">
            <el-tag 
                style='width:65px;margin:2px'
                size="mini" 
                v-for="(app,index) in scope.row.apps" 
                :key='index'
                :title='app'>{{app.slice(0,8)}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name"  width="150" label="name"></el-table-column>
        <el-table-column prop="showUtime" width="200" label="utime"></el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template slot-scope="scope">
            <el-button class="el-button--blue" @click="handleClick(scope.row)" type="primary" size="mini">查看</el-button>
            <el-dropdown  class="more-action" type="primary" size="mini" plain @command="handleCommand">
                <el-button size="mini" class="el-button--blue"  >
                  更多操作<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :command="composeValue('update',scope.row)">更新oder</el-dropdown-item>
                    <el-dropdown-item v-if='scope.row.status === 0' :command="composeValue('offline',scope.row)">机器回收</el-dropdown-item>
                    <el-dropdown-item :command="composeValue('setPrice',scope.row)">设置价格</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      <d2-pagination
        marginTop
        :currentPage='pager.page'
        :pageSize='pager.pageSize'
        :total='pager.total'
        :pageDisabled='pageDisabled'
        @doCurrentChange='handleCurrentChange'>
      </d2-pagination>
   </d2-module>
  
   <el-dialog title="详情" :visible.sync="dialogVisible" width="50%"  :before-close="handleClose">
      <codemirror v-model="dialogData" :options="common_cmOptions" class="detail-code"></codemirror>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false" size="mini">确 定</el-button>
      </span>
   </el-dialog>
   <el-dialog title="编辑" :visible.sync="editDialogVisible" width="30%"  :before-close="handleClose">
    <el-form ref="form" :model="form"  label-width="80px">
      <el-form-item label="id">
        <el-input v-model="form.id" disabled></el-input>
      </el-form-item>
      <el-form-item label="ip">
        <el-input v-model="form.ip" disabled></el-input>
      </el-form-item>
      <el-form-item 
        label="order"  
        prop="order"
        :rules="[
          { required: true, message: 'order不能为空'},
          { type: 'number', message: 'order必须为数字值'}
        ]"
      >
        <el-input v-model.number="form.order" ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleEditSubmit(true)">取 消</el-button>
        <el-button type="primary" @click="handleEditSubmit(false)">更 新</el-button>
      </el-form-item>
    </el-form>
   </el-dialog>

   <el-dialog title='设置价格' :visible.sync='setPriceDialogVisible' width='800px'>
     <el-form ref='priceForm' :model='priceForm' :rules='priceRules' label-width='80px'>
       <el-form-item label='价格' prop='price'>
         <el-input 
          v-model='priceForm.price' 
          placeholder='请输入价格(单位/分)' 
          style='width:50%'
          size='mini'></el-input>
          <el-popover
            trigger="hover"
            placement="right">
            <div style='text-align:center'>单位：分</div>
            <i style='margin-left:10px' slot="reference" class="el-icon-question"></i>
          </el-popover>
       </el-form-item>
     </el-form>
     <span slot="footer" class="dialog-footer">
      <el-button size="mini" @click="setPriceDialogVisible = false">取 消</el-button>
      <el-button type="primary" size="mini" @click="submitPriceFormUpload('priceForm')">确 定</el-button>
     </span>
   </el-dialog>
 </d2-container>
</template>

<script>
import service from "@/plugin/axios/index";
import qs from 'qs';
import "codemirror/mode/javascript/javascript.js";
import "codemirror/theme/base16-dark.css";
import { throttle } from 'lodash'
import bizutil from '@/common/bizutil'

export default {
  name: "mErrorList",
  data() {
    const validatePrice = (rule,value,callback) => {
      if (!value || value === '') {
        return callback(new Error('数据不能为空'))
      }
      if (isNaN(+value)) {
        return callback(new Error('数据必须为Number类型'))
      }
      callback()
    }
    return {
      searchForm:{
        ip:"",
        owner:""
      },
      pager: {
        page: 1,
        pageSize: 10,
        total: 1
      },
      form: {
        id: null,
        ip: null,
        order: null
      },
      rules: {
        order: [
            { required: true, message: "请输入order", trigger: "blur" },
            { type:"number", message: "请输入数字", trigger: "blur" }
          ]
      },
      list: [],
      dialogData: "",
      dialogVisible: false,
      editDialogVisible: false,
      multipleSelection: [],
      pageDisabled: false,
      common_cmOptions: {
        tabSize: 2,
        indentUnit: 2,
        mode: "text/javascript",
        theme: "base16-dark",
        readOnly: "nocursor",
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      setPriceDialogVisible: false,
      setPriceIp: '',
      priceForm: {},   
      priceRules: {
        price: [{ validator: validatePrice, trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    handleCurrentChange(val) {
      this.pager.page = val;
      this.getList();
    },
    composeValue(cmd,item){
      return {
        cmd,item
      }
    },
    handleCommand(obj){
      switch (obj.cmd){
        case "update":
        this.updateOrder(obj.item)
        break;

        case "offline":
        this.machineOffline(obj.item)
        break;

        case "setPrice":
        this.setPriceFunc(obj.item);
        break;
        default:break;
      }
    },
    handleSearch:throttle(function(){
     this.getList();
    }),
    machineOffline(item){
       this.$confirm(`此操作将下线${item.ip}上面部署的所有应用, 是否继续?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
      .then(() => {
          service({
            url: "/resource/offline",
            method: "POST",
            data: qs.stringify({
              ip: item.ip
            })
          })
          .then(list => {
           if(list&&list.length>0){
             let projectNames= list.map(p=>p.name);
             this.$message.success(`${projectNames.join(",")}已经被下线！`)
           }else{
             this.$message.warning(`没有操作成功的应用`);
           }
            this.getList()
          })
      })
      .catch(() => {
          this.$message({
            type: "info",
            message: "已取消操作"
          });
      })
    },
    getList() {
      this.pageDisabled = true;
      let { page, pageSize } = this.pager;
      service({
        url: `/resource/list?page=${page}&pageSize=${pageSize}&owner=${this.searchForm.owner}&ip=${this.searchForm.ip}`
      }).then(res => {
        this.list = this.fix(res.resourceList);
        this.pager.total = res.total;
        this.pager.pageSize =res.pageSize;
        this.pager.page=page
        this.pageDisabled = false;
      });
      setTimeout(() => {
        this.pageDisabled = false;
      }, 2000);
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    fix(data) {
      let fixData = data.map(item => {
       
        item.showUtime = bizutil.timeFormat(item.utime)
        item.showLoadAverage=item.loadAverage;
        item.showMem = this.fixMem(item.mem);
        item.showRemainMem = this.fixMem(item.remainMem);
        item.showPorts = item.ports.join(";");
        if (item.lables && item.lables.apps) {
           item.apps = item.lables.apps.split(',') !== [''] ? item.lables.apps.split(',') : []
        }
        return item;
      });
      return fixData;
    },
    fixMem(num) {
      let unit = "G";
      if (!num) return 0 + unit;
      return (num / (1024 * 1024 * 1024)).toFixed(2) + unit;
    },
    handleClick(data) {
      //丁佩单词拼写错误
      //lables+ => labels
      let { ip, name, hostname, lables, ports } = data;
      let showData = { ip, name, hostname, labels: lables, ports };
      this.dialogData = JSON.stringify(showData, null, 4);
      this.dialogVisible = true;
    },
    submitEdit(data) {
      let {id,order}=data;
      return service({
        url: `/resource/update?id=${id}&order=${order}`,
        method: "POST"
      });
    },
    updateOrder(data) {
      let {id,ip,rorder}=data;
      let showData={id,ip,order:rorder};
      this.form=showData
      this.editDialogVisible =true;
    },
    handleEditSubmit(isClose){
      if(isClose){
        this.$refs['form'].resetFields();
       this.editDialogVisible =false;
       return
      }
      this.$refs["form"].validate((valid) => {
          if (valid) {
             let {id,order}=this.form;
            this.submitEdit({id,order})
            .then(res=>{
              if(res===true){
                this.$message({
                  type:"success",
                  message:"更新成功"
                })
                this.getList()
              }else{
                  this.$message({
                    type:"error",
                    message:res.toString()
                  })
                }
            })
            .finally(e=>{
              this.editDialogVisible=false;
              this.$refs['form'].resetFields();
            })
          } else {
            return false;
          }
        });
      
   

    },
    handleClose() {
      this.dialogData = "";
      this.dialogVisible = false;
      this.editDialogVisible=false;
    },
    setPriceFunc({price,ip}) {
      this.priceForm = { price };
      this.setPriceDialogVisible = true;
      this.setPriceIp = ip;
    },
    submitPriceFormUpload(formName) {
        this.$refs[formName].validate(valid => {
          if (!valid) {
            this.$message({
              message: "请检查参数",
              type: "warning"
            });
            return false
          }
          this.setPriceDialogVisible = false;
          service({
            url: "/resource/price/set",
            method: "POST",
            data: qs.stringify({
              ip: this.setPriceIp,
              price: +this.priceForm.price
            }) 
          }).then(res => {
             this.priceForm = {};
             this.$message({
                message: "设置成功",
                type: "success"
             });
             this.getList()
          })
        })
    }
  }
};
</script>

<style lang="scss" scoped>
.page-con {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
.search-box{
  margin: 0 0 10px 0;
}
.table-container {
  margin-top: 10px;
}
.more-action{
  margin-left: 10px;
}
.port-tag {
  margin: 5px;
}
</style>
<style lang="scss">
.detail-code .CodeMirror {
  height: auto;
}
</style>
