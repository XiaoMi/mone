import Button from './src/button.vue';
import ButtonGroup from './src/button-group.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElButton: SFCWithInstall<typeof Button> & {
    ButtonGroup: typeof ButtonGroup;
};
export declare const ElButtonGroup: SFCWithInstall<typeof ButtonGroup>;
export default ElButton;
export * from './src/button';
export * from './src/constants';
export type { ButtonInstance, ButtonGroupInstance } from './src/instance';
