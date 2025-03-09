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
    hs.timer.usleep(500000) -- 500ms
    
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

return M
