<template>
    <el-drawer
        title="调用记录"
        v-model="modelValue"
        size="60%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        class="invoke-history"
        direction="rtl">
        <template #default>
            <el-table :data="invokeHistory" style="width: 100%; height: 100%;" v-if="invokeHistory.length">
                <el-table-column prop="id" label="ID" align="center"/>
                <el-table-column prop="invokeUserName" label="调用用户" align="center" />
                <el-table-column prop="invokeTime" label="调用时间" width="150" align="center">
                    <template #default="scope">
                        {{ formatDate(scope.row.invokeTime) }}
                    </template>
                </el-table-column>
                <el-table-column prop="invokeWay" label="invokeWay" align="center" >
                    <template #default="scope">
                        {{ invokeWayMap[scope.row.invokeWay] }}
                    </template>
                </el-table-column>
                <el-table-column prop="type" label="type" align="center" >
                    <template #default="scope">
                        {{ typeMap[scope.row.type] }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="100">
                    <template #default="scope">
                        <el-button type="primary" size="small" text @click="handleDetail(scope.row)">详情</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div v-else>
                暂无调用记录
            </div>
        </template>
    </el-drawer>
    <InvokeHistoryDialog v-model="detailVisible" :invokeHistory="detailInvokeHistory" />
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { getInvokeHistory } from '@/api/agent';
import VueJsonPretty from 'vue-json-pretty';
import InvokeHistoryDialog from './InvokeHistoryDialog.vue';
import 'vue-json-pretty/lib/styles.css'

interface InvokeHistory {
    id: number
    relateId: number
    inputs: string
    invokeTime: string
    invokeUserName: string
    outputs: string
}
const props = defineProps<{
    modelValue: boolean
    agentId: string
}>()

const emit = defineEmits(['update:modelValue'])

const modelValue = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
})

const invokeWayMap = ref<Record<string, string>>({
    '1': '页面',
    '2': '接口',
    '3': '系统内部',
    '4': '其他'
})

const typeMap = ref<Record<string, string>>({
    '1': 'agnet',
})

const invokeHistory = ref<InvokeHistory[]>([])
const detailVisible = ref(false)
const detailInvokeHistory = ref<InvokeHistory | null>(null)

watch(() => props.modelValue, (val) => {
    if (val) {
        getInvokeHistory(Number(props.agentId)).then((res) => {
            if (res.data.code === 200) {
                invokeHistory.value = res.data.data || [] as InvokeHistory[]
            } else {
                ElMessage.error(res.data.message)
            }
        })
    } else {
        invokeHistory.value = [] as InvokeHistory[]
    }
})


const handleParse = (str: string) => {
    try {
        // 首先尝试直接解析
        let parsed = JSON.parse(str);

        // 检查解析后的对象中是否还有字符串类型的JSON
        const deepParse = (obj: any) => {
            if (typeof obj === 'string') {
                // 如果是字符串，尝试再次解析
                try {
                    const parsedStr = JSON.parse(obj);
                    // 如果解析成功，递归处理新解析的对象
                    return deepParse(parsedStr);
                } catch (e) {
                    // 如果解析失败，保留原字符串
                    return obj;
                }
            } else if (Array.isArray(obj)) {
                // 如果是数组，递归处理每个元素
                return obj.map((item: any) => deepParse(item));
            } else if (typeof obj === 'object' && obj !== null) {
                // 如果是对象，递归处理每个属性
                const result: any = {};
                for (const key in obj) {
                    if (obj.hasOwnProperty(key)) {
                        result[key] = deepParse(obj[key]);
                    }
                }
                return result;
            }
            // 其他类型直接返回
            return obj;
        };

        return deepParse(parsed);
    } catch (e) {
        console.error("Failed to parse JSON:", e);
        return null;
    }
  }
  const formatDate = (date: string) => {
    return new Date(date).toLocaleString()
  }

  const handleDetail = (row: InvokeHistory) => {
    detailInvokeHistory.value = row
    detailVisible.value = true
  }
</script>

<style>
.invoke-history .el-drawer__header{
    margin-bottom: 0;
    font-weight: bold;
    color: #fff;
}

.invoke-history .invoke-history-wrap {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
}

.invoke-history .invoke-history-item {
    flex: 1;
    margin-bottom: 12px;
    padding: 12px;
    border-radius: 8px;
    background: rgba(48, 54, 61, 0.2);
    transition: all 0.3s ease;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-start;
}

.invoke-history strong {
    color: #31e8f9;
    font-size: 14px;
    text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
    min-width: 80px;
    display: inline-block;
    align-self: flex-start;
}
.el-table {
    background-color: transparent !important;
}
.el-table th .cell {
    color: var(--el-color-chat-link-color);
    font-weight: bold;
}
</style>
