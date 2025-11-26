<template>
    <div class="bind-inner-container">
        <a href="javascript:;" class="btn" @click="handleBind" v-if="isInternal()">
            <svg t="1760066645697" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3345" width="102" height="102"><path d="M861.658611 876.885408 164.03803 876.885408c-56.431244 0-102.340853-45.911656-102.340853-102.345969l0-518.621814c0-56.43329 45.910633-102.345969 102.340853-102.345969L861.658611 153.571654c31.7931 0 53.361325 9.448186 64.100924 28.083615 11.435447 19.834744 7.302313 42.0937-11.340279 61.066821l-11.351536 11.55722 0.001023 0.001023c-0.045025 0.045025-0.601704 0.61296-1.64957 1.679245-0.103354 0.106424-0.205685 0.209778-0.305969 0.312108l-30.90487 31.462572c-0.024559 0.024559-0.050142 0.050142-0.073678 0.074701-27.242458 27.733645-70.074985 71.338768-123.530454 125.759215L619.241466 543.229259c-26.452465 26.931373-52.902884 53.858652-79.356372 80.785931l-6.842849 6.969739c-3.352351 3.415796-7.936763 5.339612-12.721743 5.339612-0.008186 0-0.016373 0-0.023536 0-4.79419-0.007163-9.381671-1.943258-12.729929-5.373381l-354.881566-363.519294c-6.876618-7.04444-6.740518-18.329461 0.302899-25.206078 7.04444-6.877641 18.329461-6.741541 25.206078 0.302899l342.16494 350.493629c24.486694-24.925692 48.969295-49.848315 73.448826-74.772984l295.180859-300.509215c10.350742-10.534937 7.335059-15.766079 5.885034-18.279319-3.749394-6.507204-15.85613-10.238179-33.215496-10.238179L164.03803 189.222619c-36.774555 0-66.691935 29.919427-66.691935 66.696028l0 518.621814c0 36.775578 29.91738 66.696028 66.691935 66.696028L861.658611 841.23649c36.775578 0 66.696028-29.919427 66.696028-66.696028L928.35464 324.875159c-0.01535-3.1569 0.242524-47.771003 25.434276-88.073918 5.216815-8.347109 16.21531-10.885931 24.562419-5.66707 8.347109 5.217838 10.884908 16.214287 5.66707 24.562419-19.908422 31.851429-20.016893 68.64645-20.015869 69.013817 0.001023 0.055259 0.001023 0.058328 0.001023 0.113587l0 449.715445C964.004581 830.973752 918.092925 876.885408 861.658611 876.885408z" fill="currentColor" p-id="3346"></path></svg>
        <p>绑定内部账号</p> 
        </a>
        <el-form v-else label-position="top" :model="form" :rules="rules">
            <el-form-item class="icon-item">
                <el-icon size="68"><Connection /></el-icon>
            </el-form-item>
            <el-form-item label="内部账号:" prop="internalAccount">
                <el-input v-model="form.internalAccount" placeholder="请输入绑定账号" />
            </el-form-item>
            <el-form-item>
                <div class="btn-item">
                    <el-button type="primary" @click="handleBind">绑定</el-button>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router'
import { bindInner } from '@/api/user'
import { useUserStore } from '@/stores/user';
import { isInternal } from '@/utils/tools';
import { ref } from 'vue';
const router = useRouter();
const userStore = useUserStore()
const form = ref({
    internalAccount: ""
})
const rules = ref({
    internalAccount: [
        { required: true, message: '请输入内部账号', trigger: ['blur', 'change'] }
    ]
})
const handleBind = async () => {
    const response = await bindInner({
        id: userStore.user.id,
        username: userStore.user.username,
        internalAccount: isInternal() ? undefined : form.value.internalAccount
    })
    if (response.data.code === 200) {
        userStore.setUser({
            id: userStore.user.id,
            username: userStore.user.username,
            internalAccount: response.data.data.internalAccount || ""
        })
        ElMessage.success('绑定成功')
        router.push('/agents')
    } else {
        ElMessage.error(response.data.message || '绑定失败')
    }
}
</script>

<style scoped>
.bind-inner-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: url(https://p.dun.mi.com/static/images/bg.jpg) no-repeat;
    color: #FF6700;
}
.bind-inner-container .btn-item {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
}
:deep(.el-form) {
    background: #ffffff;
    border: 1px solid #FF6700;
    border-radius: 10px;
    padding: 50px 50px;
    .el-form-item__label {
        color: #333;
    }
    .el-input__wrapper {
        background: #fff;
        .el-input__inner {
            color: #333;
        }
    }
    .icon-item {
        .el-form-item__content {
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }
}
.bind-inner-container .btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10px;
    text-decoration: none;
    color: #FF6700;
    font-size: 24px;
    font-weight: bold;
    font-family: 'Orbitron', sans-serif;
    padding: 100px 80px;
    border-radius: 10px;
    background: #ffffff;
    border: 1px solid #FF6700;
    transition: all 0.3s;
    &:hover {
        background: #FF6700;
        color: #fff;
        transform: scale(1.05);
    }
}
</style>