export interface MarkStates {
    start: number;
    end: number;
    node: HTMLAnchorElement;
}
export declare enum logErrorType {
    info = "info",
    warning = "warning",
    error = "error"
}
export type logErrorTypes = keyof typeof logErrorType;
/**
 * Get Local time, format:  HH:mm:ss
 */
export declare function getLocalTime(): string;
/**
 * Create a clickable link (A tag), such as downloading the full log.
 */
export declare function createLinkMark(attrs: {
    download?: any;
    href?: string;
    hrefLang?: string;
    media?: string;
    rel?: string;
    target?: string;
    type?: string;
    [key: string]: any;
}): string;
/**
 * Get all linkMark.
 */
export declare function getLinkMarks(value: string): MarkStates[];
/**
 * Create a controllable log output type.
 * @param { string } text - contents
 * @param { string } type - Log type: 'info' | 'warning' | 'error'
 */
export declare function createLogMark(text?: string, type?: logErrorTypes): string;
/**
 * Gets the text of the current tag and returns an array of nodes.
 *
 */
export declare function getLogMark(value: string): MarkStates[];
/**
 * Create log text with time and type
 * @param { string } log - Log contents
 * @param { string } type - Log type: 'info' | 'warning' | 'error'
 * @example
 *
 * createLog("info content", "info")
 * // => [14:02:32] <info> info content
 *
 */
export declare function createLog(log: string, type: logErrorTypes): string;
/**
 * Create a custom format title.
 * @param { string } title - title value
 * @param { Number } symbolLength - Customize the number of symbols on both sides of the title. The default value is 15.
 * @param { string } symbol - Customize the symbols on both sides of the title. The default value is "="
 * @example
 *
 * createTitle("base title")
 * // => ===============base title===============
 *
 * createTitle("base title",3)
 * // => ===base title===
 *
 * createTitle("base title", 3, "*")
 * // => ***base title***
 */
export declare function createTitle(title: string, symbolLength?: number, symbol?: string): string;
