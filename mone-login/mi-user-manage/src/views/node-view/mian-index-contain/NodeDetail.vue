<template>
  <div>
    <el-descriptions title="节点信息">
      <el-descriptions-item label="节点名称">{{
        item.nodeName
      }}</el-descriptions-item>
      <el-descriptions-item label="类型"
        ><NodeType :type="item.type"
      /></el-descriptions-item>
      <el-descriptions-item label="环境" v-if="item.envFlag">
        <NodeEnv v-model="item.envFlag"/>
      </el-descriptions-item>
      <el-descriptions-item label="节点ID">{{ item.id }}</el-descriptions-item>
      <el-descriptions-item label="节点编码" v-if="item.type==3">
        {{ item.code }}
      </el-descriptions-item>
      <el-descriptions-item label="外部节点ID" v-if="item.outId != 0">{{
        item.outId
      }}</el-descriptions-item>
      <el-descriptions-item label="外部节点类型" v-if="item.outIdType">
        <OutIdType :type="item.outIdType" />
      </el-descriptions-item>
      <el-descriptions-item label="描述">{{ item.desc }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <NodeStatus :status="item.status"/>
      </el-descriptions-item>
      <el-descriptions-item label="创建人">{{
        $retUserNameType(item.createrAcc,item.createrType)
      }}</el-descriptions-item>
      <el-descriptions-item label="更新人">{{
        $retUserNameType(item.updaterAcc,item.updaterType)
      }}</el-descriptions-item>
    </el-descriptions>
    <el-descriptions title="">
    <el-descriptions-item label="部门" >
        <span>{{ item.orgInfoVo && item.orgInfoVo.namePath }}</span>
        <el-button
          :disabled="!item.supportEditOrg"
          type="primary"
          plain
          icon="el-icon-edit"
          class="edit-btn"
          @click="editOrgHandle"
        ></el-button>
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions v-if="item.supportIamNodeTypes.indexOf(item.type)>-1">
      <el-descriptions-item label="Iam" >
        <span v-if="item.iamInfoVo">
          {{item.iamInfoVo.name}}({{(item.iamInfoVo.id)}})
        </span>
        <el-button
          :disabled="!item.supportEditOrg"
          type="primary"
          plain
          icon="el-icon-edit"
          class="edit-btn"
          @click="editIamHandle"
        ></el-button>
      </el-descriptions-item>
    </el-descriptions>
    <el-dialog
      title="修改部门"
      :visible.sync="dialogVisible"
      width="500px"
      append-to-body
      center
    >
      <el-form ref="form" :model="form" label-width="80px" :rules="rules" class="input-360">
        <el-form-item
          label="部门"
          prop="orgParam"
        >
          <OrganizeListSelect v-model="form.orgParam" />
        </el-form-item>
      </el-form>
      <span slot="footer" >
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmEditOrg">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog
      title="修改Iam"
      :visible.sync="dialogVisibleIam"
      width="660px"
      append-to-body
      center
    >
      <el-form ref="form" :model="form" label-width="80px" :rules="rulesIam" class="input-200">
        <el-form-item
          label="IamId"
          prop="orgParam"
        >
          <IamInfo v-model="formIam.iamParam"/>
        </el-form-item>
      </el-form>
      <span slot="footer" >
        <el-button @click="dialogVisibleIam = false">取 消</el-button>
        <el-button type="primary" @click="confirmEditIam">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import OutIdType from '@/components/OutIdType.vue';
import NodeType from '@/components/NodeType.vue';
import NodeStatus from '@/components/NodeStatus.vue';
import OrganizeListSelect from '@/components/select/OrganizeListSelect.vue';
import { editNodeOrg, editIamApi } from '@/common/service/list/node';
import NodeEnv from '@/components/NodeEnv.vue';
import IamInfo from '@/components/IamInfo.vue';

export default {
  components: {
    OutIdType,
    NodeStatus,
    NodeType,
    OrganizeListSelect,
    NodeEnv,
    IamInfo,
  },
  props: {
    detail: {
      required: true,
    },
  },
  data() {
    return {
      dialogVisibleIam: false,
      dialogVisible: false,
      form: {
        orgParam: {
          idPath: '',
          namePath: '',
        },
      },
      formIam: {
        iamParam: {
          id: '',
          name: '',
        },
      },
      rules: {
        orgParam: this.$requireMsg,
      },
      rulesIam: {
        'iamParam.id': this.$requireMsg,
        'iamParam.name': this.$requireMsg,
      },
    };
  },
  computed: {
    item() {
      return this.detail;
    },
  },
  methods: {
    editOrgHandle() {
      this.switchShowDialog(true);
      this.form = { ...this.form, orgParam: this.detail.orgInfoVo };
    },
    switchShowDialog(val) {
      this.dialogVisible = val;
    },
    confirmEditOrg() {
      editNodeOrg({ ...this.form, id: this.detail.id }).then(() => {
        this.$message.success('修改成功');
        this.switchShowDialog(false);
        this.$emit('getInitData');
      });
    },
    editIamHandle() {
      this.switchShowDialogIam(true);
      if (this.detail.iamInfoVo) {
        this.formIam = { ...this.formIam, iamParam: this.detail.iamInfoVo };
      }
    },
    confirmEditIam() {
      if (!this.formIam.iamParam.id || !this.formIam.iamParam.name) {
        this.$message.warning('iam的Id和名称必须都有值！');
        return;
      }
      editIamApi({ ...this.formIam, id: this.detail.id }).then(() => {
        this.$message.success('修改成功');
        this.switchShowDialogIam(false);
        this.$emit('getInitData');
      });
    },
    switchShowDialogIam(val) {
      this.dialogVisibleIam = val;
    },
  },
};
</script>
<style lang="less" scoped>
.edit-btn {
  padding: 2px;
  margin-left: 3px;
}
</style>
