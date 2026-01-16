import type { Ref } from 'vue';
export interface IStepsProps {
    space: number | string;
    active: number;
    direction: string;
    alignCenter: boolean;
    simple: boolean;
    finishStatus: string;
    processStatus: string;
}
export interface StepItemState {
    uid: number;
    currentStatus: string;
    setIndex: (val: number) => void;
    calcProgress: (status: string) => void;
}
export interface IStepsInject {
    props: IStepsProps;
    steps: Ref<StepItemState[]>;
    addStep: (item: StepItemState) => void;
    removeStep: (uid: number) => void;
}
declare function __VLS_template(): {
    icon?(_: {}): any;
    title?(_: {}): any;
    description?(_: {}): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly description: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly status: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "wait" | "error" | "finish" | "success" | "process", unknown, "", boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly description: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly status: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "wait" | "error" | "finish" | "success" | "process", unknown, "", boolean>;
}>>, {
    readonly title: string;
    readonly description: string;
    readonly status: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "wait" | "error" | "finish" | "success" | "process", unknown>;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
