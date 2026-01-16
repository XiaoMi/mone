import type { ProgressColor } from './progress';
declare function __VLS_template(): {
    default?(_: {
        percentage: number;
    }): any;
    default?(_: {
        percentage: number;
    }): any;
};
declare const __VLS_component: import("vue").DefineComponent<{
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "circle" | "line" | "dashboard", unknown, "line", boolean>;
    readonly percentage: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly status: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "success" | "warning" | "exception", unknown, "", boolean>;
    readonly indeterminate: BooleanConstructor;
    readonly duration: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3, boolean>;
    readonly strokeWidth: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly strokeLinecap: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>) | ((new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>))[], unknown, unknown, "round", boolean>;
    readonly textInside: BooleanConstructor;
    readonly width: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 126, boolean>;
    readonly showText: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly color: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]) | ((new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]))[], unknown, unknown, "", boolean>;
    readonly striped: BooleanConstructor;
    readonly stripedFlow: BooleanConstructor;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./progress").ProgressFn) | (() => import("./progress").ProgressFn) | {
        (): import("./progress").ProgressFn;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => import("./progress").ProgressFn) | (() => import("./progress").ProgressFn) | {
        (): import("./progress").ProgressFn;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, (percentage: number) => string, boolean>;
}, {}, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, Record<string, any>, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "circle" | "line" | "dashboard", unknown, "line", boolean>;
    readonly percentage: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
    readonly status: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "success" | "warning" | "exception", unknown, "", boolean>;
    readonly indeterminate: BooleanConstructor;
    readonly duration: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 3, boolean>;
    readonly strokeWidth: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 6, boolean>;
    readonly strokeLinecap: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>) | ((new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>))[], unknown, unknown, "round", boolean>;
    readonly textInside: BooleanConstructor;
    readonly width: import("element-plus/es/utils").EpPropFinalized<NumberConstructor, unknown, unknown, 126, boolean>;
    readonly showText: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly color: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]) | ((new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]))[], unknown, unknown, "", boolean>;
    readonly striped: BooleanConstructor;
    readonly stripedFlow: BooleanConstructor;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => import("./progress").ProgressFn) | (() => import("./progress").ProgressFn) | {
        (): import("./progress").ProgressFn;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => import("./progress").ProgressFn) | (() => import("./progress").ProgressFn) | {
        (): import("./progress").ProgressFn;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, (percentage: number) => string, boolean>;
}>>, {
    readonly width: number;
    readonly color: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]) | ((new (...args: any[]) => string | import("./progress").ProgressFn | ProgressColor[]) | (() => string | import("./progress").ProgressFn | ProgressColor[]))[], unknown, unknown>;
    readonly strokeLinecap: import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>) | ((new (...args: any[]) => "inherit" | "round" | "butt" | "square") | (() => NonNullable<"inherit" | "round" | "butt" | "square" | undefined>))[], unknown, unknown>;
    readonly strokeWidth: number;
    readonly type: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "circle" | "line" | "dashboard", unknown>;
    readonly duration: number;
    readonly format: import("./progress").ProgressFn;
    readonly indeterminate: boolean;
    readonly percentage: number;
    readonly status: import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "success" | "warning" | "exception", unknown>;
    readonly showText: import("element-plus/es/utils").EpPropMergeType<BooleanConstructor, unknown, unknown>;
    readonly textInside: boolean;
    readonly striped: boolean;
    readonly stripedFlow: boolean;
}>;
declare const _default: __VLS_WithTemplateSlots<typeof __VLS_component, ReturnType<typeof __VLS_template>>;
export default _default;
type __VLS_WithTemplateSlots<T, S> = T & {
    new (): {
        $slots: S;
    };
};
