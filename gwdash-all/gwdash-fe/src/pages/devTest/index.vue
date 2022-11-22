<template>
  <d2-container>
    <div class="devTest-box">
      <div class="input side-box">
        <div class="side-box-header">
          Input
        </div>
        <el-form ref="form" :rules="rules" :model="input" size="mini" label-width="100px" style="width:550px;">
            <el-form-item
             label="服务名称"
             prop="serviceName">
              <el-input placeholder="xx_replace_xx" width="100px" size="mini" @change="handleChangeServiceName" v-model.trim="input.serviceName"></el-input>
            </el-form-item>
              <el-form-item
               prop="version"
               label="服务版本">
              <el-input
                placeholder="xx_replace_xx"
               size="mini" v-model.trim="input.version"  @change="handleChangeServiceName"></el-input>

            </el-form-item>
               <el-form-item
               prop="group"
               label="服务分组">
              <el-input
                placeholder="eg. staging"
               size="mini"  @change="handleChangeServiceName"  v-model.trim="input.group"></el-input>
            </el-form-item>
              <el-form-item
                prop="methodName"
               label="方法名称">
               <el-select v-model="input.methodName" placeholder="请选择">
                  <el-option
                    v-for="method in methodList"
                    :key="method"
                    :label="method"
                    :value="method">
                  </el-option>
                </el-select>
            </el-form-item>
              <el-form-item
                prop="params"
                label="参数列表(JSON数组)">
                 <codemirror v-model="input.params" :options="common_cmOptions"></codemirror>
            </el-form-item>

              <el-form-item
               prop="paramsType"
               label="参数类型">
              <el-input
               placeholder='eg. ["xx_replace_xx", "xx_replace_xx", "xx_replace_xx"]'
               size="mini" v-model.trim="input.paramsType"></el-input>
            </el-form-item>

              <el-form-item label="ip:port">
              <el-input
              placeholder="需要定向调用ip的服务时填写，不填写默认调用nacos的服务"
              size="mini" v-model.trim="input.ip"></el-input>
            </el-form-item>

            <el-form-item>
            <el-button type="primary" size="mini" @click="onSubmit">调试</el-button>
            <!-- <el-button>取消</el-button> -->
          </el-form-item>
        </el-form>
      </div>
      <div class="output side-box">
          <div class="side-box-header">
          Output
        </div>
        <div class="output-data">
             <codemirror v-model="output" :options="common_cmOptions2"></codemirror>
        </div>
      </div>
    </div>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import { type } from 'os'

export default {
  data () {
    let isYp = window.location.hostname === "xx_replace_xx"
    return {
      input: {
        serviceName: '',
        methodName: '',
        group: '',
        params: '',
        paramsType: '',
        version: '',
        ip: ''
      },
      methodList: [],
      output: '',
      common_cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        mode: 'text/json',
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      common_cmOptions2: {
        tabSize: 4,
        readOnly: 'nocursor',
        indentUnit: 4,
        mode: 'text/json',
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      rules: {
        serviceName: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ],
        version: [
          { required: !isYp, message: '必填字段', trigger: 'blur' }
        ],
        methodName: [
          { required: true, message: '必填字段', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    // let mockData ={
    //       "username":"cat",
    //       "token":"1111",
    //       "urls":["xx_replace_xx"]
    //     }
    //    this.input.params = JSON.stringify(mockData,null,3);
  },
  methods: {
    onSubmit () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.doSubmit(this.input)
        } else {
          return false
        }
      })
    },

    doSubmit (params) {
      let sendParams = Object.assign({}, params)
      if (params.ip.indexOf(':') > -1) {
        sendParams.addr = params.ip
        sendParams.ip = params.ip.split(':')[0]
      } else {
        sendParams.addr = params.ip
      }
      service({
        url: '/devTest/dubboTest',
        method: 'POSt',
        data: { ...sendParams }
      })
        .then(res => {
          if (typeof res !== 'string') {
            res = JSON.stringify(res, null, 3)
          }
          this.output = res
        })
    },
    handleChangeServiceName () {
      let params = [this.input.serviceName, this.input.version, this.input.group]
      params = params
        .filter(it => it !== null)
        .filter(it => it.trim() !== "")
        .join(":")
      this.getInstance(params)
    },
    getInstance (serviceName) {
      console.log(serviceName)
      service({
        url: '/nacos/instances/detail',
        method: 'POST',
        data: {
          serviceName: `providers:${serviceName}`
        }
      })
        .then(res => {
          if (res && res.length > 0) {
            console.log(res)
            let instance = res[0]
            this.methodList = instance.metadata.methods.split(',')
          } else {
            this.methodList = []
            this.input.methodName = ''
          }
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.side-box-header{
  padding: 5px;
  border-bottom: 1px solid #dedede;
  margin-bottom: 10px;
}
.side-box{
  flex: 1;
  padding: 10px;
}

</style>
