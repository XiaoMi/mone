package run.mone.hive.task;

/**
 * Deep Planning命令实现
 * 对应Cline中的deep-planning slash command
 */
public class DeepPlanningCommand implements SlashCommand {
    
    private static final String COMMAND_NAME = "deep-planning";
    
    @Override
    public String getName() {
        return COMMAND_NAME;
    }
    
    @Override
    public String getDescription() {
        return "Creates a comprehensive implementation plan before writing any code through a four-step process: investigation, discussion, planning, and task creation.";
    }
    
    @Override
    public boolean matches(String input) {
        return input.trim().startsWith("/" + COMMAND_NAME);
    }
    
    @Override
    public String execute(String input, FocusChainSettings focusChainSettings) {
        return generateDeepPlanningPrompt(focusChainSettings);
    }
    
    /**
     * 生成Deep Planning的完整提示词
     * 对应Cline中的deepPlanningToolResponse函数
     */
    private String generateDeepPlanningPrompt(FocusChainSettings focusChainSettings) {
        // 检测操作系统类型（简化版本，实际可能需要更复杂的检测）
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isPowerShell = osName.contains("windows");
        
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("<explicit_instructions type=\"deep-planning\">\n");
        prompt.append("Your task is to create a comprehensive implementation plan before writing any code. ");
        prompt.append("This process has four distinct steps that must be completed in order.\n\n");
        
        prompt.append("Your behavior should be methodical and thorough - take time to understand the codebase completely ");
        prompt.append("before making any recommendations. The quality of your investigation directly impacts the success of the implementation.\n\n");
        
        // Step 1: Silent Investigation
        prompt.append("## STEP 1: Silent Investigation\n\n");
        prompt.append("<important>\n");
        prompt.append("until explicitly instructed by the user to proceed with coding.\n");
        prompt.append("You must thoroughly understand the existing codebase before proposing any changes.\n");
        prompt.append("Perform your research without commentary or narration. Execute commands and read files ");
        prompt.append("without explaining what you're about to do. Only speak up if you have specific questions for the user.\n");
        prompt.append("</important>\n\n");
        
        prompt.append("### Required Research Activities\n");
        prompt.append("You must use the read_file tool to examine relevant source files, configuration files, and documentation. ");
        prompt.append("You must use terminal commands to gather information about the codebase structure and patterns. ");
        prompt.append("All terminal output must be piped to cat for visibility.\n\n");
        
        prompt.append("### Essential Terminal Commands\n");
        prompt.append("First, determine the language(s) used in the codebase, then execute these commands to build your understanding. ");
        prompt.append("You must tailor them to the codebase and ensure the output is not overly verbose. ");
        prompt.append("For example, you should exclude dependency folders such as node_modules, venv or php vendor, etc. ");
        prompt.append("These are only examples, the exact commands will differ depending on the codebase.\n\n");
        
        // 根据操作系统添加不同的命令
        if (isPowerShell) {
            prompt.append(getPowerShellCommands());
        } else {
            prompt.append(getUnixCommands());
        }
        
        // Step 2: Discussion and Questions
        prompt.append("\n## STEP 2: Discussion and Questions\n\n");
        prompt.append("Ask the user brief, targeted questions that will influence your implementation plan. ");
        prompt.append("Keep your questions concise and conversational. Ask only essential questions needed to create an accurate plan.\n\n");
        
        prompt.append("**Ask questions only when necessary for:**\n");
        prompt.append("- Clarifying ambiguous requirements or specifications\n");
        prompt.append("- Choosing between multiple equally valid implementation approaches\n");
        prompt.append("- Confirming assumptions about existing system behavior or constraints\n");
        prompt.append("- Understanding preferences for specific technical decisions that will affect the implementation\n\n");
        
        prompt.append("Your questions should be direct and specific. Avoid long explanations or multiple questions in one response.\n\n");
        
        // Step 3: Create Implementation Plan Document
        prompt.append("## STEP 3: Create Implementation Plan Document\n\n");
        prompt.append("Create a structured markdown document containing your complete implementation plan. ");
        prompt.append("The document must follow this exact format with clearly marked sections:\n\n");
        
        prompt.append("### Document Structure Requirements\n\n");
        prompt.append("Your implementation plan must be saved as implementation_plan.md, and *must* be structured as follows:\n\n");
        
        prompt.append(getImplementationPlanStructure());
        
        // Step 4: Create Implementation Task
        prompt.append("\n## STEP 4: Create Implementation Task\n\n");
        prompt.append("Use the new_task command to create a task for implementing the plan. ");
        prompt.append("The task must include a <task_progress> list that breaks down the implementation into trackable steps.\n\n");
        
        prompt.append("### Task Creation Requirements\n\n");
        prompt.append("Your new task should be self-contained and reference the plan document rather than requiring additional codebase investigation. ");
        prompt.append("Include these specific instructions in the task description:\n\n");
        
        prompt.append("**Plan Document Navigation Commands:**\n");
        prompt.append("The implementation agent should use these commands to read specific sections of the implementation plan. ");
        prompt.append("You should adapt these examples to conform to the structure of the .md file you created, ");
        prompt.append("and explicitly provide them when creating the new task:\n\n");
        
        // 添加文档导航命令
        if (isPowerShell) {
            prompt.append(getPowerShellNavigationCommands());
        } else {
            prompt.append(getUnixNavigationCommands());
        }
        
        prompt.append("\n**Task Progress Format:**\n");
        prompt.append("<IMPORTANT>\n");
        prompt.append("You absolutely must include the task_progress contents in context when creating the new task. ");
        prompt.append("When providing it, do not wrap it in XML tags- instead provide it like this:\n\n");
        
        prompt.append("task_progress Items:\n");
        prompt.append("- [ ] Step 1: Brief description of first implementation step\n");
        prompt.append("- [ ] Step 2: Brief description of second implementation step\n");
        prompt.append("- [ ] Step 3: Brief description of third implementation step\n");
        prompt.append("- [ ] Step N: Brief description of final implementation step\n\n");
        
        prompt.append("You also MUST include the path to the markdown file you have created in your new task prompt. ");
        prompt.append("You should do this as follows:\n\n");
        prompt.append("Refer to @path/to/file/markdown.md for a complete breakdown of the task requirements and steps. ");
        prompt.append("You should periodically read this file again.\n\n");
        
        // Focus Chain集成
        if (focusChainSettings != null && focusChainSettings.isEnabled()) {
            prompt.append("**Task Progress Parameter:**\n");
            prompt.append("When creating the new task, you must include a task_progress parameter that breaks down ");
            prompt.append("the implementation into trackable steps. This should follow the standard Markdown checklist ");
            prompt.append("format with \"- [ ]\" for incomplete items.\n\n");
        }
        
        prompt.append("### Mode Switching\n\n");
        prompt.append("When creating the new task, request a switch to \"act mode\" if you are currently in \"plan mode\". ");
        prompt.append("This ensures the implementation agent operates in execution mode rather than planning mode.\n");
        prompt.append("</IMPORTANT>\n\n");
        
        // Quality Standards
        prompt.append("## Quality Standards\n\n");
        prompt.append("You must be specific with exact file paths, function names, and class names. ");
        prompt.append("You must be comprehensive and avoid assuming implicit understanding. ");
        prompt.append("You must be practical and consider real-world constraints and edge cases. ");
        prompt.append("You must use precise technical language and avoid ambiguity.\n\n");
        
        prompt.append("Your implementation plan should be detailed enough that another developer could execute it ");
        prompt.append("without additional investigation.\n\n");
        
        prompt.append("---\n\n");
        prompt.append("**Execute all four steps in sequence. Your role is to plan thoroughly, not to implement. ");
        prompt.append("Code creation begins only after the new task is created and you receive explicit instruction to proceed.**\n\n");
        
        prompt.append("Below is the user's input when they indicated that they wanted to create a comprehensive implementation plan.\n");
        prompt.append("</explicit_instructions>\n");
        
        return prompt.toString();
    }
    
