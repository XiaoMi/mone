<template>
  <div class="plug-footer">
    <Logo></Logo>
    <div class="top">
      <dl>
        <dt>{{ t('home.platform') }}</dt>
        <dd>{{ t('home.platformContent') }}</dd>
      </dl>
      <dl>
        <dt>{{ t('home.product') }}</dt>
        <dd>AI Code</dd>
        <dd>AI Document</dd>
        <dd>AI Chat</dd>
        <dd>AI Translate</dd>
      </dl>
      <dl>
        <dt>{{ t('home.contact') }}</dt>
        <dd>{{ t('home.corporateName') }}：xxx</dd>
        <dd>{{ t('home.telephone') }}：xxx</dd>
        <dd>{{ t('home.contactEmail') }}：service@xxx</dd>
      </dl>
    </div>
    <div class="center">
      <ul>
        <li>
          <el-button>
            <i class="iconfont icon-weixin"></i>
          </el-button>
        </li>
        <li>
          <el-button>
            <i class="iconfont icon-youxiang"></i>
          </el-button>
        </li>
        <li>
          <el-button>
            <i class="iconfont icon-github"></i>
          </el-button>
        </li>
        <li>
          <el-button>
            <i class="iconfont icon-dingding"></i>
          </el-button>
        </li>
      </ul>
    </div>
    <div class="bottom">Copyright 2023 | M78</div>
  </div>
</template>

<script setup lang="ts">
import { t } from '@/locales'
import Logo from '@/components/common/Logo.vue'
</script>

<style lang="scss" scoped>
.plug-footer {
  position: relative;
  z-index: 3;
  padding: 40px 10% 10px;
  background-color: #e6f0f9;
  .top {
    display: flex;
    justify-content: space-between;
    padding-top: 20px;
    dl {
      &:nth-child(1) {
        width: 60%;
        padding-right: 140px;
      }
      &:nth-child(2) {
        width: 20%;
      }
      &:nth-child(3) {
        width: 20%;
      }
    }
    dt {
      font-size: 16px;
      line-height: 30px;
      padding-bottom: 10px;
      font-weight: bold;
    }
    dd {
      font-size: 12px;
      line-height: 24px;
    }
  }
  .center {
    margin-top: 20px;
    padding-top: 6px;
    border-top: 1px solid #ddd;
    ul {
      display: flex;
    }
    li {
    }
    .oz-button {
      border: none;
      border-radius: 50%;

      background-color: #e6f0f9;
      .iconfont {
        font-size: 20px;
      }
    }
  }
  .bottom {
    font-size: 12px;
    line-height: 20px;
    padding-top: 6px;
    padding-left: 6px;
  }
}
</style>
