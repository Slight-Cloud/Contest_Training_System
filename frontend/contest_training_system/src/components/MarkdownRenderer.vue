<template>
  <div class="markdown-renderer" v-if="content" v-html="renderedHtml"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MarkdownIt from 'markdown-it';
import texmath from 'markdown-it-texmath';
import katex from 'katex';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css';
import 'katex/dist/katex.min.css';

interface Props {
  content: string;
  enableHighlight?: boolean;
  enableLineNumbers?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  enableHighlight: true,
  enableLineNumbers: true,
});

// 简单的 HTML 转义函数
function escapeHtml(str: string): string {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// 兼容性处理：确保 MarkdownIt 构造函数正确获取
// 在某些构建环境中，CJS/ESM 互操作可能导致 default 导出问题
const MarkdownItClass = (MarkdownIt as any).default || MarkdownIt;

// 预处理内容：规范化 LaTeX 公式分隔符
// 处理 `$ A $` 这种前后有空格的格式，转换为 `$A$`
function preprocessLatex(content: string): string {
  // 处理行内公式：$ ... $ 转换为紧凑格式（移除 $ 内侧的空格）
  // 匹配 $ 开头，后面可能有空格，然后是非$内容，再可能有空格，最后 $
  let result = content;
  
  // 处理 `$ content $` -> `$content$` (移除 $ 内侧空格)
  result = result.replace(/\$\s+([^$]+?)\s+\$/g, (match, inner) => {
    // 检查内容是否像是数学公式（包含字母、数字、运算符等）
    // 避免误处理普通文本中的 $ 符号
    if (/[a-zA-Z0-9\\^_{}+\-*/=<>]/.test(inner)) {
      return `$${inner.trim()}$`;
    }
    return match;
  });
  
  // 处理 `$content $` 或 `$ content$` 的情况
  result = result.replace(/\$\s+([^$]+?)\$/g, (match, inner) => {
    if (/[a-zA-Z0-9\\^_{}+\-*/=<>]/.test(inner)) {
      return `$${inner.trim()}$`;
    }
    return match;
  });
  result = result.replace(/\$([^$]+?)\s+\$/g, (match, inner) => {
    if (/[a-zA-Z0-9\\^_{}+\-*/=<>]/.test(inner)) {
      return `$${inner.trim()}$`;
    }
    return match;
  });
  
  return result;
}

const md = new MarkdownItClass({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: (str: string, lang: string): string => {
    if (props.enableHighlight && lang && hljs.getLanguage(lang)) {
      try {
        const highlighted = hljs.highlight(str, {
          language: lang,
          ignoreIllegals: true
        }).value;

        if (props.enableLineNumbers) {
          // 简单的行号处理
          const lines = highlighted.trim().split('\n');
          const numberedLines = lines.map((line, index) => {
            return `<span class="line-number">${index + 1}</span><span class="line-content">${line || ' '}</span>`;
          }).join('\n');
          return `<pre class="hljs with-line-numbers"><code>${numberedLines}</code></pre>`;
        }

        return `<pre class="hljs"><code>${highlighted}</code></pre>`;
      } catch (e) {
        console.warn('Highlight error:', e);
      }
    }
    // 默认转义 - 使用自定义的 escapeHtml 函数避免循环引用
    return `<pre class="hljs"><code>${escapeHtml(str)}</code></pre>`;
  },
});

// Use the texmath plugin with katex
// Configure it to allow dollars and set strict to false for better Unicode support
md.use(texmath, {
  engine: katex,
  delimiters: 'dollars',
  katexOptions: { 
    strict: false,  // 关闭严格模式，允许 Unicode 字符
    throwOnError: false,  // 出错时不抛异常，而是显示错误信息
    trust: true,  // 信任输入
    macros: { 
      "\\RR": "\\mathbb{R}",
      "\\NN": "\\mathbb{N}",
      "\\ZZ": "\\mathbb{Z}",
    } 
  }
});

const renderedHtml = computed(() => {
  if (!props.content) return '';
  try {
    // 确保 content 是字符串
    const safeContent = String(props.content);
    // 预处理 LaTeX 公式分隔符
    const processedContent = preprocessLatex(safeContent);
    const result = md.render(processedContent);
    console.log('Markdown rendered:', safeContent.substring(0, 100), '->', result.substring(0, 200));
    return result;
  } catch (e) {
    console.error('Markdown rendering error:', e);
    return '<p style="color: var(--el-color-danger)">Markdown 渲染失败</p>';
  }
});
</script>

<style scoped>
.markdown-renderer {
  color: var(--text-primary);
  font-family: 'Inter', 'Segoe UI', -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  word-wrap: break-word;
  font-size: 15px;
}

/* 标题样式 */
.markdown-renderer :deep(h1),
.markdown-renderer :deep(h2),
.markdown-renderer :deep(h3),
.markdown-renderer :deep(h4),
.markdown-renderer :deep(h5),
.markdown-renderer :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: var(--text-primary);
}

.markdown-renderer :deep(h1) {
  font-size: 2em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid var(--border-default);
}

