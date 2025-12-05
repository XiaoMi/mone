# Skill System Usage Guide

## Overview

The Skill System allows you to define reusable task templates (skills) that AI agents can use to perform specific tasks consistently.

## Architecture

### Components

1. **SkillDocument** (`bo/SkillDocument.java`)
   - Data model for skill definitions
   - Contains: name, description, location, content

2. **SkillService** (`service/SkillService.java`)
   - Loads skills from `.hive/skills/` directory
   - Parses markdown files with YAML front matter
   - Formats skills for prompt inclusion

3. **MonerSystemPrompt** (`prompt/MonerSystemPrompt.java`)
   - Integrates skills into the system prompt
   - Makes skills available to AI agents

## Creating Skills

### 1. Create Skills Directory

```bash
mkdir -p .hive/skills
```

### 2. Define a Skill

Create a markdown file in `.hive/skills/` with YAML front matter:

```markdown
---
name: skill-name
description: Brief description of what this skill does
---

# Skill Content

Your skill definition goes here...

## Steps

1. First step
2. Second step
...
```

### Example: Code Review Skill

File: `.hive/skills/code-review.md`

```markdown
---
name: code-review
description: Perform comprehensive code review following best practices
---

# Code Review Skill

## Review Checklist

### 1. Code Quality
- Check for code readability and maintainability
- Verify proper naming conventions
...

### 2. Security
- Identify potential security vulnerabilities
...
```

## Using Skills

### AI Agent Usage

When skills are available, the AI agent can request a skill definition:

```xml
<skill_request>
<skill_name>code-review</skill_name>
</skill_request>
```

The system will return the skill's complete definition (content), which the AI can then use as a guide.

### Available Skills Display

When skills are loaded, they appear in the system prompt under the "SKILLS" section:

```
SKILLS

Skills are reusable definitions that can help you accomplish specific tasks...

## Available Skills

**code-review**
- Description: Perform comprehensive code review following best practices
- Location: /path/to/.hive/skills/code-review.md

**another-skill**
- Description: Another skill description
- Location: /path/to/.hive/skills/another-skill.md
```

## Skill File Format

### Required Fields

- `name`: Unique identifier for the skill
- `description`: Brief explanation of the skill's purpose

### Optional Fields

You can add any custom YAML fields that your application needs.

### Content

The markdown content after the front matter is the skill definition that will be returned when requested.

## Implementation Details

### Loading Process

1. `SkillService.loadSkills(hiveCwd)` scans `.hive/skills/` directory
2. Parses each `.md` file using `MarkdownParserService`
3. Extracts `name` and `description` from YAML front matter
4. Stores skill content for later retrieval

### Integration in Prompt

In `MonerSystemPrompt.mcpPrompt()`:

```java
// Load skills
SkillService skillService = new SkillService();
List<SkillDocument> skills = skillService.loadSkills(MonerSystemPrompt.hiveCwd(role));

// Add to prompt data
data.put("skillList", skills);
data.put("enableSkills", !skills.isEmpty());
data.put("skillsPrompt", skillService.formatSkillsForPrompt(skills));
```

### Template Rendering

The MCP_PROMPT template includes conditional skill section:

```
<% if(enableSkills) { %>
====

SKILLS

...skill information...

====
<% } %>
```

## Best Practices

### 1. Skill Naming

- Use kebab-case for skill names (e.g., `code-review`, `api-design`)
- Keep names short but descriptive
- Avoid special characters

### 2. Skill Descriptions

- Write clear, concise descriptions
- Focus on what the skill helps accomplish
- Keep under 100 characters

### 3. Skill Content

- Structure content with clear headers
- Use checklists for step-by-step processes
- Include examples where helpful
- Keep focused on a single purpose

### 4. Organization

- Group related skills in subdirectories if needed
- Use consistent formatting across skills
- Document dependencies between skills

## Examples

### API Design Skill

```markdown
---
name: api-design
description: Design RESTful APIs following best practices
---

# API Design Principles

## Resource Naming
- Use plural nouns for collections
- Use kebab-case for multi-word resources
...
```

### Database Schema Design

```markdown
---
name: db-schema-design
description: Design database schemas with normalization and optimization
---

# Database Schema Design

## Normalization
1. First Normal Form (1NF)
2. Second Normal Form (2NF)
...
```

## Troubleshooting

### Skills Not Loading

1. Check directory path: `.hive/skills/` must exist
2. Verify file extension: must be `.md`
3. Check YAML format: must have `---` delimiters
4. Ensure `name` field is present

### Skills Not Appearing

1. Check logs for parsing errors
2. Verify `hiveCwd` path is correct
3. Ensure skills directory has read permissions

## Future Enhancements

Potential improvements to the skill system:

1. Skill versioning
2. Skill dependencies
3. Skill parameters/variables
4. Skill inheritance
5. Remote skill repositories
