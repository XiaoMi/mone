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
    </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { computed, ref, watch, watchEffect } from "vue";
const { getInstance, setSelectedInstance } = useUserStore();
const selectedIp = ref('')
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
