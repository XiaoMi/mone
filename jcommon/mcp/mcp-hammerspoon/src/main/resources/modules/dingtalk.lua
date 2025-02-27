local utils = require('modules.utils')
local M = {}

function M.searchDingTalkContact(contactName)
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
local function sendDingTalkMessage(message)
    if not message then return end
    
    hs.timer.usleep(1000000)
    hs.eventtap.keyStrokes(message)
    hs.timer.usleep(500000)
    hs.eventtap.keyStroke({}, "return")
end

function M.searchAndSendDingTalkMessage(contactName, message)
    print("searchAndSendMessage 函数被调用，联系人：" .. contactName .. "，消息：" .. message)
    M.searchContact(contactName)
    sendMessage(message)
end

function M.captureDingTalkWindow()
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


function M.getRecentDingTalkMessages(contactName)
    -- print("getRecentMessages 函数被调用，联系人：" .. contactName)

    -- -- 先搜索并打开联系人聊天窗口
    -- M.searchContact(contactName)
    
    -- -- 等待聊天窗口加载
    -- hs.timer.usleep(2000000)

    -- -- 获取钉钉应用
    -- local dingtalk = hs.application.get("钉钉")
    -- if not dingtalk then
    --     print("钉钉未运行")
    --     return nil
    -- end

    -- -- 获取当前聊天窗口
    -- local chatWindow = dingtalk:focusedWindow()
    -- if not chatWindow then
    --     print("未找到聊天窗口")
    --     return nil
    -- end

    -- -- 获取窗口的 AXUIElement
    -- local axWindow = hs.axuielement.windowElement(chatWindow)
    -- if not axWindow then
    --     print("无法获取窗口的 AXUIElement")
    --     return nil
    -- end

    -- -- 打印窗口层级结构，用于调试
    -- print("Window hierarchy:")
    -- local function printElement(element, depth)
    --     if not element then return end
    --     local indent = string.rep("  ", depth)
    --     print(indent .. "Role: " .. (element:attributeValue("AXRole")  or "nil"))
    --     print(indent .. "Description: " .. (element:attributeValue("AXDescription") or "nil"))

    --     local children = element:attributeValue("AXChildren")
    --     if children then
    --         for _, child in ipairs(children) do
    --             printElement(child, depth + 1)
    --         end
    --     end
    -- end
    -- printElement(axWindow, 0)

    -- -- 根据截图显示的层级，尝试查找分屏组（AXSplitGroup）
    -- local splitGroup = utils.findElementRecursive(axWindow, "AXSplitGroup")
    -- if splitGroup then
    --     print("找到分屏组")
    --     -- 获取分屏组中的消息区域
    --     local children = splitGroup:attributeValue("AXChildren")
    --     if children then
    --         -- 遍历子元素，尝试找到消息列表
    --         for _, child in ipairs(children) do
    --             local role = child:attributeValue("AXRole")
    --             local desc = child:attributeValue("AXDescription")
    --             print("子元素角色:", role, "描述:", desc)
    --         end
    --     end
    -- else
    --     print("未找到分屏组")
    -- end

    -- -- 如果复杂的AXUIElement方法失败，返回一个简单的固定消息作为后备方案
    -- return {
    --     {
    --         time = os.date("%Y-%m-%d %H:%M:%S"),
    --         sender = contactName,
    --         content = "帮我用百度搜索下2021~2023年小米公司的营收情况，将结果写入excel并出个柱状图"
    --     }
    -- }


    ----------

    print("getRecentMessages 函数被调用，联系人：" .. contactName)
    
    -- 先搜索并打开联系人聊天窗口
    searchContact(contactName)
    
    -- 等待聊天窗口加载
    hs.timer.usleep(1000000)
    
    -- 返回固定消息
    return {
        {
            time = os.date("%Y-%m-%d %H:%M:%S"),
            sender = contactName,
            content = "帮我用百度搜索下2021~2023年小米公司的营收情况，将结果写入excel并出个柱状图"
        }
    }

end

return M
