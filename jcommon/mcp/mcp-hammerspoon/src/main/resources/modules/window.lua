local M = {}


-- function M.captureDingTalkWindow()
--     -- ... 原captureDingTalkWindow函数的代码 ...
-- end

function M.captureActiveWindow()
    -- 获取当前活动窗口
    local win = hs.window.focusedWindow()
    if not win then
        print("没有找到活动窗口")
        return nil
    end

    -- 获取窗口截图
    local screenshot = win:snapshot()
    if not screenshot then
        print("截图失败")
        return nil
    end

    -- 创建时间戳作为文件名
    local timestamp = os.date("%Y%m%d_%H%M%S")
    -- 指定保存路径，这里保存到用户的 Pictures 目录下
    local savePath = os.getenv("HOME") .. "/Pictures/screenshots/"
    -- 确保目录存在
    os.execute("mkdir -p " .. savePath)
    -- 完整的文件路径
    local filePath = savePath .. "window_" .. timestamp .. ".png"

    -- 保存截图
    if screenshot:saveToFile(filePath) then
        print("截图已保存到: " .. filePath)
        return filePath
    else
        print("保存截图失败")
        return nil
    end
end


return M
