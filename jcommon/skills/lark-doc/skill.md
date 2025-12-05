---
name: lark-docx-helper
description: 帮助用户创建飞书文档
---

# 基础规则
- 当用户需要将内容写入飞书文档时，请你直接使用，并且用户需要写入的内容，一定是一个markdown格式的。工具需要通过转换markdown成为飞书文档中需要的格式

# 功能说明
该技能用于创建飞书文档，自动完成以下步骤：
1. 创建新文档
2. 设置文档权限
3. 将markdown内容转换为飞书文档块
4. 插入内容到文档中

# 使用方法

## 命令格式
```bash
uv run scripts/create_document_with_markdown.py --title "文档标题" [--markdown "内容" | --file "文件路径"]
```

## 参数说明

### 必需参数
- `--title`: 文档标题
- `--markdown`: markdown格式的内容字符串（与--file二选一）
- `--file`: markdown文件路径（与--markdown二选一）

### 可选参数
- `--folder`: 创建到指定文件夹（folder token）
- `--external-access`: 外部访问权限
  - `open`: 允许外部访问
  - `closed`: 禁止外部访问（默认）
  - `allow_share_partner_tenant`: 允许分享给合作租户
- `--link-share`: 链接分享权限
  - `tenant_readable`: 租户成员可读
  - `tenant_editable`: 租户成员可编辑（默认）
  - `partner_tenant_readable`: 合作租户成员可读
  - `partner_tenant_editable`: 合作租户成员可编辑
  - `anyone_readable`: 任何人可读（需要external-access=open）
  - `anyone_editable`: 任何人可编辑（需要external-access=open）
  - `closed`: 禁止链接分享
- `--security`: 安全权限（谁可以复制、打印、下载）
  - `anyone_can_view`: 有查看权限的用户（默认）
  - `anyone_can_edit`: 有编辑权限的用户
  - `only_full_access`: 仅完全控制权限的用户
- `--comment`: 评论权限
  - `anyone_can_view`: 有查看权限的用户可评论（默认）
  - `anyone_can_edit`: 有编辑权限的用户可评论
- `--debug`: 显示调试信息
- `--log-level`: 日志级别（DEBUG, INFO, WARNING, ERROR）

# 请求示例

## 示例1：创建简单文档
```bash
python scripts/create_document_with_markdown.py \
  --title "会议纪要" \
  --markdown "# 会议纪要\n\n## 时间\n2024-12-05\n\n## 参与人\n- 张三\n- 李四\n\n## 主要内容\n1. 项目进度汇报\n2. 下周计划"
```

## 示例2：从文件创建文档
```bash
python scripts/create_document_with_markdown.py \
  --title "项目文档" \
  --file README.md
```

## 示例3：创建公开可读文档
```bash
python scripts/create_document_with_markdown.py \
  --title "公开文档" \
  --file document.md \
  --external-access open \
  --link-share anyone_readable
```

## 示例4：创建到指定文件夹
```bash
python scripts/create_document_with_markdown.py \
  --title "团队文档" \
  --file team_doc.md \
  --folder "fldcnxxxxxxxxxxxxxx"
```

# 响应示例

## 成功响应
```
============================================================
Creating document: 会议纪要
============================================================

📄 Creating document: 会议纪要
✅ Document created: NxBBdaXTkoxWqkxCs6TcF2lanpc

🔒 Setting document permissions...
✅ Permissions set: link_share=tenant_editable, external_access=closed

📝 Converting markdown (120 characters)...
✅ Markdown converted successfully

📥 Inserting blocks into document...
✅ Content inserted successfully

============================================================
🎉 Document created successfully!

📎 Document URL: https://mi.feishu.cn/docx/NxBBdaXTkoxWqkxCs6TcF2lanpc
============================================================
```

## 错误响应示例

### 文件不存在
```
Error: File 'document.md' not found.
```

### 创建失败
```
============================================================
Creating document: 测试文档
============================================================

📄 Creating document: 测试文档
❌ Document creation failed: permission denied

❌ Failed to create document
```

### 内容插入失败
```
============================================================
Creating document: 测试文档
============================================================

📄 Creating document: 测试文档
✅ Document created: NxBBdaXTkoxWqkxCs6TcF2lanpc

🔒 Setting document permissions...
✅ Permissions set: link_share=tenant_editable, external_access=closed

📝 Converting markdown (50 characters)...
✅ Markdown converted successfully

📥 Inserting blocks into document...
❌ Block insertion failed: invalid block format

============================================================
⚠️ Document created but content insertion had issues

📎 Document URL: https://mi.feishu.cn/docx/NxBBdaXTkoxWqkxCs6TcF2lanpc
============================================================
```

# 支持的Markdown元素
- **标题**：# H1, ## H2, ### H3, #### H4, ##### H5, ###### H6
- **文本格式**：
  - 粗体：`**文本**` 或 `__文本__`
  - 斜体：`*文本*` 或 `_文本_`
  - 删除线：`~~文本~~`
  - 行内代码：`` `代码` ``
- **链接**：`[链接文字](URL)`
- **图片**：`![替代文字](图片URL)`
- **列表**：
  - 有序列表：`1. 项目`
  - 无序列表：`- 项目` 或 `* 项目`
- **引用**：`> 引用文本`
- **代码块**：` ```语言 代码 ``` `
- **表格**：使用 `|` 分隔列
- **水平线**：`---` 或 `***` 或 `___`

# 环境变量配置

在使用前需要配置以下环境变量（在项目根目录的 `.env` 文件中）：

```
YOUR_APP_ID=你的飞书应用ID
YOUR_APP_SECRET=你的飞书应用密钥
```

# 注意事项

1. 文档创建后会返回文档URL，可直接在浏览器中打开
2. 默认权限为租户内可编辑，如需公开分享请设置相应权限
3. Markdown转换会保持原始内容的格式和顺序
4. 支持嵌套的列表和复杂的表格结构
5. 图片链接需要是可公开访问的URL
