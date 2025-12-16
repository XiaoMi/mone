package run.mone.hive.spring.starter;

/**
 * @author goodjava@qq.com
 * @date 2025/12/16 22:42
 */
public class Prompt {


    public static final String prompt = """
            根据 Android 设备截图和需求帮我拆分下操作列表。每一步都要明确指定操作类型和参数。
            
            ## 支持的操作类型 (Android Action Space):
            click(point='<point>x1 y1</point>') - 点击操作
            long_press(point='<point>x1 y1</point>') - 长按操作
            type(content='具体内容') - 输入文本，content必须明确指定要输入的具体内容（支持中文）
            scroll(point='<point>x1 y1</point>', direction='down/up/right/left') - 滚动操作
            open_app(app_name='应用名') - 打开应用，支持中英文名称如：微信、wechat、QQ、抖音
            drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>') - 拖拽操作
            press_home() - 按 Home 键返回主屏幕
            press_back() - 按返回键
            finished(content='完成信息') - 任务完成
            message(content='消息内容') - 向用户返回分析信息
            
            
             ## 重要规则:
                1. 每一步必须明确标注操作类型（click、type、scroll、open_app等）
                2. 对于type操作，必须在括号内明确写出 content='具体要输入的内容'，支持中文
                3. 对于click操作，必须描述清楚点击的目标元素
                4. 操作步骤要符合人类操作习惯：先点击输入框获取焦点，再输入内容
                5. 如果需要打开应用，优先使用 open_app 操作
                6. 每一步的描述格式：序号.操作描述 (操作类型, 参数说明)
                
                ## 例子1 - 打开微信:
                需求: 打开微信
                返回: (必须是json array格式)
                [
                "1.打开微信应用 (open_app, app_name='微信')",
                "2.finished(content='已成功打开微信')"
                ]
                
                ## 例子2 - 微信发消息:
                需求: 在微信中给张三发送消息"你好"
                分析: 需要先点击搜索，搜索联系人，进入聊天，输入消息并发送
                返回:
                [
                "1.点击微信顶部搜索框 (click, 定位到界面顶部的搜索区域 click(point='<point>x1 y1</point>'))",
                "2.输入联系人名字张三 (type, type(content='张三'))",
                "3.点击搜索结果中的张三 (click, 定位到搜索结果列表中的联系人 click(point='<point>x1 y1</point>'))",
                "4.点击底部消息输入框 (click, 定位到聊天界面底部的输入框 click(point='<point>x1 y1</point>'))",
                "5.输入消息内容 (type, type(content='你好'))",
                "6.点击发送按钮 (click, 定位到输入框右侧的发送按钮 click(point='<point>x1 y1</point>'))",
                "7.finished(content='已成功给张三发送消息：你好' finished(content='已成功给张三发送消息：你好'))"
                ]
                
                ## 例子3 - 滚动浏览:
                需求: 在当前页面向下滚动查看更多内容
                返回:
                [
                "1.在屏幕中央向下滚动 (scroll, direction='down')",
                "2.finished(content='已完成向下滚动')"
                ]
                
                ## 例子4 - 返回操作:
                需求: 返回上一页
                返回:
                [
                "1.按返回键 (press_back)",
                "2.finished(content='已返回上一页')"
                ]
                
                ## 例子5 - 信息识别:
                需求: 识别当前打开的是什么应用
                返回:
                [
                "1.分析当前截图识别应用 (message, 分析截图中的界面特征、标题栏等信息)",
                "2.finished(content='finish')"
                ]
                
                %s
                
                -----
                现在请根据 Android 设备截图和下面的需求生成操作列表：
                
                返回: (必须是json array格式，每一步都要明确标注操作类型),并且需要把这个结果用<list></list>标签所包裹
            """;

}
