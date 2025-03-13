local M = {}

-- 老虎证券应用名称和搜索框信息
local TIGER_APP_NAME = "老虎国际Pro"
-- local TIGER_APP_NAME = "Tiger Trade"

-- 参考屏幕分辨率 (16英寸MacBook Pro的标准分辨率)
local TIGER_BOX_DETAIL_W = 1728
local TIGER_BOX_DETAIL_H = 1117

-- 获取当前Mac的屏幕信息（尺寸与型号）
function M.getMacScreenInfo()
    -- 获取屏幕分辨率
    local mainScreen = hs.screen.mainScreen()
    local frame = mainScreen:frame()
    local width, height = frame.w, frame.h
    
    -- 获取屏幕完整尺寸（不考虑菜单栏和程序坞）
    local fullFrame = mainScreen:fullFrame()
    local fullWidth, fullHeight = fullFrame.w, fullFrame.h
    
    -- 获取屏幕模式（含缩放信息）
    local mode = mainScreen:currentMode()
    local scale = mode.scale or 1.0
    local dpi = mode.dpi or 0
    local depth = mode.depth or 0
    local refreshRate = mode.freq or 0
    
    -- 输出当前屏幕信息
    print("当前屏幕可见分辨率: " .. width .. "x" .. height)
    print("当前屏幕完整分辨率: " .. fullWidth .. "x" .. fullHeight)
    print("当前屏幕缩放比例: " .. scale)
    print("当前屏幕DPI: " .. dpi)
    print("当前屏幕色彩深度: " .. depth)
    print("当前屏幕刷新率: " .. refreshRate .. "Hz")
    
    -- 尝试获取Mac型号信息
    local model = ""
    local macModel = ""
    local macModelName = ""
    local f1 = io.popen("sysctl hw.model")
    if f1 then
        model = f1:read("*a") or ""
        f1:close()
    end
    
    -- 尝试获取更详细的型号名称
    local f2 = io.popen("system_profiler SPHardwareDataType | grep 'Model Name' | awk -F': ' '{print $2}'")
    if f2 then
        macModelName = f2:read("*a"):gsub("\n", "") or ""
        f2:close()
    end
    
    -- 尝试获取更详细的型号标识
    local f3 = io.popen("system_profiler SPHardwareDataType | grep 'Model Identifier' | awk -F': ' '{print $2}'")
    if f3 then
        macModel = f3:read("*a"):gsub("\n", "") or ""
        f3:close()
    end
    
    print("Mac型号标识: " .. macModel)
    print("Mac型号名称: " .. macModelName)
    
    -- 获取更多系统信息
    local osVersion = hs.host.operatingSystemVersion()
    print("操作系统版本: " .. osVersion.major .. "." .. osVersion.minor .. "." .. osVersion.patch)
    print("系统名称: " .. hs.host.operatingSystemVersionString())
    
    return width, height, scale, {
        fullWidth = fullWidth,
        fullHeight = fullHeight,
        dpi = dpi,
        refreshRate = refreshRate,
        macModel = macModel,
        macModelName = macModelName,
        osVersion = osVersion
    }
end

-- 获取屏幕尺寸（宽高）
-- @return width, height 屏幕的宽度和高度
function M.getScreenSize()
    local mainScreen = hs.screen.mainScreen()
    local frame = mainScreen:frame()
    local width, height = frame.w, frame.h
    
    print("当前屏幕尺寸: " .. width .. "x" .. height)
    return width, height
end

-- 获取屏幕的完整高度（包括菜单栏和程序坞）
-- @return fullHeight 屏幕的完整高度
function M.getFullScreenHeight()
    local mainScreen = hs.screen.mainScreen()
    
    -- 获取屏幕完整尺寸（不考虑菜单栏和程序坞）
    local fullFrame = mainScreen:fullFrame()
    local fullHeight = fullFrame.h
    
    -- 获取普通Frame（考虑菜单栏和程序坞）
    local frame = mainScreen:frame()
    local visibleHeight = frame.h
    
    print("屏幕完整高度: " .. fullHeight)
    print("屏幕可见高度: " .. visibleHeight)
    print("菜单栏+程序坞高度: " .. (fullHeight - visibleHeight))
    
    return fullHeight
