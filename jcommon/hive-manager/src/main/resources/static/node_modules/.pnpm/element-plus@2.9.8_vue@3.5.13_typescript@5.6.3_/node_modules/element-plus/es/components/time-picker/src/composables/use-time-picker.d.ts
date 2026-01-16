import type { Dayjs } from 'dayjs';
import type { GetDisabledHours, GetDisabledMinutes, GetDisabledSeconds } from '../common/props';
export declare const getTimeLists: (disabledHours?: GetDisabledHours, disabledMinutes?: GetDisabledMinutes, disabledSeconds?: GetDisabledSeconds) => {
    getHoursList: (role: string, compare?: Dayjs) => boolean[];
    getMinutesList: (hour: number, role: string, compare?: Dayjs) => boolean[];
    getSecondsList: (hour: number, minute: number, role: string, compare?: Dayjs) => boolean[];
};
export declare const buildAvailableTimeSlotGetter: (disabledHours: GetDisabledHours, disabledMinutes: GetDisabledMinutes, disabledSeconds: GetDisabledSeconds) => {
    getAvailableHours: GetDisabledHours;
    getAvailableMinutes: GetDisabledMinutes;
    getAvailableSeconds: GetDisabledSeconds;
};
export declare const useOldValue: (props: {
    parsedValue?: string | Dayjs | Dayjs[];
    visible: boolean;
}) => import("vue").Ref<string | {
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
} | {
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
}[] | undefined>;
