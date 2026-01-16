import Collapse from './src/collapse.vue';
import CollapseItem from './src/collapse-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElCollapse: SFCWithInstall<typeof Collapse> & {
    CollapseItem: typeof CollapseItem;
};
export default ElCollapse;
export declare const ElCollapseItem: SFCWithInstall<typeof CollapseItem>;
export * from './src/collapse';
export * from './src/collapse-item';
export * from './src/constants';
export type { CollapseInstance, CollapseItemInstance } from './src/instance';
