import type { AnyDesc, DescEnum, DescExtension, DescFile, DescMessage, DescService } from "../descriptors.js";
/**
 * Iterate over all types - enumerations, extensions, services, messages -
 * and enumerations, extensions and messages nested in messages.
 */
export declare function nestedTypes(desc: DescFile | DescMessage): Iterable<DescMessage | DescEnum | DescExtension | DescService>;
/**
 * Returns the ancestors of a given Protobuf element, up to the file.
 */
export declare function parentTypes(desc: AnyDesc): Parent[];
type Parent = DescFile | DescEnum | DescMessage | DescService;
export {};
