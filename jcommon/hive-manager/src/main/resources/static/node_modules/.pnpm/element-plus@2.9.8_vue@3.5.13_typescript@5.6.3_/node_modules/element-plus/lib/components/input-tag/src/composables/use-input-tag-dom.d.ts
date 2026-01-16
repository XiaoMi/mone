import type { ComputedRef, Ref, StyleValue } from 'vue';
import type { ComponentSize } from 'element-plus/es/constants';
import type { InputTagProps } from '../input-tag';
interface UseInputTagDomOptions {
    props: InputTagProps;
    isFocused: Ref<boolean>;
    hovering: Ref<boolean>;
    disabled: ComputedRef<boolean>;
    inputValue: Ref<string | undefined>;
    size: ComputedRef<ComponentSize>;
    validateState: ComputedRef<string>;
    validateIcon: ComputedRef<boolean>;
    needStatusIcon: ComputedRef<boolean>;
}
export declare function useInputTagDom({ props, isFocused, hovering, disabled, inputValue, size, validateState, validateIcon, needStatusIcon, }: UseInputTagDomOptions): {
    ns: {
        namespace: ComputedRef<string>;
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
    nsInput: {
        namespace: ComputedRef<string>;
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
    containerKls: ComputedRef<unknown[]>;
    containerStyle: ComputedRef<StyleValue>;
    innerKls: ComputedRef<string[]>;
    showClear: ComputedRef<boolean | "" | 0 | undefined>;
    showSuffix: ComputedRef<boolean | "" | import("vue").Slot>;
};
export {};
