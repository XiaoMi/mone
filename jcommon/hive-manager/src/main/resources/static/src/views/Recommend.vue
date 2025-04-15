<template>
  <div class="roles-container">
    <div class="user-header">
      <h2>推荐用户:</h2>
    </div>
    <ul>
      <li v-for="role in userList" :key="role.id">
        <img :src="role.avatar" alt="Avatar" class="avatar" />
        <div>
          <div class="user-info">
            <span class="user-item">{{ role.name }}</span>
            <span class="line"> | </span>
            <span class="user-item">{{ role.gender }}</span>
            <span class="line"> | </span>
            <span class="user-item">{{ role.age }}岁</span>
            <span class="line"> | </span>
            <span class="user-item">{{ role.personality }}</span>
          </div>
          <div class="user-desc">{{ role.description }}</div>
        </div>
      </li>
    </ul>
    <el-button type="primary" round plain @click="goToRoot">创建会话</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { getRoles } from "@/api/probot";
import Img1 from "@/assets/img1.png";
import Img2 from "@/assets/img2.png";
import Img3 from "@/assets/img3.png";
import Img4 from "@/assets/img4.png";
import { MALE, FE_MALE, fontColor } from "@/common/constants";
import { useRouter } from "vue-router";

const userList = ref([]);

const selectRoles = ref([]);

const images = [
  {
    avatar: Img4,
    fontColor: "#5897ff",
  },
  {
    avatar: Img2,
    fontColor: "#ff7d25",
  },
  {
    avatar: Img1,
    fontColor: "#ff25f6",
  },
  {
    avatar: Img4,
    fontColor: "#d64a4a",
  },
];

const router = useRouter();

const handleSelect = (role) => {
  if (selectRoles.value.includes(role.id)) {
    selectRoles.value = selectRoles.value.filter((v) => v != role.id);
  } else {
    selectRoles.value.push(role.id);
  }
};

const goToRoot = () => {
  router.push("/room");
};

onMounted(async () => {
  try {
    let male = [...MALE];
    let fe_male = [...FE_MALE];
    const response = await getRoles();
    if (response.code == 0) {
      userList.value = (response.data || [])
        .filter((v) => v.roleType != "USER")
        .map((v, index) => ({
          ...v,
          avatar: v.gender == "男" ? male.pop() : fe_male.pop(),
          fontColor: fontColor[index],
          current: false,
        }));
      console.log(userList);
    }
  } catch (error) {
    console.error("Error fetching roles:", error);
  }
});
</script>

<style scoped>
.roles-container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 100vh;
  width: 480px;
  justify-self: center;
  user-select: none;
  font-family: cursive;
}

.user-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

h2 {
  font-family: cursive;
}

.el-button {
  font-family: cursive;
  padding: 26px 38px;
  font-size: 20px;
}

ul {
  list-style-type: none;
  padding: 0;
  margin-bottom: 40px;
}

li {
  display: flex;
  margin-bottom: 28px;
  border: 1px solid transparent;
  border-radius: 5px;
  padding: 6px;
  /* cursor: pointer; */
}
/* li:hover {
  border-color: #a5c5f8;
  background-color: #a5c5f8;
}
li.active {
  border-color: #89b6ff;
  background-color: #89b6ff;
} */
.user-info {
  margin: 0 0 5px;
  display: flex;
  align-items: center;
}

.user-item {
  display: inline-block;
  margin: 0 8px;
  color: #333;
  font-size: 18px;
}

.user-desc {
  font-size: 16px;
  color: #6d6d6d;
  padding-left: 8px;
}

.line {
  color: #969696;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}
</style>
