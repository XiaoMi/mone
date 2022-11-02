<template>
  <div class="regist-box">
    <div class="left-img" :style="{ backgroundImage: 'url(' + backImg + ')' }"></div>
    <div class="main-wrap">
      <vue-particles class="back-img" color="#dedede"></vue-particles>

      <el-form :model="formData" class="form-box" ref="ruleForm" :rules="rules">
        <el-card class="box-card">
          <div class="title-box">
            <p class="title">邮箱登录</p>
            <p class="regist-btn-box">
              <span>没有账号？</span>
              <el-button type="text" class="to-regist" @click="toRegist">去注册</el-button>
            </p>
          </div>
          <el-form-item prop="account">
            <el-input v-model="formData.account" placeholder="邮箱" class="register-input"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="formData.password"
              placeholder="密码"
              class="register-input"
              show-password
            ></el-input>
          </el-form-item>
          <el-form-item class="btn-box">
            <el-button type="primary" @click="submitForm('ruleForm')"
             class="regist-btn">登录</el-button>
          </el-form-item>
          <div class="forget-password-box">
            <el-button type="text" class="to-regist" @click="toFind">已有账号，忘记密码？</el-button>
          </div>
          <div class="other-login-box" v-if="authAccountVos.length>0">
            <div class="other-login">
              <span class="line"></span>
              <span class="text">其他登录方式</span>
              <span class="line"></span>
            </div>
            <ul class="types">
              <li v-for="item in authAccountVos" :key="item.name" class="type-item">
                <el-button
                  :class="'btn-3-img back-'+item.name"
                  circle
                  class="type-item-btn"
                  @click="jumpClick(item.url)"
                ></el-button>
              </li>
            </ul>
          </div>
        </el-card>
      </el-form>
    </div>
  </div>
</template>
<script>
import backImg from '@/assets/back.jpeg';
import {
  checkAcountLogin,
  getLoginInfo,
  loginCall,
} from '@/common/service/list/user-manage';

export default {
  data() {
    // eslint-disable-next-line consistent-return
    const validateAcount = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入邮箱'));
      }
      const params = {
        type: 0,
        account: value,
      };
      checkAcountLogin(params).then(({ data }) => {
        if (data) {
          if (data.code !== 0) {
            callback(new Error(data.message));
          } else {
            callback();
          }
        } else {
          callback();
        }
      });
    };
    return {
      backImg,
      authAccountVos: [],
      formData: {
        account: '',
        type: 0,
        password: '',
      },
      rules: {
        account: [{ validator: validateAcount, trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        name: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
      },
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.sendLogin();
        }
        return false;
      });
    },
    async getInitInfo() {
      const { redirectUrl } = this.$route.query;
      const { data } = await getLoginInfo({ pageUrl: redirectUrl });
      this.authAccountVos = data.authAccountVos || [];
    },
    sendLogin() {
      const formDom = this.$refs.ruleForm;
      const { redirectUrl } = this.$route.query;
      const params = { ...this.formData, pageUrl: redirectUrl };
      loginCall(params).then(({ data }) => {
        console.log('data', data);
        if (data.code && data.code !== 0) {
          formDom.clearValidate();
          this.rules.account.push({
            validator: (rule, value, callback) => {
              callback(new Error(data.message || ''));
            },
            trigger: 'blur',
          });
          formDom.validateField('account');
          this.rules.account = this.rules.account.splice(0, 1);
        } else {
          const { setCookUrl } = data;
          if (setCookUrl) window.location.href = setCookUrl;
        }
      });
    },
    toRegist() {
      const { redirectUrl } = this.$route.query;
      this.$router.push({
        name: 'UserRegist',
        query: {
          redirectUrl,
        },
      });
    },
    toFind() {
      const { redirectUrl } = this.$route.query;
      this.$router.push({
        name: 'FindPwd',
        query: {
          redirectUrl,
        },
      });
    },
    jumpClick(url) {
      window.location.href = url;
    },
  },
  mounted() {
    this.getInitInfo();
  },
};
</script>
<style lang="less" scoped>
.regist-box {
  height: 100%;
  width: 100%;
  position: absolute;
  .main-wrap {
    position: relative;
    margin-left: 480px;
    height: 100%;
    .form-box {
      width: 480px;
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -60%);
      .title {
        font-size: 44px;
      }
    }
  }
}

.register-input {
  border-color: #aaa;
  /deep/ .el-input__inner {
    height: 52px;
    font-size: 20px;
  }
}
.regist-btn {
  width: 100%;
  height: 60px;
  font-size: 24px;
  background: #3487ff;
  border-color: #3487ff;
}
.btn-box {
  padding-top: 10px;
}
.back-img {
  height: 100%;
  width: 100%;
  position: absolute;
}
.left-img {
  position: fixed;
  top: 0;
  left: 0;
  height: 100%;
  width: 480px;
  float: left;
  overflow: hidden;
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
}
.other-login {
  margin-top: 20px;
  position: relative;
  width: 100%;
  height: 14px;
  display: flex;
  justify-content: space-between;
  color: #8c92a4;
  .line::before {
    position: relative;
    left: 0;
    top: 0;
    content: '';
    width: 160px;
    height: 5px;
    border-top: solid 1px #8c92a4;
    display: inline-block;
  }
  .text {
    vertical-align: top;
    font-size: 14px;
  }
}
.types {
  display: flex;
  justify-content: space-around;
  margin: 25px 0 10px 0;
  .type-item {
    list-style: none;
    .type-item-btn {
      padding: 0;
      border: none;
    }
  }
}
.title-box {
  display: flex;
  justify-content: space-between;
  padding: 10px 0 40px 0;
  align-items: baseline;
}
.regist-btn-box > span {
  font-size: 14px;
}
.to-regist {
  color: #005980;
  cursor: pointer;
  font-size: 14px;
}
.forget-password-box {
  text-align: center;
}
.back-gitee{
  background: url(../../common/imgs/gitee.png) ;
}
.back-github{
  background: url(../../common/imgs/github.png) ;
}
.back-gitlab{
  background: url(../../common/imgs/gitlab.png) ;
}
.btn-3-img{
  width: 30px;
  height: 30px;
  background-size: contain;
}
</style>
