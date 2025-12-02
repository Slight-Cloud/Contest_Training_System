# Midnight Gold 主题使用指南

## 🎨 设计哲学

**Midnight Gold** 是一套深邃、精密、优雅的暗色主题,灵感来自高级代码编辑器和金融终端。

### 核心特点
- **深邃**: 基于极深蓝灰 (#0b0e14) 的背景
- **精密**: 精确的间距、对齐和排版
- **优雅**: 暗金 (#d4b168) 和板岩蓝 (#8b80f9) 的双重强调色
- **低饱和度**: 所有颜色都经过精心调制,不刺眼

---

## 🎯 角色身份色彩系统

### 可用角色
| 角色 | 颜色 | CSS 变量 | 用途 |
|------|------|----------|------|
| **管理员** | Muted Gold (#cfb687) | `--role-admin` | 关键功能、Admin 标识 |
| **教师** | Muted Iris (#8b80f9) | `--role-teacher` | 教学相关功能 |
| **学生** | Muted Teal (#4fd1c5) | `--role-student` | 学生相关功能 |
| **评委** | Muted Orange (#f6ad55) | `--role-judge` | 评审相关功能 |

---

## 💻 在 Vue 组件中使用

### 1. 动态角色边框 (头像)

```vue
<template>
  <el-avatar 
    :size="48" 
    :class="`role-border-${userRole.toLowerCase()}`"
  >
    {{ initials }}
  </el-avatar>
</template>

<script setup>
import { computed } from 'vue';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();
const userRole = computed(() => userStore.role || 'student');
const initials = computed(() => {
  return userStore.nickname?.slice(0, 2).toUpperCase() || 'U';
});
</script>
```

**效果**: 头像会根据角色显示不同颜色的发光边框。

---

### 2. 角色标签 (Tags)

```vue
<template>
  <el-tag 
    :class="`role-tag-${role.toLowerCase()}`"
    size="small"
  >
    {{ roleLabel }}
  </el-tag>
</template>

<script setup>
defineProps({
  role: {
    type: String,
    required: true,
    validator: (val) => ['admin', 'teacher', 'student', 'judge'].includes(val.toLowerCase())
  },
  roleLabel: String
});
</script>
```

**可用 Class**:
- `.role-tag-admin` - 暗金标签
- `.role-tag-teacher` - 紫色标签
- `.role-tag-student` - 青色标签
- `.role-tag-judge` - 橙色标签

---

### 3. 角色文字颜色

```vue
<template>
  <div>
    <h3 :class="`text-role-${currentRole}`">
      {{ userName }}
    </h3>
    <span class="text-role-admin">管理员权限</span>
  </div>
</template>
```

**可用 Class**:
- `.text-role-admin`
- `.text-role-teacher`
- `.text-role-student`
- `.text-role-judge`

---

### 4. 角色背景 (Subtle)

```vue
<template>
  <div :class="`bg-role-${role}-subtle`" style="padding: 16px; border-radius: 8px;">
    <p>这是一个带有角色色彩的区域</p>
  </div>
</template>
```

---

## 📋 表格布局最佳实践

### 完整示例

```vue
<template>
  <el-table :data="tableData" stripe>
    <!-- 普通列 -->
    <el-table-column prop="id" label="ID" width="80" />
    <el-table-column prop="name" label="名称" min-width="150" />
    
    <!-- 状态列 - 添加 class-name -->
    <el-table-column 
      prop="status" 
      label="状态" 
      class-name="status-column"
      min-width="100"
    >
      <template #default="{ row }">
        <el-tag :type="getStatusType(row.status)">
          {{ row.status }}
        </el-tag>
      </template>
    </el-table-column>
    
    <!-- 操作列 - 添加 class-name -->
    <el-table-column 
      label="操作" 
      class-name="action-column"
      min-width="180"
      fixed="right"
    >
      <template #default="{ row }">
        <div class="table-actions">
          <el-button size="small" type="primary" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<style scoped>
.table-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
```

### 关键点
1. **状态列**: 使用 `class-name="status-column"` 自动应用 `min-width: 100px`
2. **操作列**: 使用 `class-name="action-column"` 自动应用 `min-width: 180px` 和允许换行
3. **按钮组**: 包裹在 `.table-actions` div 中,自动应用 `flex` 布局

---

## 🎨 Drawer (抽屉) 字段列表

### 使用 Key-Value 布局

```vue
<template>
  <el-drawer
    v-model="drawerVisible"
    title="用户详情"
    size="500px"
  >
    <div class="drawer-field-list">
      <div class="drawer-field-item">
        <div class="drawer-field-label">用户名</div>
        <div class="drawer-field-value">{{ user.username }}</div>
      </div>
      
      <div class="drawer-field-item">
        <div class="drawer-field-label">角色</div>
        <div class="drawer-field-value">
          <el-tag :class="`role-tag-${user.role.toLowerCase()}`">
            {{ user.roleLabel }}
          </el-tag>
        </div>
      </div>
      
      <div class="drawer-field-item">
        <div class="drawer-field-label">邮箱</div>
        <div class="drawer-field-value">{{ user.email }}</div>
      </div>
      
      <div class="drawer-field-item">
        <div class="drawer-field-label">注册时间</div>
        <div class="drawer-field-value">{{ user.createdAt }}</div>
      </div>
    </div>
  </el-drawer>
</template>
```

**效果**: 
- Label 灰色,左对齐,固定宽度 100px
- Value 亮色,左对齐,自动扩展
- 每项之间有细微分割线
- 无老旧表格线框

---

## 📝 表单输入框优化

### Dialog 表单示例

```vue
<template>
  <el-dialog
    v-model="dialogVisible"
    title="编辑赛事"
    width="600px"
  >
    <el-form :model="form" label-width="120px">
      <!-- 必填项会自动显示暗红色 * -->
      <el-form-item label="赛事名称" required>
        <el-input 
          v-model="form.name" 
          placeholder="请输入赛事名称"
        />
      </el-form-item>
      
      <el-form-item label="开始时间" required>
        <el-date-picker 
          v-model="form.startTime" 
          type="datetime"
          placeholder="选择开始时间"
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="描述">
        <el-input 
          v-model="form.description" 
          type="textarea"
          :rows="4"
          placeholder="请输入赛事描述"
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>
```

**特点**:
- 输入框高度自动 40px
- Label 和 Input 间距增加到 8px
- 必填项星号为暗红色 (#e5534b)
- Focus 时显示暗金色发光边框

---

## 🌈 按钮色彩使用指南

### 按钮类型推荐

```vue
<template>
  <!-- 主要操作 (暗金色) - 用于关键 CTA -->
  <el-button type="primary">创建赛事</el-button>
  
  <!-- 成功操作 (绿色) -->
  <el-button type="success">通过审核</el-button>
  
  <!-- 警告操作 (黄色) -->
  <el-button type="warning">暂停</el-button>
  
  <!-- 危险操作 (红色) -->
  <el-button type="danger">删除</el-button>
  
  <!-- 默认操作 (灰色) -->
  <el-button>取消</el-button>
  
  <!-- 文本按钮 -->
  <el-button type="primary" text>查看详情</el-button>
</template>
```

---

## 🎯 CSS 变量速查

### 背景层次
```css
var(--bg-depth-0)  /* #0b0e14 - App Background */
var(--bg-depth-1)  /* rgba(26, 32, 44, 0.6) - Card */
var(--bg-depth-2)  /* rgba(255, 255, 255, 0.03) - Input/Row */
var(--bg-depth-3)  /* rgba(255, 255, 255, 0.06) - Hover */
```

### 文字
```css
var(--text-heading)     /* rgb(226, 232, 240) - 标题 */
var(--text-primary)     /* rgb(166, 173, 187) - 主要文字 */
var(--text-secondary)   /* rgb(148, 163, 184) - 次要文字 */
```

### 强调色
```css
var(--gold-primary)     /* #d4b168 - 暗金主色 */
var(--slate-blue)       /* #8b80f9 - 板岩蓝 */
```

### 角色色彩
```css
var(--role-admin)       /* #cfb687 */
var(--role-teacher)     /* #8b80f9 */
var(--role-student)     /* #4fd1c5 */
var(--role-judge)       /* #f6ad55 */
```

---

## 🔧 迁移清单

如果你正在从旧主题迁移,请检查:

- [ ] 所有头像添加 `role-border-*` class
- [ ] 所有角色标签使用 `role-tag-*` class
- [ ] 表格操作列使用 `class-name="action-column"`
- [ ] Drawer 使用 `.drawer-field-list` 布局
- [ ] 检查表单 label 间距是否合适
- [ ] 主按钮用于关键操作 (暗金色)

---

## 📞 技术支持

如有问题,请参考 `style.css` 中的注释或联系前端团队。

**Theme Version**: Midnight Gold v1.0  
**Last Updated**: 2025-12-02
