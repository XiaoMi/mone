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

function M.captureAppWindow(appName, options)
    -- Set default options
    options = options or {}
    local filePrefix = options.filePrefix or appName:lower():gsub("%s+", "_")
    local imageFormat = options.imageFormat or "jpg" 
    local imageQuality = options.imageQuality or 100  -- JPEG quality (only applies to jpg format)
    
    -- Save current space to restore later
    local currentSpace = hs.spaces.focusedSpace()
    print("当前空间ID: " .. currentSpace)

    -- Get all screens
    local screens = hs.screen.allScreens()
    print("找到屏幕数量: " .. #screens)

    -- Look through each screen
    for _, screen in ipairs(screens) do
        print("检查屏幕: " .. tostring(screen:name()))

        -- Get all spaces for this screen
        local spaces = hs.spaces.spacesForScreen(screen)
        print("该屏幕上的空间数量: " .. #spaces)

        -- Check each space
        for _, spaceID in ipairs(spaces) do
            print("检查空间ID: " .. spaceID)

            -- Go to this space
            hs.spaces.gotoSpace(spaceID)
            -- Wait for space switch to complete
            hs.timer.usleep(500000)

            -- Look for the target app in this space
            local app = hs.application.get(appName)
            if app then
                local windows = app:allWindows()
                for _, window in ipairs(windows) do
                    print("发现窗口 - 标题: " .. tostring(window:title()) ..
                          ", 可见性: " .. tostring(window:isVisible()) ..
                          ", 子角色: " .. tostring(window:subrole()))

                    if window:isVisible() and window:subrole() == "AXStandardWindow" then
                        -- Take window screenshot
                        local screenshot = window:snapshot()
                        if screenshot then
                            -- Create save path
                            local timestamp = os.date("%Y%m%d_%H%M%S")
                            local savePath = "/tmp/agent/screenshots/"
                            os.execute("mkdir -p " .. savePath)
                            
                            -- Define file paths based on format
                            local tempPath, finalPath
                            if imageFormat == "jpg" or imageFormat == "jpeg" then
                                tempPath = savePath .. "temp_" .. timestamp .. ".png"
                                finalPath = savePath .. filePrefix .. "_" .. timestamp .. "." .. imageFormat
                            else
                                finalPath = savePath .. filePrefix .. "_" .. timestamp .. "." .. imageFormat
                            end
                            
                            if imageFormat == "jpg" or imageFormat == "jpeg" then
                                -- Save to temp PNG first, then convert
                                if screenshot:saveToFile(tempPath) then
                                    -- Get window size for logging
                                    local frame = window:frame()
                                    print("窗口尺寸: " .. frame.w .. "x" .. frame.h)
                                    
                                    -- Convert to JPG with compression
                                    local cmd = string.format("sips -s format jpeg -s formatOptions quality=0.8 -z %d %d '%s' --out '%s' && rm '%s'", 
                                                           frame.h, frame.w, 
                                                          tempPath, finalPath, tempPath)
                                    os.execute(cmd)
                                end
                            else
                                -- Direct save for other formats (like PNG)
                                screenshot:saveToFile(finalPath)
                            end
                            
                            -- Check file size
                            local fileInfo = hs.fs.attributes(finalPath)
                            if fileInfo then
                                local fileSizeMB = fileInfo.size / (1024 * 1024)
                                print(string.format("截图已保存到: %s (大小: %.2f MB)", finalPath, fileSizeMB))
                            end
                            
                            -- Restore original space
                            hs.spaces.gotoSpace(currentSpace)
                            return finalPath
                        end
                    end
                end
            end
        end
    end

    -- Restore original space if no window found
    hs.spaces.gotoSpace(currentSpace)
    print("未找到" .. appName .. "窗口")
    return nil
end


return M
