# LiteFlow插件前端编辑功能 - 完整规划

**创建时间**: 2026-03-04  
**状态**: 研究完成，待确认关键决策  
**研究来源**: 前端架构探索 + LiteFlow最佳实践研究

---

## 一、项目规范分析（已确认）

### 1.1 技术栈
- **前端框架**: Vue 3 (Composition API + `<script setup>`)
- **UI组件库**: Naive UI（与magic-boot主项目一致）
- **代码编辑器**: Monaco Editor（VS Code核心，已在magic-editor中使用）
- **构建工具**: Vite
- **包管理**: npm

### 1.2 插件前端集成规范
**参考实现**: `magic-api-plugin-job/src/console/src/index.js`

```javascript
// 标准插件注册模式
export default (opt) => ({
  resource: [{
    type: 'job',
    icon: '#magic-job-job',
    title: 'job.name',
    service: MagicJob(opt.bus, opt.constants, opt.Message, opt.request)
  }],
  toolbars: [{
    type: 'job',
    title: 'job.title',
    icon: 'parameter',
    component: EnhancedJobInfo
  }]
})
```

### 1.3 目录结构规范
```
magic-api-plugin-liteflow/
├── src/console/
│   ├── package.json          # 前端依赖配置
│   ├── vite.config.js        # Vite构建配置
│   ├── src/
│   │   ├── index.js          # 插件入口（注册到magic-editor）
│   │   ├── components/       # Vue组件
│   │   │   ├── LiteflowEditor.vue       # 主编辑器组件
│   │   │   ├── FlowList.vue             # 流程列表
│   │   │   ├── ComponentList.vue        # 组件列表
│   │   │   ├── FlowEditor.vue           # 流程编辑器
│   │   │   └── GroovyEditor.vue         # Groovy脚本编辑器
│   │   ├── service/           # 数据服务
│   │   │   └── liteflow.js    # API调用封装
│   │   └── utils/             # 工具函数
│   │       ├── el-parser.js   # EL表达式解析
│   │       └── monaco-setup.js # Monaco配置
│   └── dist/                  # 构建产物
└── pom.xml                    # Maven构建配置（已配置前端构建）
```

### 1.4 后端API（已完成）
- ✅ `GET /api/liteflow/flows` - 获取所有流程文件列表
- ✅ `GET /api/liteflow/flows/{fileName}` - 获取流程内容
- ✅ `POST /api/liteflow/flows` - 创建流程（body: `{fileName, content}`）
- ✅ `PUT /api/liteflow/flows/{fileName}` - 更新流程
- ✅ `DELETE /api/liteflow/flows/{fileName}` - 删除流程
- ✅ 组件API同理：`/api/liteflow/components/*`
- ✅ `POST /api/liteflow/refresh` - 刷新存储缓存

---

## 二、编辑器方案对比

### 2.1 三种方案对比

| 维度 | 方案A：纯文本编辑 | 方案B：可视化拖拽 | 方案C：混合模式 |
|------|------------------|------------------|----------------|
| **开发周期** | 3-5天 | 2-3周 | 3-4周 |
| **用户体验** | 适合熟练开发者 | 直观易用 | 最灵活 |
| **技术复杂度** | 低 | 高（需LogicFlow/X6） | 最高（双向转换） |
| **维护成本** | 低 | 中 | 高 |
| **适用场景** | MVP、技术团队 | 业务人员 | 混合团队 |

### 2.2 推荐实施路径：渐进式开发

**阶段1（MVP，推荐第一版）**：
- 纯文本EL编辑器 + Monaco Editor
- 基础的流程和组件CRUD
- 语法高亮和代码提示
- **工作量**: 3-5天

**阶段2（增强版）**：
- EL表达式语法验证
- 流程可视化预览（只读）
- 流程测试执行
- **工作量**: 3-5天

**阶段3（完整版，可选）**：
- 完整的可视化拖拽编辑器
- EL与可视化双向转换
- 高级调试功能
- **工作量**: 2-3周

---

## 三、核心功能规划

### 3.1 第一版（MVP）必须功能

#### 流程管理
- [x] 流程列表展示（左侧树形/列表）
- [x] 创建新流程（新建.el.xml或.el文件）
- [x] 编辑流程（Monaco编辑器，EL语法高亮）
- [x] 保存流程（Ctrl+S快捷键 + 自动保存）
- [x] 删除流程（确认对话框）
- [x] 流程重命名

