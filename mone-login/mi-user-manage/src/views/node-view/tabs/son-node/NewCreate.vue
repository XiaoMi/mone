<template>
  <el-dialog
    :title="(detailId ? '编辑' : '新增') + '节点'"
    :visible.sync="dialogVisible"
    width="700px"
    append-to-body
    center
  >
    <el-form ref="form" :model="form" label-width="80px" :rules="rules" class="input-200" inline>
      <el-form-item label="节点名称" prop="nodeName">
        <el-input v-model="form.nodeName"></el-input>
      </el-form-item>
      <el-form-item label="节点类型" prop="type">
        <NodeTypeSelect v-model="form.type" :initList="parentNodeDetail.supportNodeTypes"/>
      </el-form-item>
      <el-form-item label="管理员" prop="mgrUserId"
      v-if="!itemId && parentNodeDetail.supportMemberNodeTypes.indexOf(form.type)>-1">
        <AllUserList v-model="form.mgrUserId" />
      </el-form-item>
      <el-form-item label="编码" v-if="form.type === 3" prop="code">
        <el-input v-model="form.code"></el-input>
      </el-form-item>
      <el-form-item label="部门" prop="orgParam"
      v-if="parentNodeDetail.supportOrgNodeTypes.indexOf(form.type)>-1">
        <OrganizeListSelect v-model="form.orgParam" />
      </el-form-item>
      <el-form-item label="环境" v-if="form.type == 6" prop="envFlag">
        <NodeEnvFlagSelect v-model="form.envFlag" />
      </el-form-item>
       <el-form-item label="IamId"
        v-if="parentNodeDetail.supportIamNodeTypes &&
        parentNodeDetail.supportIamNodeTypes.length>0 &&
              parentNodeDetail.supportIamNodeTypes.indexOf(form.type)>-1">
        <IamInfo v-model="form.iamParam"/>
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.desc"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" >
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirm('form')">确 定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { editNode, addNode, getNodeDetail } from '@/common/service/list/node';
import AllUserList from '@/components/AllUserList.vue';
import NodeTypeSelect from '@/components/select/NodeTypeSelect.vue';
import NodeEnvFlagSelect from '@/components/select/NodeEnvFlagSelect.vue';
import OrganizeListSelect from '@/components/select/OrganizeListSelect.vue';
import IamInfo from '@/components/IamInfo.vue';

export default {
  components: {
    AllUserList,
    NodeTypeSelect,
    IamInfo,
    NodeEnvFlagSelect,
    OrganizeListSelect,
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
    parentNodeDetail() {
      return this.nodeInfo;
    },
    itemId() {
      return this.detailId;
    },
  },
  data() {
    return {
      form: {
        nodeName: '',
        status: '',
        type: '',
        mgrUserId: null,
        envFlag: '',
        orgParam: null,
        desc: '',
        code: '',
        iamParam: {
          id: null,
          name: '',
        },
        // createDefEnv: this.form.envFlag === 6,
      },
      rules: {
        type: this.$requireMsg,
        status: this.$requireMsg,
        nodeName: this.$requireMsg,
        envFlag: this.$requireMsg,
        orgParam: this.$requireMsg,
        mgrUserId: this.$requireMsg,
        code: this.$requireMsg,
      },
    };
  },
  mounted() {
    if (this.itemId) {
      getNodeDetail({ id: this.itemId }).then(({ data }) => {
        this.form = {
          ...this.form,
          ...data,
          ...{ orgParam: data.orgInfoVo },
          ...{ iamParam: data.iamInfoVo },
        };
      });
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
      if (!this.form.iamParam.id || !this.form.iamParam.name) {
        this.$message.warning('IamId和Iam名称必须都有值');
        return;
      }
      const params = { ...this.form, ...{ parentNodeId: this.parentNodeDetail.id } };
      const sendFunName = params.id ? editNode : addNode;
      sendFunName(params).then(() => {
        this.$emit('changeShow', false);
        this.$message.success(params.id ? '编辑成功' : '添加成功');
        this.$emit('refreshList');
      });
    },
  },
};
</script>
