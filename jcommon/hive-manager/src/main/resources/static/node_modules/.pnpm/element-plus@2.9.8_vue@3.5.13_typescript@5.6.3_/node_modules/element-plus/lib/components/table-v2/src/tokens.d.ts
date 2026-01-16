import type { InjectionKey, Ref } from 'vue';
import type { UseNamespaceReturn } from 'element-plus/es/hooks';
export type TableV2Context = {
    isScrolling: Ref<boolean>;
    isResetting: Ref<boolean>;
    ns: UseNamespaceReturn;
};
export declare const TableV2InjectionKey: InjectionKey<TableV2Context>;
