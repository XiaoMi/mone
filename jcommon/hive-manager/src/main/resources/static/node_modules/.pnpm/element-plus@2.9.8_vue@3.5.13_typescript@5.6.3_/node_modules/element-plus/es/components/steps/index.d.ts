import Steps from './src/steps.vue';
import Step from './src/item.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElSteps: SFCWithInstall<typeof Steps> & {
    Step: typeof Step;
};
export default ElSteps;
export declare const ElStep: SFCWithInstall<typeof Step>;
export * from './src/item';
export * from './src/steps';
