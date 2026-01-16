/// <reference types="node" resolution-mode="require"/>
import fs from 'node:fs';
import { Low, LowSync } from '../core/Low.js';
export declare function JSONPreset<Data>(filename: fs.PathLike, defaultData: Data): Promise<Low<Data>>;
export declare function JSONSyncPreset<Data>(filename: fs.PathLike, defaultData: Data): LowSync<Data>;
