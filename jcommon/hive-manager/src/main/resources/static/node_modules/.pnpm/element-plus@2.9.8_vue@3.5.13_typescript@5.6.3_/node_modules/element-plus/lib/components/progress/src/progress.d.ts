import type { ExtractPropTypes } from 'vue';
import type Progress from './progress.vue';
export type ProgressColor = {
    color: string;
    percentage: number;
};
export type ProgressFn = (percentage: number) => string;
export declare const progressProps: {
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
    readonly color: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | ProgressFn | ProgressColor[]) | (() => string | ProgressFn | ProgressColor[]) | ((new (...args: any[]) => string | ProgressFn | ProgressColor[]) | (() => string | ProgressFn | ProgressColor[]))[], unknown, unknown, "", boolean>;
    readonly striped: BooleanConstructor;
    readonly stripedFlow: BooleanConstructor;
    readonly format: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => ProgressFn) | (() => ProgressFn) | {
        (): ProgressFn;
        new (): any;
        readonly prototype: any;
    } | ((new (...args: any[]) => ProgressFn) | (() => ProgressFn) | {
        (): ProgressFn;
        new (): any;
        readonly prototype: any;
    })[], unknown, unknown, (percentage: number) => string, boolean>;
};
export type ProgressProps = ExtractPropTypes<typeof progressProps>;
export type ProgressInstance = InstanceType<typeof Progress> & unknown;
