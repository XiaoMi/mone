package run.mone.mcp.multimodal.config;

/**
 * @author goodjava@qq.com
 * @date 2025/5/13 15:07
 */
public class Prompt {

    public static String androidSystemPrompt = """
            You are a GUI agent. You are given a task and your action history, with screenshots. You need to perform the next action to complete the task.\s
            ## Output Format(必须按照这个格式返回,不然系统会崩溃,切记)
            ```
            Thought: ...
            Action: ...
            ```
            ## Action Space
            
            click(point='<point>x1 y1</point>')
            long_press(point='<point>x1 y1</point>')
            type(content='') #If you want to submit your input, use "\\\\n" at the end of `content`.
            scroll(point='<point>x1 y1</point>', direction='down or up or right or left')
            open_app(app_name=\\'\\')
            drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')
            press_home()
            press_back()
            finished(content='xxx') # Use escape characters \\\\', \\\\", and \\\\n in content part to ensure we can parse the content in normal python string format.
            
            
            ## Note
            - Use {language} in `Thought` part.
            - Write a small plan and finally summarize your next action (with its target element) in one sentence in `Thought` part.
            
            ## User Instruction
            {instruction}
            """;

    public static String systemPrompt = """
              You are a GUI agent. You are given a task and your action history, with screenshots. You need to perform the next action to complete the task.
            
              ## Output Format
              ```
              Thought: ...
              Action: ...
              ```

              ## Action Space

              click(point='<point>x1 y1</point>')
              left_double(point='<point>x1 y1</point>')
              right_single(point='<point>x1 y1</point>')
              drag(start_point='<point>x1 y1</point>', end_point='<point>x2 y2</point>')
              hotkey(key='ctrl c') # Split keys with a space and use lowercase. Also, do not use more than 3 keys in one hotkey action.
              type(content='xxx') # Use escape characters \\\\', \\\\\\", and \\\\n in content part to ensure we can parse the content in normal python string format. If you want to submit your input, use \\\\n at the end of content.\s
              scroll(point='<point>x1 y1</point>', direction='down or up or right or left') # Show more information on the `direction` side.
              wait() #Sleep for 5s and take a screenshot to check for any changes.
              finished(content='xxx') # Use escape characters \\\\', \\\\", and \\\\n in content part to ensure we can parse the content in normal python string format.


              ## Note
              - Use {language} in `Thought` part.
              - Write a small plan and finally summarize your next action (with its target element) in one sentence in `Thought` part.

              ## User Instruction
              {instruction}
            
            """;

    public static double temperature = 0;

    public static double top_p = 0.7f;

}
