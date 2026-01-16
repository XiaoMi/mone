import type { MaybeRef } from '@vueuse/core';
import type { App, Ref } from 'vue';
import type { ConfigProviderContext } from '../constants';
export declare function useGlobalConfig<K extends keyof ConfigProviderContext, D extends ConfigProviderContext[K]>(key: K, defaultValue?: D): Ref<Exclude<ConfigProviderContext[K], undefined> | D>;
export declare function useGlobalConfig(): Ref<ConfigProviderContext>;
export declare function useGlobalComponentSettings(block: string, sizeFallback?: MaybeRef<ConfigProviderContext['size']>): {
    ns: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    locale: import("element-plus/es/hooks").LocaleContext;
    zIndex: {
        initialZIndex: import("vue").ComputedRef<number>;
        currentZIndex: import("vue").ComputedRef<number>;
        nextZIndex: () => number;
    };
    size: import("vue").ComputedRef<"small" | "" | "default" | "large">;
};
export declare const provideGlobalConfig: (config: MaybeRef<ConfigProviderContext>, app?: App, global?: boolean) => import("vue").ComputedRef<Partial<import("element-plus").ConfigProviderProps>> | undefined;
