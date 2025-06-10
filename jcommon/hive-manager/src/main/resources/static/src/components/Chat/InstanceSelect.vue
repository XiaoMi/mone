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
            <el-icon title="配置" size="14px" color="var(--el-color-primary)" @click="handleOpenConfig"><Setting /></el-icon>
            <el-icon title="MCP配置" size="14px" color="var(--el-color-info)" @click="handleOpenMcpConfig"><CreditCard /></el-icon>
            <el-icon title="清除历史记录" size="14px" color="var(--el-color-warning)" @click="handleClearHistory"><Delete /></el-icon>
            <el-icon title="下线" size="16px" color="var(--el-color-danger)" @click="confirmOffline"><SwitchButton /></el-icon>
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

        <!-- MCP 配置对话框 -->
        <el-dialog
            v-model="mcpConfigDialogVisible"
            title="MCP 配置管理"
            width="85%"
            :close-on-click-modal="false"
            class="mcp-config-dialog"
            top="5vh"
            :destroy-on-close="true"
        >
            <McpConfig />
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="mcpConfigDialogVisible = false" size="default">
                        <el-icon><Close /></el-icon>
                        关闭
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { computed, ref, watch, watchEffect } from "vue";
import { ElMessageBox, ElMessage } from 'element-plus';
import { useChatContextStore } from "@/stores/chat-context";
import { Setting, CreditCard, Delete, SwitchButton, Close } from '@element-plus/icons-vue';
import { getAgentConfigs, setBatchAgentConfig, deleteAgentConfig } from '@/api/agent';
import McpConfig from './McpConfig.vue';

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
        setMessageList([]);
        props.onOffline?.();
    }).catch(() => {
        // 用户取消操作
    });
};

const handleClearHistory = () => {
    setMessageList([]);
    props.onClearHistory?.();
};

const handleOpenMcpConfig = () => {
    mcpConfigDialogVisible.value = true;
};

// 配置相关
const configDialogVisible = ref(false);
const configList = ref<Array<{key: string, value: string}>>([]);
const loading = ref(false);

// MCP 配置相关
const mcpConfigDialogVisible = ref(false);

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

:deep(.mcp-config-dialog) {
    .el-dialog__header {
        background: linear-gradient(135deg, var(--el-color-primary-light-9), var(--el-color-primary-light-8));
        border-bottom: 1px solid var(--el-border-color-light);
        padding: 20px 24px;

        .el-dialog__title {
            font-size: 18px;
            font-weight: 600;
            color: var(--el-color-primary);
        }
    }

    .el-dialog__body {
        padding: 0;
        background: var(--el-bg-color-page);
    }

    .el-dialog__footer {
        background: var(--el-fill-color-lighter);
        border-top: 1px solid var(--el-border-color-light);
        padding: 16px 24px;

        .dialog-footer {
            margin: 0;
        }
    }

    .el-dialog {
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
    }
}
</style>
