import { getDic } from '@/common/service/list/common-req';

export default {
  namespaced: true,
  state: {
    NodeTypeEnum: [],
    RoleStatusEnum: [],
    UserTypeEnum: [],
    NodeStatusEnum: [],
    NodeEnvFlagEnum: [],
    ResourceStatusEnum: [],
    ResourceTypeEnum: [],
    ApplyTypeEnum: [], // 申请类型
    NodeUserRelTypeEnum: [],
    OutIdTypeEnum: [],
    UserStatusEnum: [],
    ApplyStatusEnum: [],
    ApprovalStatusEnum: [],
  },
  mutations: {
    updateNodeTypeEnum(state, data) {
      state.NodeTypeEnum = data;
    },
    updateRoleStatusEnum(state, data) {
      state.RoleStatusEnum = data;
    },
    updateUserTypeEnum(state, data) {
      state.UserTypeEnum = data;
    },
    updateNodeStatusEnum(state, data) {
      state.NodeStatusEnum = data;
    },
    updateNodeEnvFlagEnum(state, data) {
      state.NodeEnvFlagEnum = data;
    },
    updateResourceStatusEnum(state, data) {
      state.ResourceStatusEnum = data;
    },
    updateResourceTypeEnum(state, data) {
      state.ResourceTypeEnum = data;
    },
    updateApplyTypeEnum(state, data) {
      state.ApplyTypeEnum = data;
    },
    updateApplyStatusEnum(state, data) {
      state.ApplyStatusEnum = data;
    },
    updateNodeUserRelTypeEnum(state, data) {
      state.NodeUserRelTypeEnum = data;
    },
    updateOutIdTypeEnum(state, data) {
      state.OutIdTypeEnum = data;
    },
    updateUserStatusEnum(state, data) {
      state.UserStatusEnum = data;
    },
    updateApprovalStatusEnum(state, data) {
      state.ApprovalStatusEnum = data;
    },
  },
  actions: {
    GETENUMS: (context) => {
      getDic({}).then(({ data }) => {
        context.commit('updateNodeTypeEnum', data.NodeTypeEnum);
        context.commit('updateRoleStatusEnum', data.RoleStatusEnum);
        context.commit('updateUserTypeEnum', data.UserTypeEnum);
        context.commit('updateNodeStatusEnum', data.NodeStatusEnum);
        context.commit('updateNodeEnvFlagEnum', data.NodeEnvFlagEnum);
        context.commit('updateResourceStatusEnum', data.ResourceStatusEnum);
        context.commit('updateResourceTypeEnum', data.ResourceTypeEnum);
        context.commit('updateApplyTypeEnum', data.ApplyTypeEnum);
        context.commit('updateApplyStatusEnum', data.ApplyStatusEnum);
        context.commit('updateNodeUserRelTypeEnum', data.NodeUserRelTypeEnum);
        context.commit('updateOutIdTypeEnum', data.OutIdTypeEnum);
        context.commit('updateUserStatusEnum', data.UserStatusEnum);
        context.commit('updateApprovalStatusEnum', data.ApprovalStatusEnum);
      });
    },
  },
};
