<template>
  <div class="btns"
    v-if="applyDetailProp.recall ||
     applyDetailProp.approval ||
     applyDetailProp.close ||
     applyDetailProp.resubmit ">
    <el-button
        @click="changeStatus(applyDetailProp, 'apply', 3)"
        :type="btnType"
        size="small"
        v-if="applyDetailProp.recall"
        >撤回
      </el-button>
    <template v-if="applyDetailProp.approval">
      <el-button
        @click="changeStatus(applyDetailProp, 'approval', 0)"
        :type="btnType"
        size="small"
        >通过
      </el-button>
      <el-button
        @click="changeStatus(applyDetailProp, 'approval', 1)"
        :type="btnType"
        size="small"
        >拒绝
      </el-button>
    </template>
    <!-- 我的申请 -->
    <template v-else>
      <el-button
        @click="changeStatus(applyDetailProp, 'apply', 4)"
        :type="btnType"
        size="small"
        v-if="applyDetailProp.close"
        >关闭
      </el-button>
      <el-button
        @click="resubmitHandle(applyDetailProp)"
        :type="btnType"
        size="small"
        v-if="applyDetailProp.resubmit"
        >重新提交
      </el-button>
    </template>
    <ApplyResource
      :relNodeId="applyDetailProp.applyNodeId"
      :show="showApplyResource"
      :applyDetail="applyDetailProp"
      @changeShow="showApplyResourceFun"
      @refeshParent="refeshParent"
      :applyType="applyType"
      v-if="showApplyResource"
    />
    <ApplyCreateNode
      :supportNodeTypes="supportNodeTypes"
      :relNodeId="applyDetailProp.applyNodeId"
      :applyDetail="applyDetailProp"
      :show="showCreateNode"
      @changeShow="showCreateNodeFun"
      @refeshParent="refeshParent"
      :applyType="applyType"
      v-if="showCreateNode"
    />
    <ApplyMember
      :relNodeId="applyDetailProp.applyNodeId"
      :supportMemberTypes="supportMemberTypes"
      :supportIamNodeTypes="supportIamNodeTypes"
      :show="showApplyMember"
      :applyDetail="applyDetailProp"
      @changeShow="showApplyMemberFun"
      @refeshParent="refeshParent"
      :applyType="applyType"
      v-if="showApplyMember"
    />
  </div>
</template>
<script>
import {
  changeApprovalStatus,
  changeApplyStatus,
} from '@/common/service/list/work-order';
import { getNodeDetail } from '@/common/service/list/node';
import ApplyResource from '@/views/node-view/main-index-action/apply-action/ApplyResource.vue';
import ApplyCreateNode from '@/views/node-view/main-index-action/apply-action/ApplyCreateNode.vue';
import ApplyMember from '@/views/node-view/main-index-action/apply-action/ApplyMember.vue';

export default {
  components: {
    ApplyResource,
    ApplyCreateNode,
    ApplyMember,
  },
  props: {
    applyDetailProp: {
      required: true,
    },
    btnType: {
      default: 'primary',
    },
  },
  data() {
    return {
      showApplyMember: false,
      showCreateNode: false,
      showApplyResource: false,
      applyType: '',
      supportNodeTypes: [],
      supportIamNodeTypes: [],
      supportMemberTypes: [],
    };
  },
  computed: {
    applayId() {
      return this.applayIdProp;
    },
  },
  methods: {
    changeStatus(item, type, status) {
      if (status === 1) {
        this.$prompt('请输入审核意见', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputErrorMessage: '请输入审核意见',
          inputValidator: (value) => {
            if (value) {
              return true;
            }
            return false;
          },
          type: 'warning',
        })
          .then(({ value }) => {
            const params = {
              id: item.id,
              status,
              desc: value,
            };
            this.changeStatusReq(type, params);
          })
          .catch((err) => {
            console.log('err', err);
          });
      } else {
        this.$confirm('确认修改状态吗?', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            const params = {
              id: item.id,
              status,
            };
            this.changeStatusReq(type, params);
          })
          .catch((err) => {
            console.log('err', err);
          });
      }
    },
    changeStatusReq(type, params) {
      const reqFun = type === 'approval' ? changeApprovalStatus : changeApplyStatus;
      reqFun(params).then(() => {
        this.$message({
          type: 'success',
          message: '修改成功!',
        });
        this.refeshParent();
      });
    },
    changeShow(val) {
      this.showCreate = val;
    },
    async resubmitHandle(item) { // 重新提交
      const { type } = item;
      if (type === 2) {
        this.showApplyResourceFun(true);
      } else {
        const nodeDetail = await getNodeDetail({ id: this.applyDetailProp.applyNodeId });
        this.supportNodeTypes = nodeDetail.data.supportNodeTypes;
        this.supportIamNodeTypes = nodeDetail.data.supportIamNodeTypes;
        this.supportMemberTypes = nodeDetail.data.supportMemberTypes;
        if (type === 0) { // 申请创建节点
          this.showCreateNodeFun(true);
        } else if (type === 1) { // 申请成员
          this.showApplyMemberFun(true);
        }
      }
    },
    showApplyResourceFun(val) {
      this.showApplyResource = val;
    },
    showCreateNodeFun(val) {
      this.showCreateNode = val;
    },
    showApplyMemberFun(val) {
      this.showApplyMember = val;
    },
    refeshParent() {
      this.$emit('getList');
    },
  },
};
</script>
<style lang="less" scoped>
.btns {
  display: inline-block;
  ::v-deep button{
    margin-bottom: 20px;
  }
}
</style>
