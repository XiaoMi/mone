// Global type declarations

declare global {
  interface Window {
    toggleFullScreen: (element: HTMLElement) => void
    markElements: (configs: any) => void
    buildDomTree: () => any
  }

  interface HTMLElement {
    requestFullscreen?: () => Promise<void>
  }

  const mermaid: {
    initialize: (config: any) => void
    init: (config: any, selector?: string | NodeListOf<Element>, callback?: (id: string) => void, errorCallback?: (id: string, error: any) => void) => void
  }
}

// Component refs extensions
interface HTMLElement {
  stopRead?: () => void
  cleanup?: () => void
  pasteFileList?: File[]
}

// Vue component ref extensions
interface ComponentPublicInstance {
  pasteFileList?: File[]
}

export {}
