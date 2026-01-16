import { WebStorage } from './WebStorage.js';
export class LocalStorage extends WebStorage {
    constructor(key) {
        super(key, localStorage);
    }
}
