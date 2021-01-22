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
    <el-dialog 
      :title='`${title}链路Case`' 
      :visible.sync='chainCaseDialogVisible' 
      width='1300px' 
      :before-close="handleChainClose"
      destroy-on-close
      class='flowchart-dialog'
      :close-on-click-modal='false'
      :close-on-press-escape='false'>
      <div class='content'>
        <div class="left">
          <el-button size='mini' type='primary' :disabled='!sceneDisabled' @click='addService'>新增服务</el-button>
          <el-button size='mini' type='primary' :disabled='!sceneDisabled' @click='delService'>删除服务</el-button>
          <el-button size='mini' type='primary' :disabled='!sceneDisabled' @click='restartChart'>重置图表</el-button>
          <el-popover
            v-if='chainCaseDialogVisible'
            placement="right-start"
            transition='fade-in-linear'
            title='能力提供:'
            width="220"
            trigger="click">
            <div><i class="el-icon-paperclip"/>&nbsp;双击创建服务</div>
            <div><i class="el-icon-paperclip"/>&nbsp;选中服务delete键删除</div>
            <div><i class="el-icon-paperclip"/>&nbsp;支持框选多个服务，删除或移动</div>
            <div><i class="el-icon-paperclip"/>&nbsp;支持[Ctrl+鼠标滚轮]缩放区域</div>
            <el-button slot="reference" size='mini' type='primary' :disabled='!sceneDisabled'>解锁更多</el-button>
          </el-popover>
        </div>
        <div class='middle'>
          <flow 
            :nodes="nodes" 
            :connections="connections" 
            @editnode="handleEditNode"
            @dblclick="handleDblClick" 
            @editconnection="handleEditConnection" 
            @save="handleChartSave" 
            ref="chart"
            width='840'
            height='600'>
          </flow>
        </div>

        <div class="right">
          <div class='totalCaseName'>
            <div class='name'>总Case名：</div>
            <el-input type="text" size='mini' v-model='chainAliasName'/>
          </div>
          <div class='title'>
            服务配置：&nbsp;&nbsp;&nbsp;{{ changeNodeId === -1 ? '' : `service - ${changeNodeId}` }}
          </div>
          <el-form 
            ref='singleNodeForm' 
            :model='singleNodeForm'
            :rules='singleNodeFormRules' 
            label-width='85px' 
            size='mini'>
            <el-form-item label='服务名' prop='caseName'>
              <el-select
                v-model='singleNodeForm.caseName'
                @change='changeCaseName'
                placeholder='请选择'
                size='mini'>
                <el-option
                  v-for='item in caseNameOptions'
                  :key='item.caseName'
                  :value='item.caseName'/>
              </el-select>
            </el-form-item>
            <transition-group>
              <el-form-item 
                v-for='item in singleNodeForm.propsName'
                :key='item'
                :label='item'
                :prop='item'>
                <el-input v-model="singleNodeForm[item]" type='text'/>
              </el-form-item>
            </transition-group>
            <el-form-item style='text-align:right' v-if='sceneDisabled'>
              <el-button 
                type="primary"
                @click="createService">立即创建</el-button>
            </el-form-item>
            <div v-if='singleNodeForm.result'>
              <el-form-item label='状态'>
                <el-button 
                  plain
                  :type='singleNodeForm.result'>
                  {{ singleNodeForm.result === 'success' ? '成功':'失败' }}</el-button>
              </el-form-item>
              <el-form-item label='执行结果'>
                <el-button 
                  size='mini'
                  type="primary" 
                  plain
                  @click='showResultMsg(singleNodeForm.msg)'>查看</el-button>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </div>
      <div slot="footer" class="dialog-footer" v-if='sceneDisabled'>
        <el-button @click="handleChainClose" size="mini">取 消</el-button>
        <el-button type="primary" @click='submitAllServiceData' size="mini">保存</el-button>
      </div>
    </el-dialog>

    <el-dialog title='执行结果' :visible.sync='resultDialogVisible' width='880px' :before-close='handleResultClose' class='result-data'>
      <codemirror v-model="codeMirrorResultData" :options="codeMirrorOptions" class='codeMirror_content'/>
    </el-dialog>
  </div>
</template>

