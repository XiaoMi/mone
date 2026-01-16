import type { SpaceProps } from './space';
import type { StyleValue } from 'vue';
export declare function useSpace(props: SpaceProps): {
    classes: import("vue").ComputedRef<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => import("element-plus/es/utils").Arrayable<string | Record<string, boolean>>) | ((new (...args: any[]) => string | Record<string, boolean> | (string | Record<string, boolean>)[]) | (() => import("element-plus/es/utils").Arrayable<string | Record<string, boolean>>))[], unknown, unknown>[]>;
    containerStyle: import("vue").ComputedRef<StyleValue>;
    itemStyle: import("vue").ComputedRef<StyleValue>;
};
