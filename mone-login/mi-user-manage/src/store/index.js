import Vue from 'vue';
import Vuex from 'vuex';
import NodeModule from '@/store/modules/node';
import UserModule from '@/store/modules/user';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    NodeModule,
    UserModule,
  },
});
