function checkArgs(adapter, defaultData) {
    if (adapter === undefined)
        throw new Error('lowdb: missing adapter');
    if (defaultData === undefined)
        throw new Error('lowdb: missing default data');
}
export class Low {
    adapter;
    data;
    constructor(adapter, defaultData) {
        checkArgs(adapter, defaultData);
        this.adapter = adapter;
        this.data = defaultData;
    }
    async read() {
        const data = await this.adapter.read();
        if (data)
            this.data = data;
    }
    async write() {
        if (this.data)
            await this.adapter.write(this.data);
    }
}
export class LowSync {
    adapter;
    data;
    constructor(adapter, defaultData) {
        checkArgs(adapter, defaultData);
        this.adapter = adapter;
        this.data = defaultData;
    }
    read() {
        const data = this.adapter.read();
        if (data)
            this.data = data;
    }
    write() {
        if (this.data)
            this.adapter.write(this.data);
    }
}
