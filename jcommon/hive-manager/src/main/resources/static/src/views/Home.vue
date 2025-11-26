<template>
  <div class="home">
    <div class="chat-wrap">
      <Chat initMsg="" :userList="userList" @changeCurrent="changeCurrent" />
    </div>
    <AgentList :userList="userList" />
  </div>
</template>

<script lang="ts" setup>
import Chat from "@/components/Chat/index.vue";
import { getRoles } from "@/api/probot";
import AgentList from "./AgentList.vue";
import { onMounted, ref } from "vue";
import { MALE, FE_MALE, fontColor } from "@/common/constants";
import Img5 from "@/assets/img5.png";

const userList = ref([]);
const agentList = ref();

onMounted(() => {
  let male = [...MALE];
  let fe_male = [...FE_MALE];
  getRoles()
    .then((data) => {
      if (data.code == 0) {
        userList.value = (data.data || []).map((v, index) => ({
          ...v,
          img:
            v.roleType == "USER"
              ? Img5
              : v.gender == "男"
                ? male.pop()
                : fe_male.pop(),
          fontColor: fontColor[index],
          current: false,
        }));
      }
    })
    .catch((e) => { });
});

const changeCurrent = (id) => {
  userList.value.forEach((v) => {
    v.current = false;
    if (v.id == id) {
      v.current = true;
    }
  });
};
</script>
<style lang="scss">
* {
  margin: 0;
  padding: 0;
  font-family: cursive !important;
}

.home {
  height: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  background: linear-gradient(135deg, #d5fcbd 0%, #b8d2fa 100%);
  background-size: 200% 200%;
  animation: search_styles_Gradient__79kRw 3s ease infinite;

  // animation: background-change 3s infinite;
  // @keyframes background-change {
  //   0% {
  //     background: linear-gradient(135deg, #e0f8d2 0%, #d2e0f8 100%);
  //   }
  //   10% {
  //     background: linear-gradient(135deg, #ddfacc 0%, #ccddfa 100%);
  //   }
  //   20% {
  //     background: linear-gradient(135deg, #dbfac9 0%, #c7dafa 100%);
  //   }
  //   30% {
  //     background: linear-gradient(135deg, #d8fac4 0%, #c1d6fb 100%);
  //   }
  //   40% {
  //     background: linear-gradient(135deg, #d7fbc1 0%, #bcd3f9 100%);
  //   }
  //   50% {
  //     background: linear-gradient(135deg, #d5fcbd 0%, #b8d2fa 100%);
  //   }
  //   60% {
  //     background: linear-gradient(135deg, #d5fcbd 0%, #b8d2fa 100%);
  //   }
  //   70% {
  //     background: linear-gradient(135deg, #d7fbc1 0%, #bcd3f9 100%);
  //   }
  //   80% {
  //     background: linear-gradient(135deg, #d8fac4 0%, #c1d6fb 100%);
  //   }
  //   90% {
  //     background: linear-gradient(135deg, #dbfac9 0%, #c7dafa 100%);
  //   }
  //   100% {
  //     background: linear-gradient(135deg, #ddfacc 0%, #ccddfa 100%);
  //   }
  // }
  .chat-wrap {
    flex: 1;
    height: 100%;
  }
}

@keyframes search_styles_Gradient__79kRw {
  0% {
    background-position: 0 50%;
  }

  50% {
    background-position: 100% 50%;
  }

  to {
    background-position: 0 50%;
  }
}
</style>
