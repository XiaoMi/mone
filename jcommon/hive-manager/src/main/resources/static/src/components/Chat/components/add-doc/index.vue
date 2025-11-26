<template>
    <el-dialog 
        v-model="state.show"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :append-to-body="true"
        @close="emits('onCancel')"
        class="add-doc-dialog"
    >
        <template #title>
            <span style="color: #fff; font-size: 14px;">新增文档</span>
        </template>
        <el-form :model="state.form">
            <el-input placeholder="名称：" v-model="state.form.alias" style="margin: 0 0 12px;"/>
            <el-input placeholder="地址：" v-model="state.form.url"/>
        </el-form>
        <template #footer>
            <el-button @click="handleSubmit" size="small">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch } from "vue"
import util from "@/libs/util";

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    groupId: {
        type: Number,
        default: 0
    }
});

const emits = defineEmits(["onCancel"])

const state = reactive({
    show: false,
    form:{
        alias: undefined,
        url: undefined
    }
})

watch(() => props.visible, (val) => {
    state.show = val
    if (!val) {
        state.form.alias = undefined;
        state.form.url = undefined
    }
},{
    deep: true,
    immediate: true
})

const handleSubmit = async () => {
    const res =  await util.filesBases({
        groupId: props.groupId,
        ...state.form,
        addDoc:1
      })
      emits('onCancel')
}

</script>

<style lang="scss">
.add-doc-dialog {
    background-color: #232323;
}

.el-overlay-dialog {
    background-color: rgba($color: #000000, $alpha: 0.7);
}

</style>