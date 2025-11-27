declare global {
  interface Window {
    showErrorCode: (data: string) => void;
    decodeURIComponent: (str: string) => string;
  }
}

export {};