end

-- 计算相对坐标 (坐标占参考屏幕的百分比)
function M.getRelativeCoordinates(x, y)
    local relX = x / TIGER_BOX_DETAIL_W
    local relY = y / TIGER_BOX_DETAIL_H
    return relX, relY
end

-- 转换X坐标 (16英寸Mac基准坐标到当前屏幕坐标)
function M.scaleX(x)
    local currentWidth, currentHeight, scale, extraInfo = M.getMacScreenInfo()

    print("getScreenSize: currentWidth=" .. currentWidth)
    
    -- 计算相对位置（百分比）
    local relativeX = x / TIGER_BOX_DETAIL_W
    
    -- 将相对位置转换为当前屏幕的像素坐标
    local scaledX = math.floor(relativeX * currentWidth)
    
    
    print("X坐标转换: 原始=" .. x .. ", 相对位置=" .. string.format("%.2f", relativeX) .. ", 目标屏幕=" .. scaledX)
    return scaledX
end

-- 转换Y坐标 (16英寸Mac基准坐标到当前屏幕坐标)
-- @param y 原始Y坐标
-- @param useFullHeight 是否使用完整屏幕高度（包括菜单栏和程序坞）
-- @return 转换后的Y坐标
function M.scaleY(y, useFullHeight)
    useFullHeight = useFullHeight or true
    
    local currentHeight
    if useFullHeight then
        -- 使用完整屏幕高度
        currentHeight = M.getFullScreenHeight()
    else
        -- 使用可见屏幕高度
        local _, height = M.getScreenSize()
        currentHeight = height
    end

    print("currentHeight: " .. currentHeight)
    
    -- 计算相对位置（百分比）
    local relativeY = y / TIGER_BOX_DETAIL_H
    
    -- 将相对位置转换为当前屏幕的像素坐标
    local scaledY = math.floor(relativeY * currentHeight)
    
    print("Y坐标转换: 原始=" .. y .. ", 相对位置=" .. string.format("%.2f", relativeY) .. ", 目标屏幕=" .. scaledY .. (useFullHeight and " (使用完整高度)" or " (使用可见高度)"))
    return scaledY
end

-- 转换坐标点
-- @param x 原始X坐标
-- @param y 原始Y坐标
-- @param useFullHeight 是否使用完整屏幕高度（包括菜单栏和程序坞）
-- @return 转换后的X坐标，Y坐标
function M.scaleCoordinate(x, y, useFullHeight)
    useFullHeight = useFullHeight or true
    local scaledX = M.scaleX(x)
    local scaledY = M.scaleY(y, useFullHeight)
    print("坐标转换: (" .. x .. "," .. y .. ") → (" .. scaledX .. "," .. scaledY .. ")" .. (useFullHeight and " (使用完整高度)" or " (使用可见高度)"))
    return scaledX, scaledY
end

