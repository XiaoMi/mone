import type { Ref } from 'vue';
import type ElSelect from 'element-plus/es/components/select';
import type { TreeInstance } from 'element-plus/es/components/tree';
export declare const useTree: (props: any, { attrs, slots, emit }: {
    attrs: any;
    slots: any;
    emit: any;
}, { select, tree, key, }: {
    select: Ref<InstanceType<typeof ElSelect> | undefined>;
    tree: Ref<TreeInstance | undefined>;
    key: Ref<string>;
}) => any;
