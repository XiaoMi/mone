declare const chrome: {
	tabs: {
		query: (queryInfo: any) => Promise<any[]>
		captureVisibleTab: () => Promise<string>
		create: (options: { url: string }) => Promise<any>
	}
	runtime: {
		sendMessage: (message: any, callback?: (response: any) => void) => void
		onMessage: {
			addListener: (callback: (message: any, sender: any, sendResponse: any) => void) => void
		}
		id: string
		getURL: (path: string) => string
	}
	scripting: {
		executeScript: (details: any) => Promise<any>
	}
	windows: {
		create: (options: any) => Promise<any>
	}
	sidePanel: {
		close: () => void
	}
	storage: {
		local: {
			get: (keys?: string | string[] | null) => Promise<any>
			set: (items: { [key: string]: any }) => Promise<void>
		}
	}
}
