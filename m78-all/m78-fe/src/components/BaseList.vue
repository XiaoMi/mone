<template>
  <div v-if="loading" class="loading-container">
    <div class="loading-content">
      <div class="spinnerSexBox p1"></div>
      <div class="spinnerSexBox p2"></div>
      <div class="spinnerSexBox p3"></div>
      <div class="spinnerSexBox p4"></div>
    </div>
    <div class="loading-txt">正在努力加载中...</div>
  </div>
  <el-empty description="暂无数据" v-else-if="!props.data.length" class="empty-container" />
  <div v-else class="list-container">
    <ul v-if="props.type === 'probot'">
      <li v-for="(item, index) in props.data" :key="index">
        <div class="probot-top">
          <div class="head">
            <BaseInfo
              :data="{
                describe: '',
                name: item.botInfo.name || '----',
                avatarUrl: item.botInfo.avatarUrl || '10'
              }"
            >
              <template #bottom>
                <BaseStar :num="item.botInfo.botAvgStar">
                  <span class="star-num">({{ item.botInfo.botAvgStar }})</span>
                </BaseStar>
              </template>
            </BaseInfo>
          </div>
          <div class="user">@{{ item.botInfo.creator }}</div>
          <div class="describe">
            <el-tooltip effect="dark" :content="item.botInfo.remark" placement="top">
              <span>{{ item.botInfo.remark || '暂无描述' }}</span>
            </el-tooltip>
          </div>
        </div>
        <div class="foot">
          <div class="tag" v-if="item.botCategory">
            <el-popover placement="top" title="" popper-class="tag-popover" trigger="hover">
              <template #reference>
                <span>
                  <el-link
                    type="primary"
                    v-for="(v, i) in item.botCategory"
                    :key="i"
                    :underline="false"
                    >{{ v ? '# ' + v : '' }}</el-link
                  >
                </span>
              </template>
              <div v-for="(v, i) in item.botCategory" :key="i" class="tag-item">
                {{ v }}
              </div>
            </el-popover>
          </div>
          <div class="foot-bottom">
            <div class="collect">
              <i class="iconfont icon-redu"></i>
              <span class="num">{{ item.botInfo.botUseTimes }}</span>
            </div>
            <div class="link">
              <el-button type="primary" plain size="large" color="#40a3ff" @click="visit(item)"
                ><span>Visit</span><el-icon> <Link /> </el-icon
              ></el-button>
            </div>
          </div>
        </div>
      </li>
    </ul>
    <ul v-else>
      <li
        v-for="(item, index) in props.data"
        :key="index"
        @click="emits('onJump', item)"
        class="plugin-item"
      >
        <div class="probot-top">
          <div class="head">
            <BaseInfo
              :data="{
                ...item,
                avatarUrl: item.avatarUrl || '10',
                desc: ''
              }"
            >
              <template #bottom>
                <BaseStar :num="item.pluginAvgStar">
                  <span class="star-num">({{ item.pluginAvgStar }})</span>
                </BaseStar>
              </template>
            </BaseInfo>
          </div>
          <div class="user">@{{ item.userName }}</div>
          <div class="describe">
            <el-tooltip effect="dark" :content="item.desc" placement="top">
              <span> {{ item.desc || '暂无描述' }}</span>
            </el-tooltip>
          </div>
        </div>
        <div class="foot">
          <div class="tag" v-if="item.botCategory">
            <el-popover placement="top" title="" popper-class="tag-popover" trigger="hover">
              <template #reference>
                <span>
                  <el-link
                    type="primary"
                    v-for="(v, i) in item.botCategory"
                    :key="i"
                    :underline="false"
                    >{{ v ? '# ' + v : '' }}</el-link
                  >
                </span>
              </template>
              <div v-for="(v, i) in item.botCategory" :key="i" class="tag-item">
                {{ v }}
              </div>
            </el-popover>
          </div>
          <div class="foot-bottom">
            <div class="collect"></div>
            <div class="yinyong">
              <i class="iconfont icon-yinyong1"></i>
              <span class="num">{{ item.botRefCnt || 0 }}</span>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import BaseInfo from './BaseInfo.vue'
import BaseStar from './BaseStar.vue'

