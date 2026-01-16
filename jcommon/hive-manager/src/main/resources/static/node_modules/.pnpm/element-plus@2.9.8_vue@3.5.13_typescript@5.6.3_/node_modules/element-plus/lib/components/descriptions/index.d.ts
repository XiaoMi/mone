import Descriptions from './src/description.vue';
import DescriptionsItem from './src/description-item';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElDescriptions: SFCWithInstall<typeof Descriptions> & {
    DescriptionsItem: typeof DescriptionsItem;
};
export declare const ElDescriptionsItem: SFCWithInstall<typeof DescriptionsItem>;
export default ElDescriptions;
export * from './src/description';
export * from './src/description-item';