-- 搜索股票并进入详情页
-- @param stockCode String 股票代码
-- @return Boolean 是否成功
function M.searchStock(stockCode)
    local utils = require('modules.utils')
    
    -- 1. 打开老虎证券应用
    print("正在打开老虎证券应用...")
    local tigerApp = hs.application.get(TIGER_APP_NAME)
    if not tigerApp then
        -- 应用未运行，尝试打开
        local success = utils.openApp(TIGER_APP_NAME)
        if not success then
            print("无法启动老虎证券")
            return false
        end
        
        -- 等待应用启动
        hs.timer.usleep(2000000) -- 2秒
        tigerApp = hs.application.get(TIGER_APP_NAME)
        if not tigerApp then
            print("老虎证券启动失败")
            return false
        end
    end
    
    -- 2. 最大化窗口
    print("正在最大化老虎证券窗口...")
    local win = tigerApp:mainWindow()
    if not win then
        print("无法获取老虎证券主窗口")
        return false
    end
    win:maximize()
    
    -- 3. 激活应用窗口
    print("正在激活老虎证券应用...")
    tigerApp:activate()
    
    -- 等待窗口激活
    hs.timer.usleep(1200000) -- 1200ms
    
    -- 4. 输入股票代码 (自动进入搜索框)
    print("正在输入股票代码: " .. stockCode)
    hs.eventtap.keyStrokes(stockCode)
    
    -- 等待搜索结果显示
    hs.timer.usleep(800000) -- 800ms
    
    -- 5. 按回车键选择第一个搜索结果
    print("选择第一个搜索结果...")
    hs.eventtap.keyStroke({}, "return")
    
    print("股票搜索完成: " .. stockCode)
    return true
end

-- 最大化老虎证券窗口
-- @return Boolean 是否成功
function M.maximizeTigerTradeWindow()
    local utils = require('modules.utils')
    return utils.maximizeAppWindow(TIGER_APP_NAME, false, true)
end

-- 点击期权链
-- @return Boolean 是否成功
function M.clickOptionsChain()
    local mouse = require('modules.mouse')

    -- 1. 打开老虎证券应用
    print("正在打开老虎证券应用...")
    local tigerApp = hs.application.get(TIGER_APP_NAME)
    if not tigerApp then
        -- 应用未运行，尝试打开
        local utils = require('modules.utils')
        local success = utils.openApp(TIGER_APP_NAME)
        if not success then
            print("无法启动老虎证券")
            return false
        end

        -- 等待应用启动
        hs.timer.usleep(2000000) -- 2秒
        tigerApp = hs.application.get(TIGER_APP_NAME)
        if not tigerApp then
            print("老虎证券启动失败")
            return false
        end
    end

    -- 2. 最大化窗口
    print("正在最大化老虎证券窗口...")
    local win = tigerApp:mainWindow()
    if not win then
        print("无法获取老虎证券主窗口")
        return false
    end
    win:maximize()

    -- 3. 激活应用窗口
    print("正在激活老虎证券应用...")
    tigerApp:activate()
    print("老虎证券应用激活完成")
    -- 等待窗口激活
    hs.timer.usleep(500000) -- 500ms

    -- 4. 点击期权链按钮 (基于16英寸Mac的坐标: 280, 95)
    print("正在点击期权链按钮...")
    local scaledX, scaledY = 280, 95
    print("点击期权链按钮坐标: " .. scaledX .. ", " .. scaledY)
    local success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX, scaledY)

    if success then
        print("成功点击期权链按钮")
        return true
    else
        print("点击期权链按钮失败")
        return false
    end
end

