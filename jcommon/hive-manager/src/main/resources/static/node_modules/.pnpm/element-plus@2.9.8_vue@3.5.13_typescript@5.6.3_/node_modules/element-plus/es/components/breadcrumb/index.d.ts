import Breadcrumb from './src/breadcrumb.vue';
import BreadcrumbItem from './src/breadcrumb-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElBreadcrumb: SFCWithInstall<typeof Breadcrumb> & {
    BreadcrumbItem: typeof BreadcrumbItem;
};
export declare const ElBreadcrumbItem: SFCWithInstall<typeof BreadcrumbItem>;
export default ElBreadcrumb;
export * from './src/breadcrumb';
export * from './src/breadcrumb-item';
export * from './src/constants';
export type { BreadcrumbInstance, BreadcrumbItemInstance, } from './src/instances';
