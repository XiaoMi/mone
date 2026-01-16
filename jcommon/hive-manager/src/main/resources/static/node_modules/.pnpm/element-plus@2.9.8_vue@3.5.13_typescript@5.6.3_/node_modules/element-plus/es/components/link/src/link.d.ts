import type { ExtractPropTypes, PropType } from 'vue';
import type Link from './link.vue';
export declare const linkProps: {
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "default" | "success" | "warning" | "info" | "primary" | "danger", unknown, "default", boolean>;
    readonly underline: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly disabled: BooleanConstructor;
    readonly href: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly target: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string) | (() => string) | ((new (...args: any[]) => string) | (() => string))[], unknown, unknown, "_self", boolean>;
    readonly icon: {
        readonly type: PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component) | ((new (...args: any[]) => (string | import("vue").Component) & {}) | (() => string | import("vue").Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type LinkProps = ExtractPropTypes<typeof linkProps>;
export declare const linkEmits: {
    click: (evt: MouseEvent) => boolean;
};
export type LinkEmits = typeof linkEmits;
export type LinkInstance = InstanceType<typeof Link> & unknown;
