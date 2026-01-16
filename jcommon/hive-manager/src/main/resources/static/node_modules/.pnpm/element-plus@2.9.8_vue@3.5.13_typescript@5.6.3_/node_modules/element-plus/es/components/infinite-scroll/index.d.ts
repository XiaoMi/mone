import InfiniteScroll from './src';
import type { SFCWithInstall } from 'element-plus/es/utils';
declare const _InfiniteScroll: SFCWithInstall<typeof InfiniteScroll>;
export default _InfiniteScroll;
export declare const ElInfiniteScroll: SFCWithInstall<import("vue").ObjectDirective<HTMLElement & {
    ElInfiniteScroll: {
        container: HTMLElement | Window;
        containerEl: HTMLElement;
        instance: import("vue").ComponentPublicInstance;
        delay: number;
        lastScrollTop: number;
        cb: () => void;
        onScroll: () => void;
        observer?: MutationObserver;
    };
}, () => void>>;
