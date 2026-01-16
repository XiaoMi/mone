import type { InjectionKey, Ref } from 'vue';
import type { ComponentSize } from 'element-plus/es/constants';
export declare const useSizeProp: {
    readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
};
export declare const useSizeProps: {
    size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export interface SizeContext {
    size: Ref<ComponentSize>;
}
export declare const SIZE_INJECTION_KEY: InjectionKey<SizeContext>;
export declare const useGlobalSize: () => import("vue").ComputedRef<"" | "small" | "default" | "large">;