#### 组件管理
- [x] Groovy组件列表
- [x] 创建新组件（基于模板）
- [x] 编辑组件（Groovy语法高亮）
- [x] 保存组件
- [x] 删除组件

#### 编辑器增强
- [x] **EL表达式语法高亮**（自定义Monarch Tokens）
- [x] **Groovy语法高亮**（Monaco内置）
- [x] **代码补全**（EL关键字 + LiteFlow上下文对象）
- [x] 代码折叠
- [x] 行号显示
- [x] 保存提示（未保存提醒）

### 3.2 第二版增强功能

- [ ] EL表达式语法验证
- [ ] 组件引用检查（验证组件是否存在）
- [ ] 流程可视化预览（EL → 流程图）
- [ ] 流程测试执行（调用后端执行接口）
- [ ] 代码片段库（常用EL模式）
- [ ] 组件模板库（script/switch_script/boolean_script）

### 3.3 第三版高级功能（可选）

- [ ] 完整的可视化拖拽编辑器
- [ ] EL与可视化双向转换
- [ ] 流程版本管理
- [ ] 流程调试（断点、变量查看）
- [ ] 性能分析工具

---

## 四、UI设计方案

### 方案A：左右分栏（推荐）

**优点**：
- 符合IDE习惯（VS Code、WebStorm）
- 空间利用率高
- 适合文件管理场景

**布局**：
```
┌──────────────┬────────────────────────────────┐
│  流程列表     │      Monaco 编辑器              │
│  组件列表     │      (EL / Groovy)              │
│              │                                │
│  [新建流程]   │      工具栏:                    │
│  [新建组件]   │      [保存] [格式化] [验证]      │
│              │                                │
│  📁 flows/   │      支持多标签页编辑            │
│    ├ flow1   │                                │
│    └ flow2   │                                │
│              │                                │
│  📦 components/                              │
│    ├ comp1   │                                │
│    └ comp2   │                                │
└──────────────┴────────────────────────────────┘
```

**技术实现**：
- 使用Naive UI的`<n-layout>`组件
- 左侧宽度：240px（可拖拽调整）
- 右侧自适应
- 支持多标签页（`<n-tabs>`）

### 方案B：标签页模式

**优点**：
- 流程和组件分离，逻辑清晰
- 避免列表过长

**缺点**：
- 切换频繁
- 不适合同时查看流程和组件

**布局**：
```
┌──────────────────────────────────────────────┐
│  [流程管理]  [组件管理]                        │
├──────────────────────────────────────────────┤
│  流程管理页:                                  │
│  ┌─────────┬───────────────────────┐         │
│  │ 流程列表 │   编辑器               │         │
│  └─────────┴───────────────────────┘         │
└──────────────────────────────────────────────┘
```

### 方案C：三栏布局

**优点**：
- 功能最完整
- 支持属性编辑

**缺点**：
- 空间拥挤（magic-editor右侧已有工具栏）
- 第一版不需要

**布局**：
```
┌────────┬──────────────────┬────────────────┐
│ 列表   │   Monaco编辑器    │   属性面板     │
│        │                  │   (节点信息)   │
│        │                  │   (组件参数)   │
└────────┴──────────────────┴────────────────┘
```

---

## 五、技术实现细节

### 5.1 Monaco Editor集成

