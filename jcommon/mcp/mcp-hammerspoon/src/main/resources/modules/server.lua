local M = {}

function M.startHttpServer()
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

function M.stopHttpServer()
    if _G.httpServer then
        _G.httpServer:stop()
        _G.httpServer = nil
        print("HTTP server stopped")
    else
        print("No HTTP server running")
    end
end

function M.restartHttpServer()
    M.stopHttpServer()
    M.startHttpServer()
end

-- 添加错误处理
hs.application.watcher.new(function(appName, eventType, app)
    if eventType == hs.application.watcher.terminated and appName == "Hammerspoon" then
        M.stopHttpServer()
    end
end):start()

return M
