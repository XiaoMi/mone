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

    print("app position " .. frame.x .. "," .. frame.y .. " in app '" .. appName .. "'")

    -- Adjust for window decorations (macOS typically has ~22px title bar)
    -- You might need to fine-tune this value based on your debugging
    local titleBarHeight = 0  -- Only for Y coordinate
    local leftBorderWidth = 40  -- For X coordinate
    
    -- Calculate absolute screen coordinates
    local absoluteX = frame.x + x + leftBorderWidth
    local absoluteY = frame.y + y + titleBarHeight
    
    -- Move mouse to the calculated position
    hs.mouse.absolutePosition({x=absoluteX, y=absoluteY})
    print("Moved mouse to position " .. absoluteX .. "," .. absoluteY .. " in app '" .. appName .. "'")
    
    return true
end


function M.moveToAppAbsolutely(appName, x, y)
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

    print("app position " .. frame.x .. "," .. frame.y .. " in app '" .. appName .. "'")

    -- Adjust for window decorations (macOS typically has ~22px title bar)
    -- You might need to fine-tune this value based on your debugging
    local titleBarHeight = 0  -- Only for Y coordinate
    local leftBorderWidth = 40  -- For X coordinate
    
    -- Calculate absolute screen coordinates
    local absoluteX = x
    local absoluteY = y
    
    -- Move mouse to the calculated position
    hs.mouse.absolutePosition({x=absoluteX, y=absoluteY})
    print("Moved mouse to position " .. absoluteX .. "," .. absoluteY .. " in app '" .. appName .. "'")
    
    return true
end

-- Combine the two functions to move to app position and click
function M.moveToAppAndClick(appName, x, y)
    -- if M.moveToApp(appName, x, y) then
    if M.moveToAppAbsolutely(appName, x, y) then
        -- Wait a moment to ensure mouse is positioned
        hs.timer.usleep(500000)  -- 500ms
        
        -- Perform the click at current position
        local pos = hs.mouse.absolutePosition()
        hs.eventtap.leftClick(pos)
        return true
    end
    return false
end

------------

-- 将图片中的坐标转换为应用窗口中的鼠标坐标
function M.imageToWindowCoords(appName, imageX, imageY, imageWidth, imageHeight, options)
    options = options or {}
    local isContentOnly = options.contentOnly or false  -- 图片是否只包含窗口内容区域
    local offsetX = options.offsetX or 0  -- 手动X轴偏移量
    local offsetY = options.offsetY or 0  -- 手动Y轴偏移量
    local debug = options.debug or true   -- 是否输出调试信息
    
    -- 获取应用窗口
    local app = hs.application.get(appName)
    if not app then
        print("找不到应用 '" .. appName .. "'")
        return nil, nil
    end

    local win = app:mainWindow()
    if not win then
        print("应用 '" .. appName .. "' 没有主窗口")
        return nil, nil
    end

    -- 获取窗口信息
    local frame = win:frame()
    local windowWidth = frame.w
    local windowHeight = frame.h
    
    if debug then
        print(string.format("窗口尺寸: %d x %d", windowWidth, windowHeight))
        print(string.format("图像尺寸: %d x %d", imageWidth, imageHeight))
        print(string.format("图像目标点: %d, %d", imageX, imageY))
    end
    
    -- 估算标题栏高度 (如果需要)
    local titleBarHeight = 0
    if isContentOnly then
        -- 标题栏高度因应用而异，macOS通常为22-28像素
        titleBarHeight = options.titleBarHeight or 22
        if debug then print(string.format("标题栏高度: %d", titleBarHeight)) end
    end
    
    -- 计算有效内容区域
    local contentWidth = windowWidth
    local contentHeight = windowHeight - (isContentOnly and 0 or titleBarHeight)
    
    -- 计算比例 (考虑图片可能只是窗口的一部分)
    local scaleX = contentWidth / imageWidth
    local scaleY = contentHeight / imageHeight
    
    -- 如果提供了自定义的比例，则使用自定义比例
    scaleX = options.scaleX or scaleX
    scaleY = options.scaleY or scaleY
    
    if debug then print(string.format("缩放比例: %.4f, %.4f", scaleX, scaleY)) end
    
    -- 转换坐标
    local windowX = imageX * scaleX + offsetX
    local windowY = imageY * scaleY + offsetY
    
    -- 如果图片不包含标题栏，需加上标题栏的高度
    if isContentOnly then
        windowY = windowY + titleBarHeight
    end
    
    if debug then print(string.format("窗口相对坐标: %.1f, %.1f", windowX, windowY)) end
    
    -- 计算绝对屏幕坐标
    local absoluteX = frame.x + windowX
    local absoluteY = frame.y + windowY
    
    if debug then print(string.format("屏幕绝对坐标: %.1f, %.1f", absoluteX, absoluteY)) end
    
    return absoluteX, absoluteY
