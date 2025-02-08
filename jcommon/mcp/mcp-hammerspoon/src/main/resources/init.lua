-- ~/.hammerspoon/init.lua
-- 递归查找函数
local function findElementRecursive(element, role, attributes)
    if not element then
        return nil
    end
    -- 检查当前元素是否匹配
    if type(element.role) == 'function' and element:role() == role then
        local match = true
        if attributes then
            for key, value in pairs(attributes) do
                -- 兼容 element:attributeValue(key) 返回 nil 的情况
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
                local foundElement = findElementRecursive(child, role, attributes)
                if foundElement then
                    return foundElement
                end
            end
        end
    end
    return nil
end

_G.searchContact = function(contactName)
    print("searchContact 函数被调用，参数是：" .. contactName)
    local dingtalk = hs.application.get("钉钉")
    if not dingtalk then
        hs.alert.show("钉钉未运行")
        print("钉钉未运行")
        return
    end

    hs.application.launchOrFocus("钉钉")
    hs.timer.usleep(2000000)
    -- 强制激活钉钉窗口
    dingtalk:activate(true)  -- true 参数表示强制激活
    
    -- 增加等待时间确保窗口已经完全激活
    hs.timer.usleep(1000000)  -- 1秒
	
    -- 验证当前激活的应用确实是钉钉
    local frontApp = hs.application.frontmostApplication()
    if frontApp:name() ~= "钉钉" then
        print("未能成功切换到钉钉窗口")
        print("当前窗口是：" .. frontApp:name())
        return
    end
    
    local mainWindow = nil
    local attempts = 3
    local delay = 500000
    
    for i = 1, attempts do
        print("尝试获取 mainWindow，第 " .. i .. " 次")
        local allWindows = dingtalk:allWindows()
        for _, win in ipairs(allWindows) do
            print("  窗口标题: " .. tostring(win:title()))
            print("  窗口角色: " .. tostring(win:role()))
            print("  窗口子角色: " .. tostring(win:subrole()))
            if win:isVisible() and win:subrole() == "AXStandardWindow" then
                mainWindow = win
                print("    找到主窗口 (使用 subrole)")
                break
            end
            if win:isVisible() and win:title() == "钉钉" then
                mainWindow = win
                print("    找到主窗口 (使用 title)")
                break
            end
        end
        if mainWindow then
            break
        else
            hs.timer.usleep(delay)  -- 这里把 ':' 改成了正确的语法
        end
    end

    if not mainWindow then
        hs.alert.show("找不到钉钉主窗口")
        print("找不到钉钉主窗口")
        return
    end
    
    print("mainWindow info:" .. hs.inspect(mainWindow))
    
	-- 模拟 Cmd+F 激活搜索框
    hs.eventtap.keyStroke({"cmd"}, "f")
    hs.timer.usleep(500000)  -- 等待搜索框激活

    -- 直接输入搜索内容
    hs.eventtap.keyStrokes(contactName)

    -- 等待一下以确保搜索文本已输入
    hs.timer.usleep(500000)
	
    -- 按Tab键切换到联系人标签
    hs.eventtap.keyStroke({}, "tab")
    hs.timer.usleep(500000)

    -- 按下方向键选中第一个联系人搜索结果
    hs.eventtap.keyStroke({}, "down")
    hs.timer.usleep(500000)

    -- 按回车键选中联系人，打开聊天窗口
    hs.eventtap.keyStroke({}, "return")

    -- 等待聊天窗口打开
    hs.timer.usleep(1000000)
	

    -- 可以添加调试信息查看当前激活的元素
    local activeApp = hs.application.frontmostApplication()
    local focusedWindow = activeApp:focusedWindow()
    if focusedWindow then
        print("当前焦点窗口:", focusedWindow:title())
    end
	
end

