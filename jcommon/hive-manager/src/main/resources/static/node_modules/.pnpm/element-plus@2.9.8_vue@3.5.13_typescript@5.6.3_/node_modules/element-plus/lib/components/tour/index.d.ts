import Tour from './src/tour.vue';
import TourStep from './src/step.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElTour: SFCWithInstall<typeof Tour> & {
    TourStep: typeof TourStep;
};
export declare const ElTourStep: SFCWithInstall<typeof TourStep>;
export default ElTour;
export * from './src/tour';
export * from './src/step';
export * from './src/content';
export type { TourMask, TourGap, TourBtnProps } from './src/types';