#### EL表达式语法高亮
```javascript
// src/console/src/utils/monaco-setup.js
import * as monaco from 'monaco-editor';

// 注册LiteFlow EL语言
monaco.languages.register({ id: 'liteflow-el' });

// 配置语法高亮（Monarch Tokens）
monaco.languages.setMonarchTokensProvider('liteflow-el', {
  keywords: [
    'THEN', 'WHEN', 'IF', 'SELECT', 'FOR', 'WHILE', 
    'DO', 'CATCH', 'FINALLY', 'BREAK', 'CONTINUE'
  ],
  
  operators: [
    '=', '>', '<', '!', '~', '?', ':', 
    '==', '<=', '>=', '!=', '&&', '||'
  ],
  
  symbols: /[=><!~?:&|+\-*\/\^%]+/,
  
  tokenizer: {
    root: [
      // 关键字
      [/[A-Z]+/, { 
        cases: { 
          '@keywords': 'keyword',
          '@default': 'identifier' 
        } 
      }],
      
      // 字符串
      [/"([^"\\]|\\.)*$/, 'string.invalid'],
      [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],
      
      // 注释
      [/\/\/.*$/, 'comment'],
      [/\/\*/, 'comment', '@comment'],
      
      // 数字
      [/\d+\.\d+/, 'number.float'],
      [/\d+/, 'number'],
      
      // 标识符
      [/[a-zA-Z_$][\w$]*/, 'identifier'],
      
      // 操作符
      [/@symbols/, {
        cases: {
          '@operators': 'operator',
          '@default': ''
        }
      }],
      
      // 分隔符
      [/[{}()\[\]]/, '@brackets'],
      [/[;,.]/, 'delimiter']
    ],
    
    string: [
      [/[^\\"]+/, 'string'],
      [/\\./, 'string.escape'],
      [/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
    ],
    
    comment: [
      [/[^\/*]+/, 'comment'],
      [/\*\//, 'comment', '@pop'],
      [/[\/*]/, 'comment']
    ]
  }
});

// 代码补全
monaco.languages.registerCompletionItemProvider('liteflow-el', {
  provideCompletionItems: (model, position) => {
    const suggestions = [
      // EL关键字
      {
        label: 'THEN',
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: 'THEN(${1:a}, ${2:b})',
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: '串行编排'
      },
      {
        label: 'WHEN',
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: 'WHEN(${1:a}, ${2:b})',
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: '并行编排'
      },
      {
        label: 'IF',
        kind: monaco.languages.CompletionItemKind.Keyword,
        insertText: 'IF(${1:condition}, ${2:trueBranch}, ${3:falseBranch})',
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: '条件分支'
      },
      // 更多关键字...
    ];
    
    return { suggestions };
  }
});
```

#### Groovy脚本增强
```javascript
// 为Groovy添加LiteFlow上下文补全
monaco.languages.registerCompletionItemProvider('groovy', {
  triggerCharacters: ['.'],
  
  provideCompletionItems: (model, position) => {
    const textUntilPosition = model.getValueInRange({
      startLineNumber: 1,
      startColumn: 1,
      endLineNumber: position.lineNumber,
      endColumn: position.column
    });
    
    const suggestions = [];
    
    // LiteFlow上下文对象
    if (textUntilPosition.endsWith('defaultContext.') || 
        textUntilPosition.endsWith('c.')) {
      suggestions.push(
        {
          label: 'setData',
          kind: monaco.languages.CompletionItemKind.Method,
          insertText: 'setData("${1:key}", ${2:value})',
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: '设置上下文数据'
        },
        {
          label: 'getData',
          kind: monaco.languages.CompletionItemKind.Method,
          insertText: 'getData("${1:key}")',
          insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
          detail: '获取上下文数据'
        },
        {
          label: 'getRequestData',
          kind: monaco.languages.CompletionItemKind.Method,
          insertText: 'getRequestData()',
          detail: '获取请求数据'
        }
      );
    }
    
    // 脚本模板
    suggestions.push(
      {
        label: 'script-template',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          '// ${1:脚本说明}',
          'def ${2:result} = ${3:logic}',
          '',
          'defaultContext.setData("${4:key}", ${2:result})',
          ''
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        detail: 'LiteFlow脚本模板'
      }
    );
    
    return { suggestions };
  }
});
```

### 5.2 数据服务封装

```javascript
// src/console/src/service/liteflow.js
export default (request, Message) => {
  const BASE_URL = '/api/liteflow';
  
  return {
    // 流程管理
    flow: {
      list: () => request(`${BASE_URL}/flows`),
      
      get: (fileName) => request(`${BASE_URL}/flows/${encodeURIComponent(fileName)}`),
      
      create: (fileName, content) => request(`${BASE_URL}/flows`, {
        method: 'POST',
        body: { fileName, content }
      }),
      
      update: (fileName, content) => request(`${BASE_URL}/flows/${encodeURIComponent(fileName)}`, {
        method: 'PUT',
        body: { content }
      }),
      
      delete: (fileName) => request(`${BASE_URL}/flows/${encodeURIComponent(fileName)}`, {
        method: 'DELETE'
      })
    },
    
    // 组件管理
    component: {
      list: () => request(`${BASE_URL}/components`),
      
      get: (fileName) => request(`${BASE_URL}/components/${encodeURIComponent(fileName)}`),
      
      create: (fileName, content) => request(`${BASE_URL}/components`, {
        method: 'POST',
        body: { fileName, content }
      }),
      
      update: (fileName, content) => request(`${BASE_URL}/components/${encodeURIComponent(fileName)}`, {
        method: 'PUT',
        body: { content }
      }),
      
      delete: (fileName) => request(`${BASE_URL}/components/${encodeURIComponent(fileName)}`, {
        method: 'DELETE'
      })
    },
    
    // 刷新缓存
    refresh: () => request(`${BASE_URL}/refresh`, { method: 'POST' })
  };
};
```

