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
  <div style="display:flex; align-items:center">
    <el-select :value="tenantId" placeholder="请选择" @change="changeValue" size="mini">
      <el-option v-for="item in tenantOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
    </el-select>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import service from '@/plugin/axios'

export default {
  inject: ['reload'],
  computed: {
    ...mapState('d2admin/tenant', ['tenantId', 'tenantOptions'])
  },
  methods: {
    ...mapActions('d2admin/tenant', ['setTenantId']),
    // 更改部门类型
    changeValue (tenantId) {
      this.setTenantId(tenantId)
      this.setTenement(true) // 设置当前租户
    },
    // 设置当前租户 此参数所有接口都需要，因数据未存库需要前端保存
    async setTenement (isReload) {
      try {
        await service({
          url: '/tenement/set',
          method: 'post',
          data: {
            tenement: this.tenantId
          }
        })
      } catch (e) {}
      if (isReload) this.reload() // 刷新页面
    }
  }
}
</script>
