local M = {}

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
    -- Get the specified application
    local app = hs.application.get(appName)
    if not app then
        print("Application '" .. appName .. "' not found")
        return false
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
