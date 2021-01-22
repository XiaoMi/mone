/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

export default {
    staging: [{
        suitId: 'ksc_va1.4',
        siteId: 'ksywq',
        machine_type: 'VA1.4(金山云虚拟机)',
        cpu: '4核vCPU',
        mem: '16G',
        disk: '20G(系统盘)+200G(数据盘)'
    }, {
        suitId: 'ksc_va2.4',
        siteId: 'ksywq',
        machine_type: 'VA2.4(金山云虚拟机)',
        cpu: '8核vCPU',
        mem: '32G',
        disk: '20G(系统盘)+200G(数据盘)'
    }, {
        suitId: 'ksc_va4.2',
        siteId: 'ksywq',
        machine_type: 'VA4.2(金山云虚拟机)',
        cpu: '32核vCPU',
        mem: '128G',
        disk: '20G(系统盘)+200G(数据盘)'
    }],
    online: [{
        suitId: 'ali_va2',
        siteId: 'bjsali',
        machine_type: 'VA2(阿里云虚拟机)',
        cpu: '8核vCPU',
        mem: '32G',
        disk: '20G(系统盘SSD)+500G(数据盘HDD)'
    },
    {
        suitId: 'ali_va4',
        siteId: 'bjsali',
        machine_type: 'VA4(阿里云虚拟机)',
        cpu: '32核vCPU',
        mem: '128G',
        disk: '20G(系统盘SSD)+500G(数据盘HDD)'
    }, {
        suitId: 'ksc_va3.2',
        siteId: 'ksybj',
        machine_type: 'VA3.2(金山云虚拟机)',
        cpu: '16核vCPU',
        mem: '64G',
        disk: '20G(系统盘)+200G(数据盘)'
    }, {
        suitId: 'ksc_vg3.2',
        siteId: 'ksybj',
        machine_type: 'VG3.2(金山云虚拟机)',
        cpu: '32核',
        mem: '64G',
        disk: '8G(系统盘SSD)+200G(数据盘HDD)'
    }]
}