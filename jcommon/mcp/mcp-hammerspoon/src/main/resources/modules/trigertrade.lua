local M = {}

-- 老虎证券应用名称和搜索框信息
-- local TIGER_APP_NAME = "老虎国际Pro"
local TIGER_APP_NAME = "Tiger Trade"

-- 参考屏幕分辨率 (16英寸MacBook Pro的标准分辨率)
local TIGER_BOX_DETAIL_W = 1728
local TIGER_BOX_DETAIL_H = 983

-- 获取当前Mac的屏幕信息（尺寸与型号）
function M.getMacScreenInfo()
    -- 获取屏幕分辨率
    local mainScreen = hs.screen.mainScreen()
    local frame = mainScreen:frame()
    local width, height = frame.w, frame.h
    
    -- 获取屏幕模式（含缩放信息）
    local mode = mainScreen:currentMode()
    local scale = mode.scale or 1.0
    
    -- 输出当前屏幕信息
    print("当前屏幕分辨率: " .. width .. "x" .. height)
    print("当前屏幕缩放比例: " .. scale)
    
    -- 尝试获取Mac型号信息
    local model = ""
    local f = io.popen("sysctl hw.model")
    if f then
        model = f:read("*a") or ""
        f:close()
    end
    
    -- 判断是否为14英寸或16英寸MacBook Pro (基于分辨率近似判断)
    local screenType = "其他Mac屏幕"
    if width >= 3000 and width <= 3500 and height >= 1800 and height <= 2200 then
        screenType = "16英寸MacBook Pro (Retina)"
    elseif width >= 2800 and width <= 3100 and height >= 1600 and height <= 2000 then
        screenType = "14英寸MacBook Pro (Retina)"
    end
    
    print("检测到的屏幕类型: " .. screenType)
    print("系统型号信息: " .. model)
    
    return width, height, screenType, scale
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

-- 计算相对坐标 (坐标占参考屏幕的百分比)
function M.getRelativeCoordinates(x, y)
    local relX = x / TIGER_BOX_DETAIL_W
    local relY = y / TIGER_BOX_DETAIL_H
    return relX, relY
end

-- 转换X坐标 (16英寸Mac基准坐标到当前屏幕坐标)
function M.scaleX(x)
    local currentWidth, currentHeight, screenType, scale = M.getMacScreenInfo()

    local mWidth, mHeight = M.getScreenSize();

    print("getScreenSize: " .. mWidth .. "x" .. mHeight)
    
    -- 计算相对位置（百分比）
    local relativeX = x / TIGER_BOX_DETAIL_W
    
    -- 将相对位置转换为当前屏幕的像素坐标
    local scaledX = math.floor(relativeX * currentWidth)
    
    
    print("X坐标转换: 原始=" .. x .. ", 相对位置=" .. string.format("%.2f", relativeX) .. ", 目标屏幕=" .. scaledX)
    return scaledX
end

-- 转换Y坐标 (16英寸Mac基准坐标到当前屏幕坐标)
function M.scaleY(y)
    local currentWidth, currentHeight, screenType, scale = M.getMacScreenInfo()
    
    -- 计算相对位置（百分比）
    local relativeY = y / TIGER_BOX_DETAIL_H
    
    -- 将相对位置转换为当前屏幕的像素坐标
    local scaledY = math.floor(relativeY * currentHeight)
    
    print("Y坐标转换: 原始=" .. y .. ", 相对位置=" .. string.format("%.2f", relativeY) .. ", 目标屏幕=" .. scaledY)
    return scaledY
end

-- 转换坐标点
function M.scaleCoordinate(x, y)
    local scaledX = M.scaleX(x)
    local scaledY = M.scaleY(y)
    print("坐标转换: (" .. x .. "," .. y .. ") → (" .. scaledX .. "," .. scaledY .. ")")
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
    local scaledX, scaledY = M.scaleCoordinate(280, 95)
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
    hs.timer.usleep(500000) -- 500ms

    -- 4. 点击目标PUT期权 (基于16英寸Mac的坐标: 160, 320)
    print("正在选择目标PUT期权...")
    local scaledX1, scaledY1 = M.scaleCoordinate(160, 320)
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
    local scaledX2, scaledY2 = M.scaleCoordinate(1500, 765)
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX2, scaledY2)
    if not success then
        print("点击数量输入框失败")
        return false
    end

    -- 等待输入框激活
    hs.timer.usleep(500000) -- 500ms

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
    local scaledX3, scaledY3 = M.scaleCoordinate(1550, 958)
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, scaledX3, scaledY3)
    if not success then
        print("点击卖出按钮失败")
        return false
    end

    print("成功执行卖出PUT期权操作")
    return true
end

return M
