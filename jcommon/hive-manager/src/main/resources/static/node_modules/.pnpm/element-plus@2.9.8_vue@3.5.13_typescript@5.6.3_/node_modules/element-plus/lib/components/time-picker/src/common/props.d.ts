import type { Component, ExtractPropTypes } from 'vue';
import type { Options } from '@popperjs/core';
import type { Dayjs } from 'dayjs';
import type { Placement } from 'element-plus/es/components/popper';
export type SingleOrRange<T> = T | [T, T];
export type DateModelType = number | string | Date;
export type ModelValueType = SingleOrRange<DateModelType> | string[];
export type DayOrDays = SingleOrRange<Dayjs>;
export type DateOrDates = SingleOrRange<Date>;
export type UserInput = SingleOrRange<string | null>;
export type GetDisabledHours = (role: string, comparingDate?: Dayjs) => number[];
export type GetDisabledMinutes = (hour: number, role: string, comparingDate?: Dayjs) => number[];
export type GetDisabledSeconds = (hour: number, minute: number, role: string, comparingDate?: Dayjs) => number[];
export declare const timePickerDefaultProps: {
    readonly showNow: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly ariaLabel: StringConstructor;
    readonly emptyValues: ArrayConstructor;
    readonly valueOnClear: import("element-plus/es/utils").EpPropFinalized<readonly [StringConstructor, NumberConstructor, BooleanConstructor, FunctionConstructor], unknown, unknown, undefined, boolean>;
    readonly disabledDate: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly cellClassName: {
        readonly type: import("vue").PropType<Function>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly shortcuts: import("element-plus/es/utils").EpPropFinalized<ArrayConstructor, unknown, unknown, () => never[], boolean>;
    readonly arrowControl: BooleanConstructor;
    readonly tabindex: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | ((new (...args: any[]) => string | number) | (() => string | number))[], unknown, unknown, 0, boolean>;
    readonly validateEvent: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly unlinkPanels: BooleanConstructor;
    readonly placement: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement) | ((new (...args: any[]) => "top" | "bottom" | "left" | "right" | "auto" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement))[], Placement, unknown, "bottom", boolean>;
    readonly fallbackPlacements: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Placement[]) | (() => Placement[]) | ((new (...args: any[]) => Placement[]) | (() => Placement[]))[], unknown, unknown, readonly ["bottom", "top", "right", "left"], boolean>;
    readonly disabledHours: {
        readonly type: import("vue").PropType<GetDisabledHours>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledMinutes: {
        readonly type: import("vue").PropType<GetDisabledMinutes>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly disabledSeconds: {
        readonly type: import("vue").PropType<GetDisabledSeconds>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly id: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly name: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string, string]) | (() => SingleOrRange<string>) | ((new (...args: any[]) => string | [string, string]) | (() => SingleOrRange<string>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly popperClass: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly format: StringConstructor;
    readonly valueFormat: StringConstructor;
    readonly dateFormat: StringConstructor;
    readonly timeFormat: StringConstructor;
    readonly type: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly clearable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly clearIcon: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly editable: import("element-plus/es/utils").EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
    readonly prefixIcon: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => (string | Component) & {}) | (() => string | Component) | ((new (...args: any[]) => (string | Component) & {}) | (() => string | Component))[], unknown, unknown, "", boolean>;
    readonly size: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<StringConstructor, "" | "small" | "default" | "large", never>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly readonly: BooleanConstructor;
    readonly disabled: BooleanConstructor;
    readonly placeholder: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
    readonly popperOptions: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => Partial<Options>) | (() => Partial<Options>) | ((new (...args: any[]) => Partial<Options>) | (() => Partial<Options>))[], unknown, unknown, () => {}, boolean>;
    readonly modelValue: import("element-plus/es/utils").EpPropFinalized<(new (...args: any[]) => string | number | string[] | Date | [DateModelType, DateModelType]) | (() => ModelValueType) | ((new (...args: any[]) => string | number | string[] | Date | [DateModelType, DateModelType]) | (() => ModelValueType))[], unknown, unknown, "", boolean>;
    readonly rangeSeparator: import("element-plus/es/utils").EpPropFinalized<StringConstructor, unknown, unknown, "-", boolean>;
    readonly startPlaceholder: StringConstructor;
    readonly endPlaceholder: StringConstructor;
    readonly defaultValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly defaultTime: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => Date | [Date, Date]) | (() => SingleOrRange<Date>) | ((new (...args: any[]) => Date | [Date, Date]) | (() => SingleOrRange<Date>))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly isRange: BooleanConstructor;
};
export type TimePickerDefaultProps = ExtractPropTypes<typeof timePickerDefaultProps>;
export interface PickerOptions {
    isValidValue: (date: DayOrDays) => boolean;
    handleKeydownInput: (event: KeyboardEvent) => void;
    parseUserInput: (value: UserInput) => DayOrDays;
    formatToString: (value: DayOrDays) => UserInput;
    getRangeAvailableTime: (date: DayOrDays) => DayOrDays;
    getDefaultValue: () => DayOrDays;
    panelReady: boolean;
    handleClear: () => void;
    handleFocusPicker?: () => void;
}
export declare const timePickerRangeTriggerProps: {
    readonly id: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly name: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string | null, string | null]) | (() => UserInput) | ((new (...args: any[]) => string | [string | null, string | null]) | (() => UserInput))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly startPlaceholder: StringConstructor;
    readonly endPlaceholder: StringConstructor;
    readonly disabled: BooleanConstructor;
};
/**
 * @deprecated Use `timePickerRangeTriggerProps` instead. This will be removed in future versions.
 */
export declare const timePickerRngeTriggerProps: {
    readonly id: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly name: {
        readonly type: import("vue").PropType<string[]>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly modelValue: {
        readonly type: import("vue").PropType<import("element-plus/es/utils").EpPropMergeType<(new (...args: any[]) => string | [string | null, string | null]) | (() => UserInput) | ((new (...args: any[]) => string | [string | null, string | null]) | (() => UserInput))[], unknown, unknown>>;
        readonly required: false;
        readonly validator: ((val: unknown) => boolean) | undefined;
        __epPropKey: true;
    };
    readonly startPlaceholder: StringConstructor;
    readonly endPlaceholder: StringConstructor;
    readonly disabled: BooleanConstructor;
};
