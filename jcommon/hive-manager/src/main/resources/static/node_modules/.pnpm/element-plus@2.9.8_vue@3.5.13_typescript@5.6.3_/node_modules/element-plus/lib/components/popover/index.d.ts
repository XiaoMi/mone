import Popover from './src/popover.vue';
import PopoverDirective from './src/directive';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElPopoverDirective: SFCWithInstall<typeof PopoverDirective>;
export declare const ElPopover: SFCWithInstall<typeof Popover> & {
    directive: typeof ElPopoverDirective;
};
export default ElPopover;
export * from './src/popover';