    /**
     * PowerShell命令集
     */
    private String getPowerShellCommands() {
        return """
# Discover project structure and file types
Get-ChildItem -Recurse -Include "*.py","*.js","*.ts","*.java","*.cpp","*.go" | Select-Object -First 30 | Select-Object FullName

# Find all class and function definitions
Get-ChildItem -Recurse -Include "*.py","*.js","*.ts","*.java","*.cpp","*.go" | Select-String -Pattern "class|function|def|interface|struct"

# Analyze import patterns and dependencies
Get-ChildItem -Recurse -Include "*.py","*.js","*.ts","*.java","*.cpp" | Select-String -Pattern "import|from|require|#include" | Sort-Object | Get-Unique

# Find dependency manifests
Get-ChildItem -Recurse -Include "requirements*.txt","package.json","Cargo.toml","pom.xml","Gemfile","go.mod" | Get-Content

# Identify technical debt and TODOs
Get-ChildItem -Recurse -Include "*.py","*.js","*.ts","*.java","*.cpp","*.go" | Select-String -Pattern "TODO|FIXME|XXX|HACK|NOTE"
""";
    }
    
    /**
     * Unix/Linux命令集
     */
    private String getUnixCommands() {
        return """
# Discover project structure and file types
find . -type f -name "*.py" -o -name "*.js" -o -name "*.ts" -o -name "*.java" -o -name "*.cpp" -o -name "*.go" | head -30 | cat

# Find all class and function definitions
grep -r "class\\|function\\|def\\|interface\\|struct\\|func\\|type.*struct\\|type.*interface" --include="*.py" --include="*.js" --include="*.ts" --include="*.java" --include="*.cpp" --include="*.go" . | cat

# Analyze import patterns and dependencies
grep -r "import\\|from\\|require\\|#include" --include="*.py" --include="*.js" --include="*.ts" --include="*.java" --include="*.cpp" . | sort | uniq | cat

# Find dependency manifests
find . -name "requirements*.txt" -o -name "package.json" -o -name "Cargo.toml" -o -name "pom.xml" -o -name "Gemfile" -o -name "go.mod" | xargs cat

# Identify technical debt and TODOs
grep -r "TODO\\|FIXME\\|XXX\\|HACK\\|NOTE" --include="*.py" --include="*.js" --include="*.ts" --include="*.java" --include="*.cpp" --include="*.go" . | cat
""";
    }
    