-- 卖出PUT期权
-- @param quantity String 卖出数量（默认为"1"，表示一手）
-- @return Boolean 是否成功
function M.sellPutOption(quantity)
    quantity = quantity or "1"  -- 默认卖出一手
    local mouse = require('modules.mouse')

    -- 1. 打开老虎证券应用
    print("正在打开老虎证券应用...")
    local tigerApp = hs.application.get(TIGER_APP_NAME)
    if not tigerApp then
        -- 应用未运行，尝试打开
        local utils = require('modules.utils')
        local success = utils.openApp(TIGER_APP_NAME)
        if not success then
            print("无法启动老虎证券")
            return false
        end

        -- 等待应用启动
        hs.timer.usleep(2000000) -- 2秒
        tigerApp = hs.application.get(TIGER_APP_NAME)
        if not tigerApp then
            print("老虎证券启动失败")
            return false
        end
    end

    -- 2. 最大化窗口
    print("正在最大化老虎证券窗口...")
    local win = tigerApp:mainWindow()
    if not win then
        print("无法获取老虎证券主窗口")
        return false
    end
    win:maximize()

    -- 3. 激活应用窗口
    print("正在激活老虎证券应用...")
    tigerApp:activate()

    -- 等待窗口激活
    hs.timer.usleep(1200000) -- 1200ms

    -- 4. 点击目标PUT期权 (基于16英寸Mac的坐标: 160, 320)
    print("正在选择目标PUT期权...")
    local scaledX1, scaledY1 = 160, 320
    print("选择目标PUT期权坐标: " .. scaledX1 .. ", " .. scaledY1)
    local success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX1, scaledY1)
    if not success then
        print("选择PUT期权失败")
        return false
    end

    -- 等待选择生效
    hs.timer.usleep(1000000) -- 1秒

    -- 5. 点击数量输入框 (基于16英寸Mac的坐标: 1500, 765)
    print("正在点击数量输入框...")
    local scaledX2, scaledY2 = 1500, 710
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX2, scaledY2)
    if not success then
        print("点击数量输入框失败")
        return false
    end

    -- 等待输入框激活
    hs.timer.usleep(1200000) -- 1200ms

    -- 6. 清空输入框并输入数量
    print("正在清空输入框...")
    -- 全选当前内容 (Cmd+A)
    hs.eventtap.keyStroke({"cmd"}, "a")
    hs.timer.usleep(200000) -- 200ms

    -- 删除选中内容
    hs.eventtap.keyStroke({}, "delete")
    hs.timer.usleep(200000) -- 200ms

    -- 输入新数量
    print("正在输入卖出数量: " .. quantity)
    hs.eventtap.keyStrokes(quantity)

    -- 等待输入完成
    hs.timer.usleep(500000) -- 500ms

    -- 7. 点击卖出按钮 (基于16英寸Mac的坐标: 1550, 958)
    print("正在点击卖出按钮...")
    local scaledX3, scaledY3 = 1550, 958
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX3, scaledY3)
    if not success then
        print("点击卖出按钮失败")
        return false
    end

    print("成功执行卖出PUT期权操作")
    return true
end

-- 获取Mac型号信息
-- @return macModelInfo 包含Mac型号相关信息的表
function M.getMacModelInfo()
    -- 获取屏幕信息
    local width, height, scale, extraInfo = M.getMacScreenInfo()
    
    -- 构建返回结果
    local macModelInfo = {
        macModel = extraInfo.macModel,   -- Mac型号标识（如"MacBookPro18,2"）
        macModelName = extraInfo.macModelName, -- Mac型号名称（如"MacBook Pro (16-inch, 2021)"）
        screenWidth = width,             -- 屏幕可见宽度
        screenHeight = height,           -- 屏幕可见高度
        fullWidth = extraInfo.fullWidth, -- 屏幕完整宽度
        fullHeight = extraInfo.fullHeight, -- 屏幕完整高度
        scale = scale,                   -- 屏幕缩放比例
        dpi = extraInfo.dpi,             -- 屏幕DPI
        refreshRate = extraInfo.refreshRate, -- 屏幕刷新率
        osVersion = extraInfo.osVersion  -- 操作系统版本
    }
    
    -- 打印主要信息
    print("Mac型号: " .. macModelInfo.macModelName)
    
    return macModelInfo
end

-- 判断当前Mac是否为某型号
-- @param modelPattern 要匹配的型号模式（字符串或正则表达式）
-- @return boolean 是否匹配
function M.isMacModel(modelPattern)
    local modelInfo = M.getMacModelInfo()
    
    -- 检查型号名称和型号标识是否匹配
    return modelInfo.macModelName:find(modelPattern) ~= nil or
           modelInfo.macModel:find(modelPattern) ~= nil
end

-- 判断当前Mac是否为MacBook Pro 14英寸
-- @return boolean
function M.isMacBookPro14()
    return M.isMacModel("14.+MacBook Pro")
end

-- 判断当前Mac是否为MacBook Pro 16英寸
-- @return boolean
function M.isMacBookPro16()
    return M.isMacModel("16.+MacBook Pro")
end

return M
