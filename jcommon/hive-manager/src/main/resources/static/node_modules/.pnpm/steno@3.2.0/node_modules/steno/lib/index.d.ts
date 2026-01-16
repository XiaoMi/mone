/// <reference types="node" resolution-mode="require"/>
/// <reference types="node" resolution-mode="require"/>
import { PathLike } from 'node:fs';
import { writeFile } from 'node:fs/promises';
type Data = Parameters<typeof writeFile>[1];
export declare class Writer {
    #private;
    constructor(filename: PathLike);
    write(data: Data): Promise<void>;
}
export {};
