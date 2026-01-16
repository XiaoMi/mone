import type Step from './item.vue';
import type { ExtractPropTypes } from 'vue';
export declare const stepProps: {
    readonly title: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly icon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly description: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly status: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "" | "wait" | "error" | "finish" | "success" | "process", unknown, "", boolean>;
};
export type StepProps = ExtractPropTypes<typeof stepProps>;
export type StepInstance = InstanceType<typeof Step> & unknown;
