local M = {}

-- Enable Spotlight for name searches to improve application finding
hs.application.enableSpotlightForNameSearches(true)

-- 中文应用名到英文应用名/Bundle ID的映射表
local chineseAppNameMap = {
    ["钉钉"] = "DingTalk",
    ["微信"] = "WeChat",
    ["企业微信"] = "WXWork",
    ["飞书"] = "Lark",
    ["谷歌浏览器"] = "Google Chrome", 
    ["搜狗输入法"] = "SogouInput",
    ["迅雷"] = "Thunder",
    ["爱思助手"] = "iTools",
    ["腾讯会议"] = "TencentMeeting",
    ["QQ"] = "QQ",
    ["QQ音乐"] = "QQMusic",
    ["网易云音乐"] = "NeteaseMusic",
    ["有道词典"] = "YoudaoDict",
    ["百度网盘"] = "BaiduNetdisk",
    ["爱奇艺"] = "iQIYI",
    ["优酷"] = "Youku"
}

-- 递归查找函数
function M.findElementRecursive(element, role, attributes)
    if not element then
        return nil
    end
    -- 检查当前元素是否匹配
    if type(element.role) == 'function' and element:role() == role then
        local match = true
        if attributes then
            for key, value in pairs(attributes) do
                if type(element.attributeValue) == 'function' and element:attributeValue(key) ~= value then
                    match = false
                    break
                end
            end
        end
        if match then
            return element
        end
    end
    -- 递归查找子元素
    if type(element.children) == 'function' then
        local children = element:children()
        if children then
            for _, child in ipairs(children) do
                local foundElement = M.findElementRecursive(child, role, attributes)
                if foundElement then
                    return foundElement
                end
            end
        end
    end
    return nil
end

-- Open an application by name or bundle ID
-- If app is not running, it will be launched
-- If app is already running, it will NOT be brought to foreground
-- @param appNameOrBundleID String name or bundle ID of the application to open
-- @param isBundleID Boolean (optional) if true, the first parameter is treated as bundle ID
-- @return Boolean true if successful, false otherwise
function M.openApp(appNameOrBundleID, isBundleID)
    local originalName = appNameOrBundleID
    
    -- 检查是否是中文名称，如果是，转换为英文名称
    if not isBundleID and chineseAppNameMap[appNameOrBundleID] then
        appNameOrBundleID = chineseAppNameMap[appNameOrBundleID]
        print("转换应用名: " .. originalName .. " -> " .. appNameOrBundleID)
    end
    
    -- First check if the app is already running
    local app
    
    if isBundleID then
        app = hs.application.get(appNameOrBundleID)
    else
        -- Try to get by name first
        app = hs.application.get(appNameOrBundleID)
        
        -- If not found, try to find any app containing this name
        if not app then
            local allApps = hs.application.runningApplications()
            for _, runningApp in ipairs(allApps) do
                local name = runningApp:name()
                if name and string.find(string.lower(name), string.lower(appNameOrBundleID)) then
                    app = runningApp
                    print("Found partial match: " .. name)
                    break
                end
            end
        end
    end
    
    if app then
        -- App is running, just confirm and return
        print("Application '" .. app:name() .. "' is already running")
        return true
    else
        -- App is not running, try multiple methods to launch it
        
        -- Method 1: Use launchOrFocus (we'll use this even though it might focus,
        -- since we need to launch and it's the most reliable way)
        local success = hs.application.launchOrFocus(appNameOrBundleID)
        
        -- Method 2: If not successful and it's not a bundle ID, try to find using Spotlight
        if not success and not isBundleID then
            -- Get all application paths from Spotlight
            local script = [[
                tell application "System Events"
                    set appList to name of every application process
                end tell
                return appList
            ]]
            
            local ok, appList = hs.osascript.applescript(script)
            if ok then
                print("Available applications:")
                for _, name in ipairs(appList) do
                    print("  - " .. name)
                    if string.find(string.lower(name), string.lower(appNameOrBundleID)) then
                        print("Trying to launch: " .. name)
                        success = hs.application.launchOrFocus(name)
                        if success then break end
                    end
                end
            end
        end
        
        -- Method 3: Try with hs.application.open (works better with bundle IDs)
        if not success then
            app = hs.application.open(appNameOrBundleID)
            success = (app ~= nil)
        end
        
        if success then
            print("Application '" .. originalName .. "' launched")
            return true
        else
            print("Failed to launch application '" .. originalName .. "'")
            print("Try using the exact application name or bundle ID (e.g. 'com.apple.Safari')")
            return false
        end
    end
end

-- Open and activate an application by name or bundle ID
-- If app is already running, it will be brought to the foreground
-- Otherwise, it will be launched
-- @param appNameOrBundleID String name or bundle ID of the application to open
-- @param isBundleID Boolean (optional) if true, the first parameter is treated as bundle ID
-- @return Boolean true if successful, false otherwise
function M.openAndActivateApp(appNameOrBundleID, isBundleID)
    local originalName = appNameOrBundleID
    
    -- 检查是否是中文名称，如果是，转换为英文名称
    if not isBundleID and chineseAppNameMap[appNameOrBundleID] then
        appNameOrBundleID = chineseAppNameMap[appNameOrBundleID]
        print("转换应用名: " .. originalName .. " -> " .. appNameOrBundleID)
    end
    
    -- First check if the app is already running
    local app
    
    if isBundleID then
        app = hs.application.get(appNameOrBundleID)
    else
        -- Try to get by name first
        app = hs.application.get(appNameOrBundleID)
        
        -- If not found, try to find any app containing this name
        if not app then
            local allApps = hs.application.runningApplications()
            for _, runningApp in ipairs(allApps) do
                local name = runningApp:name()
                if name and string.find(string.lower(name), string.lower(appNameOrBundleID)) then
                    app = runningApp
                    print("Found partial match: " .. name)
                    break
                end
            end
        end
    end
    
    if app then
        -- App is running, activate it
        app:activate()
        print("Application '" .. app:name() .. "' activated")
        return true
    else
        -- Same launch logic as openApp
        local success = hs.application.launchOrFocus(appNameOrBundleID)
        
        if not success and not isBundleID then
            local script = [[
                tell application "System Events"
                    set appList to name of every application process
                end tell
                return appList
            ]]
            
            local ok, appList = hs.osascript.applescript(script)
            if ok then
                for _, name in ipairs(appList) do
                    if string.find(string.lower(name), string.lower(appNameOrBundleID)) then
                        print("Trying to launch: " .. name)
                        success = hs.application.launchOrFocus(name)
                        if success then break end
                    end
                end
            end
        end
        
        if not success then
            app = hs.application.open(appNameOrBundleID)
            success = (app ~= nil)
        end
        
        if success then
            print("Application '" .. originalName .. "' launched and activated")
            return true
        else
            print("Failed to launch application '" .. originalName .. "'")
            return false
        end
    end
end

-- Open an application by bundle ID
-- Wrapper around openApp with isBundleID set to true
-- @param bundleID String bundle ID of the application
-- @return Boolean true if successful, false otherwise
function M.openAppByBundleID(bundleID)
    return M.openApp(bundleID, true)
end

return M
