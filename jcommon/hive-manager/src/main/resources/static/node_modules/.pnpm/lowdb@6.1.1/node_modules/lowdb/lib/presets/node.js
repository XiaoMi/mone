import { Memory, MemorySync } from '../adapters/Memory.js';
import { JSONFile, JSONFileSync } from '../adapters/node/JSONFile.js';
import { Low, LowSync } from '../core/Low.js';
export async function JSONPreset(filename, defaultData) {
    const adapter = process.env.NODE_ENV === 'test'
        ? new Memory()
        : new JSONFile(filename);
    const db = new Low(adapter, defaultData);
    await db.read();
    return db;
}
export function JSONSyncPreset(filename, defaultData) {
    const adapter = process.env.NODE_ENV === 'test'
        ? new MemorySync()
        : new JSONFileSync(filename);
    const db = new LowSync(adapter, defaultData);
    db.read();
    return db;
}
