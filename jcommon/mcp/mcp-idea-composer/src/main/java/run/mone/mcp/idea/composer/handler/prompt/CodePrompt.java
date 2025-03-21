package run.mone.mcp.idea.composer.handler.prompt;

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

    public static final String SR_DIFF_PROMPT = """
            + IMPORTANT:如果使用 write_to_file ，内容中务必包含完整的代码内容
            + IMPORTANT:如果使用 replace_in_file ，内容中务必包含一个或多个"SEARCH/REPLACE"。格式如下：
                                ```
                                <<<<<<< SEARCH
                                [exact content to find]
                                =======
                                [new content to replace with]
                                >>>>>>> REPLACE
                                ```
                    Critical rules:
                        - SEARCH content must match the associated file section to find EXACTLY:
                            * Match character-for-character including whitespace, indentation, line endings
                            * Include all comments, docstrings, etc.
                        - SEARCH/REPLACE blocks will ONLY replace the first match occurrence.
                            * Including multiple unique SEARCH/REPLACE blocks if you need to make multiple changes.
                            * Include *just* enough lines in each SEARCH section to uniquely match each set of lines that need to change.
                            * When using multiple SEARCH/REPLACE blocks, list them in the order they appear in the file.
                        - Keep SEARCH/REPLACE blocks concise:
                            * Break large SEARCH/REPLACE blocks into a series of smaller blocks that each change a small portion of the file.
                            * Include just the changing lines, and a few surrounding lines if needed for uniqueness.
                            * Do not include long runs of unchanging lines in SEARCH/REPLACE blocks.
                            * Each line must be complete. Never truncate lines mid-way through as this can cause matching failures.
                        - Special operations:
                            * To move code: Use two SEARCH/REPLACE blocks (one to delete from original + one to insert at new location)
                            * To delete code: Use empty REPLACE section
            IMPORTANT: 多个"SEARCH/REPLACE"操作请放在一个boltAction中执行，不要拆分成多个boltAction。例如：
                                <example>
                                    <user_query>******</user_query> \s
                                    <assistant_response> \s
                                    <<<<<<< SEARCH
                                    import React from 'react';
                                    =======
                                    import React, { useState } from 'react';
                                    >>>>>>> REPLACE
                                    
                                    <<<<<<< SEARCH
                                    function handleSubmit() {
                                      saveData();
                                      setLoading(false);
                                    }
                                    
                                    =======
                                    >>>>>>> REPLACE
                                    
                                    <<<<<<< SEARCH
                                    return (
                                      <div>
                                    =======
                                    function handleSubmit() {
                                      saveData();
                                      setLoading(false);
                                    }
                                    
                                    return (
                                      <div>
                                    >>>>>>> REPLACE
                                    </boltAction> \s
                                    </boltArtifact> \s
                                    </assistant_response> \s
                                </example>
                更多例子：
                <examples>
                    <example>
                        <user_query>在User类中添加一个新的getFullName方法</user_query> \s
                        
                        <assistant_response> \s
                        我来帮你添加getFullName方法。 \s
                        
                        <boltArtifact id="add-user-method" title="Add getFullName method to User class"> \s
                        <boltAction type="replace_in_file" filePath="src/User.js"> \s
                        <<<<<<< SEARCH
                        class User { \s
                          constructor(firstName, lastName) { \s
                            this.firstName = firstName; \s
                            this.lastName = lastName; \s
                          } \s
                        =======
                        class User { \s
                          constructor(firstName, lastName) { \s
                            this.firstName = firstName; \s
                            this.lastName = lastName; \s
                          } \s
                          getFullName() { \s
                            return `${this.firstName} ${this.lastName}`; \s
                          } \s
                        >>>>>>> REPLACE
                        </boltAction> \s
                        </boltArtifact> \s
                        </assistant_response> \s
                    </example>
                        
                    <example>
                        <user_query>创建一个新的Product类</user_query> \s
                        
                        <assistant_response> \s
                        好的,我来创建Product类。 \s
                        
                        <boltArtifact id="create-product" title="Create Product class"> \s
                        <boltAction type="write_to_file" filePath="src/Product.js"> \s
                        class Product { \s
                          constructor(id, name, price) { \s
                            this.id = id; \s
                            this.name = name; \s
                            this.price = price; \s
                          } \s
                        
                          getFormattedPrice() { \s
                            return `$${this.price.toFixed(2)}`; \s
                          } \s
                        
                          updatePrice(newPrice) { \s
                            this.price = newPrice; \s
                          } \s
                        } \s
                        
                        export default Product; \s
                        </boltAction> \s
                        </boltArtifact> \s
                        </assistant_response>
                    </example>
                        
                </examples>
            """;

    public static final String SR_FULL_PROMPT = """
            + IMPORTANT:本次代码变动请全部使用 write_to_file 完成
            """;

}