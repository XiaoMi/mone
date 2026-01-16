interface Options {
    /** Fixes IE by appending element to body */
    appendToBody: boolean;
}
declare const _default: (opts?: Options | undefined) => {
    toClipboard(text: string, container?: HTMLElement | undefined): Promise<unknown>;
};
export default _default;
