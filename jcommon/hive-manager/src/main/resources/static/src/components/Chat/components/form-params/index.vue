<template>
    <el-dialog v-model="dialogVisible" title="参数表单" width="80%" :show-close="false" :close-on-click-modal="false" :close-on-press-escape="false">
        <div class="form-info">
            <el-form ref="ruleFormRef" :model="form" :rules="rules" :label-width="formUi.labelWidth">
                <el-form-item v-for="item of formUi.listUi" :prop="item.value" :label="item.label" :key="item.value">
                    <template v-if="item.type === 'textarea'">
                        <el-input v-model="form[item.value]" type="textarea" />
                    </template>
                    <template v-else-if="item.type === 'radio'">
                        <el-radio-group v-model="form[item.value]">
                            <div v-for="option of item.options" :key="option.index" class="message-item">
                                <el-radio :label="option.index">{{
                                    `${option.index}. ${option.label}`
                                }}</el-radio>
                            </div>
                        </el-radio-group>
                    </template>
                    <template v-else-if="item.type === 'boolean'">
                        <el-switch v-model="form[item.value]" />
                    </template>
                    <template v-else-if="item.type === 'select'">
                        <el-select v-model="form[item.value]">
                            <el-option v-for="option in item.options" :key="option.value" :label="option.label"
                                :value="option.value" />
                        </el-select>
                    </template>
                    <template v-else>
                        <el-input v-model="form[item.value]"></el-input>
                    </template>
                </el-form-item>
            </el-form>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <!-- <el-button @click="dialogVisible = false">取消</el-button> -->
                <el-button type="primary" @click="submit(ruleFormRef)">确认</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from "vue";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";

const emits = defineEmits(["update:dialogVisible", "submit"]);

const props = withDefaults(
    defineProps<{
        dialogVisible: boolean
        formUi: {
            labelWidth: number
            listUi: {
                label: string
                value: string
                type: 'textarea' | 'select' | 'input' | 'boolean' | 'radio' | 'input'
                placeholder?: string
                options?: {
                    index: number
                    label: string
                    value: string
                }[]
            }[]
        }
    }>(),
    {
        dialogVisible: false,
        formUi: {
            labelWidth: 0,
            listUi: []
        }
    }
);

const dialogVisible = computed({
    get() {
        return props.dialogVisible;
    },
    set(val) {
        emits("update:dialogVisible", val);
    },
});
const formUi = computed(() => {
    return props.formUi;
})
const form = ref<Record<string, string>>({});
const ruleFormRef = ref<FormInstance>();
const rules = reactive<FormRules<Record<string, string>>>({});

const submit = async function (formEl: FormInstance | undefined) {
    if (!formEl) return;
    await formEl.validate(async (valid: boolean) => {
        if (valid) {
            try {
                emits("submit", JSON.stringify({ ...form.value }));
                dialogVisible.value = false; // 关闭对话框
            } catch (e) {
                console.error(e);
                ElMessage.error("操作失败");
            }
        }
    });
};
</script>

<style scoped lang="scss">
.message-item {
    display: flex;
    width: 100%;
    align-items: center;
    height: 36px;
}
</style>