import type { Ref } from 'vue';
export type ThrottleType = {
    leading?: number;
    trailing?: number;
    initVal?: boolean;
} | number;
export declare const useThrottleRender: (loading: Ref<boolean>, throttle?: ThrottleType) => Ref<boolean>;
