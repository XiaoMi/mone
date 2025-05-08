<template>
    <div class="instance-select">
        <el-select v-model="selectedIp" placeholder="请选择实例IP" class="ip-select" popper-class="instance-select-popper">
            <el-option
                v-for="item in list"
                :key="item.id"
                :label="`${item.ip}:${item.port}`"
                :value="item.ip"
            />
        </el-select>
        <div class="right-btns">
            <el-icon title="清除历史记录" size="14px" color="var(--el-color-warning)" @click="handleClearHistory"><Delete /></el-icon>
            <el-icon title="下线" size="16px" color="var(--el-color-danger)" @click="confirmOffline"><SwitchButton /></el-icon>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { computed, ref, watch, watchEffect } from "vue";
import { ElMessageBox } from 'element-plus';
import { useChatContextStore } from "@/stores/chat-context";
const { getInstance, setSelectedInstance } = useUserStore();
const { setMessageList } = useChatContextStore();
const selectedIp = ref('')
const props = defineProps({
    onClearHistory: {
        type: Function,
        required: true,
    },
    onOffline: {
        type: Function,
        required: true,
    },
})
const list = computed(() => {
    return getInstance()
})

watch(()=>selectedIp.value, (newIp) => {
    setSelectedInstance(list.value?.find((item: any) => item.ip === newIp))
})

// 添加 watchEffect 来设置默认值
watchEffect(() => {
    if (list.value && list.value.length > 0 && !selectedIp.value) {
        selectedIp.value = list.value[0].ip;
    }
})

const confirmOffline = () => {
    ElMessageBox.confirm(
        '确定要下线该实例吗？',
        '确认下线',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        props.onOffline?.();
    }).catch(() => {
        // 用户取消操作
    });
};

const handleClearHistory = () => {
    setMessageList([]);
    props.onClearHistory?.();
};
</script>

<style>
.instance-select {
    width: 100%;
    height: 48px;
    background: rgba(20, 20, 50, 0.5);
    border-top: none;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.instance-select .right-btns {
    position: absolute;
    right: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
}

.instance-select .right-btns .el-icon {
    cursor: pointer;
}

.instance-select .right-btns .el-icon:hover {
    transition: all 0.3s ease-in-out;
    transform: scale(1.2);
}

.instance-select .ip-select {
    width: 200px;
    margin: 6px 12px;
    border: none !important;
    background-color: transparent;
}

.instance-select .el-select__wrapper {
    background-color: transparent;
    box-shadow: none !important;
    border: none;
}

.instance-select .el-select__wrapper:hover {
    border-color: transparent;
}

.instance-select .el-input__inner {
    color: #fff;
}
.instance-select .el-select__selected-item {
    color: #fff;
}
.instance-select-popper {
    border: none !important;
    background-color: transparent;
}
.instance-select-popper .el-popper__arrow:before {
    background-color: rgba(22, 27, 34,1) !important;
    border-color: rgba(22, 27, 34,1) !important;
}
</style>