.markdown-renderer :deep(h2) {
  font-size: 1.5em;
  padding-bottom: 0.3em;
  border-bottom: 1px solid var(--border-muted);
}

.markdown-renderer :deep(h3) {
  font-size: 1.25em;
}

.markdown-renderer :deep(h4) {
  font-size: 1.1em;
}

.markdown-renderer :deep(h5),
.markdown-renderer :deep(h6) {
  font-size: 1em;
}

/* 段落与文本 */
.markdown-renderer :deep(p) {
  margin: 0 0 16px;
  line-height: 1.6;
}

.markdown-renderer :deep(strong) {
  font-weight: 600;
  color: var(--text-primary);
}

.markdown-renderer :deep(em) {
  font-style: italic;
}

/* 列表 */
.markdown-renderer :deep(ul),
.markdown-renderer :deep(ol) {
  padding-left: 2em;
  margin: 0 0 16px;
}

.markdown-renderer :deep(li) {
  margin: 0.25em 0;
  line-height: 1.6;
}

.markdown-renderer :deep(li > p) {
  margin: 0.25em 0;
}

/* 链接 */
.markdown-renderer :deep(a) {
  color: var(--text-link);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.markdown-renderer :deep(a:hover) {
  color: var(--accent-emphasis);
  text-decoration: underline;
}

/* 引用块 */
.markdown-renderer :deep(blockquote) {
  margin: 0 0 16px;
  padding: 0 1em;
  color: var(--text-secondary);
  border-left: 0.25em solid var(--border-emphasis);
  background-color: var(--bg-canvas-inset);
  border-radius: var(--radius-sm);
}

.markdown-renderer :deep(blockquote p) {
  margin: 0.5em 0;
}

/* 代码块 */
.markdown-renderer :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: var(--bg-canvas-inset);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-default);
  margin: 0 0 16px;
}

.markdown-renderer :deep(pre code) {
  background-color: transparent;
  padding: 0;
  margin: 0;
  border: 0;
  word-break: normal;
  white-space: pre;
  color: var(--text-primary);
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

/* 行内代码 */
.markdown-renderer :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(110, 118, 129, 0.4);
  border-radius: var(--radius-sm);
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  color: var(--text-primary);
}

.markdown-renderer :deep(p code),
.markdown-renderer :deep(li code) {
  color: var(--accent-emphasis);
}

/* 行号样式 */
.markdown-renderer :deep(.line-number) {
  user-select: none;
  opacity: 0.4;
  margin-right: 1em;
  display: inline-block;
  width: 2em;
  text-align: right;
  color: var(--text-tertiary);
}

.markdown-renderer :deep(.line-content) {
  display: inline;
}

/* 表格 */
.markdown-renderer :deep(table) {
  border-spacing: 0;
  border-collapse: collapse;
  margin: 0 0 16px;
  width: 100%;
  overflow: hidden;
  border-radius: var(--radius-md);
}

.markdown-renderer :deep(table th),
.markdown-renderer :deep(table td) {
  padding: 8px 13px;
  border: 1px solid var(--border-default);
  text-align: left;
}

.markdown-renderer :deep(table th) {
  font-weight: 600;
  background-color: var(--bg-canvas-inset);
  color: var(--text-primary);
}

.markdown-renderer :deep(table tr) {
  background-color: var(--bg-surface);
}

.markdown-renderer :deep(table tr:nth-child(2n)) {
  background-color: var(--bg-canvas-subtle);
}

.markdown-renderer :deep(table tr:hover) {
  background-color: var(--bg-hover);
}

/* 图片 */
.markdown-renderer :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: var(--radius-md);
  margin: 1em 0;
  box-shadow: var(--shadow-sm);
  background-color: var(--bg-canvas-inset);
}

/* 分隔线 */
.markdown-renderer :deep(hr) {
  height: 2px;
  padding: 0;
  margin: 24px 0;
  background-color: var(--border-default);
  border: 0;
  border-radius: 1px;
}

/* 任务列表 */
.markdown-renderer :deep(input[type="checkbox"]) {
  margin-right: 0.5em;
}

/* 数学公式占位（如果未来添加 KaTeX） */
.markdown-renderer :deep(.katex) {
  font-size: 1.1em;
}

.markdown-renderer :deep(.katex-display) {
  margin: 1em 0;
  overflow-x: auto;
  overflow-y: hidden;
}

/* 首行缩进（中文排版） */
.markdown-renderer :deep(p:not(:first-child)) {
  text-indent: 0;
}

/* 确保内容可读性 */
.markdown-renderer :deep(*) {
  max-width: 100%;
}

/* 代码块滚动条样式 */
.markdown-renderer :deep(pre::-webkit-scrollbar) {
  height: 8px;
}

.markdown-renderer :deep(pre::-webkit-scrollbar-track) {
  background: var(--bg-canvas);
  border-radius: var(--radius-sm);
}

.markdown-renderer :deep(pre::-webkit-scrollbar-thumb) {
  background: var(--border-emphasis);
  border-radius: var(--radius-sm);
}

.markdown-renderer :deep(pre::-webkit-scrollbar-thumb:hover) {
  background: var(--text-tertiary);
}
</style>