### 5.3 主编辑器组件

```vue
<!-- src/console/src/components/LiteflowEditor.vue -->
<template>
  <n-layout has-sider style="height: 100%">
    <!-- 左侧列表 -->
    <n-layout-sider
      bordered
      :width="240"
      :native-scrollbar="false"
      collapsible
    >
      <n-menu
        :options="menuOptions"
        :value="activeTab"
        @update:value="handleTabChange"
      />
      
      <!-- 流程列表 -->
      <div v-if="activeTab === 'flows'" class="file-list">
        <n-button block @click="createFlow">
          <template #icon><n-icon><add-icon /></n-icon></template>
          新建流程
        </n-button>
        
        <n-tree
          :data="flowTree"
          :selected-keys="selectedFlow"
          @update:selected-keys="handleFlowSelect"
        />
      </div>
      
      <!-- 组件列表 -->
      <div v-else class="file-list">
        <n-button block @click="createComponent">
          <template #icon><n-icon><add-icon /></n-icon></template>
          新建组件
        </n-button>
        
        <n-tree
          :data="componentTree"
          :selected-keys="selectedComponent"
          @update:selected-keys="handleComponentSelect"
        />
      </div>
    </n-layout-sider>
    
    <!-- 右侧编辑器 -->
    <n-layout-content>
      <!-- 工具栏 -->
      <n-space justify="space-between" style="padding: 12px">
        <n-text strong>{{ currentFileName }}</n-text>
        
        <n-space>
          <n-button size="small" @click="handleSave">
            <template #icon><n-icon><save-icon /></n-icon></template>
            保存 (Ctrl+S)
          </n-button>
          
          <n-button size="small" @click="handleFormat">
            格式化
          </n-button>
          
          <n-button size="small" @click="handleValidate">
            验证
          </n-button>
        </n-space>
      </n-space>
      
      <!-- Monaco编辑器 -->
      <monaco-editor
        v-model:value="code"
        :language="currentLanguage"
        :options="editorOptions"
        style="height: calc(100% - 60px)"
        @save="handleSave"
      />
    </n-layout-content>
  </n-layout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useMessage } from 'naive-ui';
import MonacoEditor from '@/components/MonacoEditor.vue';
import { liteflowService } from '@/service/liteflow';

const message = useMessage();

// 状态
const activeTab = ref('flows');
const flows = ref([]);
const components = ref([]);
const selectedFlow = ref([]);
const selectedComponent = ref([]);
const code = ref('');
const currentFileName = ref('');

// 计算属性
const currentLanguage = computed(() => {
  if (activeTab.value === 'flows') return 'liteflow-el';
  if (activeTab.value === 'components') return 'groovy';
  return 'plaintext';
});

const flowTree = computed(() => {
  return flows.value.map(name => ({
    key: name,
    label: name,
    isLeaf: true
  }));
});

// 方法
const loadFlows = async () => {
  try {
    flows.value = await liteflowService.flow.list();
  } catch (e) {
    message.error('加载流程列表失败');
  }
};

const handleFlowSelect = async (keys) => {
  if (keys.length === 0) return;
  
  selectedFlow.value = keys;
  currentFileName.value = keys[0];
  
  try {
    code.value = await liteflowService.flow.get(keys[0]);
  } catch (e) {
    message.error('加载流程失败');
  }
};

const handleSave = async () => {
  if (!currentFileName.value) return;
  
  try {
    if (activeTab.value === 'flows') {
      await liteflowService.flow.update(currentFileName.value, code.value);
    } else {
      await liteflowService.component.update(currentFileName.value, code.value);
    }
    message.success('保存成功');
  } catch (e) {
    message.error('保存失败');
  }
};

// 生命周期
onMounted(() => {
  loadFlows();
  loadComponents();
});
</script>
```

---

