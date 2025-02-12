package run.mone.mcp.playwright.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/11 16:37
 */
public class BilibiliPublisher extends Role {

    public BilibiliPublisher() {
        super("bilibili 视频发布者","bilibili 视频发布者");
        this.goal = """
                如果用户需求描述中包含视频发布相关的关键词，例如"视频发布"等，请使用视频发布相关的工具。
                以下是具体的视频发布步骤：
                1.打开bilibili首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.在首页点击右上角的粉色的类似于上传按钮(OperationAction)
                3.在新打开的创作中心页面点击页面中心的上传视频按钮(OpenTabAction)
                4.在弹出的文件选择框中，选择桌面上的视频文件，然后点击打开按钮(OpenTabAction)
                5.填写页面上的“标题”，然后向下滚动屏幕(ScrollAction)，然后点击“立即投稿”按钮 (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                6.点击完“立即投稿”按钮就算结束了(attempt_completion)
                """;
        // 目前用户新开tab页面的Action
        this.constraints = """
                "action": {
                    "type": "createNewTab",
                    "url": "https://www.bilibili.com/",
                    "auto": "true",
                    "desc": "打开bilibili"
                }
                """;
    }
}