end

-- 基于图片识别的点击功能
function M.clickOnImageTarget(appName, imageX, imageY, imageWidth, imageHeight, options)
    options = options or {}
    
    -- 转换坐标
    local absoluteX, absoluteY = M.imageToWindowCoords(appName, imageX, imageY, imageWidth, imageHeight, options)

    if not absoluteX or not absoluteY then
        print("无法转换坐标")
        return false
    end

    -- 确保应用在前台
    local app = hs.application.get(appName)
    if app then
        app:activate()
        hs.timer.usleep(300000)  -- 300ms延迟确保应用激活
    end

    -- 移动鼠标并点击
    hs.mouse.absolutePosition({x=absoluteX, y=absoluteY})
    hs.timer.usleep(100000)  -- 100ms等待鼠标移动到位
    hs.eventtap.leftClick({x=absoluteX, y=absoluteY})

    print("在坐标 " .. absoluteX .. "," .. absoluteY .. " 点击了图片目标")
    return true
end

-- 新增一个简单的通用函数，从任意截图映射到屏幕坐标

function M.clickOnScreenshot(screenshotPath, x, y, options)
    options = options or {}
    local debug = options.debug ~= false  -- 默认开启调试输出
    
    if not screenshotPath or not hs.fs.attributes(screenshotPath) then
        print("错误: 截图文件不存在: " .. tostring(screenshotPath))
        return false
    end
    
    -- 1. 获取截图信息
    local imgInfo = io.popen("sips -g pixelWidth -g pixelHeight " .. screenshotPath):read("*all")
    local imageWidth = tonumber(imgInfo:match("pixelWidth: (%d+)"))
    local imageHeight = tonumber(imgInfo:match("pixelHeight: (%d+)"))
    
    if not imageWidth or not imageHeight then
        print("错误: 无法获取图片尺寸")
        return false
    end
    
    if debug then
        print(string.format("截图尺寸: %d x %d", imageWidth, imageHeight))
        print(string.format("目标点: %d, %d", x, y))
    end
    
    -- 2. 查找应用窗口 (如果指定)
    local targetApp = options.appName and hs.application.get(options.appName)
    
    -- 3. 确定目标坐标
    local absoluteX, absoluteY
    
    -- 如果提供了目标应用，尝试将图像映射到应用窗口
    if targetApp and targetApp:mainWindow() then
        local win = targetApp:mainWindow()
        local frame = win:frame()
        
        -- 激活应用
        targetApp:activate()
        hs.timer.usleep(300000)  -- 300ms等待激活
        
        -- 获取屏幕信息 (处理Retina显示器)
        local screen = win:screen()
        local screenFrame = screen:frame()
        local screenFullFrame = screen:fullFrame()
        local dpiScale = screenFullFrame.w / screenFrame.w  -- 计算DPI缩放因子
        
        if debug then
            print(string.format("DPI缩放因子: %.2f", dpiScale))
            print(string.format("窗口位置: %.1f, %.1f", frame.x, frame.y))
            print(string.format("窗口尺寸: %d x %d", frame.w, frame.h))
        end
        
        -- 调整窗口尺寸计算 (考虑DPI缩放和全屏模式)
        local effectiveWinWidth = options.forceWindowWidth or frame.w
        local effectiveWinHeight = options.forceWindowHeight or frame.h
        
        -- 如果窗口明显小于屏幕尺寸，可能是全屏但报告错误
        if effectiveWinWidth < screenFrame.w * 0.9 and options.assumeFullScreen then
            if debug then print("假设窗口是全屏的，调整尺寸") end
            effectiveWinWidth = screenFrame.w
            effectiveWinHeight = screenFrame.h
        end
        
        -- 计算缩放比例，考虑内容区域可能小于窗口
        local contentMarginTop = options.contentMarginTop or 0
        local contentMarginLeft = options.contentMarginLeft or 0
        local contentWidth = options.contentWidth or effectiveWinWidth
        local contentHeight = options.contentHeight or (effectiveWinHeight - contentMarginTop)
        
        if debug then
            print(string.format("有效内容区域: x=%d, y=%d, w=%d, h=%d", 
                contentMarginLeft, contentMarginTop, contentWidth, contentHeight))
        end
        
        -- 计算比例 (从图像坐标到窗口坐标)
        local scaleX = contentWidth / imageWidth
        local scaleY = contentHeight / imageHeight
        
        -- 应用手动缩放修正 (如果提供)
        if options.scaleX then scaleX = options.scaleX end  -- 直接替换而非乘以
        if options.scaleY then scaleY = options.scaleY end  -- 直接替换而非乘以
        
        if debug then
            print(string.format("计算缩放比例: %.4f, %.4f", scaleX, scaleY))
        end
        
        -- 计算绝对坐标 (相对于屏幕)
        absoluteX = frame.x + contentMarginLeft + (x * scaleX) + (options.offsetX or 0)
        absoluteY = frame.y + contentMarginTop + (y * scaleY) + (options.offsetY or 0)
        
        if debug then
            print(string.format("计算出的屏幕坐标: %.1f, %.1f", absoluteX, absoluteY))
        end
    else
        -- 如果没有指定应用或找不到应用窗口，使用备选方法
        print("未指定应用或找不到应用窗口，使用全屏模式")
        
        -- 获取主屏幕信息
        local screen = hs.screen.mainScreen()
        local screenFrame = screen:frame()
        
        if options.region then
            -- 如果提供了搜索区域，使用给定区域
            local region = options.region
            absoluteX = region.x + (x * (region.w / imageWidth)) + (options.offsetX or 0)
            absoluteY = region.y + (y * (region.h / imageHeight)) + (options.offsetY or 0)
        else
            -- 假设截图是在屏幕中心，计算区域位置
            local regionX = screenFrame.x + (screenFrame.w / 2) - (imageWidth / 2)
            local regionY = screenFrame.y + (screenFrame.h / 2) - (imageHeight / 2)
            
            absoluteX = regionX + x + (options.offsetX or 0)
            absoluteY = regionY + y + (options.offsetY or 0)
        end
    end
    
    print(string.format("最终屏幕坐标: %.1f, %.1f", absoluteX, absoluteY))
    
    -- 4. 移动鼠标并点击
    hs.mouse.absolutePosition({x=absoluteX, y=absoluteY})
    hs.timer.usleep(100000)  -- 100ms等待鼠标移动


    -- 仅在明确要求时点击 (否则只移动鼠标)
    -- if options.click ~= false then
    --     hs.eventtap.leftClick({x=absoluteX, y=absoluteY})
    -- end
    
    return true, absoluteX, absoluteY