## 六、关键决策点（需要您确认）

### ✅ 决策1：编辑模式
**推荐**: 纯文本EL编辑（MVP）  
**理由**: 
- 开发快速（3-5天可完成）
- 符合开发者习惯
- 可视化可作为后续增强

**您的选择**：
- [ ] **纯文本EL编辑**（推荐，3-5天）
- [ ] 可视化拖拽编辑（2-3周）
- [ ] 混合模式（3-4周）

---

### ✅ 决策2：UI布局
**推荐**: 左右分栏  
**理由**:
- 符合IDE习惯
- 空间利用率高
- 易于扩展多标签页

**您的选择**：
- [ ] **左右分栏**（推荐，列表 | 编辑器）
- [ ] 标签页模式（流程 | 组件）
- [ ] 三栏布局（列表 | 编辑器 | 属性）

---

### ✅ 决策3：功能优先级
**推荐**: 第一版包含以下核心功能  
**必需**（MVP）：
- [x] 流程CRUD
- [x] 组件CRUD
- [x] EL语法高亮
- [x] Groovy语法高亮 + LiteFlow上下文补全

**可选**（增强）：
- [ ] 流程测试执行
- [ ] 流程可视化预览
- [ ] 组件引用验证

**您的选择**（可多选）：
- [ ] 仅MVP功能（推荐）
- [ ] 包含流程测试执行
- [ ] 包含可视化预览
- [ ] 包含组件引用验证

---

### ✅ 决策4：UI集成位置
**推荐**: 独立菜单项  
**理由**:
- 清晰独立
- 不影响现有功能
- 易于维护

**您的选择**：
- [ ] **独立菜单项**（推荐，左侧导航添加"LiteFlow"）
- [ ] 集成到magic-script编辑器
- [ ] 右侧可折叠面板

---

### ✅ 决策5：文件格式
**推荐**: EL XML格式  
**理由**:
- 当前示例已使用
- LiteFlow官方支持
- 无需额外转换

**您的选择**：
- [ ] **EL XML格式**（推荐）
- [ ] EL JSON格式
- [ ] 两种都支持

---

## 七、开发计划

### 第一阶段：MVP（3-5天）

**Day 1**: 项目搭建
- 创建前端模块结构
- 配置Vite构建
- 实现插件注册入口

**Day 2**: 基础CRUD界面
- 流程列表组件
- 组件列表组件
- API服务封装

**Day 3**: Monaco编辑器集成
- Monaco组件封装
- EL语法高亮配置
- Groovy语法增强

**Day 4**: 编辑器功能
- 文件保存/加载
- 快捷键支持
- 自动保存

**Day 5**: 测试和优化
- 功能测试
- UI调整
- Bug修复

### 第二阶段：增强功能（3-5天）

**Day 6-7**: 语法验证和补全
- EL表达式验证
- 组件引用检查
- 代码片段库

**Day 8-9**: 流程测试
- 执行接口对接
- 结果展示
- 错误处理

**Day 10**: 可视化预览（可选）
- EL解析器实现
- 流程图渲染
- 预览面板

---

## 八、下一步行动

请您确认以上**5个关键决策点**后，我将：

1. ✅ 创建详细的工作计划（`.sisyphus/plans/liteflow-frontend-[date].md`）
2. ✅ 细化每个任务的技术实现
3. ✅ 开始执行开发任务

**重点确认**：
- 决策1：编辑模式（纯文本/可视化/混合）
- 决策2：UI布局（左右分栏/标签页/三栏）
- 决策3：功能优先级（MVP/增强/完整）
- 决策4：UI集成位置（独立菜单/集成/面板）
- 决策5：文件格式（XML/JSON/两者）

---

## 九、风险和注意事项

### 技术风险
- ⚠️ Monaco Editor体积较大（~2MB），需优化加载
- ⚠️ EL表达式解析复杂，需充分测试
- ⚠️ Groovy动态特性，代码补全可能不完整

### 兼容性
- ✅ Windows环境：PowerShell构建脚本已验证
- ✅ 浏览器支持：Chrome 90+, Firefox 88+, Edge 90+
- ⚠️ IE不支持（Monaco要求）

### 维护成本
- ✅ 第一版维护成本低（纯文本编辑）
- ⚠️ 可视化编辑器维护成本较高（如需实现）

---

**请确认决策后，我将立即开始执行开发任务！**
