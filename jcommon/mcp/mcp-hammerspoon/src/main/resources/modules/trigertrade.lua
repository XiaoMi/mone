local M = {}

-- 老虎证券应用名称和搜索框信息
local TIGER_APP_NAME = "老虎国际Pro"

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

    -- 4. 点击期权链按钮 (坐标: 280, 95)
    print("正在点击期权链按钮...")
    local success = mouse.moveToAppAndClick(TIGER_APP_NAME, 280, 95)

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

    -- 4. 点击目标PUT期权 (坐标: 160, 320)
    print("正在选择目标PUT期权...")
    local success = mouse.moveToAppAndClick(TIGER_APP_NAME, 160, 320)
    if not success then
        print("选择PUT期权失败")
        return false
    end

    -- 等待选择生效
    hs.timer.usleep(1000000) -- 1秒

    -- 5. 点击数量输入框 (坐标: 1500, 765)
    print("正在点击数量输入框...")
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, 1500, 765)
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

    -- 7. 点击卖出按钮 (坐标: 1550, 958)
    print("正在点击卖出按钮...")
    success = mouse.moveToAppAndClick(TIGER_APP_NAME, 1550, 958)
    if not success then
        print("点击卖出按钮失败")
        return false
    end

    print("成功执行卖出PUT期权操作")
    return true
end

return M