end

-- 高级版本: 使用图像匹配来查找目标区域
function M.findAndClickOnImage(targetImagePath, x, y, options)
    options = options or {}
    local debug = options.debug ~= false
    
    -- 这个函数需要外部图像匹配能力，例如使用hs.image.imageFromPath和OpenCV
    -- 或者通过调用Python脚本等方式实现
    
    if debug then
        print("查找图像: " .. targetImagePath)
        print("目标点: " .. x .. ", " .. y)
    end
    
    -- 此处应该实现图像查找逻辑
    -- 1. 截取当前屏幕
    -- 2. 在屏幕截图中搜索目标图像
    -- 3. 如果找到，计算目标点的绝对坐标
    -- 4. 移动鼠标并点击
    
    -- 示例实现(假设使用外部工具)
    local success, result = pcall(function()
        -- 可以调用Python脚本执行图像匹配
        local cmd = string.format("python3 /path/to/image_finder.py '%s' %d %d", 
                                  targetImagePath, x, y)
        local output = hs.execute(cmd)
        
        -- 假设输出格式为 "x,y"
        local screenX, screenY = output:match("(%d+),(%d+)")
        return {x = tonumber(screenX), y = tonumber(screenY)}
    end)
    
    if not success or not result then
        print("错误: 无法找到匹配图像")
        return false
    end
    
    if debug then
        print(string.format("找到匹配: 屏幕坐标 %.1f, %.1f", result.x, result.y))
    end
    
    -- 移动鼠标并点击
    hs.mouse.absolutePosition({x=result.x, y=result.y})
    hs.timer.usleep(100000)  -- 100ms等待鼠标移动
    hs.eventtap.leftClick({x=result.x, y=result.y})
    
    return true
