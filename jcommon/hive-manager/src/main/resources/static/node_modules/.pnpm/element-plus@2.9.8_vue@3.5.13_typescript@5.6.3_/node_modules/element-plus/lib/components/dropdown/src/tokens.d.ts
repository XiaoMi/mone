import { PopperProps } from 'element-plus/es/components/popper';
import type { ComputedRef, InjectionKey, Ref } from 'vue';
export type ElDropdownInjectionContext = {
    contentRef: Ref<HTMLElement | undefined>;
    role: ComputedRef<PopperProps['role']>;
    triggerId: ComputedRef<string>;
    isUsingKeyboard: Ref<boolean>;
    onItemLeave: (e: PointerEvent) => void;
    onItemEnter: (e: PointerEvent) => void;
};
export declare const DROPDOWN_INJECTION_KEY: InjectionKey<ElDropdownInjectionContext>;
