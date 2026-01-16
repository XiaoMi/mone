import type SkeletonItem from './skeleton-item.vue';
import type { ExtractPropTypes } from 'vue';
export declare const skeletonItemProps: {
    readonly variant: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "button" | "caption" | "h1" | "h3" | "p" | "circle" | "image" | "rect" | "text", unknown, "text", boolean>;
};
export type SkeletonItemProps = ExtractPropTypes<typeof skeletonItemProps>;
export type SkeletonItemInstance = InstanceType<typeof SkeletonItem> & unknown;
