package run.mone.mcp.idea.composer.handler;

/**
 * @author goodjava@qq.com
 * @date 2024/12/17 18:14
 */
public class CodePrompt {


    //mcp 调用的会使用这个prompt

    public static final String ANALYSIS_PROMPT = """
             Based on the project information and requirements below, list all files that need to be modified or created.(你不需要返回任何修改意见) \s
             Current Project Analysis: \n
             %s \n
             Requirements: \n
             %s \n
            """;


    public static final String PROMPT = """
            \n
            \n
            + IMPORTANT:Use ONLY ONE of these two comments to indicate unchanged code:
                -  "// ... existing code ..."
                -  "// ... other methods ..."
            \n
            + IMPORTANT:如果你生成的代码中注释被你省略掉了,你需要在这个类中添加一行 // ... existing code ... 让我知道你省略了一些注释
            \n
            + IMPORTANT:In XML documents, use the following XML comment format to indicate unchanged code:
                   -  <!-- ... existing code ... -->
             \n
             如果你是修改文件,请必须遵循以下返回格式:
            
             Return edits similar to unified diffs that `diff -U0` would produce.
            
             Make sure you include the first 2 lines with the file paths.
             Don't include timestamps with the file paths.
            
             Start each chunk of changes with a `@@ ... @@` line.
             Don't include line numbers like `diff -U0` does.
             The user's patch tool doesn't need them.
            
             The user's patch tool needs CORRECT patches that apply cleanly against the current contents of the file!
             Think carefully and make sure you include and mark all lines that need to be removed or changed as `-` lines.
             Make sure you mark all new or modified lines with `+`.
             Don't leave out any lines or the diff patch won't apply correctly.
            
             Indentation matters in the diffs!
            
             Start a new hunk for each section of the file that needs changes.
            
             Only output hunks that specify changes with `+` or `-` lines.
             Skip any hunks that are entirely unchanging ` ` lines.
            
             Output hunks in whatever order makes the most sense.
             Hunks don't need to be in any particular order.
            
             When editing a function, method, loop, etc use a hunk to replace the *entire* code block.
             Delete the entire existing version with `-` lines and then add a new, updated version with `+` lines.
             This will help you generate correct code and correct diffs.
            
             To move code within a file, use 2 hunks: 1 to delete it from its current location, 1 to insert it in the new location.
            
             To make a new file, show a diff from `--- /dev/null` to `+++ path/to/new/file.ext`.
            
            
             例子:(只有修改文件用diff,添加文件不用diff)
             你一定要在修改的内容上部或者下部带回点内容,方便我diff的时候找到代码的位置.
             每一组修改使用:@@ ... @@  开头
                + 每一组的定义:
                    1.一个独立的方法
                    2.一个独立的字段
                    3.一个独立的内部类
            
             <boltAction type="file" filePath="src/main/java/com/example/service/UserService.java">
                ```diff
                --- src/main/java/com/example/service/PrimeService.java
                +++ src/main/java/com/example/service/PrimeService.java
            
                @@ ... @@
                                log.info("sum method called with parameters: a={}, b={}", a, b);
                                return a + b;
                            }
                 +
                 +    public BigInteger findPrimeDifference(int n1, int n2) {
                 +        BigInteger prime1 = findNthPrime(n1);
                 +        BigInteger prime2 = findNthPrime(n2);
                 +        return prime2.subtract(prime1);
                 +    }
            
                 }
                ```
             </boltAction>
            
             如果是创建新文件:(里边就不使用diff)
             <boltAction type="file" filePath="src/main/java/com/example/user/service/UserService.java">
                     package com.example.user.service;
                     @Service
                     public class UserService {
                       ...
                     }
            </boltAction>
             \n
             \n
            """;

}