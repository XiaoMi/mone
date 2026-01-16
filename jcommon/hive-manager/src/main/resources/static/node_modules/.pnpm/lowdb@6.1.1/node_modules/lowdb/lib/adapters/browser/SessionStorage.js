import { WebStorage } from './WebStorage.js';
export class SessionStorage extends WebStorage {
    constructor(key) {
        super(key, sessionStorage);
    }
}
