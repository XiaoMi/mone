import Skeleton from './src/skeleton.vue';
import SkeletonItem from './src/skeleton-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElSkeleton: SFCWithInstall<typeof Skeleton> & {
    SkeletonItem: typeof SkeletonItem;
};
export declare const ElSkeletonItem: SFCWithInstall<typeof SkeletonItem>;
export default ElSkeleton;
export * from './src/skeleton';
export * from './src/skeleton-item';
