# 测试用例示例

## 测试用例1：包含安全风险的代码
```java
public class SecurityRiskFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String command = request.getParameter("cmd");
        try {
            Runtime.getRuntime().exec(command);
            System.out.println("执行命令: " + command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

**预期结果：**
- 安全风险：Runtime.getRuntime().exec (扣3分)
- 日志输出：System.out.println (扣1分)
- 日志输出：printStackTrace (扣1分)
- 最终得分：5/10

## 测试用例2：包含循环的代码
```java
public class LoopFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        List<String> items = getItems();
        for (String item : items) {
            processItem(item);
        }
        
        int count = 0;
        while (count < 100) {
            doSomething();
            count++;
        }
        
        log.info("处理完成");
    }
}
```

**预期结果：**
- 循环复杂度：for循环 (扣1分)
- 循环复杂度：while循环 (扣2分)
- 日志输出：log.info (扣1分)
- 最终得分：6/10

## 测试用例3：安全的代码
```java
public class SafeFilter implements Filter {
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String userId = request.getParameter("userId");
        if (isValidUser(userId)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    
    private boolean isValidUser(String userId) {
        return userId != null && userId.matches("[a-zA-Z0-9]+");
    }
}
```

**预期结果：**
- 无安全风险
- 无循环
- 无日志输出
- 最终得分：10/10

## 测试用例4：严格模式测试
```java
public class StrictModeTest {
    public void test() {
        Class.forName("com.example.Test");
        for (int i = 0; i < 10; i++) {
            System.out.println("test");
        }
    }
}
```

**普通模式预期结果：**
- 安全风险：Class.forName (扣2分)
- 循环复杂度：for循环 (扣1分)
- 日志输出：System.out.println (扣1分)
- 最终得分：6/10

**严格模式预期结果：**
- 安全风险：Class.forName (扣3分)
- 循环复杂度：for循环 (扣2分)
- 日志输出：System.out.println (扣2分)
- 最终得分：3/10
