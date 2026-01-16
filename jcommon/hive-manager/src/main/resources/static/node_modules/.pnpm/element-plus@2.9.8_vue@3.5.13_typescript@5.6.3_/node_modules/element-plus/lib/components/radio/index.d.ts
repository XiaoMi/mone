import Radio from './src/radio.vue';
import RadioButton from './src/radio-button.vue';
import RadioGroup from './src/radio-group.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElRadio: SFCWithInstall<typeof Radio> & {
    RadioButton: typeof RadioButton;
    RadioGroup: typeof RadioGroup;
};
export default ElRadio;
export declare const ElRadioGroup: SFCWithInstall<typeof RadioGroup>;
export declare const ElRadioButton: SFCWithInstall<typeof RadioButton>;
export * from './src/radio';
export * from './src/radio-group';
export * from './src/radio-button';
export * from './src/constants';
