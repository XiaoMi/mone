import type { ExtractPropTypes, StyleValue } from 'vue';
import type { Dayjs } from 'dayjs';
import type Countdown from './countdown.vue';
export declare const countdownProps: {
    readonly format: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "HH:mm:ss", boolean>;
    readonly prefix: StringConstructor;
    readonly suffix: StringConstructor;
    readonly title: StringConstructor;
    readonly value: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => number | Dayjs) | (() => number | Dayjs) | ((new (...args: any[]) => number | Dayjs) | (() => number | Dayjs))[], unknown, unknown, 0, boolean>;
    readonly valueStyle: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue) | ((new (...args: any[]) => string | import("vue").CSSProperties | StyleValue[]) | (() => StyleValue))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
};
export type CountdownProps = ExtractPropTypes<typeof countdownProps>;
export declare const countdownEmits: {
    finish: () => boolean;
    change: (value: number) => boolean;
};
export type CountdownEmits = typeof countdownEmits;
export type CountdownInstance = InstanceType<typeof Countdown> & unknown;
