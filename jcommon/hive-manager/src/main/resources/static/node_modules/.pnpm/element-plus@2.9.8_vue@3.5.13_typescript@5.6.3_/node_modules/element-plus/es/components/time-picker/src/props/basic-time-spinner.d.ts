import type { ExtractPropTypes } from 'vue';
import type { Dayjs } from 'dayjs';
export declare const basicTimeSpinnerProps: {
    readonly disabledHours: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledMinutes: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledSeconds: {
        readonly type: import("vue").PropType<import("element-plus").GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly role: {
        readonly type: import("vue").PropType<string>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly spinnerDate: {
        readonly type: import("vue").PropType<Dayjs>;
        readonly required: true;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly showSeconds: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly arrowControl: BooleanConstructor;
    readonly amPmMode: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A") | ((new (...args: any[]) => "" | "a" | "A") | (() => "" | "a" | "A"))[], unknown, unknown, "", boolean>;
};
export type BasicTimeSpinnerProps = ExtractPropTypes<typeof basicTimeSpinnerProps>;
