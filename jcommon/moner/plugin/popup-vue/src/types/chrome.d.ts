declare const chrome: {
	tabs: {
		query: (queryInfo: any) => Promise<any[]>
		captureVisibleTab: () => Promise<string>
	}
	runtime: {
		sendMessage: (message: any, callback?: (response: any) => void) => void
		onMessage: {
			addListener: (callback: (message: any, sender: any, sendResponse: any) => void) => void
		}
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
}