-- 发送消息函数
local function sendMessage(message)
    if not message then return end
    
    -- 等待聊天窗口完全加载
    hs.timer.usleep(1000000)
    
    -- 输入消息内容
    hs.eventtap.keyStrokes(message)
    hs.timer.usleep(500000)
    
    -- 按回车键发送消息
    hs.eventtap.keyStroke({}, "return")
end

_G.searchAndSendMessage = function(contactName, message)
    print("searchAndSendMessage 函数被调用，联系人：" .. contactName .. "，消息：" .. message)
    
    -- 先搜索并打开联系人聊天窗口
    searchContact(contactName)
    
    -- 发送消息
    sendMessage(message)
end

_G.captureDingTalkWindow = function()
    -- 保存当前空间ID，以便后续恢复
    local currentSpace = hs.spaces.focusedSpace()
    print("当前空间ID: " .. currentSpace)
    
    -- 获取所有屏幕
    local screens = hs.screen.allScreens()
    print("找到屏幕数量: " .. #screens)
    
    -- 遍历每个屏幕
    for _, screen in ipairs(screens) do
        print("检查屏幕: " .. tostring(screen:name()))
        
        -- 获取该屏幕上的所有空间
        local spaces = hs.spaces.spacesForScreen(screen)
        print("该屏幕上的空间数量: " .. #spaces)
        
        -- 遍历该屏幕的所有空间
        for _, spaceID in ipairs(spaces) do
            print("检查空间ID: " .. spaceID)
            
            -- 切换到该空间
            hs.spaces.gotoSpace(spaceID)
            -- 等待空间切换完成
            hs.timer.usleep(500000)
            
            -- 在当前空间查找钉钉窗口
            local dingtalk = hs.application.get("钉钉")
            if dingtalk then
                local windows = dingtalk:allWindows()
                for _, window in ipairs(windows) do
                    print("发现窗口 - 标题: " .. tostring(window:title()) .. 
                          ", 可见性: " .. tostring(window:isVisible()) .. 
                          ", 子角色: " .. tostring(window:subrole()))
                    
                    if window:isVisible() and window:subrole() == "AXStandardWindow" then
                        -- 获取窗口截图
                        local screenshot = window:snapshot()
                        if screenshot then
                            -- 创建保存路径
                            local timestamp = os.date("%Y%m%d_%H%M%S")
                            local savePath = os.getenv("HOME") .. "/Pictures/screenshots/"
                            os.execute("mkdir -p " .. savePath)
                            local filePath = savePath .. "dingtalk_" .. timestamp .. ".png"
                            
                            -- 保存截图
                            if screenshot:saveToFile(filePath) then
                                print("钉钉截图已保存到: " .. filePath)
                                -- 恢复到原来的空间
                                hs.spaces.gotoSpace(currentSpace)
                                return filePath
                            end
                        end
                    end
                end
            end
        end
    end
    
    -- 恢复到原来的空间
    hs.spaces.gotoSpace(currentSpace)
    print("未找到钉钉窗口")
    return nil
end

_G.captureActiveWindow = function()
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

-- 获取最近聊天记录函数
_G.getRecentMessages = function(contactName)
    print("getRecentMessages 函数被调用，联系人：" .. contactName)
    
    -- 先搜索并打开联系人聊天窗口
    searchContact(contactName)
    
    -- 等待聊天窗口加载
    hs.timer.usleep(2000000)
    
    -- 获取钉钉应用
    local dingtalk = hs.application.get("钉钉")
    if not dingtalk then
        print("钉钉未运行")
        return nil
    end
    
    -- 获取当前聊天窗口
    local chatWindow = dingtalk:focusedWindow()
    if not chatWindow then
        print("未找到聊天窗口")
        return nil
    end
    
    -- 获取窗口的 AXUIElement
    local axWindow = hs.axuielement.windowElement(chatWindow)
    if not axWindow then
        print("无法获取窗口的 AXUIElement")
        return nil
    end

    -- 打印窗口层级结构，用于调试
    print("Window hierarchy:")
    local function printElement(element, depth)
        if not element then return end
        local indent = string.rep("  ", depth)
        print(indent .. "Role: " .. (element:role() or "nil"))
        print(indent .. "Description: " .. (element:attributeValue("AXDescription") or "nil"))
        
        local children = element:attributeValue("AXChildren")
        if children then
            for _, child in ipairs(children) do
                printElement(child, depth + 1)
            end
        end
    end
    printElement(axWindow, 0)
    
    -- 根据截图显示的层级，尝试查找分屏组（AXSplitGroup）
    local splitGroup = findElementRecursive(axWindow, "AXSplitGroup")
    if splitGroup then
        print("找到分屏组")
        -- 获取分屏组中的消息区域
        local children = splitGroup:attributeValue("AXChildren")
        if children then
            -- 遍历子元素，尝试找到消息列表
            for _, child in ipairs(children) do
                local role = child:role()
                local desc = child:attributeValue("AXDescription")
                print("子元素角色:", role, "描述:", desc)
            end
        end
    else
        print("未找到分屏组")
    end

    -- 返回消息数组（目前为测试版本）
    return {}
end

print("init.lua 加载完成")
-- searchAndSendMessage("单文榜", "你好！这是一条测试消息")

-- 测试调用
-- local messages = getRecentMessages("单文榜")
-- if messages then
--     for _, msg in ipairs(messages) do
--         print(string.format("[%s] %s: %s", msg.time, msg.sender, msg.content))
--     end
-- end






-------------------------------- 以下是开启本地http服务 -----------------------------
-- 在文件顶部声明全局变量以保持引用
_G.httpServer = nil

-- 创建 HTTP 服务器
local function startHttpServer()
    if _G.httpServer then
        print("HTTP server already running")
        return
    end

    _G.httpServer = hs.httpserver.new()

    -- 处理请求的函数
    local function handleRequest(method, path, headers, body)
        print(string.format("Received %s request for %s", method, path))
        
        if method == "POST" then
            local success, result = pcall(function()
                -- 解析请求体中的 JSON
                local command = hs.json.decode(body)
                if not command then
                    return {
                        status = 400,
                        body = hs.json.encode({
                            success = false,
                            error = "Invalid JSON body"
                        })
                    }
                end

                -- 执行代码
                local fn = load(command.code)
                if not fn then
                    return {
                        status = 400,
                        body = hs.json.encode({
                            success = false,
                            error = "Invalid Lua code"
                        })
                    }
                end

                -- 执行函数并获取结果
                local success, result = pcall(fn)
                
                return {
                    status = 200,
                    body = hs.json.encode({
                        success = success,
                        result = result
                    })
                }
            end)

            if success then
                return result.body, result.status, {["Content-Type"] = "application/json"}
            else
                return hs.json.encode({
                    success = false,
                    error = tostring(result)
                }), 500, {["Content-Type"] = "application/json"}
            end
        end

        return "Not Found", 404, {["Content-Type"] = "text/plain"}
    end

    -- 配置并启动服务器
    _G.httpServer:setCallback(handleRequest)
    _G.httpServer:setPort(27123)
    local success = _G.httpServer:start()
    
    if success then
        print("HTTP server started on port 27123")
    else
        print("Failed to start HTTP server")
    end
end

-- 添加停止服务器的函数
_G.stopHttpServer = function()
    if _G.httpServer then
        _G.httpServer:stop()
        _G.httpServer = nil
        print("HTTP server stopped")
    else
        print("No HTTP server running")
    end
end

-- 添加重启服务器的函数
_G.restartHttpServer = function()
    _G.stopHttpServer()
    startHttpServer()
end

-- 启动服务器
startHttpServer()

-- 添加错误处理
hs.application.watcher.new(function(appName, eventType, app)
    if eventType == hs.application.watcher.terminated and appName == "Hammerspoon" then
        _G.stopHttpServer()
    end
end):start()