end


--------------

-- 显示鼠标坐标的悬浮窗
M.coordsDisplay = nil
M.coordsWatcher = nil
M.isShowingCoords = false

-- 实时显示鼠标坐标
function M.showMouseCoordinates(toggle)
    -- 如果调用时传入false，或者已经在显示坐标并没有传参数，则停止显示
    if toggle == false or (M.isShowingCoords and toggle == nil) then
        if M.coordsWatcher then
            M.coordsWatcher:stop()
            M.coordsWatcher = nil
        end
        
        if M.coordsDisplay then
            M.coordsDisplay:delete()
            M.coordsDisplay = nil
        end
        
        M.isShowingCoords = false
        print("停止显示鼠标坐标")
        return
    end
    
    -- 如果已经在显示，不需要重复初始化
    if M.isShowingCoords then return end
    
    -- 创建悬浮窗显示坐标
    M.coordsDisplay = hs.canvas.new({x=10, y=10, w=200, h=60})
    M.coordsDisplay:appendElements({
        type = "rectangle",
        action = "fill",
        fillColor = {alpha = 0.7, red = 0, green = 0, blue = 0},
        roundedRectRadii = {xRadius = 10, yRadius = 10},
    }, {
        type = "text",
        text = "鼠标坐标: 0, 0",
        textSize = 16,
        textColor = {white = 1.0},
        textAlignment = "center",
        frame = {x = 0, y = 5, w = 200, h = 50}
    })
    
    M.coordsDisplay:show()
    
    -- 创建鼠标移动事件监听器，实时更新坐标显示
    M.coordsWatcher = hs.timer.new(0.05, function()
        local pos = hs.mouse.absolutePosition()
        local x, y = math.floor(pos.x), math.floor(pos.y)
        
        -- 获取当前鼠标下方的窗口和应用信息
        local app = hs.application.applicationForPID(hs.window.focusedWindow():pid())
        local appName = app and app:name() or "未知应用"
        
        -- 更新显示内容
        M.coordsDisplay[2] = {
            type = "text",
            text = string.format("坐标: %d, %d\n应用: %s", x, y, appName),
            textSize = 14,
            textColor = {white = 1.0},
            textAlignment = "center",
            frame = {x = 0, y = 5, w = 200, h = 50}
        }
        
        -- 根据鼠标位置调整显示窗口位置，避免遮挡
        local screen = hs.mouse.getCurrentScreen():frame()
        local xPos = (x + 210 > screen.w) and (x - 210) or x + 10
        local yPos = (y + 70 > screen.h) and (y - 70) or y + 10
        
        M.coordsDisplay:topLeft({x=xPos, y=yPos})
    end)
    
    M.coordsWatcher:start()
    M.isShowingCoords = true
    print("开始显示鼠标坐标")
end

return M
