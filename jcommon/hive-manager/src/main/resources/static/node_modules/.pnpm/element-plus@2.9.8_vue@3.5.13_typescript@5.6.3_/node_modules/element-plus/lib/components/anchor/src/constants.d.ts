import type { InjectionKey, Ref } from 'vue';
import type { UseNamespaceReturn } from 'element-plus/es/hooks';
export interface AnchorLinkState {
    el: HTMLElement;
    href: string;
}
export interface AnchorContext {
    ns: UseNamespaceReturn;
    direction: string;
    currentAnchor: Ref<string>;
    addLink(state: AnchorLinkState): void;
    removeLink(href: string): void;
    handleClick(e: MouseEvent, href?: string): void;
}
export declare const anchorKey: InjectionKey<AnchorContext>;
