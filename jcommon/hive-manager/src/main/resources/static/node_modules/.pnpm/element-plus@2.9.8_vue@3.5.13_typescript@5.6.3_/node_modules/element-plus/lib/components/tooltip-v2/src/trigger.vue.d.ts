declare function __VLS_template(): {
    default?(_: {}): any;
    default?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    onBlur: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onClick: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onFocus: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseDown: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseEnter: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseLeave: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    nowrap: BooleanConstructor;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    onBlur: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onClick: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onFocus: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseDown: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseEnter: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    onMouseLeave: {
        readonly type: import("vue").PropType<(e: Event) => boolean | void>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    nowrap: BooleanConstructor;
}>>, {
    nowrap: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
