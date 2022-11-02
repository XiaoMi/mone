<template>
  <el-dialog
    :title="detailId ? '编辑' : '新增' + '资源'"
    :visible.sync="dialogVisible"
    width="720px"
    append-to-body
  >
    <el-form ref="form" :model="form" label-width="100px"
      inline
      :rules="rules"
      class="input-200"
    >
      <el-form-item label="资源名称" prop="resourceName">
        <el-input v-model="form.resourceName" class="input-box"></el-input>
      </el-form-item>
      <el-form-item label="资源类型" prop="type">
        <ResourceTypeSelect v-model="form.type" />
      </el-form-item>
      <el-form-item label="资源状态" prop="status">
        <ResourceStatusSelect v-model="form.status" />
      </el-form-item>
      <el-form-item label="环境标识" prop="envFlag">
        <NodeEnvFlagSelect v-model="form.envFlag" />
      </el-form-item>
      <template v-if="form.type === 0">
        <el-form-item label="驱动类" prop="param.driverClass">
          <el-input v-model="form.param.driverClass" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template
        v-if="
          form.type === 0 ||
          form.type === 3 ||
          form.type === 4 ||
          form.type === 5 ||
          form.type === 8 // ZK
        "
      >
        <el-form-item label="资源地址" prop="param.dataSourceUrl">
          <el-input v-model="form.param.dataSourceUrl" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template v-if="form.type === 0">
        <el-form-item label="用户名" prop="param.userName">
          <el-input v-model="form.param.userName" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template v-if="form.type === 0 || form.type === 3">
        <el-form-item label="密码" prop="param.passWd">
          <el-input v-model="form.param.passWd" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template v-if="form.type === 0">
        <el-form-item label="连接池" prop="param.poolSize">
          <el-input-number
            v-model="form.param.poolSize"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="最小连接池" prop="param.minPoolSize">
          <el-input-number
            v-model="form.param.minPoolSize"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="最大连接池" prop="param.maxPoolSize">
          <el-input-number
            v-model="form.param.maxPoolSize"
            :min="1"
          ></el-input-number>
        </el-form-item>
      </template>
      <template
        v-if="
          form.type === 0 ||
          form.type === 2 ||
          form.type === 3 ||
          form.type === 4 ||
          form.type === 5 ||
          form.type === 6 ||// es
          form.type === 8  // ZK
        "
      >
        <el-form-item label="线程数" prop="param.threads">
          <el-input-number
            v-model="form.param.threads"
            :min="1"
          ></el-input-number>
        </el-form-item>
      </template>
      <template v-if="form.type === 1">
        <el-form-item label="插件路径" prop="param.jarPath">
          <el-input v-model="form.param.jarPath" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="包路径" prop="param.iocPackage">
          <el-input v-model="form.param.iocPackage" class="input-box"></el-input>
        </el-form-item>
      </template>
      <!-- dubbo -->
      <template v-if="form.type === 2">
        <el-form-item label="服务名称" prop="param.appName">
          <el-input v-model="form.param.appName" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="注册地址" prop="param.regAddress">
          <el-input v-model="form.param.regAddress" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="api包路径" prop="param.apiPackage">
          <el-input v-model="form.param.apiPackage" class="input-box"></el-input>
        </el-form-item>
      </template>
      <!-- redis -->
      <template v-if="form.type === 3">
        <el-form-item label="Redis类型" prop="param.redisType">
          <el-input v-model="form.param.redisType" class="input-box"></el-input>
        </el-form-item>
      </template>
      <!-- nacos -->
      <template v-if="form.type === 4">
        <el-form-item label="Group" prop="param.nacosGroup">
          <el-input v-model="form.param.nacosGroup" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="Data Id" prop="param.nacosDataId">
          <el-input v-model="form.param.nacosDataId" class="input-box"></el-input>
        </el-form-item>
      </template>
      <!-- mongodb -->
      <template v-if="form.type === 5">
        <el-form-item label="数据库" prop="param.mongoDatabase">
          <el-input v-model="form.param.mongoDatabase" class="input-box"></el-input>
        </el-form-item>
      </template>
      <!-- es -->
      <template v-if="form.type === 6">
        <el-form-item label="es用户名" prop="param.esUser">
          <el-input v-model="form.param.esUser" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="es密码" prop="param.esPwd">
          <el-input v-model="form.param.esPwd" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="es地址" prop="param.esAddress">
          <el-input v-model="form.param.esAddress" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="每批数量" prop="param.bulkActions">
          <el-input-number
            v-model="form.param.bulkActions"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="每批大小" prop="param.bulkByteSize">
          <el-input-number
            v-model="form.param.bulkByteSize"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="并发请求数" prop="param.concurrentRequests">
          <el-input-number
            v-model="form.param.concurrentRequests"
            :min="1"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="刷新间隔" prop="param.flushInterval">
          <el-input-number
            v-model="form.param.flushInterval"
            :min="1"
          ></el-input-number> s
        </el-form-item>
        <el-form-item label="重试次数" prop="param.retryNum">
          <el-input-number
            v-model="form.param.retryNum"
            :min="1"
          ></el-input-number> s
        </el-form-item>
        <el-form-item label="重试间隔" prop="param.retryInterval">
          <el-input-number
            v-model="form.param.retryInterval"
            :min="1"
          ></el-input-number> s
        </el-form-item>
      </template>
      <!-- rocketmq -->
      <template v-if="form.type === 7">
        <el-form-item label="MQAK" prop="param.rocketMQAK">
          <el-input v-model="form.param.rocketMQAK" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="MQSK" prop="param.rocketMQSK">
          <el-input v-model="form.param.rocketMQSK" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="MQ地址" prop="param.rocketMQAddress">
          <el-input v-model="form.param.rocketMQAddress" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="生产者组" prop="param.pGroup">
          <el-input v-model="form.param.pGroup" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template v-if="form.type === 9||form.type === 11">
        <el-form-item label="Dubbo分组" prop="param.dubboGroup">
          <el-input v-model="form.param.dubboGroup" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="Dubbo版本" prop="param.dubboVersion">
          <el-input v-model="form.param.dubboVersion" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label="nocas地址" prop="param.nocasAddr">
          <el-input v-model="form.param.nocasAddr" class="input-box"></el-input>
        </el-form-item>
      </template>
      <template v-if="form.type === 10">
        <el-form-item label="k8s URL" prop="param.url">
          <el-input v-model="form.param.url" class="input-box"></el-input>
        </el-form-item>
        <el-form-item label=" k8s Token" prop="param.token">
          <el-input v-model="form.param.token" class="input-box"></el-input>
        </el-form-item>
      </template>
      <el-form-item label="描述">
        <el-input v-model="form.desc" class="input-box"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" >
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirm('form')">确 定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import ResourceTypeSelect from '@/components/select/ResourceTypeSelect.vue';
import ResourceStatusSelect from '@/components/select/ResourceStatusSelect.vue';
import NodeEnvFlagSelect from '@/components/select/NodeEnvFlagSelect.vue';
import {
  addResource,
  editResource,
  getResourceDetail,
} from '@/common/service/list/resource';

