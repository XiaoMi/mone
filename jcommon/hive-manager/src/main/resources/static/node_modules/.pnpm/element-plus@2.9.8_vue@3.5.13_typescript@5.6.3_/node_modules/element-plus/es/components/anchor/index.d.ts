import Anchor from './src/anchor.vue';
import AnchorLink from './src/anchor-link.vue';
import type { SFCWithInstall } from 'element-plus/es/utils';
export declare const ElAnchor: SFCWithInstall<typeof Anchor> & {
    AnchorLink: typeof AnchorLink;
};
export declare const ElAnchorLink: SFCWithInstall<typeof AnchorLink>;
export default ElAnchor;
export * from './src/anchor';
