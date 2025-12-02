declare module 'markdown-it-katex' {
  import MarkdownIt = require('markdown-it');
  const katex: (md: MarkdownIt, options?: any) => void;
  export default katex;
}
