await chrome.scripting.executeScript({
    target: { tabId },
    files: ['managers/errorManager.js']
});

await chrome.scripting.executeScript({
    target: { tabId },
    files: ['managers/scrollManager.js']
});

await chrome.scripting.executeScript({
    target: { tabId },
    files: ['managers/actionManager.js']
}); 