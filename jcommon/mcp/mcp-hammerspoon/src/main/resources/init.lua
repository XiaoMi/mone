-- ~/.hammerspoon/init.lua
-- 加载所有模块
local utils = require('modules.utils')
local dingtalk = require('modules.dingtalk')
local window = require('modules.window')
local mouse = require('modules.mouse')
local server = require('modules.server')
local tigertrade = require('modules.trigertrade')  -- 添加老虎证券模块

-- 将模块功能暴露到全局作用域
_G.searchDingTalkContact = dingtalk.searchDingTalkContact
_G.searchAndSendDingTalkMessage = dingtalk.searchAndSendDingTalkMessage
_G.getRecentDingTalkMessages = dingtalk.getRecentDingTalkMessages
-- _G.captureDingTalkWindow = dingtalk.captureDingTalkWindow

_G.captureAppWindow = window.captureAppWindow

_G.moveAndClick = mouse.moveAndClick
_G.moveToApp = mouse.moveToApp
_G.moveToAppAndClick = mouse.moveToAppAndClick
_G.imageToWindowCoords = mouse.imageToWindowCoords
_G.clickOnImageTarget = mouse.clickOnImageTarget

_G.clickOnScreenshot = mouse.clickOnScreenshot

_G.showMouseCoordinates = mouse.showMouseCoordinates

_G.findElementRecursive = utils.findElementRecursive
_G.openApp = utils.openApp
_G.maximizeAppWindow = utils.maximizeAppWindow

-- 老虎证券相关功能
_G.searchStock = tigertrade.searchStock

-- HTTP服务相关
_G.httpServer = nil
_G.stopHttpServer = server.stopHttpServer
_G.restartHttpServer = server.restartHttpServer

-- 启动HTTP服务器
server.startHttpServer()

print("init.lua 加载完成")


-- 测试调用
-- local messages = getRecentMessages("单文榜")
-- if messages then
--     for _, msg in ipairs(messages) do
--         print(string.format("[%s] %s: %s", msg.time, msg.sender, msg.content))
--     end
-- end

-- 添加错误处理
hs.application.watcher.new(function(appName, eventType, app)
    if eventType == hs.application.watcher.terminated and appName == "Hammerspoon" then
        _G.stopHttpServer()
    end
end):start()