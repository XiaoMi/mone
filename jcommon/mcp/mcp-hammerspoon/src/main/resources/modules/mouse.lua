local M = {}
local utils = require("modules.utils")  -- Add this to import the utils module

function M.moveAndClick(x, y, width, height)
    local targetX, targetY
    
    if width and height then
        -- 如果提供了宽高，计算区域的中心点
        targetX = x + (width / 2)
        targetY = y + (height / 2)
    else
        -- 如果没有提供宽高，直接使用给定坐标
        targetX = x
        targetY = y
    end

    -- 移动鼠标到目标位置
    hs.mouse.absolutePosition({x=targetX, y=targetY})

    -- 等待一小段时间确保鼠标已经移动到位
    hs.timer.usleep(100000)  -- 100ms

    -- 模拟鼠标左键点击
    hs.eventtap.leftClick({x=targetX, y=targetY})

    return targetX, targetY -- 返回实际点击的坐标
end

function M.moveToApp(appName, x, y)
    -- 创建英文到中文的反向映射表（基于utils中的chineseAppNameMap）
    local reversedNameMap = {}
    
    -- 安全地访问 chineseAppNameMap，如果它不存在则使用空表
    local nameMap = utils.chineseAppNameMap or {}
    for chineseName, englishName in pairs(nameMap) do
        reversedNameMap[englishName] = chineseName
    end
    
    -- 检查是否是英文名，如果是则尝试转换为中文名
    local effectiveName = reversedNameMap[appName] or appName
    if effectiveName ~= appName then
        print("将应用名从 '" .. appName .. "' 转换为 '" .. effectiveName .. "'")
    end
    
    -- 尝试获取应用引用
    local app = hs.application.get(effectiveName)
    
    -- 如果找不到应用，尝试启动它
    if not app then
        print("Application '" .. appName .. "' not found, attempting to open it")
        
        -- 使用utils.openApp来打开应用
        if utils.openApp(appName) then
            -- 等待应用启动
            hs.timer.usleep(1000000)  -- 1秒延迟
            
            -- 再次尝试获取应用引用，使用转换后的名称
            app = hs.application.get(effectiveName)
            
            -- 如果还找不到，尝试部分匹配
            if not app then
                local allApps = hs.application.runningApplications()
                for _, runningApp in ipairs(allApps) do
                    local name = runningApp:name()
                    if name and (
                        string.find(string.lower(name), string.lower(appName)) or 
                        (effectiveName ~= appName and string.find(string.lower(name), string.lower(effectiveName)))
                    ) then
                        app = runningApp
                        print("找到部分匹配的应用: " .. name)
                        break
                    end
                end
            end
            
            if not app then
                print("应用'" .. appName .. "'已启动但无法获取引用")
                return false
            end
        else
            print("无法打开应用'" .. appName .. "'")
            return false
        end
    end
    
    -- Get the main window of the application
    local win = app:mainWindow()
    if not win then
        print("No main window found for '" .. appName .. "'")
        return false
    end
    
    -- Only activate the app if it's not already the frontmost application
    local currentApp = hs.application.frontmostApplication()
    if currentApp:bundleID() ~= app:bundleID() then
        app:activate()
        
        -- Small delay to ensure app is focused
        hs.timer.usleep(300000)  -- 300ms delay
    end
    
    -- Get window position and size (after focusing if needed)
    local frame = win:frame()
    
    -- Calculate absolute screen coordinates
    local absoluteX = frame.x + x
    local absoluteY = frame.y + y
    
    -- Move mouse to the calculated position
    hs.mouse.absolutePosition({x=absoluteX, y=absoluteY})
    print("Moved mouse to position " .. absoluteX .. "," .. absoluteY .. " in app '" .. appName .. "'")
    
    return true
end

-- Combine the two functions to move to app position and click
function M.moveToAppAndClick(appName, x, y)
    if M.moveToApp(appName, x, y) then
        -- Wait a moment to ensure mouse is positioned
        hs.timer.usleep(100000)  -- 100ms
        
        -- Perform the click at current position
        local pos = hs.mouse.absolutePosition()
        hs.eventtap.leftClick(pos)
        return true
    end
    return false
end

return M
