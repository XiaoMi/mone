<template>
  <div class="main">
    <div class="user-info-wrap">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-image
            class="user-avatar"
            :src="photo"
            :preview-src-list="[photo]">
          </el-image>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><span>{{$i18n.t('nickName')}}:</span></el-col>
        <el-col :span="6"><span>{{selfUserInfo.name}}</span></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><span>{{$i18n.t('account')}}:</span></el-col>
        <el-col :span="6"><span>{{selfUserInfo.userName}}</span></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><span>{{$i18n.t('password')}}:</span></el-col>
        <el-col :span="6"><span>******</span></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><span>{{$i18n.t('email')}}:</span></el-col>
        <el-col :span="6"><span>{{selfUserInfo.email}}</span></el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6"><span>{{$i18n.t('role')}}:</span></el-col>
        <el-col :span="6"><span>{{roleBos}}</span></el-col>
      </el-row>
    </div>
  </div>
</template>
<script lang="ts">
import jsMd5 from 'js-md5'
import { defineComponent, reactive, toRefs, computed, watch } from "vue"
import { useStore } from "vuex"

export default defineComponent({
  setup(props, ctx){
    const store = useStore()
    const state = reactive({
      dialogFormVisible: false,
      photo: '',
      roleBos: '',
      selfUserInfo: computed(() => store.getters.selfUserInfo)
    })
    
    watch(() => state.selfUserInfo, (val) => {
      if (val.userName) {
        state.photo = `xxx`
        let arr = []
        if (val.roleBos) {
          val.roleBos.forEach((item) => {
            arr.push(item.name)
          })
        }
        state.roleBos = arr.join(',')
      }
    },{
      immediate: true,
      deep: true
    })

    return {
      ...toRefs(state)
    }
  }
})
</script>
<style lang="scss" scoped>
.main {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  white-space: nowrap;
	// box-shadow: 0 1px 6px 0 #ccc;
  height: 100vh;
  .user-info-wrap{
    width: 400px;
    margin: 0 auto;
    .el-row {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      margin-bottom: 16px;
      .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        margin-left: 30px;
        border: 1px solid #E9E9E9;
      }
      .user-password-change {
        padding: 0;
        font-size: inherit;
        font-weight: normal;
      }
    }
  }
}
</style>
