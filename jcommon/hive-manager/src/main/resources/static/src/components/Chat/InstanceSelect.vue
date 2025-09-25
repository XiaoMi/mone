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
            <el-tooltip
                class="instance-select-tooltip"
                effect="dark"
                content="配置"
                placement="top"
            >
                <el-icon size="14px" color="var(--el-color-primary)" @click="handleOpenConfig"><Setting /></el-icon>
            </el-tooltip>
            <el-tooltip
                class="instance-select-tooltip"
                effect="dark"
                content="终止对话"
                placement="top"
            >
                <el-icon size="16px" color="var(--el-color-warning)"  @click="handleStopMsg">
                    <svg t="1758526773421" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2595" width="16" height="16"><path d="M783.058824 602.352941a210.823529 210.823529 0 1 1 0 421.647059 210.823529 210.823529 0 0 1 0-421.647059z m-121.072942 139.444706a140.528941 140.528941 0 0 0 192.451765 192.451765l-192.512-192.451765z m121.072942-69.150118c-26.081882 0-50.477176 7.047529-71.378824 19.395765l192.512 192.512a140.528941 140.528941 0 0 0-121.072941-211.907765zM451.764706 60.235294c232.869647 0 421.647059 161.792 421.647059 361.411765 0 44.995765-9.637647 88.124235-27.105883 127.879529a271.058824 271.058824 0 0 0-332.137411 229.616941 485.857882 485.857882 0 0 1-193.656471-13.854117l-132.999529 79.570823a60.235294 60.235294 0 0 1-91.136-54.512941l0.783058-6.987294 23.070118-138.420706C63.789176 583.499294 30.117647 505.976471 30.117647 421.647059c0-199.619765 188.777412-361.411765 421.647059-361.411765zM271.058824 361.411765a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z m180.705882 0a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z m180.705882 0a60.235294 60.235294 0 1 0 0 120.470588 60.235294 60.235294 0 0 0 0-120.470588z" fill="currentColor" p-id="2596"></path></svg>
                </el-icon>
            </el-tooltip>

            <el-tooltip
                class="instance-select-tooltip"
                effect="dark"
                content="清除历史记录"
                placement="top"
            >
                <el-icon size="14px" color="var(--el-color-warning)" @click="handleClearHistory"><Delete /></el-icon>
            </el-tooltip>
            <el-icon size="16px" color="var(--el-color-danger)" @click="confirmOffline"><SwitchButton /></el-icon>
        </div>

        <!-- 配置对话框 -->
        <el-dialog
            v-model="configDialogVisible"
            title="实例配置"
            width="80%"
            :close-on-click-modal="false"
        >
            <div class="config-list">
                <div v-for="(item, index) in configList" :key="index" class="config-item">
                    <el-input v-model="item.key" placeholder="键" />
                    <el-input v-model="item.value" placeholder="值" />
                    <el-button type="danger" circle @click="removeConfig(index)">
                        <el-icon><Delete /></el-icon>
                    </el-button>
                </div>
            </div>
            <el-button type="primary" @click="addConfig">添加配置</el-button>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="configDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmitConfig">确认</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { computed, ref, watch, watchEffect } from "vue";
import { ElMessageBox, ElMessage } from 'element-plus';
import { useChatContextStore } from "@/stores/chat-context";
import { Setting } from '@element-plus/icons-vue';
import { getAgentConfigs, setBatchAgentConfig, type AgentConfig, deleteAgentConfig } from '@/api/agent';

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
    onStopMsg: {
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
        setMessageList([]);
        props.onOffline?.();
    }).catch(() => {
        // 用户取消操作
    });
};

const handleStopMsg = () => {
    props.onStopMsg?.();
}

const handleClearHistory = () => {
    setMessageList([]);
    props.onClearHistory?.();
};

// 配置相关
const configDialogVisible = ref(false);
const configList = ref<Array<{key: string, value: string}>>([]);
const loading = ref(false);

const handleOpenConfig = async () => {
    const selectedInstance = getInstance()?.find((item: any) => item.ip === selectedIp.value);
    if (!selectedInstance?.agentId) {
        ElMessage.error('未找到当前实例对应的Agent');
        return;
    }

    loading.value = true;
    try {
        const response = await getAgentConfigs(selectedInstance.agentId);
        if (response.data?.data) {
            configList.value = response.data.data.map(config => ({
                key: config.key,
                value: config.value
            }));
        }
        configDialogVisible.value = true;
    } catch (error) {
        ElMessage.error('获取配置失败');
        console.error('获取配置失败:', error);
    } finally {
        loading.value = false;
    }
};

const addConfig = () => {
    configList.value.push({
        key: '',
        value: ''
    });
};

const removeConfig = async (index: number) => {
    const config = configList.value[index];
    try {
        await ElMessageBox.confirm(
            `确定要删除配置 "${config.key}" 吗？`,
            '确认删除',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        );

        const selectedInstance = getInstance()?.find((item: any) => item.ip === selectedIp.value);
        if (!selectedInstance?.agentId) {
            ElMessage.error('未找到当前实例对应的Agent');
            return;
        }

        loading.value = true;
        try {
            await deleteAgentConfig(selectedInstance.agentId, config.key);
            configList.value.splice(index, 1);
            ElMessage.success('配置删除成功');
        } catch (error) {
            ElMessage.error('配置删除失败');
            console.error('配置删除失败:', error);
        } finally {
            loading.value = false;
        }
    } catch {
        // 用户取消删除操作
    }
};

const handleSubmitConfig = async () => {
    // 验证配置是否完整
    if (configList.value.some(item => !item.key || !item.value)) {
        ElMessage.warning('请填写完整的配置信息');
        return;
    }

    const selectedInstance = getInstance()?.find((item: any) => item.ip === selectedIp.value);
    if (!selectedInstance?.agentId) {
        ElMessage.error('未找到当前实例对应的Agent');
        return;
    }

    // 转换配置格式
    const config = configList.value.reduce((acc, curr) => {
        acc[curr.key] = curr.value;
        return acc;
    }, {} as Record<string, string>);

    loading.value = true;
    try {
        await setBatchAgentConfig(selectedInstance.agentId, config);
        ElMessage.success('配置更新成功');
        configDialogVisible.value = false;
    } catch (error) {
        ElMessage.error('配置更新失败');
        console.error('配置更新失败:', error);
    } finally {
        loading.value = false;
    }
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

.config-list {
    margin-bottom: 20px;
    max-height: 400px;
    overflow-y: auto;
}

.config-item {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;
    align-items: center;
}

.config-item .el-input {
    flex: 1;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>