export default {
  components: {
    ResourceTypeSelect,
    ResourceStatusSelect,
    NodeEnvFlagSelect,
  },
  props: {
    show: {
      required: true,
    },
    nodeInfo: {
      required: true,
    },
    detailId: {},
  },
  computed: {
    dialogVisible: {
      get() {
        return this.show;
      },
      set(val) {
        this.$emit('changeShow', val);
      },
    },
    nodeDetail() {
      return this.nodeInfo;
    },
    itemId() {
      return this.detailId;
    },
  },
  data() {
    const validateDubboGroup = (rule, value, callback) => {
      if (value === '' && this.form.type === 11) {
        callback(new Error('此项为必填项'));
      } else {
        callback();
      }
    };
    return {
      form: {
        resourceName: '',
        desc: '',
        type: '',
        status: '',
        envFlag: '',
        param: {
          driverClass: '',
          dataSourceUrl: '',
          userName: '',
          passWd: '',
          poolSize: 0,
          maxPoolSize: 0,
          minPoolSize: 0,
          threads: 0,
          jarPath: '',
          iocPackage: '',
          appName: '',
          regAddress: '',
          apiPackage: '',
          redisType: '',
          // nacos
          nacosDataId: '',
          nacosGroup: '',
          // mongodb
          mongoDatabase: '',
          esUser: '',
          esPwd: '',
          esAddress: '',
          bulkActions: 0,
          bulkByteSize: 0,
          concurrentRequests: 0,
          flushInterval: 0,
          retryNum: 0,
          retryInterval: 0,
          // rocketmq
          rocketMQAK: '',
          rocketMQSK: '',
          rocketMQAddress: '',
          pGroup: '',

          dubboGroup: '',
          dubboVersion: '',
          nocasAddr: '',
          // k8s token
          url: '',
          token: '',
        },
      },
      rules: {
        resourceName: [
          { required: true, message: '请输入资源名称', trigger: 'blur' },
        ],
        type: [{ required: true, message: '请选择资源类型', trigger: 'blur' }],
        envFlag: [
          { required: true, message: '请选择环境标识', trigger: 'blur' },
        ],
        'param.driverClass': this.$requireMsg,
        'param.dataSourceUrl': this.$requireMsg,
        'param.userName': this.$requireMsg,
        'param.passWd': this.$requireMsg,
        'param.poolSize': this.$requireMsg,
        'param.maxPoolSize': this.$requireMsg,
        'param.minPoolSize': this.$requireMsg,
        'param.threads': this.$requireMsg,
        'param.jarPath': this.$requireMsg,
        'param.iocPackage': this.$requireMsg,
        'param.appName': this.$requireMsg,
        'param.regAddress': this.$requireMsg,
        'param.apiPackage': this.$requireMsg,
        'param.redisType': this.$requireMsg,
        'param.nacosDataId': this.$requireMsg,
        'param.nacosGroup': this.$requireMsg,
        // mongodb
        'param.mongoDatabase': this.$requireMsg,
        // es
        'param.esUser': this.$requireMsg,
        'param.esPwd': this.$requireMsg,
        'param.esAddress': this.$requireMsg,
        'param.bulkActions': this.$requireMsg,
        'param.bulkByteSize': this.$requireMsg,
        'param.concurrentRequests': this.$requireMsg,
        'param.flushInterval': this.$requireMsg,
        'param.retryNum': this.$requireMsg,
        'param.retryInterval': this.$requireMsg,
        // rocketMQ
        'param.rocketMQAK': this.$requireMsg,
        'param.rocketMQSK': this.$requireMsg,
        'param.rocketMQAddress': this.$requireMsg,
        'param.pGroup': this.$requireMsg,
        'param.dubboGroup': [
          { validator: validateDubboGroup, trigger: 'blur' },
        ],
        'param.dubboVersion': [
          { validator: validateDubboGroup, trigger: 'blur' },
        ],
        'param.nocasAddr': this.$requireMsg,
        // k8s token
        'param.url': this.$requireMsg,
        'param.token': this.$requireMsg,
      },
    };
  },
  mounted() {
    if (this.itemId) {
      getResourceDetail({ id: this.itemId, parentId: this.nodeDetail.id }).then(
        ({ data }) => {
          this.form = {
            ...this.form,
            ...data,
            ...{ param: JSON.parse(data.content) },
          };
        },
      );
    }
  },
  methods: {
    confirm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.sendFn();
        } else {
          console.log('error submit!!');
          return false;
        }
        return true;
      });
    },
    sendFn() {
      const req = {
        ...this.form,
        ...{ nodeId: this.nodeDetail.id },
      };
      const sendFunName = req.id ? editResource : addResource;
      sendFunName(req).then(() => {
        this.$emit('changeShow', false);
        this.$message.success(req.id ? '编辑成功' : '添加成功');
        this.$emit('refreshList');
      });
    },
  },
};
</script>
