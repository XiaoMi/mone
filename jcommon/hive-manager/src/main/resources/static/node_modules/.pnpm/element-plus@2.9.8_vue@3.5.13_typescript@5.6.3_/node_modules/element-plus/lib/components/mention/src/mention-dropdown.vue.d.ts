import type { MentionOption } from './types';
declare function __VLS_template(): {
    header?(_: {}): any;
    label?(_: {
        item: MentionOption;
        index: number;
    }): any;
    loading?(_: {}): any;
    footer?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => MentionOption[]) | (() => MentionOption[]) | ((new (...args: any[]) => MentionOption[]) | (() => MentionOption[]))[], unknown, unknown, () => never[], boolean>;
    loading: BooleanConstructor;
    disabled: BooleanConstructor;
    contentId: StringConstructor;
    ariaLabel: StringConstructor;
}, {
    hoveringIndex: import("vue").Ref<number>;
    navigateOptions: (direction: "next" | "prev") => void;
    selectHoverOption: () => void;
    hoverOption: import("vue").ComputedRef<MentionOption>;
}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {
    select: (option: MentionOption) => void;
}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    options: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => MentionOption[]) | (() => MentionOption[]) | ((new (...args: any[]) => MentionOption[]) | (() => MentionOption[]))[], unknown, unknown, () => never[], boolean>;
    loading: BooleanConstructor;
    disabled: BooleanConstructor;
    contentId: StringConstructor;
    ariaLabel: StringConstructor;
}>> & {
    onSelect?: ((option: MentionOption) => any) | undefined;
}, {
    disabled: boolean;
    loading: boolean;
    options: MentionOption[];
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
