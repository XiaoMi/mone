<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
    <div>
        <el-table
            :data="machineList"
            ref="multipleTable"
            style="width: 100%"
            @select-all="handleMachineSelect"
            @select="handleMachineSelect"
        >
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column prop="name" label="机器名" />
            <el-table-column prop="hostname" label="hostname" />
            <el-table-column prop="ip" label="ip" />
            

        </el-table>
        <div class="footer">
            <el-button size="mini" type="primary" @click="setMachines">更新</el-button>
        </div>
    </div>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'

export default {
    data () {
        return {
            machineList: []
        }
    },
    props: {
        projectId: {
            type: [Number, String],
            required: true
        },
        envId: {
            type: [Number, String],
            required: true
        }
    },
    created () {
        this.getMachines()
    },
    methods: {
        getMachines() {
            const envId = this.envId
            request({
                url: `/project/env/setting/machine/list`,
                method: 'post',
                data: qs.stringify({envId})
            }).then(list => {
                if (Array.isArray(list)) {
                    this.machineList = list
                    this.$nextTick(() => {
                        this.initMachineSelection()
                    })
                }
            })
        },
        initMachineSelection() {
            this.machineList.forEach(row => {
                if (row.used) this.$refs.multipleTable.toggleRowSelection(row, true)
            })
        },
        handleMachineSelect (selectedMachine) {
            this.machineList.forEach(row => {
                row.used = false
            })
            selectedMachine.forEach(row => {
                row.used = true
            })
        },
        setMachines() {
            request({
                url: `/project/env/setting/machine/set`,
                method: 'post',
                data: this.machineList
            }).then(res => {
                if (res) {
                    this.$message.success("更新成功");
                }
            })
        }
    }
}
</script>
<style lang="scss" scoped>
.footer {
    margin-top: 20px;
    text-align: right;
}
</style>