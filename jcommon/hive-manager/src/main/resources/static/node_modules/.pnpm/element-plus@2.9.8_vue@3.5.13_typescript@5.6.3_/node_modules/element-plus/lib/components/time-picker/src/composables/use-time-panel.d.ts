import type { Dayjs } from 'dayjs';
import type { GetDisabledHours, GetDisabledMinutes, GetDisabledSeconds } from '../common/props';
type UseTimePanelProps = {
    getAvailableHours: GetDisabledHours;
    getAvailableMinutes: GetDisabledMinutes;
    getAvailableSeconds: GetDisabledSeconds;
};
export declare const useTimePanel: ({ getAvailableHours, getAvailableMinutes, getAvailableSeconds, }: UseTimePanelProps) => {
    timePickerOptions: Record<string, (...args: any[]) => void>;
    getAvailableTime: (date: Dayjs, role: string, first: boolean, compareDate?: Dayjs) => Dayjs;
    onSetOption: ([key, val]: [string, (...args: any[]) => void]) => void;
};
export {};
