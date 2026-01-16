import dayjs from 'dayjs';
import type { SetupContext } from 'vue';
import type { DateCell } from '../date-picker.type';
import type { BasicDateTableEmits, BasicDateTableProps } from '../props/basic-date-table';
export declare const useBasicDateTable: (props: BasicDateTableProps, emit: SetupContext<BasicDateTableEmits>["emit"]) => {
    WEEKS: import("vue").ComputedRef<string[]>;
    rows: import("vue").ComputedRef<{
        column?: number | undefined;
        customClass?: string | undefined;
        disabled?: boolean | undefined;
        end?: boolean | undefined;
        inRange?: boolean | undefined;
        row?: number | undefined;
        selected?: {
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
            set: (unit: dayjs.UnitType, value: number) => Dayjs;
            get: (unit: dayjs.UnitType) => number;
            add: (value: number, unit?: dayjs.ManipulateType) => Dayjs;
            subtract: (value: number, unit?: dayjs.ManipulateType) => Dayjs;
            startOf: (unit: dayjs.OpUnitType) => Dayjs;
            endOf: (unit: dayjs.OpUnitType) => Dayjs;
            format: (template?: string) => string;
            diff: (date?: dayjs.ConfigType, unit?: dayjs.QUnitType | dayjs.OpUnitType, float?: boolean) => number;
            valueOf: () => number;
            unix: () => number;
            daysInMonth: () => number;
            toDate: () => Date;
            toJSON: () => string;
            toISOString: () => string;
            toString: () => string;
            utcOffset: () => number;
            isBefore: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isSame: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isAfter: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            locale: {
                (): string;
                (preset: string | ILocale, object?: Partial<ILocale>): Dayjs;
            };
            localeData: () => dayjs.InstanceLocaleDataReturn;
            week: {
                (): number;
                (value: number): Dayjs;
            };
            weekYear: () => number;
            dayOfYear: {
                (): number;
                (value: number): Dayjs;
            };
            isSameOrAfter: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isSameOrBefore: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
        } | undefined;
        isCurrent?: boolean | undefined;
        isSelected?: boolean | undefined;
        start?: boolean | undefined;
        text?: number | undefined;
        renderText?: string | undefined;
        timestamp?: number | undefined;
        date?: {
            toString: () => string;
            toDateString: () => string;
            toTimeString: () => string;
            toLocaleString: {
                (): string;
                (locales?: string | string[], options?: Intl.DateTimeFormatOptions): string;
            };
            toLocaleDateString: {
                (): string;
                (locales?: string | string[], options?: Intl.DateTimeFormatOptions): string;
            };
            toLocaleTimeString: {
                (): string;
                (locales?: string | string[], options?: Intl.DateTimeFormatOptions): string;
            };
            valueOf: () => number;
            getTime: () => number;
            getFullYear: () => number;
            getUTCFullYear: () => number;
            getMonth: () => number;
            getUTCMonth: () => number;
            getDate: () => number;
            getUTCDate: () => number;
            getDay: () => number;
            getUTCDay: () => number;
            getHours: () => number;
            getUTCHours: () => number;
            getMinutes: () => number;
            getUTCMinutes: () => number;
            getSeconds: () => number;
            getUTCSeconds: () => number;
            getMilliseconds: () => number;
            getUTCMilliseconds: () => number;
            getTimezoneOffset: () => number;
            setTime: (time: number) => number;
            setMilliseconds: (ms: number) => number;
            setUTCMilliseconds: (ms: number) => number;
            setSeconds: (sec: number, ms?: number) => number;
            setUTCSeconds: (sec: number, ms?: number) => number;
            setMinutes: (min: number, sec?: number, ms?: number) => number;
            setUTCMinutes: (min: number, sec?: number, ms?: number) => number;
            setHours: (hours: number, min?: number, sec?: number, ms?: number) => number;
            setUTCHours: (hours: number, min?: number, sec?: number, ms?: number) => number;
            setDate: (date: number) => number;
            setUTCDate: (date: number) => number;
            setMonth: (month: number, date?: number) => number;
            setUTCMonth: (month: number, date?: number) => number;
            setFullYear: (year: number, month?: number, date?: number) => number;
            setUTCFullYear: (year: number, month?: number, date?: number) => number;
            toUTCString: () => string;
            toISOString: () => string;
            toJSON: (key?: any) => string;
            [Symbol.toPrimitive]: {
                (hint: "default"): string;
                (hint: "string"): string;
                (hint: "number"): number;
                (hint: string): string | number;
            };
        } | undefined;
        dayjs?: {
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
            set: (unit: dayjs.UnitType, value: number) => Dayjs;
            get: (unit: dayjs.UnitType) => number;
            add: (value: number, unit?: dayjs.ManipulateType) => Dayjs;
            subtract: (value: number, unit?: dayjs.ManipulateType) => Dayjs;
            startOf: (unit: dayjs.OpUnitType) => Dayjs;
            endOf: (unit: dayjs.OpUnitType) => Dayjs;
            format: (template?: string) => string;
            diff: (date?: dayjs.ConfigType, unit?: dayjs.QUnitType | dayjs.OpUnitType, float?: boolean) => number;
            valueOf: () => number;
            unix: () => number;
            daysInMonth: () => number;
            toDate: () => Date;
            toJSON: () => string;
            toISOString: () => string;
            toString: () => string;
            utcOffset: () => number;
            isBefore: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isSame: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isAfter: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            locale: {
                (): string;
                (preset: string | ILocale, object?: Partial<ILocale>): Dayjs;
            };
            localeData: () => dayjs.InstanceLocaleDataReturn;
            week: {
                (): number;
                (value: number): Dayjs;
            };
            weekYear: () => number;
            dayOfYear: {
                (): number;
                (value: number): Dayjs;
            };
            isSameOrAfter: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
            isSameOrBefore: (date?: dayjs.ConfigType, unit?: dayjs.OpUnitType) => boolean;
        } | undefined;
        type?: ("normal" | "week" | "today" | "prev-month" | "next-month") | undefined;
    }[][]>;
    tbodyRef: import("vue").Ref<HTMLElement | undefined>;
    currentCellRef: import("vue").Ref<HTMLElement | undefined>;
    focus: () => Promise<void | undefined>;
    isCurrent: (cell: DateCell) => boolean;
    isWeekActive: (cell: DateCell) => boolean;
    isSelectedCell: (cell: DateCell) => boolean | undefined;
    handlePickDate: (event: FocusEvent | MouseEvent, isKeyboardMovement?: boolean) => void;
    handleMouseUp: (event: MouseEvent) => void;
    handleMouseDown: (event: MouseEvent) => void;
    handleMouseMove: (event: MouseEvent) => void;
    handleFocus: (event: FocusEvent) => void;
};
export declare const useBasicDateTableDOM: (props: BasicDateTableProps, { isCurrent, isWeekActive, }: Pick<ReturnType<typeof useBasicDateTable>, "isCurrent" | "isWeekActive">) => {
    tableKls: import("vue").ComputedRef<(string | {
        'is-week-mode': boolean;
    })[]>;
    tableLabel: import("vue").ComputedRef<string>;
    weekLabel: import("vue").ComputedRef<string>;
    getCellClasses: (cell: DateCell) => string;
    getRowKls: (cell: DateCell) => (string | {
        current: boolean;
    })[];
    t: import("element-plus/es/hooks").Translator;
};
