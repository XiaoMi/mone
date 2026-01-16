import Dropdown from './src/dropdown.vue';
import DropdownItem from './src/dropdown-item.vue';
import DropdownMenu from './src/dropdown-menu.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElDropdown: SFCWithInstall<typeof Dropdown> & {
    DropdownItem: typeof DropdownItem;
    DropdownMenu: typeof DropdownMenu;
};
export default ElDropdown;
export declare const ElDropdownItem: SFCWithInstall<typeof DropdownItem>;
export declare const ElDropdownMenu: SFCWithInstall<typeof DropdownMenu>;
export * from './src/dropdown';
export * from './src/instance';
export * from './src/tokens';
