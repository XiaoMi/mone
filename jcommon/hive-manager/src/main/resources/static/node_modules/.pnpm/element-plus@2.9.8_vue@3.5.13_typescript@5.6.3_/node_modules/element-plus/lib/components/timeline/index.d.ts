import Timeline from './src/timeline';
import TimelineItem from './src/timeline-item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElTimeline: SFCWithInstall<typeof Timeline> & {
    TimelineItem: typeof TimelineItem;
};
export default ElTimeline;
export declare const ElTimelineItem: SFCWithInstall<typeof TimelineItem>;
export * from './src/timeline';
export * from './src/timeline-item';
