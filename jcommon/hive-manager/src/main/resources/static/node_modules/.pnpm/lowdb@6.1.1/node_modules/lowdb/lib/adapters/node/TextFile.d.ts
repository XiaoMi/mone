/// <reference types="node" resolution-mode="require"/>
import fs from 'node:fs';
import { Adapter, SyncAdapter } from '../../core/Low.js';
export declare class TextFile implements Adapter<string> {
    #private;
    constructor(filename: fs.PathLike);
    read(): Promise<string | null>;
    write(str: string): Promise<void>;
}
export declare class TextFileSync implements SyncAdapter<string> {
    #private;
    constructor(filename: fs.PathLike);
    read(): string | null;
    write(str: string): void;
}
