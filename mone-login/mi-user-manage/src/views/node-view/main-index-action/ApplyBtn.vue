<template>
  <div class="apply-btn" v-if="nodeDetail.supportApplyTypes">
    <el-dropdown
      style="margin-left: 10px"
      @command="clickApply"
      v-if="nodeDetail.supportApplyTypes.length > 0"
    >
      <el-button type="primary">
        申请<i class="el-icon-arrow-down el-icon--right"></i>
      </el-button>

      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="item in ApplyTypeEnum.filter(
            (item) => nodeDetail.supportApplyTypes.indexOf(item.k) > -1
          )"
          :command="item.k"
          :key="item.k"
          >{{ item.v }}</el-dropdown-item
        >
      </el-dropdown-menu>
    </el-dropdown>
    <ApplyResource
      :relNodeId="nodeDetail.id"
      :show="showApplyResource"
      @changeShow="showApplyResourceFun"
      :applyType="applyType"
      v-if="showApplyResource"
    />
    <ApplyCreateNode
      :supportNodeTypes="nodeDetail.supportNodeTypes"
      :supportOrgNodeTypes="nodeDetail.supportOrgNodeTypes"
      :supportIamNodeTypes="nodeDetail.supportIamNodeTypes"
      :relNodeId="nodeDetail.id"
      :show="showCreateNode"
      @changeShow="showCreateNodeFun"
      :applyType="applyType"
      v-if="showCreateNode"
    />
    <ApplyMember
      :relNodeId="nodeDetail.id"
      :supportMemberTypes="nodeDetail.supportMemberTypes"
      :show="showApplyMember"
      @changeShow="showApplyMemberFun"
      :applyType="applyType"
      v-if="showApplyMember"
    />
  </div>
</template>
<script>
import ApplyResource from '@/views/node-view/main-index-action/apply-action/ApplyResource.vue';
import { mapState } from 'vuex';
import ApplyCreateNode from '@/views/node-view/main-index-action/apply-action/ApplyCreateNode.vue';
import ApplyMember from '@/views/node-view/main-index-action/apply-action/ApplyMember.vue';

export default {
  props: {
    nodeInfo: {
      required: true,
    },
  },
  computed: {
    ...mapState({
      ApplyTypeEnum: (state) => state.NodeModule.ApplyTypeEnum,
    }),
    nodeDetail() {
      return this.nodeInfo;
    },
  },
  data() {
    return {
      showCreateNode: false, // 申请新建节点
      showApplyResource: false,
      showApplyMember: false, // 申请成员
      applyType: '',
    };
  },
  components: {
    ApplyResource,
    ApplyCreateNode,
    ApplyMember,
  },
  methods: {
    clickApply(command) {
      console.log('command', command);
      this.applyType = command;
      if (command === 2 || command === 5) {
        this.showApplyResourceFun(true);
      } else if (command === 0) {
        // 申请创建节点
        this.showCreateNodeFun(true);
      } else if (command === 1) {
        // 申请成员
        this.showApplyMemberFun(true);
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
  },
};
</script>
<style lang="less" scoped>
.apply-btn {
  display: inline-block;
}
</style>
