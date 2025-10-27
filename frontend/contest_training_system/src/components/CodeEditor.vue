<template>
  <div ref="editorContainer" class="code-editor"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import * as monaco from 'monaco-editor';

interface Props {
  modelValue: string;
  language?: string;
  theme?: 'vs-dark' | 'vs-light';
  readOnly?: boolean;
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  language: 'cpp',
  theme: 'vs-dark',
  readOnly: false,
  height: '500px',
});

const emit = defineEmits<{
  'update:modelValue': [value: string];
  change: [value: string];
}>();

const editorContainer = ref<HTMLElement | null>(null);
let editor: monaco.editor.IStandaloneCodeEditor | null = null;

// 语言映射
const languageMap: Record<string, string> = {
  c: 'c',
  cpp: 'cpp',
  'c++': 'cpp',
  java: 'java',
  python: 'python',
  python3: 'python',
  javascript: 'javascript',
  typescript: 'typescript',
  go: 'go',
  rust: 'rust',
};

const getMonacoLanguage = (lang: string) => {
  return languageMap[lang.toLowerCase()] || 'plaintext';
};

onMounted(() => {
  if (!editorContainer.value) return;

  // 配置 Monaco Editor
  editor = monaco.editor.create(editorContainer.value, {
    value: props.modelValue,
    language: getMonacoLanguage(props.language),
    theme: props.theme,
    readOnly: props.readOnly,
    automaticLayout: true,
    fontSize: 14,
    minimap: {
      enabled: true,
    },
    scrollBeyondLastLine: false,
    folding: true,
    lineNumbers: 'on',
    renderWhitespace: 'selection',
    tabSize: 4,
    insertSpaces: true,
    wordWrap: 'off',
    contextmenu: true,
    formatOnPaste: true,
    formatOnType: true,
  });

  // 监听内容变化
  editor.onDidChangeModelContent(() => {
    const value = editor?.getValue() || '';
    emit('update:modelValue', value);
    emit('change', value);
  });

  // 设置容器高度
  if (editorContainer.value) {
    editorContainer.value.style.height = props.height;
  }
});

// 监听语言变化
watch(
  () => props.language,
  (newLang) => {
    if (editor) {
      const model = editor.getModel();
      if (model) {
        monaco.editor.setModelLanguage(model, getMonacoLanguage(newLang));
      }
    }
  }
);

// 监听主题变化
watch(
  () => props.theme,
  (newTheme) => {
    if (editor) {
      monaco.editor.setTheme(newTheme);
    }
  }
);

// 监听外部值变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (editor && editor.getValue() !== newValue) {
      editor.setValue(newValue);
    }
  }
);

onUnmounted(() => {
  if (editor) {
    editor.dispose();
  }
});

// 导出方法供父组件调用
defineExpose({
  focus: () => editor?.focus(),
  setValue: (value: string) => editor?.setValue(value),
  getValue: () => editor?.getValue() || '',
});
</script>

<style scoped>
.code-editor {
  width: 100%;
  border: 1px solid var(--border-default);
  border-radius: 8px;
  overflow: hidden;
}
</style>

