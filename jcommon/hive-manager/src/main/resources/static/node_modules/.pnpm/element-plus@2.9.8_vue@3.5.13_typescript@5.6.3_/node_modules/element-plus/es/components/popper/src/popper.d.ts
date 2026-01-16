import type { ExtractPropTypes } from 'vue';
import type Popper from './popper.vue';
declare const effects: readonly ["light", "dark"];
declare const triggers: readonly ["click", "contextmenu", "hover", "focus"];
export declare const Effect: {
    readonly LIGHT: "light";
    readonly DARK: "dark";
};
export declare const roleTypes: readonly ["dialog", "grid", "group", "listbox", "menu", "navigation", "tooltip", "tree"];
export type PopperEffect = typeof effects[number] | (string & NonNullable<unknown>);
export type PopperTrigger = typeof triggers[number];
export declare const popperProps: {
    readonly role: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "dialog" | "menu" | "grid" | "listbox" | "tooltip" | "tree" | "group" | "navigation", unknown, "tooltip", boolean>;
};
export type PopperProps = ExtractPropTypes<typeof popperProps>;
export type PopperInstance = InstanceType<typeof Popper> & unknown;
/** @deprecated use `popperProps` instead, and it will be deprecated in the next major version */
export declare const usePopperProps: {
    readonly role: import("element-plus/es/utils").EpPropFinalized<StringConstructor, "dialog" | "menu" | "grid" | "listbox" | "tooltip" | "tree" | "group" | "navigation", unknown, "tooltip", boolean>;
};
/** @deprecated use `PopperProps` instead, and it will be deprecated in the next major version */
export type UsePopperProps = PopperProps;
export {};
