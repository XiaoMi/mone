import type { InjectionKey, Ref } from 'vue';
export interface ElZIndexInjectionContext {
    current: number;
}
export declare const defaultInitialZIndex = 2000;
export declare const ZINDEX_INJECTION_KEY: InjectionKey<ElZIndexInjectionContext>;
export declare const zIndexContextKey: InjectionKey<Ref<number | undefined>>;
export declare const useZIndex: (zIndexOverrides?: Ref<number>) => {
    initialZIndex: import("vue").ComputedRef<number>;
    currentZIndex: import("vue").ComputedRef<number>;
    nextZIndex: () => number;
};
export type UseZIndexReturn = ReturnType<typeof useZIndex>;
