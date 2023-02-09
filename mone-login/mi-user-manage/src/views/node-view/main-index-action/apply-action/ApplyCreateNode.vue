<template>
  <el-dialog
    title="申请节点"
    :visible.sync="dialogVisible"
    width="1000px"
    append-to-body
    center
  >
    <el-form ref="form" :model="form" label-width="80px" :rules="rules" inline
      class="input-360">
      <el-card style="margin-bottom:20px">
        <div slot="header" class="clearfix">
          <span>工单信息</span>
        </div>
        <el-form-item label="工单名称" prop="applyName" >
          <el-input v-model="form.applyName"></el-input>
        </el-form-item>
        <el-form-item label="工单描述">
          <el-input v-model="form.desc"></el-input>
        </el-form-item>
      </el-card>
      <el-card>
        <div slot="header" class="clearfix">
          <span>节点信息</span>
        </div>
        <div>
           <el-form-item label="节点名称" prop="typeParam.nodeName">
              <el-input v-model="form.typeParam.nodeName"></el-input>
            </el-form-item>
            <el-form-item label="节点类型" prop="typeParam.type">
              <NodeTypeSelect
              v-model="form.typeParam.type" :initList="supportNodeTypes"/>
            </el-form-item>
            <el-form-item label="部门" prop="typeParam.orgParam"
            v-if="supportOrgNodeTypes.indexOf(form.typeParam.type)>-1">
              <OrganizeListSelect v-model="form.typeParam.orgParam" />
            </el-form-item>
            <template v-if="supportIamNodeTypes && supportIamNodeTypes.length>0 &&
              supportIamNodeTypes.indexOf(form.typeParam.type)>-1">
              <el-form-item label="IamId" >
                <IamInfo v-model="form.typeParam.iamParam"/>
              </el-form-item>
            </template>
            <el-form-item label="编码" v-if="form.typeParam.type === 3" prop="typeParam.code">
              <el-input v-model="form.typeParam.code"></el-input>
            </el-form-item>
            <el-form-item label="节点描述">
              <el-input v-model="form.typeParam.desc"></el-input>
            </el-form-item>
        </div>
      </el-card>
    </el-form>
    <span slot="footer" >
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirm('form')">确 定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { addApply, resubmitApply } from '@/common/service/list/node-apply';
import NodeTypeSelect from '@/components/select/NodeTypeSelect.vue';
import OrganizeListSelect from '@/components/select/OrganizeListSelect.vue';
import IamInfo from '@/components/IamInfo.vue';

export default {
  components: {
    NodeTypeSelect,
    OrganizeListSelect,
    IamInfo,
  },
  props: {
    show: {
      required: true,
    },
    supportNodeTypes: {
      required: true,
    },
    supportOrgNodeTypes: {
      required: true,
    },
    applyType: {
      required: true,
    },
    applyDetail: {
    },
    relNodeId: {
      required: true,
    },
    supportIamNodeTypes: {
      required: true,
    },
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
  },
  mounted() {
    if (!this.applyDetail) return;
    const typeParam = JSON.parse(this.applyDetail.content);
    this.form = {
      ...this.form,
      ...this.applyDetail,
      typeParam,
    };
  },
  data() {
    return {
      form: {
        applyName: '',
        desc: '',
        typeParam: {
          type: '',
          nodeName: '',
          orgParam: {},
          code: '',
          iamParam: {
            id: null,
            name: '',
          },
        },
      },
      rules: {
        applyName: this.$requireMsg,
        'typeParam.type': this.$requireMsg,
        'typeParam.nodeName': this.$requireMsg,
        'typeParam.orgParam': this.$requireMsg,
        'typeParam.code': this.$requireMsg,
      },
    };
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
      if (this.supportOrgNodeTypes.indexOf(this.form.typeParam.type) > -1
      && !this.form.typeParam.orgParam.idPath) {
        this.$message.warning('请选择部门');
        return;
      }
      if (!this.form.typeParam.iamParam.id || !this.form.typeParam.iamParam.name) {
        this.$message.warning('IamId和Iam名称必须都有值');
        return;
      }
      const params = {
        nodeId: this.relNodeId,
        type: this.applyType,
        ...this.form,
      };
      const reqFun = params.id ? resubmitApply : addApply;
      reqFun(params).then(() => {
        this.$message.success('申请成功');
        this.$emit('refeshParent');
        this.$emit('changeShow', false);
      });
    },
  },
};
</script>
