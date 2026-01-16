import { LocalStorage } from '../adapters/browser/LocalStorage.js';
import { SessionStorage } from '../adapters/browser/SessionStorage.js';
import { LowSync } from '../index.js';
export function LocalStoragePreset(key, defaultData) {
    const adapter = new LocalStorage(key);
    const db = new LowSync(adapter, defaultData);
    db.read();
    return db;
}
export function SessionStoragePreset(key, defaultData) {
    const adapter = new SessionStorage(key);
    const db = new LowSync(adapter, defaultData);
    db.read();
    return db;
}