<script>
import service from "@/plugin/axios"
import qs from 'qs'
import flow from 'flowchart-vue'

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    },
    tag: {
      type: String,
      required: true
    },
    contentData: {
      type: Object,
      required: false,
      default: () => ({})
    }
  },
  components: {
    flow
  },
  created() {
    this.getCaseList()
  },
  data() {
    return {
      title: '',
      chainCaseDialogVisible: false,
      submitUrl: '',
      chainAliasName: '',
      singleNodeForm: {},
      serviceConfigMap: {},
      singleNodeFormRules: {
        caseName: [{ required: true, message: "请输入服务名", trigger: "blur" }]
      },
      caseNameOptions: [],
      changeNodeId: -1,
      editId: '',
      nodes: [
        // 可添加任何字段，例如description -> 该字段可在@editnode、@save等钩子获取到
        { id: 1, x: 140, y: 180, name: 'server-1', type: 'operation', approvers: [{ id: 1,name: 'no status' } ], width: 100, height:50 },
        { id: 2, x: 440, y: 180, name: 'server-2', type: 'operation', approvers: [{ id: 1,name: 'no status' } ], width: 100, height:50 }
      ],
      connections: [
        {
          source: {id: 1, position: 'right' },
          destination: { id: 2, position: 'left' },
          id: 1,
          type: 'pass'
        }
      ],
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        // mode: 'text/javascript',
        theme: 'base16-dark',
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      resultDialogVisible: false,
      codeMirrorResultData: ''
    }
  },
  watch: {
    show() {
      if (this.show) {
        switch(this.tag) {
          case 'add':
            this.title = '新增';
            this.submitUrl = '/chain/add';
            break;
          case 'edit':
            this.title = '编辑';
            this.submitUrl = '/chain/update';
            this.backFillData();
            break;
          case 'show':
            this.title = '查看';
            this.backFillData();
            break;
          case 'result':
            this.title = '执行结果';
            this.backFillData();
            break;
          default: 
            break
        }
        this.chainCaseDialogVisible = true
      }
    },
    changeNodeId() {
      for (const key in (this.serviceConfigMap)) {
        if (this.serviceConfigMap.hasOwnProperty(key)) {
          if (+key === this.changeNodeId) {
            this.singleNodeForm = this.serviceConfigMap[key]
            return
          }
        }
      }
      this.singleNodeForm = {}
    }
  },
  computed: {
    sceneDisabled() {
      return this.tag === 'add' || this.tag === 'edit'
    }
  },
  methods: {
    getCaseList() {
      service({
        url: '/chain/caseList',
        method: 'GET'
      }).then( res => {
        if (!Array.isArray(res)) return;
        this.caseNameOptions = res;
        // 表单校验注册
        const finalArr = []
        this.caseNameOptions.map(item => {
          if (item.paramsName.length !== 0) {
            item.paramsName.map(item => {
              finalArr.indexOf(item) === -1 && finalArr.push(item)
            })
          }
        })
        finalArr.forEach(key => {
          this.singleNodeFormRules[key] = [{
            required: true,
            message: '请输入',
            trigger: 'blur'
          }]
        })
      })
    },
    changeCaseName(name) {
      const data = this.caseNameOptions.find(item => item.caseName === name);
      this.singleNodeForm.propsName = data && data.paramsName || [];
    },
    createService() {
      this.$refs['singleNodeForm'].validate(valid => {
        if (!valid) {
          // this.$message.warning('请检查参数');
          return
        }
        const id = this.changeNodeId;
        this.serviceConfigMap[this.changeNodeId] = this.singleNodeForm;
        this.$message.success(`服务${id}配置成功`)
      })
    },
    addService() {
      const chart = this.$refs.chart;
      const id = chart.internalNodes && chart.internalNodes.length + 1;
      const newNode = {
        id,
        x: 20,
        y: 20,
        name: `server-${id}`,
        type: 'operation',
        approvers: [{id:1,name:'no status'}],
        width: 100,
        height: 50
      }
      this.$refs.chart.add(newNode)
    },
    delService() {
      this.$refs.chart.remove()
    },
    handleDblClick(position) {
      const chart = this.$refs.chart;
      const id = chart.internalNodes && chart.internalNodes.length + 1;
      const newNode = {
        id,
        x: position.x,
        y: position.y,
        name: `server-${id}`,
        type: 'operation',
        approvers:[{id:1,name:'no status'}],
        width: 100,
        height: 50
      }
      this.$refs.chart.add(newNode)
    },
    submitAllServiceData() {
      this.$refs.chart.save();
    },
    handleChartSave(nodes,connections) {
      if (this.chainAliasName === '') {
        this.$message.warning('请输入总Case名')
        return
      }
      let finalData = this.dealRequestData(nodes,connections);
      this.tag === 'edit' && (finalData.uuid = this.editId);
      service({
        url: this.submitUrl,
        method: 'POST',
        data: finalData
      }).then( res => {
        if (res || res === null){
          this.$message.success(`链路Case${this.tag === 'add' ? '创建' : '编辑'}成功`);
          this.handleChainClose()
        }
      })
    },
    dealRequestData(nodes,connections) {
      let frontParam = {};
      let backParam = [];

      // 处理backParam
      const list = this.serviceConfigMap;
      for (const id in list) {
        if (list.hasOwnProperty(id)) {
         const config = list[id];
         const propsName = config.propsName;
         let chainParam = '';
         let arr = [];
         propsName.forEach(item => {
           arr.push(config[item])
           chainParam = arr.toString()
         });
         const obj = {
           id,
           chainName: config.caseName,
           chainCount: '1',
           chainRef: '1',
           chainParam
         }
         backParam.push(obj)
        }
      }

      // 处理frontParm
      frontParam.nodes = nodes;
      frontParam.connections = connections

      return {
        frontParam,
        backParam,
        chainAliasName: this.chainAliasName
      }
    },
    handleEditNode(node) {
      this.changeNodeId = node.id;
    },
    handleEditConnection(param) {
      this.$message.warning('暂不支持线段间的编辑')
    },
    handleChainClose() {
      // 数据初始化
      this.$emit('doCloseDialog',false,this.tag);
      this.chainCaseDialogVisible = false;
      this.title = '';
      this.chainAliasName = '';
      this.changeNodeId = -1;
      this.singleNodeForm = {};
      this.serviceConfigMap = {}
    },
    restartChart() {
      this.nodes = [
        { id: 1, x: 140, y: 180, name: 'server-1', type: 'operation', approvers: [{ id: 1,name: 'no status' } ], width: 100, height:50 },
        { id: 2, x: 440, y: 180, name: 'server-2', type: 'operation', approvers: [{ id: 1,name: 'no status' } ], width: 100, height:50 }
      ];
      this.connections = [
        {
          source: {id: 1, position: 'right'},
          destination: {id: 2, position: 'left'},
          id: 1,
          type: 'pass',
        }
      ]
    },
    // 数据回填
    backFillData() {
      let { chainAliasName,backParam,frontParam,resultArr,uuid } = this.contentData;
      // chainAliasName
      this.chainAliasName = chainAliasName;

      try {
        // frontParam
        frontParam = JSON.parse(frontParam);
        this.nodes = frontParam.nodes;
        this.connections = frontParam.connections;

        // backParam
        backParam = JSON.parse(backParam);
        backParam.map((item,index) => {
          const data = this.caseNameOptions.find(i => i.caseName === item.chainName);
          let propsName = data && data.paramsName;
          let chainParam = item.chainParam && item.chainParam.split(',')
          let obj = {};
          propsName.map( (prop,index) => {
            obj[prop] = chainParam[index]
          })

          this.serviceConfigMap[index+1] = {
            caseName: item.chainName,
            propsName, 
            ...obj
          }
        })
      } catch(e) {
        this.$message.error('JSON数据解析失败');
        throw new Error(e)
      }

      // 编辑数据处理
      if (uuid) {
        this.editId = uuid
      }

      // 执行结果数据处理
      if (Array.isArray(resultArr)) {
        resultArr.map((item,index) => {
          const status = item.result ? 'success': 'danger';
          const text = item.result ? '成功' : '失败';
          if (this.nodes[index].approvers) {
            this.nodes[index].approvers[0] = { id: 1, name: text };
          }
          this.serviceConfigMap[index+1].result = status;
          this.serviceConfigMap[index+1].msg = item.msg;
        })
        // 回填服务名
        this.nodes.forEach((item,index) => {
          item.name = this.serviceConfigMap[index+1].caseName
        })
      }
    },
    showResultMsg(param) {
      try {
        this.codeMirrorResultData = JSON.stringify(JSON.parse(param),null,2);
      }catch {
        this.codeMirrorResultData = param;
      }
      this.resultDialogVisible = true
    },
    handleResultClose() {
      this.codeMirrorResultData = '';
      this.resultDialogVisible = false
    }
  }
}
</script>
<style lang="scss" scoped>
.flowchart-dialog {
  .content {
    padding: 15px 12px;
    border: 0.5px solid #ccc;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    .left {
      width: 80px;
      height: 150px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      align-items: flex-end;
    };
    .middle {
      padding: 0px 10px;
    }
    .right {
      padding: 18px 15px 15px 20px;
      box-sizing: border-box;
      border: 1px solid #ebeef5;
      background-color: #fff;
      transition: .3s;
      box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
      .totalCaseName {
        display: flex;
        justify-content: space-between;
        align-items: center;
        .name {
          width: 120px;
          font-size: 15px;
          font-weight: 700;
          color: #909399
        }
      }
      .title {
        font-size: 15px;
        font-weight: 700;
        color: #909399;
        margin: 15px 0px
      }
    }
    .right:hover {
      box-shadow: 10px 10px 15px 0 rgba(0,0,0,.1)
    }
  }
}
.v-enter {
  opacity: 0;
  transform: translateY(24px)
}
.v-enter-active {
  transition: all 1.5s
}
.v-enter-to{
  opacity: 1;
  transform: translateY(0px)
}
</style>
<style lang="scss">
.result-data {
  .codeMirror_content,
  .CodeMirror {
    height: 380px
  }
}
</style>