    /**
     * 实施计划文档结构
     */
    private String getImplementationPlanStructure() {
        return """
# Implementation Plan

[Overview]
Single sentence describing the overall goal.

Multiple paragraphs outlining the scope, context, and high-level approach. Explain why this implementation is needed and how it fits into the existing system.

[Types]  
Single sentence describing the type system changes.

Detailed type definitions, interfaces, enums, or data structures with complete specifications. Include field names, types, validation rules, and relationships.

[Files]
Single sentence describing file modifications.

Detailed breakdown:
- New files to be created (with full paths and purpose)
- Existing files to be modified (with specific changes)  
- Files to be deleted or moved
- Configuration file updates

[Functions]
Single sentence describing function modifications.

Detailed breakdown:
- New functions (name, signature, file path, purpose)
- Modified functions (exact name, current file path, required changes)
- Removed functions (name, file path, reason, migration strategy)

[Classes]
Single sentence describing class modifications.

Detailed breakdown:
- New classes (name, file path, key methods, inheritance)
- Modified classes (exact name, file path, specific modifications)
- Removed classes (name, file path, replacement strategy)

[Dependencies]
Single sentence describing dependency modifications.

Details of new packages, version changes, and integration requirements.

[Testing]
Single sentence describing testing approach.

Test file requirements, existing test modifications, and validation strategies.

[Implementation Order]
Single sentence describing the implementation sequence.

Numbered steps showing the logical order of changes to minimize conflicts and ensure successful integration.
""";
    }
    
    /**
     * PowerShell文档导航命令
     */
    private String getPowerShellNavigationCommands() {
        return """
# Read Overview section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Overview\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Types\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Types section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Types\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Files\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Files section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Files\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Functions\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Functions section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Functions\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Classes\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Classes section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Classes\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Dependencies\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Dependencies section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Dependencies\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Testing\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Testing section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Testing\\]').LineNumber; $end = ($content | Select-String -Pattern '\\[Implementation Order\\]').LineNumber; $content[($start-1)..($end-2)]

# Read Implementation Order section
$content = Get-Content implementation_plan.md; $start = ($content | Select-String -Pattern '\\[Implementation Order\\]').LineNumber; $content[($start-1)..($content.Length-1)]
""";
    }
    
    /**
     * Unix文档导航命令
     */
    private String getUnixNavigationCommands() {
        return """
# Read Overview section
sed -n '/\\[Overview\\]/,/\\[Types\\]/p' implementation_plan.md | head -n -1 | cat

# Read Types section  
sed -n '/\\[Types\\]/,/\\[Files\\]/p' implementation_plan.md | head -n -1 | cat

# Read Files section
sed -n '/\\[Files\\]/,/\\[Functions\\]/p' implementation_plan.md | head -n -1 | cat

# Read Functions section
sed -n '/\\[Functions\\]/,/\\[Classes\\]/p' implementation_plan.md | head -n -1 | cat

# Read Classes section
sed -n '/\\[Classes\\]/,/\\[Dependencies\\]/p' implementation_plan.md | head -n -1 | cat

# Read Dependencies section
sed -n '/\\[Dependencies\\]/,/\\[Testing\\]/p' implementation_plan.md | head -n -1 | cat

# Read Testing section
sed -n '/\\[Testing\\]/,/\\[Implementation Order\\]/p' implementation_plan.md | head -n -1 | cat

# Read Implementation Order section
sed -n '/\\[Implementation Order\\]/,$p' implementation_plan.md | cat
""";
    }
}