const props = defineProps({
  type: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['onJump'])

const visit = (item: any) => {
  window.open(
    window.location.origin + import.meta.env.VITE_APP_ROUTER_PATH + 'probot-visit/' + item.botId,
    '_blank'
  )
}
</script>

<style lang="scss">
.tag-popover {
  .tag-item {
    line-height: 20px;
    padding: 6px 0;
    text-align: center;
  }
}
</style>
<style scoped lang="scss">
.loading-container {
  height: 100%;
  background: rgba(255, 255, 255, 0.3);
  padding: 100px;
  box-shadow:
    (0 0 #0000, 0 0 #0000),
    (0 0 #0000, 0 0 #0000),
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  .loading-content {
    align-items: center;
    display: flex;
    justify-content: center;
  }
  .loading-txt {
    text-align: center;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.5);
    line-height: 30px;
    padding-top: 20px;
  }
  .spinnerSexBox {
    background: #40a3ff;
    border-radius: 50%;
    height: 1em;
    margin: 0.1em;
    width: 1em;
  }

  .p1 {
    animation: fall 1s linear 0.3s infinite;
  }

  .p2 {
    animation: fall 1s linear 0.2s infinite;
  }

  .p3 {
    animation: fall 1s linear 0.1s infinite;
  }

  .p4 {
    animation: fall 1s linear infinite;
  }
  @keyframes fall {
    0% {
      transform: translateY(-15px);
    }
    25%,
    75% {
      transform: translateY(0);
    }
    100% {
      transform: translateY(-15px);
    }
  }
}
.empty-container {
  background: rgba(255, 255, 255, 0.3);
  box-shadow:
    (0 0 #0000, 0 0 #0000),
    (0 0 #0000, 0 0 #0000),
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -4px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
  margin-bottom: 10px;
}
.list-container {
  ul {
    padding-top: 10px;
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  li {
    width: 24%;
    margin-bottom: 20px;
    margin-right: 1.3333%;
    padding: 10px;
    box-shadow:
      (0 0 #0000, 0 0 #0000),
      (0 0 #0000, 0 0 #0000),
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -4px rgba(0, 0, 0, 0.1);
    background-color: hsl(0 0% 100%);
    color: hsl(224 71.4% 4.1%);
    border: 1px solid rgb(229, 231, 235);
    border-radius: 10px;
    display: flex;
    flex-direction: column;
    transform-origin: center center;
    transition: all 0.3s;
    &:hover {
      transform: scale(1.01);
    }
    &:nth-child(4n) {
      margin-right: 0;
    }
    &.plugin-item {
      cursor: pointer;
    }
    .probot-top {
      flex: 1;
    }
    .head {
      padding: 20px 15px 10px 15px;
      .star-num {
        padding-left: 4px;
        font-size: 18px;
        line-height: 28px;
        color: rgb(3, 7, 18);
      }
    }

    .user {
      font-size: 14px;
      color: #666;
      padding: 0 20px;
      margin: 8px auto;
    }
    .describe {
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      margin: 8px auto;
      padding: 0 20px;
      word-wrap: break-word;
      span {
        font-size: 14px;
        line-height: 24px;
        font-weight: 500;
        color: rgb(107, 114, 128);
        height: 48px;
      }
    }

    .tag {
      margin: 2px 20px 10px 20px;
      span {
        display: inline-block;
        max-width: 100%;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        .oz-link {
          margin-right: 8px;
          margin-bottom: 2px;
          font-size: 14px;
          color: rgb(73, 173, 255);
        }
      }
    }
    .foot-bottom {
      display: flex;
      justify-content: space-between;
      padding: 8px 20px;
      align-items: center;

      .collect {
        display: flex;
        align-items: center;
        .icon-redu {
          font-size: 22px;
          color: #666;
        }
        .num {
          font-size: 15px;
          padding-left: 8px;
          color: #666;
        }
      }

      .link {
        span {
          padding-right: 6px;
          font-size: 18px;
        }

        .oz-icon {
          font-size: 20px;
        }
      }
      .yinyong {
        display: flex;
        align-items: center;
        .icon-yinyong1 {
          font-size: 22px;
          color: #666;
        }
        .num {
          font-size: 15px;
          padding-left: 8px;
          color: #666;
        }
      }
    }
  }
}
</style>
