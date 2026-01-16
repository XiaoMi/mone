import Tabs from './src/tabs';
import TabPane from './src/tab-pane.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElTabs: SFCWithInstall<typeof Tabs> & {
    TabPane: typeof TabPane;
};
export declare const ElTabPane: SFCWithInstall<typeof TabPane>;
export default ElTabs;
export * from './src/tabs';
export * from './src/tab-bar';
export * from './src/tab-nav';
export * from './src/tab-pane';
export * from './src/constants';
