<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
/* Apple Design 全局样式 */
:root {
  /* 颜色变量 - Apple Design System */
  --color-primary: #007AFF;
  --color-primary-hover: #0051D5;
  --color-success: #34C759;
  --color-warning: #FF9500;
  --color-danger: #FF3B30;
  
  /* 灰度系统 */
  --color-text-primary: #1C1C1E;
  --color-text-secondary: #3C3C43;
  --color-text-tertiary: #48484A;
  --color-text-quaternary: #636366;
  --color-text-light: #8E8E93;
  
  /* 背景色 */
  --color-bg-primary: #FFFFFF;
  --color-bg-secondary: #F2F2F7;
  --color-bg-tertiary: #E5E5EA;
  --color-bg-elevated: rgba(255, 255, 255, 0.8);
  
  /* 边框和分割线 */
  --color-border: rgba(60, 60, 67, 0.12);
  --color-separator: rgba(60, 60, 67, 0.36);
  
  /* 阴影 */
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.07);
  --shadow-lg: 0 10px 15px rgba(0, 0, 0, 0.08);
  --shadow-xl: 0 20px 25px rgba(0, 0, 0, 0.1);
  
  /* 圆角 */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  
  /* 动画 */
  --transition-fast: 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-base: 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'SF Pro Display', 'Helvetica Neue', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--color-text-primary);
  background-color: var(--color-bg-secondary);
  line-height: 1.5;
  font-size: 16px;
}

#app {
  min-height: 100vh;
  transition: background-color var(--transition-base);
}

/* 全局链接样式 */
a {
  color: var(--color-primary);
  text-decoration: none;
  transition: color var(--transition-fast);
}

a:hover {
  color: var(--color-primary-hover);
}

/* 全局按钮样式 */
button {
  font-family: inherit;
  cursor: pointer;
  transition: all var(--transition-fast);
}

/* 全局输入框样式 */
input, textarea, select {
  font-family: inherit;
  font-size: inherit;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
  transition: background-color var(--transition-fast);
}

::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.3);
}

/* 全局选中样式 */
::selection {
  background-color: rgba(0, 122, 255, 0.2);
  color: var(--color-text-primary);
}

/* 全局焦点样式 */
:focus {
  outline: none;
}

:focus-visible {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

/* 通用工具类 */
.glass-effect {
  background: var(--color-bg-elevated);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--color-border);
}

.text-secondary {
  color: var(--color-text-secondary);
}

.text-tertiary {
  color: var(--color-text-tertiary);
}

.text-light {
  color: var(--color-text-light);
}

/* 响应式文字大小 */
@media (max-width: 768px) {
  body {
    font-size: 15px;
  }
}

/* Element Plus 全局样式覆盖 */
.el-button--primary {
  background-color: var(--color-primary) !important;
  border-color: var(--color-primary) !important;
  font-weight: 500;
  transition: all var(--transition-fast);
}

.el-button--primary:hover {
  background-color: var(--color-primary-hover) !important;
  border-color: var(--color-primary-hover) !important;
}

.el-input__wrapper {
  border-radius: var(--radius-sm);
  box-shadow: none !important;
  border: 1px solid var(--color-border);
  transition: all var(--transition-fast);
}

.el-input__wrapper:hover {
  border-color: var(--color-separator);
}

.el-input.is-focus .el-input__wrapper {
  border-color: var(--color-primary);
}
</style>