import type { Ref } from 'vue';
import type { Dayjs } from 'dayjs';
import type { PanelRangeSharedProps, RangeState } from '../props/shared';
import type { DefaultValue } from '../utils';
type UseRangePickerProps = {
    onParsedValueChanged: (minDate: Dayjs | undefined, maxDate: Dayjs | undefined) => void;
    defaultValue: Ref<DefaultValue>;
    leftDate: Ref<Dayjs>;
    rightDate: Ref<Dayjs>;
    unit: 'month' | 'year';
};
export declare const useRangePicker: (props: PanelRangeSharedProps, { defaultValue, leftDate, rightDate, unit, onParsedValueChanged, }: UseRangePickerProps) => {
    minDate: Ref<Dayjs | undefined>;
    maxDate: Ref<Dayjs | undefined>;
    rangeState: Ref<{
        endDate: {
            clone: () => Dayjs;
            isValid: () => boolean;
            year: {
                (): number;
                (value: number): Dayjs;
            };
            month: {
                (): number;
                (value: number): Dayjs;
            };
            date: {
                (): number;
                (value: number): Dayjs;
            };
            day: {
                (): 0 | 1 | 2 | 3 | 4 | 5 | 6;
                (value: number): Dayjs;
            };
            hour: {
                (): number;
                (value: number): Dayjs;
            };
            minute: {
                (): number;
                (value: number): Dayjs;
            };
            second: {
                (): number;
                (value: number): Dayjs;
            };
            millisecond: {
                (): number;
                (value: number): Dayjs;
            };
            set: (unit: import("dayjs").UnitType, value: number) => Dayjs;
            get: (unit: import("dayjs").UnitType) => number;
            add: (value: number, unit?: import("dayjs").ManipulateType) => Dayjs;
            subtract: (value: number, unit?: import("dayjs").ManipulateType) => Dayjs;
            startOf: (unit: import("dayjs").OpUnitType) => Dayjs;
            endOf: (unit: import("dayjs").OpUnitType) => Dayjs;
            format: (template?: string) => string;
            diff: (date?: import("dayjs").ConfigType, unit?: import("dayjs").QUnitType | import("dayjs").OpUnitType, float?: boolean) => number;
            valueOf: () => number;
            unix: () => number;
            daysInMonth: () => number;
            toDate: () => Date;
            toJSON: () => string;
            toISOString: () => string;
            toString: () => string;
            utcOffset: () => number;
            isBefore: (date?: import("dayjs").ConfigType, unit?: import("dayjs").OpUnitType) => boolean;
            isSame: (date?: import("dayjs").ConfigType, unit?: import("dayjs").OpUnitType) => boolean;
            isAfter: (date?: import("dayjs").ConfigType, unit?: import("dayjs").OpUnitType) => boolean;
            locale: {
                (): string;
                (preset: string | ILocale, object?: Partial<ILocale>): Dayjs;
            };
            localeData: () => import("dayjs").InstanceLocaleDataReturn;
            week: {
                (): number;
                (value: number): Dayjs;
            };
            weekYear: () => number;
            dayOfYear: {
                (): number;
                (value: number): Dayjs;
            };
            isSameOrAfter: (date?: import("dayjs").ConfigType, unit?: import("dayjs").OpUnitType) => boolean;
            isSameOrBefore: (date?: import("dayjs").ConfigType, unit?: import("dayjs").OpUnitType) => boolean;
        } | null;
        selecting: boolean;
    }>;
    lang: Ref<string>;
    ppNs: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    drpNs: {
        namespace: import("vue").ComputedRef<string>;
        b: (blockSuffix?: string) => string;
        e: (element?: string) => string;
        m: (modifier?: string) => string;
        be: (blockSuffix?: string, element?: string) => string;
        em: (element?: string, modifier?: string) => string;
        bm: (blockSuffix?: string, modifier?: string) => string;
        bem: (blockSuffix?: string, element?: string, modifier?: string) => string;
        is: {
            (name: string, state: boolean | undefined): string;
            (name: string): string;
        };
        cssVar: (object: Record<string, string>) => Record<string, string>;
        cssVarName: (name: string) => string;
        cssVarBlock: (object: Record<string, string>) => Record<string, string>;
        cssVarBlockName: (name: string) => string;
    };
    handleChangeRange: (val: RangeState) => void;
    handleRangeConfirm: (visible?: boolean) => void;
    handleShortcutClick: (shortcut: import("./use-shortcut").Shortcut) => void;
    onSelect: (selecting: boolean) => void;
    onReset: (parsedValue: PanelRangeSharedProps["parsedValue"]) => void;
    t: import("element-plus/es/hooks").Translator;
};
export {};
